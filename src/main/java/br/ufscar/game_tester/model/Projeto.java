package br.ufscar.game_tester.model;

import java.util.ArrayList;
import java.util.Date; // Ou java.time.LocalDateTime para APIs de data/hora mais modernas
import java.util.List;

public class Projeto {

    private int id;
    private String nomeProjeto;
    private String descricao;
    private Date dataCriacao; // Considere usar java.time.LocalDateTime
    private List<Usuario> membrosPermitidos; // Representa os membros do projeto

    // Construtor padrão
    public Projeto() {
        this.membrosPermitidos = new ArrayList<>(); // Inicializa a lista
    }

    // Construtor com atributos principais (exceto id e dataCriacao que podem ser automáticos)
    public Projeto(String nomeProjeto, String descricao) {
        this.nomeProjeto = nomeProjeto;
        this.descricao = descricao;
        this.membrosPermitidos = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<Usuario> getMembrosPermitidos() {
        return membrosPermitidos;
    }

    public void setMembrosPermitidos(List<Usuario> membrosPermitidos) {
        this.membrosPermitidos = membrosPermitidos;
    }

    // Métodos utilitários para adicionar/remover membros (opcional)
    public void adicionarMembro(Usuario usuario) {
        if (this.membrosPermitidos == null) {
            this.membrosPermitidos = new ArrayList<>();
        }
        if (!this.membrosPermitidos.contains(usuario)) { // Evitar duplicados
            this.membrosPermitidos.add(usuario);
        }
    }

    public void removerMembro(Usuario usuario) {
        if (this.membrosPermitidos != null) {
            this.membrosPermitidos.remove(usuario);
        }
    }


    @Override
    public String toString() {
        return "Projeto{" +
                "id=" + id +
                ", nomeProjeto='" + nomeProjeto + '\'' +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}