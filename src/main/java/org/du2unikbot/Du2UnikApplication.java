package org.du2unikbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Du2UnikApplication {
    public static void main(String[] args){
        SpringApplication.run(Du2UnikApplication.class, args);
    }
}