package io.ussopm;


import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DdosApplication {

    private static final List<String> TARGETS = Arrays.asList(
            "http://localhost:8080/product/all",
            "http://localhost:8080/product/get/1"
    );

    private static final int THREAD_COUNT = 50;
    private static final int REQUESTS_PER_THREAD = 1000;
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.execute(new DdosThread());
        }
    }


    private static class DdosThread extends Thread{
        WebClient webClient;
        private final Random random = new Random();


        public DdosThread() {
            this.webClient = WebClient.builder().build();
        }

        private String randomTarget() {
            return TARGETS.get(random.nextInt(TARGETS.size()));
        }

        @Override
        public void run() {
            for (int i = 0; i < REQUESTS_PER_THREAD; i++) {
                try {
                    attack();
                    Thread.sleep(randomDelay());
                } catch (Exception e) {
                    // Handle or ignore
                }
            }
        }
        private long randomDelay() {
            return 50 + random.nextInt(200); // Random delay between requests
        }

        public void attack() throws Exception {
            webClient.get()
                    .uri(randomTarget())
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe();
        }

    }
}
