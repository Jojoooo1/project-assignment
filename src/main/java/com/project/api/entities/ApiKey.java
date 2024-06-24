package com.project.api.entities;

import com.project.api.entities.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = ApiKey.TABLE_NAME, schema = "public")
public class ApiKey extends BaseEntity<Long> {

  public static final String TABLE_NAME = "api_key";
  public static final int KEY_DEFAULT_SIZE = 12;

  @Serial private static final long serialVersionUID = -3552577854495026179L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long companyId;

  @Column(nullable = false)
  private String name;

  @JsonIgnore
  @Column(unique = true, nullable = false)
  private String key;

  @Column(nullable = false)
  private Boolean isActive;

  public static String logName() {
    return TABLE_NAME;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  @Override
  public String toString() {
    return "Apikey{"
        + "id="
        + this.id
        + ", companyId="
        + this.companyId
        + ", name='"
        + this.name
        + ", isActive="
        + this.isActive
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
    if (!(o instanceof final ApiKey other)) {
      return false;
    }
    return this.getId() != null && this.getId().equals(other.getId());
  }

  @Override
  public int hashCode() {
    return this.getClass().hashCode();
  }
}
