package biblioteca.apresentacao;

import biblioteca.aplicacao.LivroServico;
import biblioteca.aplicacao.UsuarioServico;
import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.Livro;
import biblioteca.dominio.SituacaoUsuario;
import biblioteca.dominio.Usuario;
import biblioteca.dominio.evento.DevolucaoRegistradaEvento;
import biblioteca.dominio.evento.EmprestimoRealizadoEvento;
import biblioteca.dominio.evento.EventBus;
import biblioteca.dominio.porta.entrada.PortaEmprestimo;
import biblioteca.dominio.porta.saida.PortaEmprestimoRepositorio;
import biblioteca.dominio.porta.saida.PortaLivroRepositorio;
import biblioteca.dominio.porta.saida.PortaNotificacao;
import biblioteca.dominio.porta.saida.PortaUsuarioRepositorio;
import biblioteca.dominio.servico.EmprestimoServico;
import biblioteca.infraestrutura.adaptador.EmprestimoRepositorioMemoria;
import biblioteca.infraestrutura.adaptador.LivroRepositorioMemoria;
import biblioteca.infraestrutura.adaptador.NotificacaoConsole;
import biblioteca.infraestrutura.adaptador.UsuarioRepositorioMemoria;

public class VerificacaoManual {
    public static void main(String[] args) {
        PortaLivroRepositorio livroRepositorio = new LivroRepositorioMemoria();
        PortaUsuarioRepositorio usuarioRepositorio = new UsuarioRepositorioMemoria();
        PortaEmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorioMemoria();
        PortaNotificacao notificacao = new NotificacaoConsole();
        EventBus<EmprestimoRealizadoEvento> eventosEmprestimo = new EventBus<>();
        EventBus<DevolucaoRegistradaEvento> eventosDevolucao = new EventBus<>();

        LivroServico livroServico = new LivroServico(livroRepositorio);
        UsuarioServico usuarioServico = new UsuarioServico(usuarioRepositorio);
        PortaEmprestimo emprestimoServico = new EmprestimoServico(
                livroRepositorio,
                usuarioRepositorio,
                emprestimoRepositorio,
                notificacao,
                eventosEmprestimo,
                eventosDevolucao
        );

        Livro livro = livroServico.cadastrar("Domain-Driven Design", "Eric Evans", "978-0321125217", 1);
        Usuario usuario = usuarioServico.cadastrar("Joao Teste", "joao.teste@email.com");
        Emprestimo emprestimo = emprestimoServico.realizarEmprestimo(usuario.getId(), livro.getId());

        exigir(livro.getQuantidadeDisponivel() == 0, "Estoque deve decrementar ao emprestar.");
        exigir(emprestimoServico.listarEmprestimosAtivos().size() == 1, "Emprestimo deve ficar ativo.");

        emprestimoServico.registrarDevolucao(emprestimo.getId());
        exigir(livro.getQuantidadeDisponivel() == 1, "Estoque deve incrementar na devolucao.");
        exigir(emprestimoServico.listarEmprestimosAtivos().isEmpty(), "Nao deve haver emprestimos ativos apos devolucao.");

        Usuario suspenso = new Usuario(null, "Usuario Suspenso", "suspenso@email.com", SituacaoUsuario.SUSPENSO);
        usuarioRepositorio.salvar(suspenso);
        exigirFalha(() -> emprestimoServico.realizarEmprestimo(suspenso.getId(), livro.getId()), "Usuario suspenso nao deve emprestar.");

        eventosEmprestimo.aguardarProcessamento();
        eventosDevolucao.aguardarProcessamento();
        System.out.println("Verificacao manual concluida com sucesso.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new IllegalStateException(mensagem);
        }
    }

    private static void exigirFalha(Runnable acao, String mensagem) {
        try {
            acao.run();
        } catch (RuntimeException esperado) {
            return;
        }
        throw new IllegalStateException(mensagem);
    }
}
