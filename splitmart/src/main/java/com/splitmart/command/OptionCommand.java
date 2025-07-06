package com.splitmart.command;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OptionCommand {
    
    private String name;
    private BigDecimal additionalPrice;
    
    public BigDecimal getAdditionalPrice() {
        return additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
    }
} 