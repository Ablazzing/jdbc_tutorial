package org.example.lesson4.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
public class Product {
    @EqualsAndHashCode.Include
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer companyId;
}
