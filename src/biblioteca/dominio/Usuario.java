package biblioteca.dominio;

public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private SituacaoUsuario situacao;

    public Usuario(Long id, String nome, String email, SituacaoUsuario situacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.situacao = situacao;
    }

    public boolean estaAtivo() {
        return SituacaoUsuario.ATIVO.equals(situacao);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public SituacaoUsuario getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoUsuario situacao) {
        this.situacao = situacao;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", situacao=" + situacao +
                '}';
    }
}
