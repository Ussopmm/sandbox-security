package io.ussopm;


import org.springframework.web.reactive.function.client.WebClient;

public class DdosApplication {

    public static void main(String[] args) {
        WebClient webClient = WebClient.builder().build();
        while (true) {
            webClient.get()
                    .uri("http://localhost:8080/product/all")
                    .retrieve()
                    .bodyToMono(ProductDTO[].class)
                    .block();
        }
    }


}
