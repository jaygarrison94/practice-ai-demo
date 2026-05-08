package com.privatebutler.domain.bookkeeping.valueobject;

public enum RecordType {
    EXPENSE(1, "支出"),
    INCOME(2, "收入");

    private final int code;
    private final String displayName;

    RecordType(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() { return code; }
    public String getDisplayName() { return displayName; }

    public static RecordType fromCode(Integer code) {
        if (code == null) return EXPENSE;
        for (RecordType t : values()) {
            if (t.code == code) return t;
        }
        return EXPENSE;
    }
}
