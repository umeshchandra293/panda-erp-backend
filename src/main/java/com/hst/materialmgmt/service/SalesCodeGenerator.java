package com.hst.materialmgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SalesCodeGenerator {

    @Autowired private DatabaseClient db;

    private Mono<String> next(String seq, String fmt) {
        return db.sql("SELECT nextval('erp_finance_schema." + seq + "')")
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format(fmt, n));
    }

    public Mono<String> nextRetailerId()  { return next("sales_retailer_seq",   "RET-%06d"); }
    public Mono<String> nextVisitId()     { return next("sales_visit_seq",      "VIS-%06d"); }
    public Mono<String> nextOrderId()     { return next("sales_order_seq",      "ORD-%06d"); }
    public Mono<String> nextOrderItemId() { return next("sales_order_item_seq", "ITM-%06d"); }
    public Mono<String> nextPaymentId()   { return next("sales_payment_seq",    "PAY-%06d"); }
}
