package com.project.api.validators.phoneorcellphone;

import com.project.api.utils.PhoneNumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class PhoneOrCellphoneValidator implements ConstraintValidator<PhoneOrCellphone, String> {

  @Override
  public void initialize(final PhoneOrCellphone input) {}

  @Override
  public boolean isValid(final String input, final ConstraintValidatorContext cxt) {
    // By definition, we use @NotBlank to validate empty value
    if (StringUtils.isBlank(input)) {
      return true;
    } else {
      return PhoneNumberUtils.isValidCellphoneOrLandline(input);
    }
  }
}
