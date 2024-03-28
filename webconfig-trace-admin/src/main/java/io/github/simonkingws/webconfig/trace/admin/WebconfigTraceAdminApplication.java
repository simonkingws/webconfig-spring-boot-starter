package io.github.simonkingws.webconfig.trace.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("io.github.simonkingws.webconfig.trace.admin.mapper")
public class WebconfigTraceAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebconfigTraceAdminApplication.class, args);
    }

}
