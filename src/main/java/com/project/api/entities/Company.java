package com.project.api.entities;

import static org.apache.commons.lang3.StringUtils.getDigits;

import com.project.api.entities.base.BaseEntity;
import com.project.api.infra.auth.UserRoles;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = Company.TABLE_NAME, schema = "public")
public class Company extends BaseEntity<Long> {

  public static final String TABLE_NAME = "company";

  @Serial private static final long serialVersionUID = 2137607105409362080L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String slug;

  @Column private String name;

  @Column(unique = true)
  private String officialName;

  @Column(nullable = false, unique = true)
  private String federalTaxId;

  @Column(unique = true)
  private String stateTaxId;

  @Column private String phone;
  @Column private String email;

  @Column private String addressStreet;
  @Column private String addressStreetNumber;
  @Column private String addressComplement;
  @Column private String addressCityDistrict;

  @Column private String addressPostCode;
  @Column private String addressCity;
  @Column private String addressStateCode;
  @Column private String addressCountry;

  @Column private BigDecimal addressLatitude;
  @Column private BigDecimal addressLongitude;

  @Column private Boolean isManagement;
  @Column private Boolean isInternal;
  @Column private Boolean isCustomer;

  public Company(final Long id) {
    this.id = id;
  }

  public static String logName() {
    return TABLE_NAME;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  @PrePersist
  @PreUpdate
  private void preSave() {
    this.phone = getDigits(this.phone);
    this.federalTaxId = getDigits(this.federalTaxId);
    this.stateTaxId = getDigits(this.stateTaxId);
    this.addressPostCode = getDigits(this.addressPostCode);
  }

  @Override
  public String toString() {
    return "Company{"
        + "id="
        + this.id
        + ", slug='"
        + this.slug
        + "', name='"
        + this.name
        + "', officialName='"
        + this.officialName
        + "', federalTaxId='"
        + this.federalTaxId
        + "', stateTaxId='"
        + this.stateTaxId
        + "', phone='"
        + this.phone
        + "', email='"
        + this.email
        + "', addressStreet='"
        + this.addressStreet
        + "', addressStreetNumber='"
        + this.addressStreetNumber
        + "', addressComplement='"
        + this.addressComplement
        + "', addressCityDistrict='"
        + this.addressCityDistrict
        + "', addressPostCode='"
        + this.addressPostCode
        + "', addressCity='"
        + this.addressCity
        + "', addressStateCode='"
        + this.addressStateCode
        + "', addressCountry='"
        + this.addressCountry
        + "', addressLatitude="
        + this.addressLatitude
        + ", addressLongitude="
        + this.addressLongitude
        + ", isCustomer="
        + this.isCustomer
        + ", isManagement="
        + this.isManagement
        + ", isInternal="
        + this.isInternal
        + ", createdBy='"
        + this.getCreatedBy()
        + "', updatedBy='"
        + this.getUpdatedBy()
        + "', createdAt="
        + this.getCreatedAt()
        + ", updatedAt="
        + this.getUpdatedAt()
        + '}';
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof final Company other)) {
      return false;
    }
    return this.getId() != null && this.getId().equals(other.getId());
  }

  @Override
  public int hashCode() {
    return this.getClass().hashCode();
  }

  public boolean is(final String slug) {
    return StringUtils.isNotBlank(this.slug) && this.slug.equals(slug);
  }

  public Collection<GrantedAuthority> getGrantedAuthoritiesFromCompanyType() {
    return this.getApiRolesFromCompanyType().stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
        .collect(Collectors.toSet());
  }

  private List<UserRoles> getApiRolesFromCompanyType() {
    final List<UserRoles> roles = new ArrayList<>();

    if (Boolean.TRUE.equals(this.isInternal)) {
      roles.add(UserRoles.INTERNAL_API_USER);
    }
    if (Boolean.TRUE.equals(this.isManagement)) {
      roles.add(UserRoles.MANAGEMENT_API_USER);
    }
    if (Boolean.TRUE.equals(this.isCustomer)) {
      roles.add(UserRoles.CUSTOMER_API_USER);
    }

    return roles;
  }
}
