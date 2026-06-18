package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.Livro;
import biblioteca.dominio.porta.saida.PortaLivroRepositorio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LivroRepositorioCsv implements PortaLivroRepositorio {
    private final Path arquivo;
    private final Map<Long, Livro> livros = new LinkedHashMap<>();
    private long proximoId = 1L;

    public LivroRepositorioCsv(String caminhoArquivo) {
        this.arquivo = Path.of(caminhoArquivo);
        carregar();
    }

    @Override
    public void salvar(Livro livro) {
        if (livro.getId() == null) {
            livro.setId(proximoId++);
        }
        livros.put(livro.getId(), livro);
        persistir();
    }

    @Override
    public Optional<Livro> buscarPorId(Long id) {
        return Optional.ofNullable(livros.get(id));
    }

    @Override
    public List<Livro> listarTodos() {
        return new ArrayList<>(livros.values());
    }

    @Override
    public void remover(Long id) {
        livros.remove(id);
        persistir();
    }

    private void carregar() {
        if (!Files.exists(arquivo)) {
            return;
        }
        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
            for (String linha : linhas) {
                if (linha.isBlank() || linha.startsWith("id;")) {
                    continue;
                }
                List<String> colunas = parseCsv(linha);
                if (colunas.size() != 5) {
                    throw new IllegalStateException("Linha CSV invalida: " + linha);
                }
                Livro livro = new Livro(
                        Long.parseLong(colunas.get(0)),
                        colunas.get(1),
                        colunas.get(2),
                        colunas.get(3),
                        Integer.parseInt(colunas.get(4))
                );
                livros.put(livro.getId(), livro);
                proximoId = Math.max(proximoId, livro.getId() + 1);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler arquivo CSV de livros.", e);
        }
    }

    private void persistir() {
        List<String> linhas = new ArrayList<>();
        linhas.add("id;titulo;autor;isbn;quantidadeDisponivel");
        livros.values().forEach(livro -> linhas.add(String.join(";",
                livro.getId().toString(),
                csv(livro.getTitulo()),
                csv(livro.getAutor()),
                csv(livro.getIsbn()),
                Integer.toString(livro.getQuantidadeDisponivel())
        )));
        try {
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao gravar arquivo CSV de livros.", e);
        }
    }

    private String csv(String valor) {
        String texto = valor == null ? "" : valor;
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }

    private List<String> parseCsv(String linha) {
        List<String> colunas = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean entreAspas = false;

        for (int i = 0; i < linha.length(); i++) {
            char caractere = linha.charAt(i);
            if (caractere == '"') {
                if (entreAspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                    atual.append('"');
                    i++;
                } else {
                    entreAspas = !entreAspas;
                }
            } else if (caractere == ';' && !entreAspas) {
                colunas.add(atual.toString());
                atual.setLength(0);
            } else {
                atual.append(caractere);
            }
        }
        colunas.add(atual.toString());
        return colunas;
    }
}
