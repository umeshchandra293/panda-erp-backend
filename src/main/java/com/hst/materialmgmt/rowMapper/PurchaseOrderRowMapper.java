package com.hst.materialmgmt.rowMapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.PurchaseOrderEntity;
import java.time.LocalDate;

@Component
public class PurchaseOrderRowMapper extends BaseRowMapper<PurchaseOrderEntity> {
    
    @Override
    public PurchaseOrderEntity apply(Row row, RowMetadata rowMetadata) {
        PurchaseOrderEntity entity = new PurchaseOrderEntity();
        
        entity.setPoId(row.get("po_id", String.class));
        entity.setSupplierCode(row.get("supplier_code", String.class));
        entity.setOrderDate(row.get("order_date", LocalDate.class));
        entity.setExpectedDeliveryDate(row.get("expected_delivery_date", LocalDate.class));
        entity.setNotes(row.get("notes", String.class));
        
        // Safely map the numeric value
        Number total = row.get("total_amount", Number.class);
        if (total != null) {
            entity.setTotalAmount(total.doubleValue());
        }
        
        entity.setStatus(row.get("status", String.class));
        
        // This populates created_at, created_by, etc., from your BaseRowMapper
        populateAuditInfo(entity, row); 
        
        return entity; 
    }
}