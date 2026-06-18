package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.Livro;
import biblioteca.dominio.porta.saida.PortaLivroRepositorio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LivroRepositorioMemoria implements PortaLivroRepositorio {
    private final Map<Long, Livro> livros = new LinkedHashMap<>();
    private long proximoId = 1L;

    @Override
    public void salvar(Livro livro) {
        if (livro.getId() == null) {
            livro.setId(proximoId++);
        }
        livros.put(livro.getId(), livro);
    }

    @Override
    public Optional<Livro> buscarPorId(Long id) {
        return Optional.ofNullable(livros.get(id));
    }

    @Override
    public List<Livro> listarTodos() {
        return new ArrayList<>(livros.values());
    }

    @Override
    public void remover(Long id) {
        livros.remove(id);
    }
}
