package biblioteca.apresentacao;

import biblioteca.aplicacao.LivroServico;
import biblioteca.aplicacao.UsuarioServico;
import biblioteca.dominio.Emprestimo;
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
import biblioteca.infraestrutura.adaptador.LivroRepositorioCsv;
import biblioteca.infraestrutura.adaptador.LivroRepositorioMemoria;
import biblioteca.infraestrutura.adaptador.NotificacaoConsole;
import biblioteca.infraestrutura.adaptador.ServicoDeLog;
import biblioteca.infraestrutura.adaptador.ServicoDeNotificacao;
import biblioteca.infraestrutura.adaptador.UsuarioRepositorioMemoria;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Sistema de Gerenciamento de Biblioteca ===");
        executarFluxo("Adaptador em memoria", new LivroRepositorioMemoria());
        executarFluxo("Adaptador CSV para livros", new LivroRepositorioCsv("livros.csv"));
    }

    private static void executarFluxo(String nomeFluxo, PortaLivroRepositorio livroRepositorio) {
        System.out.println();
        System.out.println("--- " + nomeFluxo + " ---");

        PortaUsuarioRepositorio usuarioRepositorio = new UsuarioRepositorioMemoria();
        PortaEmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorioMemoria();
        PortaNotificacao notificacao = new NotificacaoConsole();

        EventBus<EmprestimoRealizadoEvento> eventosEmprestimo = new EventBus<>();
        EventBus<DevolucaoRegistradaEvento> eventosDevolucao = new EventBus<>();

        ServicoDeNotificacao servicoDeNotificacao = new ServicoDeNotificacao();
        ServicoDeLog servicoDeLog = new ServicoDeLog("biblioteca.log");

        eventosEmprestimo.assinar(servicoDeNotificacao::notificarEmprestimoRealizado);
        eventosEmprestimo.assinar(servicoDeLog::registrarEmprestimo);
        eventosDevolucao.assinar(servicoDeLog::registrarDevolucao);

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

        var livro = livroServico.cadastrar("Arquitetura Limpa", "Robert C. Martin", "978-8550804606", 2);
        var usuario = usuarioServico.cadastrar("Maria Silva", "maria.silva@email.com");

        System.out.println("Livro cadastrado: " + livro);
        System.out.println("Usuario cadastrado: " + usuario);

        Emprestimo emprestimo = emprestimoServico.realizarEmprestimo(usuario.getId(), livro.getId());
        System.out.println("Emprestimo realizado: " + emprestimo);
        System.out.println("Emprestimos ativos: " + emprestimoServico.listarEmprestimosAtivos().size());

        emprestimoServico.registrarDevolucao(emprestimo.getId());
        System.out.println("Devolucao registrada para emprestimo: " + emprestimo.getId());
        System.out.println("Emprestimos ativos apos devolucao: " + emprestimoServico.listarEmprestimosAtivos().size());

        eventosEmprestimo.aguardarProcessamento();
        eventosDevolucao.aguardarProcessamento();
        System.out.println("Eventos processados. Verifique biblioteca.log para os registros com timestamp.");
    }
}
