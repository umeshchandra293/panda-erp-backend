package com.hst.materialmgmt.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MaterialUom {
    KG, GM, LTR, ML, PCS, BOX, BAG, ROLL, METER;

    @JsonValue
    public String getValue() { return this.name(); }
}