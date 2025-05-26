package br.ufscar.game_tester.model;

public class Estrategia {

    private int id;
    private String nome;
    private String descricao;
    private String exemplos;
    private String dicas;
    private String imagemPath; // Para o caminho da imagem

    // Construtor padrão
    public Estrategia() {
    }

    // Construtor com todos os atributos (exceto id)
    public Estrategia(String nome, String descricao, String exemplos, String dicas, String imagemPath) {
        this.nome = nome;
        this.descricao = descricao;
        this.exemplos = exemplos;
        this.dicas = dicas;
        this.imagemPath = imagemPath;
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

    public String getExemplos() {
        return exemplos;
    }

    public void setExemplos(String exemplos) {
        this.exemplos = exemplos;
    }

    public String getDicas() {
        return dicas;
    }

    public void setDicas(String dicas) {
        this.dicas = dicas;
    }

    public String getImagemPath() {
        return imagemPath;
    }

    public void setImagemPath(String imagemPath) {
        this.imagemPath = imagemPath;
    }

    @Override
    public String toString() {
        return "Estrategia{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}