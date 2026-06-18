package biblioteca.dominio;

public class Livro {
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private int quantidadeDisponivel;

    public Livro(Long id, String titulo, String autor, String isbn, int quantidadeDisponivel) {
        if (quantidadeDisponivel < 0) {
            throw new IllegalArgumentException("Quantidade disponivel nao pode ser negativa.");
        }
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public void realizarEmprestimo() {
        if (quantidadeDisponivel <= 0) {
            throw new IllegalStateException("Livro indisponivel para emprestimo.");
        }
        quantidadeDisponivel--;
    }

    public void registrarDevolucao() {
        quantidadeDisponivel++;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        if (quantidadeDisponivel < 0) {
            throw new IllegalArgumentException("Quantidade disponivel nao pode ser negativa.");
        }
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    @Override
    public String toString() {
        return "Livro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", isbn='" + isbn + '\'' +
                ", quantidadeDisponivel=" + quantidadeDisponivel +
                '}';
    }
}
