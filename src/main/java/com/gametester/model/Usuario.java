// src/main/java/com/gametester/model/Usuario.java
package com.gametester.model;

import java.io.Serializable; // Boa prática para objetos que podem ser serializados (ex: sessões HTTP)

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String email;
    private String senha;
    private String tipoPerfil; // Usaremos String para o enum por simplicidade inicial no DB

    // Construtores
    public Usuario() {
    }

    public Usuario(int id, String nome, String email, String senha, String tipoPerfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoPerfil = tipoPerfil;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTipoPerfil() { return tipoPerfil; }
    public void setTipoPerfil(String tipoPerfil) { this.tipoPerfil = tipoPerfil; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipoPerfil='" + tipoPerfil + '\'' +
                '}';
    }
}