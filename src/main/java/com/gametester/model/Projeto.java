// src/main/java/com/gametester/model/Projeto.java
package com.gametester.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List; // Para a lista de membros

public class Projeto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String descricao;
    private Timestamp dataCriacao;
    private List<Usuario> membrosPermitidos; // Opcional: carregar membros no DAO

    public Projeto() {
    }

    public Projeto(int id, String nome, String descricao, Timestamp dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Timestamp getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Timestamp dataCriacao) { this.dataCriacao = dataCriacao; }
    public List<Usuario> getMembrosPermitidos() { return membrosPermitidos; }
    public void setMembrosPermitidos(List<Usuario> membrosPermitidos) { this.membrosPermitidos = membrosPermitidos; }
}