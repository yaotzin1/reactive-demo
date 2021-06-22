package pl.apsw.reactivedemo.foodish;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class ClientHandler {

    private final FoodishConfig foodishConfig;

    public Mono<ServerResponse> burger(ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");
        final String resourceUrl = "/images/burger/burger"+id+".jpg";
        WebClient foodishWebClient = WebClient.create(foodishConfig.getUrl());
            Mono<ServerResponse> responseMono = foodishWebClient.get()
                .uri(resourceUrl)
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.statusCode().isError()){
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(ByteArrayResource.class);
                })
                .flatMap(byteArrayResource -> {
                   final String b64Image = Base64.getEncoder().encodeToString(byteArrayResource.getByteArray());
                   Map<String, String> response = new HashMap<>();
                   response.put("originalUrl", resourceUrl);
                   response.put("image", b64Image);
                 return ServerResponse.ok().bodyValue(response);
           }).switchIfEmpty(ServerResponse.badRequest().bodyValue(new HashMap<String, String>() {{
               put("error", "Bad Request");
               put("code", HttpStatus.BAD_REQUEST.toString());
           }}));

        return responseMono;
    }
}
