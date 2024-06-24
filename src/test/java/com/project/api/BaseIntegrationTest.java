package com.project.api;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE;
import static org.testcontainers.utility.DockerImageName.parse;

import com.project.api.testutils.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.redis.testcontainers.RedisContainer;
import java.util.Map;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.lifecycle.Startables;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

  private static final ObjectMapper defaultObjectMapper =
      new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .setPropertyNamingStrategy(LOWER_CAMEL_CASE)
          .registerModule(new JavaTimeModule());

  @Container @ServiceConnection
  public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @Container
  static RedisContainer redis =
      new RedisContainer(parse("bitnami/valkey:7.2"))
          .withEnv(Map.of("VALKEY_PASSWORD", "password"));

  static {
    Startables.deepStart(postgres, redis).join();
  }

  @Autowired public MockMvc mockMvc;

  @DynamicPropertySource
  static void applicationProperties(final DynamicPropertyRegistry registry) {
    registry.add("redis.port", () -> redis.getMappedPort(6379));
  }

  public static String random(final Integer... args) {
    return TestUtil.random(args);
  }

  public static String randomNumeric(final Integer... args) {
    return TestUtil.randomNumeric(args);
  }

  protected static String toJSON(final Object object) throws JsonProcessingException {
    return defaultObjectMapper.writeValueAsString(object);
  }
}
