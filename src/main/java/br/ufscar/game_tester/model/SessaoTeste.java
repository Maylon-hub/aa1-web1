package br.ufscar.game_tester.model;

import java.util.ArrayList;
import java.util.Date; // Ou java.time.LocalDateTime
import java.util.List;

public class SessaoTeste {

    private int id;
    private Projeto projeto; // Referência ao objeto Projeto
    private Usuario testador; // Referência ao objeto Usuario (testador)
    private Estrategia estrategiaUtilizada; // Referência ao objeto Estrategia
    private int tempoSessaoMinutos;
    private String descricaoSessao;
    private String status; // Ex: "CRIADO", "EM_EXECUCAO", "FINALIZADO"
    private Date dataCriacaoSessao;
    private Date dataInicioExecucao;
    private Date dataFinalizacao;
    private List<Bug> bugsRegistrados;

    // Construtor padrão
    public SessaoTeste() {
        this.bugsRegistrados = new ArrayList<>();
    }

    // Construtor com atributos principais (alguns podem ser definidos depois, como datas de status)
    public SessaoTeste(Projeto projeto, Usuario testador, Estrategia estrategiaUtilizada,
                       int tempoSessaoMinutos, String descricaoSessao) {
        this.projeto = projeto;
        this.testador = testador;
        this.estrategiaUtilizada = estrategiaUtilizada;
        this.tempoSessaoMinutos = tempoSessaoMinutos;
        this.descricaoSessao = descricaoSessao;
        this.status = "CRIADO"; // Status inicial [cite: 15]
        this.dataCriacaoSessao = new Date(); // Data de criação automática
        this.bugsRegistrados = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public Usuario getTestador() {
        return testador;
    }

    public void setTestador(Usuario testador) {
        this.testador = testador;
    }

    public Estrategia getEstrategiaUtilizada() {
        return estrategiaUtilizada;
    }

    public void setEstrategiaUtilizada(Estrategia estrategiaUtilizada) {
        this.estrategiaUtilizada = estrategiaUtilizada;
    }

    public int getTempoSessaoMinutos() {
        return tempoSessaoMinutos;
    }

    public void setTempoSessaoMinutos(int tempoSessaoMinutos) {
        this.tempoSessaoMinutos = tempoSessaoMinutos;
    }

    public String getDescricaoSessao() {
        return descricaoSessao;
    }

    public void setDescricaoSessao(String descricaoSessao) {
        this.descricaoSessao = descricaoSessao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDataCriacaoSessao() {
        return dataCriacaoSessao;
    }

    public void setDataCriacaoSessao(Date dataCriacaoSessao) {
        this.dataCriacaoSessao = dataCriacaoSessao;
    }

    public Date getDataInicioExecucao() {
        return dataInicioExecucao;
    }

    public void setDataInicioExecucao(Date dataInicioExecucao) {
        this.dataInicioExecucao = dataInicioExecucao;
    }

    public Date getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(Date dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public List<Bug> getBugsRegistrados() {
        return bugsRegistrados;
    }

    public void setBugsRegistrados(List<Bug> bugsRegistrados) {
        this.bugsRegistrados = bugsRegistrados;
    }

    public void adicionarBug(Bug bug) {
        if (this.bugsRegistrados == null) {
            this.bugsRegistrados = new ArrayList<>();
        }
        this.bugsRegistrados.add(bug);
    }

    @Override
    public String toString() {
        return "SessaoTeste{" +
                "id=" + id +
                ", projeto=" + (projeto != null ? projeto.getNomeProjeto() : "null") +
                ", testador=" + (testador != null ? testador.getNome() : "null") +
                ", status='" + status + '\'' +
                '}';
    }
}