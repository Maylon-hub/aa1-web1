// src/main/java/com/gametester/model/Bug.java
package com.gametester.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Bug implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int sessaoTesteId;
    private String descricao;
    private String severidade; // 'BAIXA', 'MEDIA', 'ALTA'
    private Timestamp dataRegistro;
    private String screenshotUrl;

    public Bug() {
    }

    public Bug(int id, int sessaoTesteId, String descricao, String severidade, Timestamp dataRegistro, String screenshotUrl) {
        this.id = id;
        this.sessaoTesteId = sessaoTesteId;
        this.descricao = descricao;
        this.severidade = severidade;
        this.dataRegistro = dataRegistro;
        this.screenshotUrl = screenshotUrl;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSessaoTesteId() { return sessaoTesteId; }
    public void setSessaoTesteId(int sessaoTesteId) { this.sessaoTesteId = sessaoTesteId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getSeveridade() { return severidade; }
    public void setSeveridade(String severidade) { this.severidade = severidade; }
    public Timestamp getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(Timestamp dataRegistro) { this.dataRegistro = dataRegistro; }
    public String getScreenshotUrl() { return screenshotUrl; }
    public void setScreenshotUrl(String screenshotUrl) { this.screenshotUrl = screenshotUrl; }
}