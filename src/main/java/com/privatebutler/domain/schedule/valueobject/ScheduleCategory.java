package com.privatebutler.domain.schedule.valueobject;

public enum ScheduleCategory {
    WORK("工作"),
    LIFE("生活"),
    IMPORTANT("重要事项"),
    OTHER("其他");

    private final String displayName;

    ScheduleCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ScheduleCategory fromString(String value) {
        if (value == null) return OTHER;
        try {
            return ScheduleCategory.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
