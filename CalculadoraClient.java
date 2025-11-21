import java.util.Scanner;

public class CalculadoraClient {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Em vez de RMI → usa HTTP
        ICalculadora calc = new Calculadora("http://localhost:3000");

        while (true) {
            System.out.println("\n=== CALCULADORA DISTRIBUÍDA ===");
            System.out.println("1 - Modo A: decomposição no cliente");
            System.out.println("2 - Modo B: servidor resolve tudo");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            String op = sc.nextLine();

            if (op.equals("0"))
                break;

            System.out.print("Digite a expressão: ");
            String expr = sc.nextLine();

            try {

                if (op.equals("1")) { 
                    // Cliente resolve a expressão chamando soma/multiplica/etc no servidor
                    double r = Expressao.resolver(calc, expr);
                    System.out.println("RESULTADO = " + r);
                }
                else if (op.equals("2")) {
                    // Servidor resolve tudo
                    double r = calc.expressao(expr);
                    System.out.println("RESULTADO = " + r);
                }
                else {
                    System.out.println("Opção inválida!");
                }

            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }

        sc.close();
    }
}
