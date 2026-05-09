package com.hst.materialmgmt.rowMapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.PoItemEntity;

@Component
public class PoItemRowMapper extends BaseRowMapper<PoItemEntity> {
    
    @Override
    public PoItemEntity apply(Row row, RowMetadata rowMetadata) {
        PoItemEntity entity = new PoItemEntity();
        
        // Since you extend BaseRowMapper, you can now easily populate audit info!
        // populateAuditInfo(entity, row); 
        
        // Stub: map the rest of your columns here later
        return entity; 
    }
}