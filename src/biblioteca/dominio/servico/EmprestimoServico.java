package biblioteca.dominio.servico;

import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.Livro;
import biblioteca.dominio.SituacaoEmprestimo;
import biblioteca.dominio.Usuario;
import biblioteca.dominio.evento.DevolucaoRegistradaEvento;
import biblioteca.dominio.evento.EmprestimoRealizadoEvento;
import biblioteca.dominio.evento.EventBus;
import biblioteca.dominio.porta.entrada.PortaEmprestimo;
import biblioteca.dominio.porta.saida.PortaEmprestimoRepositorio;
import biblioteca.dominio.porta.saida.PortaLivroRepositorio;
import biblioteca.dominio.porta.saida.PortaNotificacao;
import biblioteca.dominio.porta.saida.PortaUsuarioRepositorio;

import java.time.LocalDate;
import java.util.List;

public class EmprestimoServico implements PortaEmprestimo {
    private static final int DIAS_PARA_DEVOLUCAO = 7;

    private final PortaLivroRepositorio livroRepositorio;
    private final PortaUsuarioRepositorio usuarioRepositorio;
    private final PortaEmprestimoRepositorio emprestimoRepositorio;
    private final PortaNotificacao notificacao;
    private final EventBus<EmprestimoRealizadoEvento> eventosBusEmprestimo;
    private final EventBus<DevolucaoRegistradaEvento> eventosBusDevolucao;

    public EmprestimoServico(
            PortaLivroRepositorio livroRepositorio,
            PortaUsuarioRepositorio usuarioRepositorio,
            PortaEmprestimoRepositorio emprestimoRepositorio,
            PortaNotificacao notificacao,
            EventBus<EmprestimoRealizadoEvento> eventosBusEmprestimo,
            EventBus<DevolucaoRegistradaEvento> eventosBusDevolucao
    ) {
        this.livroRepositorio = livroRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.emprestimoRepositorio = emprestimoRepositorio;
        this.notificacao = notificacao;
        this.eventosBusEmprestimo = eventosBusEmprestimo;
        this.eventosBusDevolucao = eventosBusDevolucao;
    }

    @Override
    public Emprestimo realizarEmprestimo(Long usuarioId, Long livroId) {
        Usuario usuario = usuarioRepositorio.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado: " + usuarioId));
        Livro livro = livroRepositorio.buscarPorId(livroId)
                .orElseThrow(() -> new IllegalArgumentException("Livro nao encontrado: " + livroId));

        if (!usuario.estaAtivo()) {
            throw new IllegalStateException("Usuario suspenso nao pode realizar emprestimo.");
        }

        LocalDate hoje = LocalDate.now();
        livro.realizarEmprestimo();
        Emprestimo emprestimo = new Emprestimo(
                null,
                livro,
                usuario,
                hoje,
                hoje.plusDays(DIAS_PARA_DEVOLUCAO),
                SituacaoEmprestimo.ATIVO
        );

        livroRepositorio.salvar(livro);
        emprestimoRepositorio.salvar(emprestimo);
        eventosBusEmprestimo.publicar(new EmprestimoRealizadoEvento(
                emprestimo.getId(),
                usuario.getId(),
                livro.getId(),
                emprestimo.getDataRetirada(),
                emprestimo.getDataPrevistaDevolucao()
        ));
        return emprestimo;
    }

    @Override
    public void registrarDevolucao(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepositorio.buscarPorId(emprestimoId)
                .orElseThrow(() -> new IllegalArgumentException("Emprestimo nao encontrado: " + emprestimoId));

        LocalDate dataDevolucao = LocalDate.now();
        boolean comAtraso = emprestimo.registrarDevolucao(dataDevolucao);
        emprestimo.getLivro().registrarDevolucao();

        livroRepositorio.salvar(emprestimo.getLivro());
        emprestimoRepositorio.salvar(emprestimo);
        eventosBusDevolucao.publicar(new DevolucaoRegistradaEvento(emprestimo.getId(), dataDevolucao, comAtraso));
    }

    @Override
    public List<Emprestimo> listarEmprestimosAtivos() {
        return emprestimoRepositorio.listarTodos().stream()
                .filter(Emprestimo::estaAtivo)
                .toList();
    }

    @Override
    public List<Emprestimo> verificarAtrasos() {
        LocalDate hoje = LocalDate.now();
        List<Emprestimo> atrasados = emprestimoRepositorio.listarTodos().stream()
                .filter(emprestimo -> emprestimo.estaAtrasado(hoje))
                .toList();

        atrasados.forEach(emprestimo -> {
            emprestimo.marcarComoAtrasado();
            emprestimoRepositorio.salvar(emprestimo);
            notificacao.notificarAtraso(emprestimo.getUsuario(), emprestimo);
        });

        return atrasados;
    }
}
