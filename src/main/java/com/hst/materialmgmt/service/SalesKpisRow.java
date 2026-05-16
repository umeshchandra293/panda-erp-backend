package com.hst.materialmgmt.service;

import java.math.BigDecimal;

/** Internal DTO — result of the retailer KPI aggregation query. */
public record SalesKpisRow(
        Long totalRetailers,
        Long activeRetailers,
        BigDecimal totalReceivables,
        Long outstandingShops,
        Long criticalAccounts) {}
