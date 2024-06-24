package com.project.api.utils;

import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class PhoneNumberUtils {

  public static boolean isValidCellphoneOrLandline(final String phoneNumber) {
    return isValidLandline(phoneNumber) || isValidCellphone(phoneNumber);
  }

  public static boolean isValidCellphone(final String phoneNumber) {

    if (StringUtils.isBlank(phoneNumber)) {
      return false;
    }

    final var pattern = Pattern.compile("\\d{11}");
    final var matcher = pattern.matcher(phoneNumber);
    return matcher.matches();
  }

  public static boolean isValidLandline(final String phoneNumber) {

    if (StringUtils.isBlank(phoneNumber)) {
      return false;
    }

    final var pattern = Pattern.compile("\\d{10}");
    final var matcher = pattern.matcher(phoneNumber);
    return matcher.matches();
  }
}
