package br.com.nogueiranogueira.aularefatoracao;

import br.com.nogueiranogueira.aularefatoracao.dto.SolicitacaoAnalise;
import br.com.nogueiranogueira.aularefatoracao.dto.TipoConta;
import br.com.nogueiranogueira.aularefatoracao.service.AnaliseCreditoService;
import br.com.nogueiranogueira.aularefatoracao.strategy.AnalisePF;
import br.com.nogueiranogueira.aularefatoracao.strategy.AnalisePJ;
import br.com.nogueiranogueira.aularefatoracao.strategy.SolicitacaoStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestAnaliseCreditoService {

    private AnaliseCreditoService service;

    @BeforeEach
    public void setup() {
        List<SolicitacaoStrategy> strategies = Arrays.asList(new AnalisePF(), new AnalisePJ());
        service = new AnaliseCreditoService(strategies);
    }

    @Test
    public void testAnalisarSolicitacaoValorInvalido() {
        SolicitacaoAnalise s = new SolicitacaoAnalise("João Silva", -1000.0, 600, false, TipoConta.PF);
        assertFalse(service.analisarSolicitacao(s), "Solicitação com valor inválido deve ser reprovada");
    }

    @Test
    public void testAnalisarSolicitacaoClienteNegativado() {
        SolicitacaoAnalise s = new SolicitacaoAnalise("Maria Santos", 1000.0, 600, true, TipoConta.PF);
        assertFalse(service.analisarSolicitacao(s), "Cliente negativado deve ser reprovado");
    }

    @Test
    public void testAnalisarSolicitacaoScoreBaixo() {
        SolicitacaoAnalise s = new SolicitacaoAnalise("Pedro Costa", 1000.0, 400, false, TipoConta.PF);
        assertFalse(service.analisarSolicitacao(s), "Score abaixo de 500 deve resultar em reprovação");
    }

    @Test
    public void testAnalisarSolicitacaoPFValorAltoScoreMedio() {
        SolicitacaoAnalise s = new SolicitacaoAnalise("Ana Costa", 6000.0, 700, false, TipoConta.PF);
        assertFalse(service.analisarSolicitacao(s), "PF com valor alto e score médio deve ser reprovado");
    }

    @Test
    public void testAnalisarSolicitacaoPJValorAltoScoreBaixo() {
        SolicitacaoAnalise s = new SolicitacaoAnalise("Empresa XYZ LTDA", 60000.0, 650, false, TipoConta.PJ);
        assertFalse(service.analisarSolicitacao(s), "PJ com valor alto e score baixo deve ser reprovado");
    }

    @Test
    public void testAnalisarSolicitacaoPJValorAltoScoreAlto() {
        SolicitacaoAnalise s = new SolicitacaoAnalise("Tech Solutions LTDA", 60000.0, 750, false, TipoConta.PJ);
        assertTrue(service.analisarSolicitacao(s), "PJ com valor alto e score alto deve ser aprovado");
    }
}