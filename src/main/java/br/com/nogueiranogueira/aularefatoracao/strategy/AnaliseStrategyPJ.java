package br.com.nogueiranogueira.aularefatoracao.strategy;

import br.com.nogueiranogueira.aularefatoracao.dto.SolicitacaoCreditoRecord;
import br.com.nogueiranogueira.aularefatoracao.dto.TipoConta;

import java.math.BigDecimal;

public class AnaliseStrategyPJ implements AnaliseStrategy {

    private static final BigDecimal LIMITE_PJ = new BigDecimal("50000");

    @Override
    public boolean analisar(SolicitacaoCreditoRecord solicitacao) {
        if (solicitacao.valor().compareTo(LIMITE_PJ) > 0 && solicitacao.score() < 700) {
            System.out.println("PJ Reprovado: Risco corporativo.");
            return false;
        }
        System.out.println("PJ Aprovado.");
        return true;
    }

    @Override
    public boolean elegivel(SolicitacaoCreditoRecord solicitacao) {
        return solicitacao.tipo().equals(TipoConta.PJ);
    }
}
