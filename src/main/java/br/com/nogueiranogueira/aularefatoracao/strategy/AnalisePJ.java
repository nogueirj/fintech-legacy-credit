package br.com.nogueiranogueira.aularefatoracao.strategy;

import br.com.nogueiranogueira.aularefatoracao.dto.SolicitacaoAnalise;
import br.com.nogueiranogueira.aularefatoracao.dto.TipoConta;

public class AnalisePJ implements SolicitacaoStrategy {

    @Override
    public boolean Analisar(SolicitacaoAnalise solicitacao) {
        if (solicitacao.valor() > 50000 && solicitacao.score() < 700) {
            System.out.println("Reprovado: Risco PJ");
            return false;
        }
        System.out.println("Aprovado PJ");
        return true;
    }

    @Override
    public boolean Elegivel(SolicitacaoAnalise solicitacao) {
        return solicitacao.tipoConta().equals(TipoConta.PF);
    }
}
