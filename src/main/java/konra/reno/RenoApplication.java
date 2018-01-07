package konra.reno;

import konra.reno.p2p.P2PConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(P2PConfig.class)
public class RenoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RenoApplication.class, args);
	}
}
