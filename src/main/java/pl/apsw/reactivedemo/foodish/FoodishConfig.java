package pl.apsw.reactivedemo.foodish;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
@ConfigurationProperties("foodish")
@Data
public class FoodishConfig {
    private String url;

    @Bean
    public RouterFunction<ServerResponse> route(ClientHandler clientHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/hello/{id}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), clientHandler::burger);
    }
}
