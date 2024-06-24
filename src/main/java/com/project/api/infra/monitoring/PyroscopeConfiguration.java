package com.project.api.infra.monitoring;

import io.pyroscope.http.Format;
import io.pyroscope.javaagent.EventType;
import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "profiling", name = "pyroscope.enabled", havingValue = "true")
public class PyroscopeConfiguration {

  public PyroscopeConfiguration(@Value("${pyroscope.server}") final String pyroscopeServer) {
    PyroscopeAgent.start(
        new Config.Builder()
            .setApplicationName("api")
            .setLabels(Map.of("team", "api", "type", "api"))
            .setProfilingEvent(EventType.ITIMER)
            .setProfilingAlloc("512k")
            // .setAllocLive(true)
            .setFormat(Format.JFR)
            .setServerAddress(pyroscopeServer)
            .build());
  }
}
