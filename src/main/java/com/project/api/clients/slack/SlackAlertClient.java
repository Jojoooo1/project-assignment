package com.project.api.clients.slack;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
@Profile("!test")
public class SlackAlertClient extends BaseSlackClient {

  private final String url;
  private final String channel;

  public SlackAlertClient(
      @Value("${client.slack.api-alert.webhook}") final String url,
      @Value("${client.slack.api-alert.name}") final String channel) {
    this.url = url;
    this.channel = channel;
  }
}
