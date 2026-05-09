package com.hst.materialmgmt.production.dto;

import java.time.LocalDate;

public class FgMovement {

    public enum MovementTypeEnum {
        PRODUCED("PRODUCED"),
        DISPATCHED("DISPATCHED");

        private final String value;
        MovementTypeEnum(String value) { this.value = value; }
        public String getValue() { return value; }

        public static MovementTypeEnum fromValue(String value) {
            for (MovementTypeEnum e : values()) {
                if (e.value.equalsIgnoreCase(value)) return e;
            }
            return null;
        }
    }

    private String          movementId;
    private String          productId;
    private MovementTypeEnum movementType;
    private Double          quantity;
    private String          referenceType;
    private String          referenceId;
    private LocalDate       movementDate;
    private String          notes;

    public String           getMovementId()   { return movementId;   }
    public String           getProductId()    { return productId;    }
    public MovementTypeEnum getMovementType() { return movementType; }
    public Double           getQuantity()     { return quantity;     }
    public String           getReferenceType(){ return referenceType;}
    public String           getReferenceId()  { return referenceId;  }
    public LocalDate        getMovementDate() { return movementDate; }
    public String           getNotes()        { return notes;        }

    public void setMovementId(String v)            { this.movementId   = v; }
    public void setProductId(String v)             { this.productId    = v; }
    public void setMovementType(MovementTypeEnum v){ this.movementType = v; }
    public void setQuantity(Double v)              { this.quantity     = v; }
    public void setReferenceType(String v)         { this.referenceType= v; }
    public void setReferenceId(String v)           { this.referenceId  = v; }
    public void setMovementDate(LocalDate v)       { this.movementDate = v; }
    public void setNotes(String v)                 { this.notes        = v; }
}