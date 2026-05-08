package com.privatebutler.domain.bookkeeping.entity;

import com.privatebutler.domain.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomCategory extends BaseEntity {

    private Long userId;

    private String name;

    private Integer type;

    private String color;

    private Integer sortOrder;

    public CustomCategory(Long userId, String name, Integer type, String color, Integer sortOrder) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.color = color;
        this.sortOrder = sortOrder;
        this.status = 1;
    }
}
