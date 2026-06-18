package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.Usuario;
import biblioteca.dominio.porta.saida.PortaUsuarioRepositorio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UsuarioRepositorioMemoria implements PortaUsuarioRepositorio {
    private final Map<Long, Usuario> usuarios = new LinkedHashMap<>();
    private long proximoId = 1L;

    @Override
    public void salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(proximoId++);
        }
        usuarios.put(usuario.getId(), usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios.values());
    }

    @Override
    public void remover(Long id) {
        usuarios.remove(id);
    }
}
