package com.vikas.cowselling.enums;

public enum CowSortField {

    PRICE("price"),
    AGE("age"),
    MILK_PRODUCTION("milkProduction"),
    CREATED_AT("createdAt");

    private final String field;
    CowSortField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
