// src/main/java/com/gametester/model/Estrategia.java
package com.gametester.model;

import java.io.Serializable;
import java.util.List; // Para a lista de imagens (se implementado)

public class Estrategia implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String descricao;
    private String exemplos;
    private String dicas;
    // private List<String> imagens; // Se você for gerenciar URLs de imagem diretamente aqui

    public Estrategia() {
    }

    public Estrategia(int id, String nome, String descricao, String exemplos, String dicas) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.exemplos = exemplos;
        this.dicas = dicas;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getExemplos() { return exemplos; }
    public void setExemplos(String exemplos) { this.exemplos = exemplos; }
    public String getDicas() { return dicas; }
    public void setDicas(String dicas) { this.dicas = dicas; }
    // public List<String> getImagens() { return imagens; }
    // public void setImagens(List<String> imagens) { this.imagens = imagens; }
}