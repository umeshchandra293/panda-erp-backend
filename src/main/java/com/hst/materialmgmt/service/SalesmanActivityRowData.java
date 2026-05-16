package com.hst.materialmgmt.service;

import java.math.BigDecimal;

/** Internal DTO — one row from the salesman activity aggregation query. */
public record SalesmanActivityRowData(
        String salesmanId,
        String salesmanName,
        String routeName,
        Long visitTarget,
        Long visitsToday,
        Long ordersToday,
        BigDecimal collectionsToday) {}
