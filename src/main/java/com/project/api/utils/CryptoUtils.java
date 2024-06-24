package com.project.api.utils;

import java.security.SecureRandom;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class CryptoUtils {

  public static String createSecureRandom(final int length) {
    if (length < 0) {
      return StringUtils.EMPTY;
    }

    final byte[] apiKey = new byte[length];
    final SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(apiKey);

    final StringBuilder sb = new StringBuilder();
    for (final byte b : apiKey) {
      sb.append(String.format("%02x", b));
    }

    return sb.toString();
  }
}
