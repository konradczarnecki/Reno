package konra.reno;

import konra.reno.core.CoreConfig;
import konra.reno.crypto.Crypto;
import konra.reno.crypto.CryptoConfig;
import konra.reno.miner.txpicker.PickerStrategy;
import konra.reno.miner.txpicker.TxPickerConfig;
import konra.reno.p2p.P2PConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableConfigurationProperties({P2PConfig.class, CoreConfig.class, CryptoConfig.class, TxPickerConfig.class})
public class RenoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RenoApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("http://localhost:4200").allowedMethods("GET", "POST");
			}
		};
	}
}
