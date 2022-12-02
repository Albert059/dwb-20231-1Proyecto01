package com.Product.api.service;

import java.util.List;

import com.Product.api.dto.ApiResponse;
import com.Product.api.entity.Category;

public interface SvcCategory {
	
	List<Category> getCategorys();
	Category getCategory(Integer category_id);
	ApiResponse createCategory(Category category);
	ApiResponse updateCategory(Integer category_id, Category category);
	ApiResponse deleteCategory(Integer category_id);

}
