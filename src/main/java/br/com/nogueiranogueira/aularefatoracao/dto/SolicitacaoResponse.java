package br.com.nogueiranogueira.aularefatoracao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SolicitacaoResponse {

    private String cliente;
    private Double valor;
    private Integer score;
    private boolean aprovado;
    private String mensagem;
}
