package com.project.api.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PhoneNumberUtilsTest {

  @Test
  void testIsValidCellphoneOrLandline() {
    assertTrue(PhoneNumberUtils.isValidCellphoneOrLandline("1234567890")); // Valid landline
    assertTrue(PhoneNumberUtils.isValidCellphoneOrLandline("12345678901")); // Valid cellphone
    assertFalse(PhoneNumberUtils.isValidCellphoneOrLandline("123456789")); // Invalid phone number
    assertFalse(
        PhoneNumberUtils.isValidCellphoneOrLandline("123456789012")); // Invalid phone number
    assertFalse(PhoneNumberUtils.isValidCellphoneOrLandline("abcdefghijk")); // Invalid phone number
    assertFalse(PhoneNumberUtils.isValidCellphoneOrLandline("")); // Blank phone number
    assertFalse(PhoneNumberUtils.isValidCellphoneOrLandline(null)); // Null phone number
  }

  @Test
  void testIsValidCellphone() {
    assertTrue(PhoneNumberUtils.isValidCellphone("12345678901")); // Valid cellphone
    assertFalse(PhoneNumberUtils.isValidCellphone("1234567890")); // Invalid cellphone (too short)
    assertFalse(PhoneNumberUtils.isValidCellphone("123456789012")); // Invalid cellphone (too long)
    assertFalse(PhoneNumberUtils.isValidCellphone("abcdefghijk")); // Invalid cellphone (letters)
    assertFalse(PhoneNumberUtils.isValidCellphone("")); // Blank phone number
    assertFalse(PhoneNumberUtils.isValidCellphone(null)); // Null phone number
  }

  @Test
  void testIsValidLandline() {
    assertTrue(PhoneNumberUtils.isValidLandline("1234567890")); // Valid landline
    assertFalse(PhoneNumberUtils.isValidLandline("123456789")); // Invalid landline (too short)
    assertFalse(PhoneNumberUtils.isValidLandline("12345678901")); // Invalid landline (too long)
    assertFalse(PhoneNumberUtils.isValidLandline("abcdefghij")); // Invalid landline (letters)
    assertFalse(PhoneNumberUtils.isValidLandline("")); // Blank phone number
    assertFalse(PhoneNumberUtils.isValidLandline(null)); // Null phone number
  }
}
