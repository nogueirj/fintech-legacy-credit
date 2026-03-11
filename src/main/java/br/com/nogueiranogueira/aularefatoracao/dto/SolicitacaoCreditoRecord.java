package br.com.nogueiranogueira.aularefatoracao.dto;

import java.math.BigDecimal;

public record SolicitacaoCreditoRecord(
        String cliente,
        BigDecimal valor,
        int score,
        boolean negativado,
        TipoConta tipo
) {
}
