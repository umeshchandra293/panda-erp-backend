package com.hst.materialmgmt.rowMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.SupplierMaterialMapEntity;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class SupplierMaterialMapRowMapper extends BaseRowMapper<SupplierMaterialMapEntity> {

    @Override
    public SupplierMaterialMapEntity apply(Row row, RowMetadata rowMetadata) {
        SupplierMaterialMapEntity entity = SupplierMaterialMapEntity.builder()
                .mappingId(row.get("mapping_id", String.class))
                .supplierCode(row.get("supplier_code", String.class))
                .materialId(row.get("material_id", String.class))
                .agreedPrice(row.get("agreed_price", BigDecimal.class))
                .uom(row.get("uom", String.class))
                .packSize(row.get("pack_size", BigDecimal.class))
                .packUom(row.get("pack_uom", String.class))
                .minOrderQty(row.get("min_order_qty", BigDecimal.class))
                .hsnSacCode(row.get("hsn_sac_code", String.class))
                .gstRate(row.get("gst_rate", BigDecimal.class))
                .leadTimeDays(row.get("lead_time_days", Integer.class))
                .currencyCode(row.get("currency_code", String.class))
                .effectiveDate(row.get("effective_date", LocalDate.class))
                .expiryDate(row.get("expiry_date", LocalDate.class))
                .isActive(row.get("is_active", Boolean.class))
                .build();

        populateAuditInfo(entity, row);
        return entity;
    }
}
