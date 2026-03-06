package br.com.nogueiranogueira.aularefatoracao.service;

import br.com.nogueiranogueira.aularefatoracao.strategy.CalculadoraImposto;

public class ProcessadorVendaService {

    public void processar(String cliente, double valor, String tipo, String cep) {

        if (cliente == null || cliente.isEmpty()) {
            System.out.println("Erro: Cliente inválido");
            return;
        }
        if (valor <= 0) {
            System.out.println("Erro: Valor inválido");
            return;
        }


        double frete = 0;
        if (cep.startsWith("85")) { // Paraná
            frete = 10.0;
        } else if (cep.startsWith("01")) { // SP
            frete = 20.0;
        } else {
            frete = 50.0;
        }


        double imposto = CalculadoraImposto.calcularPorTipo(tipo, valor);

        System.out.println("Conectando no banco...");
        System.out.println("INSERT INTO PEDIDOS VALUES (" + cliente + ", " + (valor + frete + imposto) + ")");
        System.out.println("Enviando recibo para " + cliente);
    }
}
