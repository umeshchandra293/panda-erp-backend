package com.hst.materialmgmt.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PoItemEntity extends BaseEntity {
    private Long itemId;
    private String poId;
    private String materialId;
    private Double quantity;
    private Double unitPrice;
    private Double lineTotal;

    // Required by BaseEntity
    @Override
    public String getId() {
        return this.itemId != null ? String.valueOf(this.itemId) : null;
    }
}
