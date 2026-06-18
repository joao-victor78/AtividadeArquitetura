# Sistema de Gerenciamento de Biblioteca

Projeto Java puro para a atividade prática de Arquiteturas de Software com Java.

## Requisitos

- Java 17 ou superior
- Nenhum framework externo

## Como Compilar

```bash
javac -d out $(find src -name "*.java")
```

## Como Executar

```bash
java -cp out biblioteca.apresentacao.Main
```

Também é possível executar a classe `Main` na raiz de `src`, que apenas delega para `biblioteca.apresentacao.Main`.

## Verificação Manual

Além da demonstração principal, o projeto possui uma classe simples de verificação sem frameworks externos:

```bash
java -cp out biblioteca.apresentacao.VerificacaoManual
```

Ela valida decremento de estoque, empréstimo ativo, devolução, recomposição de estoque e bloqueio de usuário suspenso.

## Estrutura

- `biblioteca.dominio`: entidades, enums, portas, eventos e serviço principal de empréstimo.
- `biblioteca.aplicacao`: serviços simples de cadastro de livro e usuário.
- `biblioteca.infraestrutura.adaptador`: repositórios em memória, repositório CSV, notificação por console e handlers de eventos.
- `biblioteca.apresentacao`: composição da aplicação e demonstração no console.

## Decisões de Design

- As entidades `Livro`, `Usuario` e `Emprestimo` são POJOs sem dependência de frameworks.
- O domínio não importa classes de `infraestrutura` nem de `apresentacao`.
- Os repositórios são interfaces no domínio, atuando como portas de saída.
- `EmprestimoServico` implementa a porta de entrada `PortaEmprestimo` e depende apenas das portas e do `EventBus`.
- A troca entre `LivroRepositorioMemoria` e `LivroRepositorioCsv` é feita apenas na composição do `Main`, sem alterar a lógica de negócio.
- O `EventBus<T>` usa generics e executa handlers em threads separadas para desacoplar efeitos colaterais do fluxo principal.
- `ServicoDeNotificacao` e `ServicoDeLog` são consumidores independentes; `EmprestimoServico` não importa nenhum deles.

## Arquivos Gerados em Execução

- `livros.csv`: persistência do adaptador CSV de livros.
- `biblioteca.log`: log de eventos com timestamp.

## Dificuldades e Soluções

- Para manter o domínio isolado, todos os detalhes de persistência foram empurrados para adaptadores que implementam portas.
- Para demonstrar comunicação por eventos sem framework externo, foi criado um `EventBus` genérico com `Consumer<T>` e `ExecutorService`.
- Para o CSV, a serialização trata campos com aspas usando escape padrão de CSV.

## Checklist de Entrega

- Compilar o projeto com Java 17+.
- Executar `biblioteca.apresentacao.Main`.
- Executar `biblioteca.apresentacao.VerificacaoManual`.
- Conferir que `livros.csv` e `biblioteca.log` são gerados em tempo de execução.
- Publicar o repositório com histórico de commits organizado.
