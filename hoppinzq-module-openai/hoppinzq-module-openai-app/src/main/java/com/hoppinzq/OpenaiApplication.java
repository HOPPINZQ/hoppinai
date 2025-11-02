package com.hoppinzq;

import com.hoppinzq.service.annotation.EnableHoppin;
import com.hoppinzq.service.annotation.EnableHoppinGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableHoppinGateway
@EnableHoppin
@EnableAsync
@SpringBootApplication
public class OpenaiApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenaiApplication.class, args);
    }
}
