package br.com.nogueiranogueira.aularefatoracao.strategy;

import br.com.nogueiranogueira.aularefatoracao.dto.SolicitacaoCreditoRecord;
import br.com.nogueiranogueira.aularefatoracao.dto.TipoConta;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class AnaliseStrategyPF implements AnaliseStrategy {

    private static final BigDecimal LIMITE_ALTO_VALOR = new BigDecimal("5000");
    private static final int SCORE_MINIMO = 800;

    @Override
    public boolean analisar(SolicitacaoCreditoRecord solicitacao) {
        // Regra 1: Valor alto com score baixo
        if (solicitacao.valor().compareTo(LIMITE_ALTO_VALOR) > 0 && solicitacao.score() < SCORE_MINIMO) {
            System.out.println("PF Reprovado: Risco alto.");
            return false;
        }

        // Regra 2: Fim de semana (Usando Java Time API moderno)
        DayOfWeek diaHoje = LocalDate.now().getDayOfWeek();
        if (diaHoje == DayOfWeek.SATURDAY || diaHoje == DayOfWeek.SUNDAY) {
            System.out.println("PF: Aprovação manual necessária no fim de semana.");
            return false;
        }

        System.out.println("PF Aprovado.");
        return true;
    }

    @Override
    public boolean elegivel(SolicitacaoCreditoRecord solicitacao) {
        return solicitacao.tipo().equals(TipoConta.PF);
    }
}
