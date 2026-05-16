package com.hst.materialmgmt.rowMapper;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.GrnEntity;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class GrnRowMapper extends BaseRowMapper<GrnEntity> {
    @Override
    public GrnEntity apply(Row row, RowMetadata meta) {
        GrnEntity e = GrnEntity.builder()
                .grnId(row.get("grn_id",          String.class))
                .poId(row.get("po_id",             String.class))
                .supplierCode(row.get("supplier_code", String.class))
                .receivedDate(row.get("received_date", LocalDate.class))
                .invoiceNumber(row.get("invoice_number", String.class))
                .status(row.get("status",          String.class))
                .notes(row.get("notes",            String.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}
