package com.hst.materialmgmt.production.dto;

import java.time.LocalDate;

public class FgDispatchRequest {
    private String    productId;
    private Double    quantity;
    private String    referenceId;
    private String    notes;
    private LocalDate dispatchDate;

    public String    getProductId()   { return productId;   }
    public Double    getQuantity()    { return quantity;     }
    public String    getReferenceId() { return referenceId; }
    public String    getNotes()       { return notes;        }
    public LocalDate getDispatchDate(){ return dispatchDate; }

    public void setProductId(String v)    { this.productId   = v; }
    public void setQuantity(Double v)     { this.quantity    = v; }
    public void setReferenceId(String v)  { this.referenceId = v; }
    public void setNotes(String v)        { this.notes       = v; }
    public void setDispatchDate(LocalDate v){ this.dispatchDate = v; }
}