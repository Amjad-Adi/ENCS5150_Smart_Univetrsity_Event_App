package com.example.encs5150_project;

import org.junit.Test;
import com.example.encs5150_project.model.PasswordHashingAlgorithm;

public class GenerateHashTest {

    @Test
    public void generateAdminHash() {
        try {
            PasswordHashingAlgorithm hasher = new PasswordHashingAlgorithm();
            String hash = hasher.hashPassword("Admin123!");

            System.out.println("\n\n===========================================");
            System.out.println("COPY THE STRING BELOW:");
            System.out.println(hash);
            System.out.println("===========================================\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}