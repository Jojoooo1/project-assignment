package com.project.api.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CryptoUtilsTest {

  @Test
  void verifySecureRandomLength() {
    final int length = 10;
    Assertions.assertEquals(length * 2, CryptoUtils.createSecureRandom(length).length());
  }

  @Test
  void verifySecureRandomLengthNegative() {
    final int length = -10;
    Assertions.assertEquals(StringUtils.EMPTY, CryptoUtils.createSecureRandom(length));
  }
}
