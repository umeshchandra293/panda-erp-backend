package com.hst.materialmgmt.entity;

import org.springframework.data.annotation.Id;
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
@Table(value = "rm_address_tbl", schema = "rm_material_schema")
public class AddressEntity extends BaseEntity {

    @Id
    @Column("address_id")
    private String addressId;

    @Column("address_type")
    private String addressType;

    @Column("address_line_1")
    private String addressLine1;

    @Column("address_line_2")
    private String addressLine2;

    @Column("po_box_number")
    private String poBoxNumber;

    @Column("city")
    private String city;

    @Column("state_cd")
    private String stateCode;

    @Column("postal_cd")
    private String postalCode;

    @Column("country_cd")
    private String countryCode;

    @Column("time_zone")
    private String timeZone;

    @Column("is_primary")
    private Boolean isPrimary;

    @Override
    public String getId() {
        return addressId;
    }
}
