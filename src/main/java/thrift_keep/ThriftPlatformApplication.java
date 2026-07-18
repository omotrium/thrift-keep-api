package thrift_keep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ThriftPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThriftPlatformApplication.class, args);
	}

}
