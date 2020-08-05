package softuni;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SelfproojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SelfproojectApplication.class, args);
    }

}
