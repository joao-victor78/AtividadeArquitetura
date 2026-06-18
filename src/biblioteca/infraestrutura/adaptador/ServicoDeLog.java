package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.evento.DevolucaoRegistradaEvento;
import biblioteca.dominio.evento.EmprestimoRealizadoEvento;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class ServicoDeLog {
    private final Path arquivoLog;

    public ServicoDeLog(String caminhoArquivo) {
        this.arquivoLog = Path.of(caminhoArquivo);
    }

    public void registrarEmprestimo(EmprestimoRealizadoEvento evento) {
        escrever("Emprestimo realizado: emprestimoId=" + evento.emprestimoId()
                + ", usuarioId=" + evento.usuarioId()
                + ", livroId=" + evento.livroId()
                + ", dataRetirada=" + evento.dataRetirada());
    }

    public void registrarDevolucao(DevolucaoRegistradaEvento evento) {
        escrever("Devolucao registrada: emprestimoId=" + evento.emprestimoId()
                + ", dataDevolucao=" + evento.dataDevolucao()
                + ", comAtraso=" + evento.comAtraso());
    }

    private synchronized void escrever(String mensagem) {
        String linha = LocalDateTime.now() + " - " + mensagem + System.lineSeparator();
        try {
            Files.writeString(
                    arquivoLog,
                    linha,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao escrever log de eventos.", e);
        }
    }
}
