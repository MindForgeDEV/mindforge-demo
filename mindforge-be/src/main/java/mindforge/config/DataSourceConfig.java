package mindforge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

@Configuration
public class DataSourceConfig {

  @Value("${SPRING_DATASOURCE_URL:}")
  private String dbUrl;

  @Value("${POSTGRES_USER:}")
  private String dbUser;

  @Value("${POSTGRES_PASSWORD:}")
  private String dbPassword;

  @Bean
  @Profile({ "dev", "prod" })
  public DataSource postgresDataSource() {
    return DataSourceBuilder.create()
        .url(dbUrl)
        .username(dbUser)
        .password(dbPassword)
        .build();
  }

  @Bean
  @Profile("test")
  public DataSource h2DataSource() {
    return DataSourceBuilder.create()
        .url("jdbc:h2:mem:mindforge;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
        .driverClassName("org.h2.Driver")
        .username("sa")
        .build();
  }
}
