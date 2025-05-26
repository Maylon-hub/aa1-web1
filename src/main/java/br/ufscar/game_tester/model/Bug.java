package br.ufscar.game_tester.model;

import java.util.Date; // Ou java.time.LocalDateTime

public class Bug {

    private int id;
    private SessaoTeste sessaoTeste; // Referência à SessaoTeste
    private String descricaoBug;
    private Date dataRegistroBug;
    private String severidade; // Ex: "BAIXA", "MEDIA", "ALTA", "CRITICA"
    private String statusBug;  // Ex: "ABERTO", "EM_ANALISE", "CORRIGIDO", "FECHADO"
    private String screenshotPath; // Opcional

    // Construtor padrão
    public Bug() {
    }

    // Construtor com atributos principais
    public Bug(SessaoTeste sessaoTeste, String descricaoBug, String severidade, String statusBug) {
        this.sessaoTeste = sessaoTeste;
        this.descricaoBug = descricaoBug;
        this.dataRegistroBug = new Date(); // Data de registro automática
        this.severidade = severidade;
        this.statusBug = statusBug;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SessaoTeste getSessaoTeste() {
        return sessaoTeste;
    }

    public void setSessaoTeste(SessaoTeste sessaoTeste) {
        this.sessaoTeste = sessaoTeste;
    }

    public String getDescricaoBug() {
        return descricaoBug;
    }

    public void setDescricaoBug(String descricaoBug) {
        this.descricaoBug = descricaoBug;
    }

    public Date getDataRegistroBug() {
        return dataRegistroBug;
    }

    public void setDataRegistroBug(Date dataRegistroBug) {
        this.dataRegistroBug = dataRegistroBug;
    }

    public String getSeveridade() {
        return severidade;
    }

    public void setSeveridade(String severidade) {
        this.severidade = severidade;
    }

    public String getStatusBug() {
        return statusBug;
    }

    public void setStatusBug(String statusBug) {
        this.statusBug = statusBug;
    }

    public String getScreenshotPath() {
        return screenshotPath;
    }

    public void setScreenshotPath(String screenshotPath) {
        this.screenshotPath = screenshotPath;
    }

    @Override
    public String toString() {
        return "Bug{" +
                "id=" + id +
                ", descricaoBug='" + descricaoBug + '\'' +
                ", statusBug='" + statusBug + '\'' +
                '}';
    }
}