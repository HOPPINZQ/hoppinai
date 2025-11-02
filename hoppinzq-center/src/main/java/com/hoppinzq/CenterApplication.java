package com.hoppinzq;

import com.hoppinzq.service.annotation.EnableHoppin;
import com.hoppinzq.service.annotation.EnableHoppinCore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableHoppinCore
@EnableHoppin
@SpringBootApplication
public class CenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CenterApplication.class, args);
    }
}