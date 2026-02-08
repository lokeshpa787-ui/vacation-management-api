package vacation.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private Security security = new Security();
    private Staffing staffing = new Staffing();

    @Getter
    @Setter
    public static class Security {
        private String jwtSecret;
    }

    @Getter
    @Setter
    public static class Staffing {
        private int minEmployeesOnSite = 2;
    }
}
