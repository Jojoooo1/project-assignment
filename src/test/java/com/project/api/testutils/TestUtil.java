package com.project.api.testutils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class TestUtil {

  public static String random(final Integer... args) {
    return RandomStringUtils.randomAlphabetic(args.length == 0 ? 10 : args[0]);
  }

  public static String randomNumeric(final Integer... args) {
    return RandomStringUtils.randomNumeric(args.length == 0 ? 10 : args[0]);
  }
}
