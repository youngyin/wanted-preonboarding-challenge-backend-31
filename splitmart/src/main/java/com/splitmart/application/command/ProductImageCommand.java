package com.splitmart.application.command;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageCommand {
    
    private String url;
    private String altText;
    private Boolean isPrimary;
    private Integer displayOrder;
    private Long optionId; // 옵션과 연결된 이미지인 경우
    
    // 기본값 설정
    public Boolean getIsPrimary() {
        return isPrimary != null ? isPrimary : false;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder != null ? displayOrder : 0;
    }
} 