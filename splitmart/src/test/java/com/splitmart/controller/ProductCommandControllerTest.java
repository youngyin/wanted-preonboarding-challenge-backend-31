package com.splitmart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splitmart.adapter.controller.ProductCommandController;
import com.splitmart.application.command.CreateProductCommand;
import com.splitmart.application.service.ProductDeleteService;
import com.splitmart.application.service.ProductRegisterService;
import com.splitmart.application.service.ProductUpdateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductCommandController.class)
class ProductCommandControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProductRegisterService productRegisterService;
    @MockitoBean
    ProductUpdateService productUpdateService;
    @MockitoBean
    ProductDeleteService productDeleteService;

    @Test
    void createProduct() throws Exception {
        CreateProductCommand command = CreateProductCommand.builder()
                .name("테스트 상품")
                .slug("test-product")
                .shortDescription("설명")
                .fullDescription("상세")
                .sellerId(1L)
                .brandId(2L)
                .basePrice(BigDecimal.valueOf(100000))
                .salePrice(BigDecimal.valueOf(90000))
                .categoryIds(List.of(1L))
                .tagIds(List.of(1L, 2L))
                .build();

        given(productRegisterService.createProduct(any())).willReturn(123L);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(123L));
    }

    @Test
    void updateProduct() throws Exception {
        mockMvc.perform(put("/api/products/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"업데이트 상품\"}")) // 간단한 페이로드
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/123"))
                .andExpect(status().isNoContent());
    }
}