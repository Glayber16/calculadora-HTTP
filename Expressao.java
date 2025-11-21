import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Expressao {

    public static double resolver(ICalculadora calc, String expr) throws Exception { // Codigo reaproveitado do RMI

        // Resolve parênteses
        while (expr.contains("(")) {
            Matcher m = Pattern.compile("\\([^()]+\\)").matcher(expr);
            if (m.find()) {
                String dentro = m.group().substring(1, m.group().length() - 1);
                double valor = resolver(calc, dentro);
                expr = expr.replace(m.group(), String.valueOf(valor));
            }
        }

        // Tokeniza
        Pattern p = Pattern.compile("\\d+\\.?\\d*|[+\\-*/]");
        Matcher m = p.matcher(expr);

        java.util.List<String> tokens = new java.util.ArrayList<>();
        while (m.find()) tokens.add(m.group());

        // * e /
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals("*") || tokens.get(i).equals("/")) {
                double a = Double.parseDouble(tokens.get(i - 1));
                double b = Double.parseDouble(tokens.get(i + 1));

                double r = tokens.get(i).equals("*")
                        ? calc.multiplica(a, b)
                        : calc.divide(a, b);

                tokens.set(i - 1, String.valueOf(r));
                tokens.remove(i);
                tokens.remove(i);
                i--;
            }
        }

        // + e -
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals("+") || tokens.get(i).equals("-")) {
                double a = Double.parseDouble(tokens.get(i - 1));
                double b = Double.parseDouble(tokens.get(i + 1));

                double r = tokens.get(i).equals("+")
                        ? calc.soma(a, b)
                        : calc.subtrai(a, b);

                tokens.set(i - 1, String.valueOf(r));
                tokens.remove(i);
                tokens.remove(i);
                i--;
            }
        }

        return Double.parseDouble(tokens.get(0));
    }
}
