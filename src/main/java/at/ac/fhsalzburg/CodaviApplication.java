package at.ac.fhsalzburg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class CodaviApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodaviApplication.class, args);
	}
}
