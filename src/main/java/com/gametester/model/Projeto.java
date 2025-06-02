package com.gametester.model;

import java.io.Serializable;
import java.sql.Timestamp;
// import java.util.List; // Para membros, se for uma lista de objetos Usuario

public class Projeto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String descricao;
    private Timestamp dataCriacao;
    // private List<Usuario> membros; // Implementação futura para membros
    // Ou, para simplificar inicialmente, podemos ter apenas uma lista de IDs de membros ou nenhum gerenciamento de membros nesta fase.

    public Projeto() {
    }

    public Projeto(int id, String nome, String descricao, Timestamp dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Timestamp getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Timestamp dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    // Getters e setters para membros seriam adicionados depois
    // public List<Usuario> getMembros() { return membros; }
    // public void setMembros(List<Usuario> membros) { this.membros = membros; }

    @Override
    public String toString() {
        return "Projeto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}