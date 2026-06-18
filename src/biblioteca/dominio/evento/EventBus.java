package biblioteca.dominio.evento;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class EventBus<T> implements AutoCloseable {
    private final List<Consumer<T>> assinantes = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public void assinar(Consumer<T> handler) {
        assinantes.add(handler);
    }

    public void publicar(T evento) {
        assinantes.forEach(handler -> executor.submit(() -> handler.accept(evento)));
    }

    public void aguardarProcessamento() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Tempo esgotado aguardando processamento dos eventos.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Processamento de eventos interrompido.", e);
        }
    }

    @Override
    public void close() {
        if (!executor.isShutdown()) {
            executor.shutdownNow();
        }
    }
}
