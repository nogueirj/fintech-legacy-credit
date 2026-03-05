package br.com.nogueiranogueira.aularefatoracao.strategy;

import br.com.nogueiranogueira.aularefatoracao.dto.SolicitacaoAnalise;
import br.com.nogueiranogueira.aularefatoracao.dto.TipoConta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Slf4j
@Component
public class AnalisePF implements SolicitacaoStrategy {

    @Override
    public boolean Analisar(SolicitacaoAnalise solicitacao) {
        if (solicitacao.valor() > 5000 && solicitacao.score() < 800) {
            log.warn("Reprovado: Valor alto para PF com score médio");
            return false;
        }

        // Refatoração do new Date().getDay() para API moderna do Java
        DayOfWeek diaSemana = LocalDate.now().getDayOfWeek();
        if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) {
            log.warn("Aprovação manual necessária no fim de semana");
            return false;
        }

        log.info("Aprovado PF");
        return true;
    }

    @Override
    public boolean Elegivel(SolicitacaoAnalise solicitacao) {
        return solicitacao.tipoConta() == TipoConta.PF;
    }
}