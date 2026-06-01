package com.hst.materialmgmt.entity.fgstock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.hst.materialmgmt.entity.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @ToString @SuperBuilder
@Table(value = "fg_dispatch_tbl", schema = "rm_material_schema")
public class FgDispatchEntity extends BaseEntity {

    @Id @Column("dispatch_id")          private String     dispatchId;
    @Column("dispatch_date")            private LocalDate  dispatchDate;
    @Column("driver_name")              private String     driverName;
    @Column("driver_phone")             private String     driverPhone;
    @Column("vehicle_number")           private String     vehicleNumber;
    @Column("delivery_order")           private String     deliveryOrder;
    @Column("destination")              private String     destination;
    @Column("status")                   private String     status;
    @Column("amount_to_collect")        private BigDecimal amountToCollect;
    @Column("amount_collected")         private BigDecimal amountCollected;
    @Column("payment_mode")             private String     paymentMode;
    @Column("notes")                    private String     notes;
    @Column("settled_at")               private LocalDateTime settledAt;

    @Override public String getId() { return dispatchId; }
}