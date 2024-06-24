package com.project.api.utils;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslClosedEngineException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.PrematureCloseException;
import reactor.util.retry.Retry;

@Slf4j
@UtilityClass
public class WebClientUtils {

  /*
   * Error handler to improve network reliability.
   * */
  private ExchangeFilterFunction retryOnNetworkInstability() {
    return (request, next) ->
        next.exchange(request)
            .retryWhen(
                Retry.backoff(3, Duration.ofMillis(100))
                    .filter(
                        ex -> {
                          if (ExceptionUtils.getRootCause(ex) instanceof PrematureCloseException) {
                            log.info("HTTP[RETRY] PrematureCloseException detected retrying");
                            return true;
                          } else if (ExceptionUtils.getRootCause(ex)
                              instanceof SslClosedEngineException) {
                            log.info("HTTP[RETRY] SslClosedEngineException detected retrying");
                            return true;
                          }
                          return false;
                        }));
  }

  public static WebClient createWebClient(
      final Builder builder,
      final String baseUrl,
      final int timeOutInMs,
      final String bearerToken) {

    final Builder webClientBuilder =
        builder
            .clientConnector(
                new ReactorClientHttpConnector(createHttpClientWithProvider(timeOutInMs)))
            .baseUrl(baseUrl)
            .defaultHeaders(
                headers -> {
                  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                  headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                  headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
                })
            .filter(retryOnNetworkInstability());
    return webClientBuilder.build();
  }

  public static HttpClient createHttpClientWithProvider(final int timeOutInMs) {
    return HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOutInMs)
        .responseTimeout(Duration.ofMillis(timeOutInMs))
        .doOnConnected(
            conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(timeOutInMs, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(timeOutInMs, TimeUnit.MILLISECONDS)));
  }
}
