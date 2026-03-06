package br.com.nogueiranogueira.aularefatoracao.strategy;

public enum CalculadoraImposto {
    PRODUTO {
        @Override
        public double calcular(double valor) {
            return valor * 0.18;
        }
    },
    SERVICO {
        @Override
        public double calcular(double valor) {
            return valor * 0.05;
        }
    };

    public abstract double calcular(double valor);

    public static double calcularPorTipo(String tipo, double valor) {
        try {
            if (tipo == null) return 0.0;
            return valueOf(tipo).calcular(valor);
        } catch (IllegalArgumentException e) {
            return 0.0;
        }
    }
}