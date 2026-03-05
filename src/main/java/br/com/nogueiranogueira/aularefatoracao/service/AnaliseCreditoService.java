package br.com.nogueiranogueira.aularefatoracao.service;

import br.com.nogueiranogueira.aularefatoracao.dto.SolicitacaoAnalise;
import br.com.nogueiranogueira.aularefatoracao.strategy.SolicitacaoStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnaliseCreditoService {

    // O Spring injeta automaticamente o AnalisePF e AnalisePJ aqui
    private final List<SolicitacaoStrategy> strategies;

    public boolean analisarSolicitacao(SolicitacaoAnalise solicitacao) {
        log.info("Iniciando análise para: {}", solicitacao.cliente());

        // Fail-Fast: Validações iniciais (Regras aplicáveis a ambos)
        if (solicitacao.valor() <= 0) {
            log.warn("Valor inválido para a solicitação.");
            return false;
        }
        if (solicitacao.negativado()) {
            log.warn("Cliente negativado. Solicitação reprovada.");
            return false;
        }
        if (solicitacao.score() <= 500) {
            log.warn("Score muito baixo. Solicitação reprovada.");
            return false;
        }

        try {
            log.info("Consultando Bureau de Crédito Externo...");
            Thread.sleep(2000); // Simula delay do serviço externo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Erro na comunicação com o Bureau de Crédito", e);
            return false;
        }

        // Executa a estratégia compatível
        return strategies.stream()
                .filter(strategy -> strategy.Elegivel(solicitacao))
                .findFirst()
                .map(strategy -> strategy.Analisar(solicitacao))
                .orElseGet(() -> {
                    log.error("Nenhuma estratégia encontrada para o tipo de conta: {}", solicitacao.tipoConta());
                    return false;
                });
    }

    public void processarLote(List<SolicitacaoAnalise> solicitacoes) {
        for (SolicitacaoAnalise solicitacao : solicitacoes) {
            analisarSolicitacao(solicitacao);
        }
    }
}