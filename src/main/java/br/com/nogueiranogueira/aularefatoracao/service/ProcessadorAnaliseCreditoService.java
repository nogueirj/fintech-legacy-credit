package br.com.nogueiranogueira.aularefatoracao.service;

import br.com.nogueiranogueira.aularefatoracao.dto.SolicitacaoCreditoRecord;
import br.com.nogueiranogueira.aularefatoracao.factory.AnaliseCreditoFactory;
import br.com.nogueiranogueira.aularefatoracao.strategy.AnaliseStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.AnaliseStrategyPF;
import br.com.nogueiranogueira.aularefatoracao.strategy.AnaliseStrategyPJ;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;

@Service
public class ProcessadorAnaliseCreditoService {

    private List<AnaliseStrategy> analiseStrategies;

    public ProcessadorAnaliseCreditoService() {

    }

    public void processarLote(List<SolicitacaoCreditoRecord> solicitacoes) {
        System.out.println("=== Iniciando Processamento Paralelo (Virtual Threads) ===");
        long inicio = System.currentTimeMillis();

        // Tenta criar uma thread virtual para CADA solicitação.
        // Se houver 10.000 solicitações, ele dispara as 10.000 de uma vez.
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (SolicitacaoCreditoRecord solicitacao : solicitacoes) {
                executor.submit(() -> processarIndividual(solicitacao));
            }

        } // O try-with-resources aguarda TODAS as threads terminarem aqui.

        long fim = System.currentTimeMillis();
        System.out.println("Tempo Total: " + (fim - inicio) + " ms");
    }

    public void processarIndividual(SolicitacaoCreditoRecord solicitacao) {
        if (solicitacao.negativado()) {
            System.out.println("Rejeição Automática: Negativado -> " + solicitacao.cliente());
            return;
        }

        analiseStrategies.stream()
                .filter(e -> e.elegivel(solicitacao))
                .findFirst()
                .ifPresentOrElse(
                        estrategia -> estrategia.analisar(solicitacao),
                        () -> System.out.println("Nenhuma estratégia encontrada para este perfil.")
                );
    }
}
