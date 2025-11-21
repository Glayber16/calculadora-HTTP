public interface ICalculadora { // Reaproveitando o codigo do RMI, só que dessa vez sem o remote
    double soma(double a, double b) throws Exception;
    double subtrai(double a, double b) throws Exception;
    double multiplica(double a, double b) throws Exception;
    double divide(double a, double b) throws Exception;
    double expressao(String expr) throws Exception;

}
