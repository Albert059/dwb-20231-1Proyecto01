package com.Product.api.service;

import java.util.List;

import com.Product.api.dto.ApiResponse;
import com.Product.api.entity.ProductImage;

public interface SvcProductImage {
    
    public List<ProductImage> getProductImages(Integer product_id);
    public ApiResponse createProductImage(ProductImage in);
    public ApiResponse deleteProductImage(Integer id);

}
