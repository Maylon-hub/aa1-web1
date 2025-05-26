package br.ufscar.game_tester.model;
import java.util.List; // Adicionado para o relacionamento com Projeto, se necessário

public class Usuario {

    private int id;
    private String nome;
    private String email;
    private String senha; // Lembre-se que na prática armazenamos o hash
    private String tipoPerfil;

    // Relacionamento: Um usuário pode ser membro de vários projetos
    // private List<Projeto> projetosMembro; // Opcional, dependendo de como você gerencia o relacionamento

    // Construtor padrão
    public Usuario() {
    }

    // Construtor com todos os atributos (exceto id, que é gerado pelo BD)
    public Usuario(String nome, String email, String senha, String tipoPerfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
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

    // Getters e Setters para projetosMembro, se incluído
    // public List<Projeto> getProjetosMembro() {
    //     return projetosMembro;
    // }
    //
    // public void setProjetosMembro(List<Projeto> projetosMembro) {
    //     this.projetosMembro = projetosMembro;
    // }


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