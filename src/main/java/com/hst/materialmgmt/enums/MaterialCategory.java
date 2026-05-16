package com.hst.materialmgmt.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MaterialCategory {
    PREFORMS,
    CHEMICALS,
    PACKAGING,
    BRANDING,
    CAPS,
    LABELS,
    INGREDIENTS,
    EQUIPMENT,
    CONSUMABLES,
    OTHER;

    @JsonValue
    public String getValue() { return this.name(); }
}
