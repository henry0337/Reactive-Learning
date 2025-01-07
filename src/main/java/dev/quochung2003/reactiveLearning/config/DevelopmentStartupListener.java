package dev.quochung2003.reactiveLearning.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@Profile({"dev"})
public class DevelopmentStartupListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        final String swaggerUrl = "http://localhost:8080/webjars/swagger-ui/index.html";
        final String os = System.getProperty("os.name").toLowerCase();

        Mono.fromRunnable(() -> {
            ProcessBuilder builder = null;
            try {
                if (os.contains("win")) {
                    builder = new ProcessBuilder("cmd", "/c", "start", swaggerUrl);
                } else if (os.contains("nix") || os.contains("nux")) {
                    builder = new ProcessBuilder("xdg-open", swaggerUrl);
                }

                assert builder != null;
                builder.start();
                log.info("Swagger UI started successfully.");
            } catch (Exception e) {
                log.error("Error opening browser: {}", e.getLocalizedMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic()).subscribe();
    }
}
