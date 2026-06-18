package biblioteca.aplicacao;

import biblioteca.dominio.Livro;
import biblioteca.dominio.porta.saida.PortaLivroRepositorio;

import java.util.List;

public class LivroServico {
    private final PortaLivroRepositorio livroRepositorio;

    public LivroServico(PortaLivroRepositorio livroRepositorio) {
        this.livroRepositorio = livroRepositorio;
    }

    public Livro cadastrar(String titulo, String autor, String isbn, int quantidadeDisponivel) {
        Livro livro = new Livro(null, titulo, autor, isbn, quantidadeDisponivel);
        livroRepositorio.salvar(livro);
        return livro;
    }

    public List<Livro> listarTodos() {
        return livroRepositorio.listarTodos();
    }
}
