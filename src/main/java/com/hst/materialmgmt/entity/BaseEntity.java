package com.hst.materialmgmt.entity;

import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
public abstract class BaseEntity {

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("created_by")
  private String createdBy;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  @Column("updated_by")
  private String updatedBy;

  public void setCreateDefaults() {
    this.createdAt = LocalDateTime.now();

    if (this.createdBy == null || this.createdBy.isEmpty())
      this.createdBy = "Admin"; // In real scenarios, fetch the actual user
  }

  public void setUpdateDefaults() {
    this.updatedAt = LocalDateTime.now();

    if (this.updatedBy == null || this.updatedBy.isEmpty())
      this.updatedBy = "Admin"; // In real scenarios, fetch the actual user
  }

  public abstract String getId();
}
