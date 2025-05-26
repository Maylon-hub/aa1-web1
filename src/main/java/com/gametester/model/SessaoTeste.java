// src/main/java/com/gametester/model/SessaoTeste.java
package com.gametester.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List; // Para a lista de bugs

public class SessaoTeste implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int projetoId;
    private int testadorId;
    private int estrategiaId;
    private int tempoSessaoMinutos;
    private String descricao;
    private String status; // 'CRIADO', 'EM_EXECUCAO', 'FINALIZADO'
    private Timestamp dataHoraCriacao;
    private Timestamp dataHoraInicio;
    private Timestamp dataHoraFim;

    // Opcional: para carregar o objeto completo
    private Projeto projeto;
    private Usuario testador;
    private Estrategia estrategia;
    private List<Bug> bugs; // Se você quiser carregar os bugs diretamente com a sessão

    public SessaoTeste() {
    }

    // Construtor completo para criação
    public SessaoTeste(int id, int projetoId, int testadorId, int estrategiaId, int tempoSessaoMinutos,
                       String descricao, String status, Timestamp dataHoraCriacao,
                       Timestamp dataHoraInicio, Timestamp dataHoraFim) {
        this.id = id;
        this.projetoId = projetoId;
        this.testadorId = testadorId;
        this.estrategiaId = estrategiaId;
        this.tempoSessaoMinutos = tempoSessaoMinutos;
        this.descricao = descricao;
        this.status = status;
        this.dataHoraCriacao = dataHoraCriacao;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProjetoId() { return projetoId; }
    public void setProjetoId(int projetoId) { this.projetoId = projetoId; }
    public int getTestadorId() { return testadorId; }
    public void setTestadorId(int testadorId) { this.testadorId = testadorId; }
    public int getEstrategiaId() { return estrategiaId; }
    public void setEstrategiaId(int estrategiaId) { this.estrategiaId = estrategiaId; }
    public int getTempoSessaoMinutos() { return tempoSessaoMinutos; }
    public void setTempoSessaoMinutos(int tempoSessaoMinutos) { this.tempoSessaoMinutos = tempoSessaoMinutos; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getDataHoraCriacao() { return dataHoraCriacao; }
    public void setDataHoraCriacao(Timestamp dataHoraCriacao) { this.dataHoraCriacao = dataHoraCriacao; }
    public Timestamp getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(Timestamp dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }
    public Timestamp getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(Timestamp dataHoraFim) { this.dataHoraFim = dataHoraFim; }

    // Getters e Setters para objetos relacionados (se carregados)
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public Usuario getTestador() { return testador; }
    public void setTestador(Usuario testador) { this.testador = testador; }
    public Estrategia getEstrategia() { return estrategia; }
    public void setEstrategia(Estrategia estrategia) { this.estrategia = estrategia; }
    public List<Bug> getBugs() { return bugs; }
    public void setBugs(List<Bug> bugs) { this.bugs = bugs; }
}