package org.example.proyectohospital.Controller;
import javafx.application.Platform;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Async {
    // Pool fijo para operaciones de BD / IO
    public static final ExecutorService EXECUTOR =
            new ThreadPoolExecutor(
                    2,                       // core
                    4,                       // max
                    60L, TimeUnit.SECONDS,   // ociosidad
                    new LinkedBlockingQueue<>(),
                    r -> {
                        Thread t = new Thread(r);
                        t.setName("bg-exec-" + t.getId());
                        t.setDaemon(true);
                        return t;
                    }
            );

    private Async() {}

    // Correr una tarea con resultado fuera del hilo de UI
    public static <T> void run(
            Supplier<T> supplier,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError
    ) {
        EXECUTOR.submit(() -> {
            try {
                T result = supplier.get();
                if (onSuccess != null) Platform.runLater(() -> onSuccess.accept(result));
            } catch (Throwable ex) {
                if (onError != null) Platform.runLater(() -> onError.accept(ex));
            }
        });
    }

    // Versión sin resultado
    public static void runVoid(
            Runnable action,
            Runnable onSuccess,
            Consumer<Throwable> onError
    ) {
        EXECUTOR.submit(() -> {
            try {
                action.run();
                if (onSuccess != null) Platform.runLater(onSuccess);
            } catch (Throwable ex) {
                if (onError != null) Platform.runLater(() -> onError.accept(ex));
            }
        });
    }

    // Apagar el pool al salir (opcional: llamalo en stop() de tu Application)
    public static void shutdown() {
        EXECUTOR.shutdown();
    }
}
