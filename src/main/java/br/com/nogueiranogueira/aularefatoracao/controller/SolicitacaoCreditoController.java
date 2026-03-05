package br.com.nogueiranogueira.aularefatoracao.controller;

import br.com.nogueiranogueira.aularefatoracao.dto.SolicitacaoAnalise;
import br.com.nogueiranogueira.aularefatoracao.dto.TipoConta;
import br.com.nogueiranogueira.aularefatoracao.service.AnaliseCreditoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/solicitacoes")
@RequiredArgsConstructor
@Slf4j
public class SolicitacaoCreditoController {

    // Agora usamos a injeção do serviço refatorado
    private final AnaliseCreditoService analiseCreditoService;

    @PostMapping("/analisar")
    public ResponseEntity<Map<String, Object>> analisarSolicitacao(
            @RequestParam String cliente,
            @RequestParam Double valor,
            @RequestParam Integer score,
            @RequestParam(defaultValue = "false") Boolean negativado,
            @RequestParam(defaultValue = "PF") String tipoConta) {

        log.info("Recebida requisição de análise para cliente: {}", cliente);

        try {
            TipoConta tipo = TipoConta.valueOf(tipoConta.toUpperCase());
            SolicitacaoAnalise solicitacao = new SolicitacaoAnalise(cliente, valor, score, negativado, tipo);

            boolean aprovado = analiseCreditoService.analisarSolicitacao(solicitacao);

            Map<String, Object> response = new HashMap<>();
            response.put("cliente", cliente);
            response.put("valor", valor);
            response.put("score", score);
            response.put("aprovado", aprovado);
            response.put("mensagem", aprovado ? "Solicitação aprovada" : "Solicitação reprovada");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Tipo de conta inválido", e);
            Map<String, Object> error = new HashMap<>();
            error.put("erro", "Tipo de conta inválido. Use PF ou PJ.");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Erro ao analisar solicitação", e);
            Map<String, Object> error = new HashMap<>();
            error.put("erro", "Erro ao processar solicitação");
            error.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/processar-lote")
    public ResponseEntity<Map<String, String>> processarLote(@RequestBody List<String> clientes) {
        log.info("Recebida requisição para processar lote com {} clientes", clientes.size());

        try {
            List<SolicitacaoAnalise> lote = clientes.stream()
                    .map(c -> new SolicitacaoAnalise(c, 1000.0, 600, false, TipoConta.PF))
                    .collect(Collectors.toList());

            analiseCreditoService.processarLote(lote);

            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Lote processado com sucesso");
            response.put("totalClientes", String.valueOf(clientes.size()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao processar lote", e);
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao processar lote");
            error.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/saude")
    public ResponseEntity<Map<String, String>> saude() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("mensagem", "Aplicação funcionando corretamente");
        return ResponseEntity.ok(response);
    }
}