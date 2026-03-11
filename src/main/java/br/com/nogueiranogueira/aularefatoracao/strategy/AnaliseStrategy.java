package br.com.nogueiranogueira.aularefatoracao.strategy;

import br.com.nogueiranogueira.aularefatoracao.dto.SolicitacaoCreditoRecord;

public interface AnaliseStrategy {

    boolean analisar(SolicitacaoCreditoRecord solicitacao);
    boolean elegivel(SolicitacaoCreditoRecord solicitacao);

}
