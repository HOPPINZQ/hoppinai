package com.hoppinzq;

import com.hoppinzq.service.annotation.EnableHoppin;
import com.hoppinzq.service.annotation.EnableHoppinGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableHoppinGateway
@EnableHoppin
@SpringBootApplication
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}