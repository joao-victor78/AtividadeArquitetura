package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.evento.EmprestimoRealizadoEvento;

public class ServicoDeNotificacao {
    public void notificarEmprestimoRealizado(EmprestimoRealizadoEvento evento) {
        System.out.println("[EVENTO][NOTIFICACAO] Emprestimo " + evento.emprestimoId()
                + " realizado para usuario " + evento.usuarioId()
                + ". Devolucao prevista: " + evento.dataPrevistaDevolucao());
    }
}
