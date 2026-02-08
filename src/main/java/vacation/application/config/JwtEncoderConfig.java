package vacation.application.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtEncoderConfig {

    @Bean
    public JwtEncoder jwtEncoder(AppProperties properties) {

        SecretKey secretKey = new SecretKeySpec(
                properties.getSecurity().getJwtSecret().getBytes(),
                "HmacSHA256"
        );

        return NimbusJwtEncoder.withSecretKey(secretKey).build();
    }
}
