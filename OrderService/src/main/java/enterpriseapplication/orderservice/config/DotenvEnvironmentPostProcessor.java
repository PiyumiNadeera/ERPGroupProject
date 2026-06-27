package enterpriseapplication.orderservice.config;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads variables from a local .env file (if present) into the Spring Environment,
 * without overriding variables already set in the OS environment. Keeps DB
 * credentials out of application.properties and out of source control.
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String DOTENV_FILE = ".env";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path dotenvPath = Path.of(DOTENV_FILE);
        if (!Files.exists(dotenvPath)) {
            return;
        }

        Map<String, Object> values = new LinkedHashMap<>();
        try {
            for (String line : Files.readAllLines(dotenvPath)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
                    continue;
                }
                int separatorIndex = trimmed.indexOf('=');
                String key = trimmed.substring(0, separatorIndex).trim();
                String value = trimmed.substring(separatorIndex + 1).trim();
                if (environment.getSystemEnvironment().containsKey(key)) {
                    continue;
                }
                values.put(key, value);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + DOTENV_FILE, e);
        }

        if (!values.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource("dotenv", values));
        }
    }
}
