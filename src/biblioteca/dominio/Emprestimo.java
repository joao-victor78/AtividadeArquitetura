package biblioteca.dominio;

import java.time.LocalDate;

public class Emprestimo {
    private Long id;
    private Livro livro;
    private Usuario usuario;
    private LocalDate dataRetirada;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private SituacaoEmprestimo situacao;

    public Emprestimo(Long id, Livro livro, Usuario usuario, LocalDate dataRetirada, LocalDate dataPrevistaDevolucao, SituacaoEmprestimo situacao) {
        this.id = id;
        this.livro = livro;
        this.usuario = usuario;
        this.dataRetirada = dataRetirada;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.situacao = situacao;
    }

    public boolean estaAtivo() {
        return SituacaoEmprestimo.ATIVO.equals(situacao) || SituacaoEmprestimo.ATRASADO.equals(situacao);
    }

    public boolean estaAtrasado(LocalDate dataReferencia) {
        return estaAtivo() && dataPrevistaDevolucao.isBefore(dataReferencia);
    }

    public void marcarComoAtrasado() {
        if (SituacaoEmprestimo.ATIVO.equals(situacao)) {
            situacao = SituacaoEmprestimo.ATRASADO;
        }
    }

    public boolean registrarDevolucao(LocalDate dataDevolucao) {
        if (!estaAtivo()) {
            throw new IllegalStateException("Emprestimo ja devolvido.");
        }
        boolean comAtraso = dataPrevistaDevolucao.isBefore(dataDevolucao);
        this.dataDevolucao = dataDevolucao;
        this.situacao = SituacaoEmprestimo.DEVOLVIDO;
        return comAtraso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(LocalDate dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public void setDataPrevistaDevolucao(LocalDate dataPrevistaDevolucao) {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public SituacaoEmprestimo getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEmprestimo situacao) {
        this.situacao = situacao;
    }

    @Override
    public String toString() {
        return "Emprestimo{" +
                "id=" + id +
                ", livro=" + livro.getTitulo() +
                ", usuario=" + usuario.getNome() +
                ", dataRetirada=" + dataRetirada +
                ", dataPrevistaDevolucao=" + dataPrevistaDevolucao +
                ", dataDevolucao=" + dataDevolucao +
                ", situacao=" + situacao +
                '}';
    }
}
