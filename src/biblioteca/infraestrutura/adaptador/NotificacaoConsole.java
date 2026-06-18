package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.Usuario;
import biblioteca.dominio.porta.saida.PortaNotificacao;

public class NotificacaoConsole implements PortaNotificacao {
    @Override
    public void notificarAtraso(Usuario usuario, Emprestimo emprestimo) {
        System.out.println("[NOTIFICACAO] Usuario " + usuario.getNome()
                + " possui emprestimo atrasado: " + emprestimo.getId());
    }
}
