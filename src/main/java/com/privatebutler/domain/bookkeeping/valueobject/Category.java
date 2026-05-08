package com.privatebutler.domain.bookkeeping.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Category {

    private final Long id;
    private final String name;
    private final RecordType type;
    private final String color;

    public static final Category DEFAULT = new Category(null, "其他", RecordType.EXPENSE, "#999999");

    public Category(Long id, String name, RecordType type, String color) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.color = color;
    }
}
