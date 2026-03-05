package br.com.nogueiranogueira.aularefatoracao.strategy;

import br.com.nogueiranogueira.aularefatoracao.dto.SolicitacaoAnalise;
import br.com.nogueiranogueira.aularefatoracao.dto.TipoConta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AnalisePJ implements SolicitacaoStrategy {

    @Override
    public boolean Analisar(SolicitacaoAnalise solicitacao) {
        if (solicitacao.valor() > 50000 && solicitacao.score() < 700) {
            log.warn("Reprovado: Risco PJ");
            return false;
        }
        log.info("Aprovado PJ");
        return true;
    }

    @Override
    public boolean Elegivel(SolicitacaoAnalise solicitacao) {
        return solicitacao.tipoConta() == TipoConta.PJ;
    }
}