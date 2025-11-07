package mindforge.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "mindforge")
public class MindforgeApplication {

  public static void main(String[] args) {
    SpringApplication.run(MindforgeApplication.class, args);
  }
}
