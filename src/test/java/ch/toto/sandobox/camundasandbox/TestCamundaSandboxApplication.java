package ch.toto.sandobox.camundasandbox;

import org.springframework.boot.SpringApplication;

public class TestCamundaSandboxApplication {

    public static void main(String[] args) {
        SpringApplication.from(CamundaSandboxApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
