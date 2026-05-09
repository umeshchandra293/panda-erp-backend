package com.hst.materialmgmt.entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
@SuperBuilder
@Table(value = "rm_supplier_tbl", schema = "erp_finance_schema")
public class SupplierEntity extends BaseEntity {

  @Column("supplier_code")
  private String supplierCode;

  @Column("supplier_name")
  private String supplierName;

  @Column("supplier_category")
  private String supplierCategory;

  @Column("supplier_group")
  private String supplierGroup;

  @Column("legal_entity_id")
  private String legalEntity;

  @Column("contact_person_name")
  private String contactPersonName;

  @Column("gst_number")
  private String gstNumber;

  @Column("gst_registration_type")
  private String gstRegistrationType;

  @Column("pan_number")
  private String panNumber;

  @Column("state_code")
  private String stateCode;

  @Column("country_code")
  private String countryCode;

  @Column("lead_time_days")
  private Integer leadTimeDays;

  @Column("payment_term")
  private String paymentTerm;

  @Column("effective_date")
  private LocalDate effectiveDate;

  @Column("end_date")
  private LocalDate endDate;

  @Column("is_active")
  private Boolean isActive;

  @Override
  public String getId() {
    return supplierCode;
  }

  @Transient private List<BaseEntity> addressEntities;

  @Transient private List<BaseEntity> phoneEntities;

  @Transient private List<BaseEntity> emailEntities;
}