package com.hst.materialmgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.PurchaseOrderMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.PurchaseOrderRepository;

/**
 * Purchase Order service — header only.
 *
 * Line items are NOT saved here. R2DBC cannot serialize entity collections,
 * and the parent/child link-table pattern from BaseServiceImpl regenerates
 * child IDs which would lose item references. PO line items will need their
 * own dedicated service that knows about po_id as a foreign key.
 *
 * For now this saves the PO header (supplier, date, notes, total, status)
 * and that is enough to unblock the frontend form.
 */
@Service
public class PurchaseOrderService extends ParentBaseServiceImpl {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Override
    protected BaseMapper getMapper() {
        return purchaseOrderMapper;
    }

    @Override
    protected ParentRepositoryImpl getParentRepository() {
        return purchaseOrderRepository;
    }
}