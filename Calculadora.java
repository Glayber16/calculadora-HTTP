import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class Calculadora implements ICalculadora {

    private final String servidor;

    private final int MAX_TENTATIVAS = 3;     // número de retries
    private final int ESPERA_MS = 1000;       // 1 segundo entre tentativas

    public Calculadora(String servidor) {
        this.servidor = servidor;
    }

    /** Executa uma operação com política de retry */
    private <T> T executarComRetry(Callable<T> tarefa) throws Exception {
        Exception ultimoErro = null;

        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {

            try {
                return tarefa.call();   // se funcionar, retorna
            }
            catch (Exception e) {
                ultimoErro = e;
                System.err.println("Falha na tentativa " + tentativa + ": " + e.getMessage());

                if (tentativa < MAX_TENTATIVAS) {
                    Thread.sleep(ESPERA_MS); // espera antes de tentar de novo
                }
            }
        }

        throw new Exception("Falha após " + MAX_TENTATIVAS + " tentativas: " + ultimoErro.getMessage());
    }


    /** Faz chamada HTTP para /calc */
    private double enviar(String operacao, double a, double b) throws Exception {

        URL url = new URL(servidor + "/calc");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        String json = "{ \"oper1\": " + a + ", \"oper2\": " + b + ", \"operacao\": " + operacao + " }";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes("UTF-8"));
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder resposta = new StringBuilder();
        String linha;

        while ((linha = br.readLine()) != null)
            resposta.append(linha);

        br.close();
        String resp = resposta.toString();

        if (resp.contains("\"resultado\"")) {
            String temp = resp.split("\"resultado\"")[1];
            temp = temp.substring(temp.indexOf(':') + 1);
            temp = temp.replace("}", "").trim();
            return Double.parseDouble(temp);
        }

        throw new Exception("Erro do servidor: " + resp);
    }


    /** Faz chamada HTTP para /expr */
    @Override
    public double expressao(String expr) throws Exception {

        URL url = new URL(servidor + "/expr");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        String json = "{ \"expressao\": \"" + expr.replace("\"", "\\\"") + "\" }";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes("UTF-8"));
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder resposta = new StringBuilder();
        String linha;

        while ((linha = br.readLine()) != null)
            resposta.append(linha);

        br.close();
        String resp = resposta.toString();

        if (resp.contains("\"resultado\"")) {
            String temp = resp.split("\"resultado\"")[1];
            temp = temp.substring(temp.indexOf(':') + 1);
            temp = temp.replace("}", "").trim();
            return Double.parseDouble(temp);
        }

        throw new Exception("Erro do servidor: " + resp);
    }


    // Usa retry para cada operação
    @Override
    public double soma(double a, double b) throws Exception {
        return executarComRetry(() -> enviar("1", a, b));
    }

    @Override
    public double subtrai(double a, double b) throws Exception {
        return executarComRetry(() -> enviar("2", a, b));
    }

    @Override
    public double multiplica(double a, double b) throws Exception {
        return executarComRetry(() -> enviar("3", a, b));
    }

    @Override
    public double divide(double a, double b) throws Exception {
        return executarComRetry(() -> enviar("4", a, b));
    }


}
