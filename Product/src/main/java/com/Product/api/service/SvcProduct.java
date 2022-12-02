package com.Product.api.service;

import java.util.List;

import com.Product.api.dto.ApiResponse;
import com.Product.api.dto.DtoProduct;
import com.Product.api.entity.Product;

public interface SvcProduct {

	public Product getProduct(String gtin);
	public ApiResponse createProduct(Product in);
	public ApiResponse updateProduct(Product in, Integer id);
	public ApiResponse updateProductStock(String gtin, Integer stock);
	public ApiResponse deleteProduct(Integer id);
    public ApiResponse updateProductCategory(String gtin, Integer category_id);
    public List<DtoProduct> listProducts(Integer categoryId);

}
