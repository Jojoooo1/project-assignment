package com.project.api.requests.management.company;

import com.project.api.validators.phoneorcellphone.PhoneOrCellphone;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdateCompanyManagementRequest(
    @NotBlank String name,
    String stateTaxId,
    @PhoneOrCellphone String phone,
    @Email String email,
    String addressStreet,
    String addressStreetNumber,
    String addressComplement,
    String addressCityDistrict,
    String addressPostCode,
    String addressCity,
    String addressStateCode,
    String addressCountry,
    @DecimalMin(value = "-90.0000000")
        @DecimalMax(value = "90.0000000")
        @Digits(integer = 2, fraction = 7)
        BigDecimal addressLatitude,
    @DecimalMin(value = "-180.0000000")
        @DecimalMax(value = "180.0000000")
        @Digits(integer = 3, fraction = 7)
        BigDecimal addressLongitude,
    @NotNull Boolean isManagement,
    @NotNull Boolean isInternal,
    @NotNull Boolean isCustomer) {}
