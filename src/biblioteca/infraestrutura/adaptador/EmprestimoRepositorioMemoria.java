package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.porta.saida.PortaEmprestimoRepositorio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmprestimoRepositorioMemoria implements PortaEmprestimoRepositorio {
    private final Map<Long, Emprestimo> emprestimos = new LinkedHashMap<>();
    private long proximoId = 1L;

    @Override
    public void salvar(Emprestimo emprestimo) {
        if (emprestimo.getId() == null) {
            emprestimo.setId(proximoId++);
        }
        emprestimos.put(emprestimo.getId(), emprestimo);
    }

    @Override
    public Optional<Emprestimo> buscarPorId(Long id) {
        return Optional.ofNullable(emprestimos.get(id));
    }

    @Override
    public List<Emprestimo> listarTodos() {
        return new ArrayList<>(emprestimos.values());
    }

    @Override
    public void remover(Long id) {
        emprestimos.remove(id);
    }
}
