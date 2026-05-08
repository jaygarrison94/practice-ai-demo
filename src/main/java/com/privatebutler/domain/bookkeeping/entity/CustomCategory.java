package com.privatebutler.domain.bookkeeping.entity;

import com.privatebutler.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bk_category")
@Getter
@Setter
@NoArgsConstructor
public class CustomCategory extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private Integer type;

    @Column(length = 10)
    private String color;

    @Column(name = "sort_order")
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
