package biblioteca.aplicacao;

import biblioteca.dominio.SituacaoUsuario;
import biblioteca.dominio.Usuario;
import biblioteca.dominio.porta.saida.PortaUsuarioRepositorio;

import java.util.List;

public class UsuarioServico {
    private final PortaUsuarioRepositorio usuarioRepositorio;

    public UsuarioServico(PortaUsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public Usuario cadastrar(String nome, String email) {
        Usuario usuario = new Usuario(null, nome, email, SituacaoUsuario.ATIVO);
        usuarioRepositorio.salvar(usuario);
        return usuario;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepositorio.listarTodos();
    }
}
