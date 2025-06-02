package com.gametester.model;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String email;
    private String senha; // No banco, esta senha deve ser armazenada de forma segura (hash)
    private String tipoPerfil; // Ex: "ADMINISTRADOR", "TESTADOR"

    public Usuario() {
    }

    public Usuario(int id, String nome, String email, String senha, String tipoPerfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha; // Ao criar/atualizar, esta seria a senha em texto plano antes do hash
        this.tipoPerfil = tipoPerfil;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(String tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                // Não inclua a senha no toString por segurança
                ", tipoPerfil='" + tipoPerfil + '\'' +
                '}';
    }
}