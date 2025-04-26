package com.did.docdiffserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.did.docdiffserver.**"})
public class DocDiffServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocDiffServerApplication.class, args);
    }

}
