package dev.quochung2003.reactiveLearning.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PostExchange;

import static dev.quochung2003.reactiveLearning.constant.Endpoint.SHUTDOWN_ROUTE;

@RestController
@Tag(name = "Shutdown URL", description = "Used to safely terminate the application.")
public class ShutdownController {

    /**
     * Shutdown the application in the safely way.
     * @apiNote This route <b>always</b> implicitly returns {@code 200 OK} as status code, even you may get error when entering this route.
     */
    @PostExchange(SHUTDOWN_ROUTE)
    public final void shutdown() {
        System.exit(0);
    }
}
