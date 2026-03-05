package br.com.nogueiranogueira.aularefatoracao.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class AnaliseCreditoService {

    // Extração de Constantes (Adeus Magic Numbers)
    private static final int SCORE_MINIMO_GERAL = 500;
    private static final int SCORE_CORTE_PF = 800;
    private static final int SCORE_CORTE_PJ = 700;
    private static final double LIMITE_VALOR_PF = 5000.0;
    private static final double LIMITE_VALOR_PJ = 50000.0;

    public boolean analisarSolicitacao(String cliente, double valor, int score, boolean negativado, String tipoConta) {
        log.info("Iniciando análise para cliente: {}", cliente);

        // 1. Cláusulas de Guarda (Validações iniciais)
        if (valor <= 0) {
            log.warn("Análise abortada: Valor inválido ({})", valor);
            return false;
        }

        if (negativado) {
            log.warn("Análise reprovada: Cliente {} está negativado", cliente);
            return false;
        }

        if (score <= SCORE_MINIMO_GERAL) {
            log.warn("Análise reprovada: Score insuficiente ({})", score);
            return false;
        }

        consultarBureauExterno();

        // 2. Delegação da lógica por tipo de conta
        return engineDeDecisao(tipoConta, valor, score);
    }

    private boolean engineDeDecisao(String tipoConta, double valor, int score) {
        return switch (tipoConta.toUpperCase()) {
            case "PF" -> avaliarPF(valor, score);
            case "PJ" -> avaliarPJ(valor, score);
            default -> {
                log.error("Tipo de conta desconhecido: {}", tipoConta);
                yield false;
            }
        };
    }

    private boolean avaliarPF(double valor, int score) {
        if (valor > LIMITE_VALOR_PF && score < SCORE_CORTE_PF) {
            log.info("Reprovado PF: Valor alto para score médio");
            return false;
        }

        if (isFimDeSemana()) {
            log.info("Reprovado PF: Aprovação manual necessária (Fim de semana)");
            return false;
        }

        log.info("Aprovado PF");
        return true;
    }

    private boolean avaliarPJ(double valor, int score) {
        if (valor > LIMITE_VALOR_PJ && score < SCORE_CORTE_PJ) {
            log.info("Reprovado PJ: Risco elevado");
            return false;
        }
        log.info("Aprovado PJ");
        return true;
    }

    private void consultarBureauExterno() {
        try {
            log.info("Consultando Bureau de Crédito Externo...");
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            log.error("Falha na consulta externa", e);
            Thread.currentThread().interrupt(); // Boa prática: restaura o status de interrupção
        }
    }

    private boolean isFimDeSemana() {
        DayOfWeek hoje = LocalDate.now().getDayOfWeek();
        return hoje == DayOfWeek.SATURDAY || hoje == DayOfWeek.SUNDAY;
    }

    public void processarLote(List<String> clientes) {
        // No mundo real, aqui receberíamos uma lista de objetos DTO, não apenas Strings
        clientes.forEach(c -> analisarSolicitacao(c, 1000.0, 600, false, "PF"));
    }
}