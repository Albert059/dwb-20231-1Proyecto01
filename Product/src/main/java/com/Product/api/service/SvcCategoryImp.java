package com.Product.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.Product.api.dto.ApiResponse;
import com.Product.api.entity.Category;
import com.Product.api.repository.RepoCategory;
import com.Product.exception.ApiException;


@Service
public class SvcCategoryImp implements SvcCategory {

	@Autowired
	RepoCategory repo;

	@Override
	public List<Category> getCategorys() {
		return repo.findByStatus(1);
	}

	@Override
	public Category getCategory(Integer category_id) {
	    Category category = repo.findByCategoryId(category_id);
        if(category == null){
            throw new ApiException(HttpStatus.NOT_FOUND, "category does exit");
        } else return category;
	}

	@Override
	public ApiResponse createCategory(Category category) {
	    Category caregorySaved= (Category)repo.findByCategory(category.getCategory());
        if(caregorySaved != null){
            if(caregorySaved.getStatus() == 0) {
                repo.activateCategory(caregorySaved.getCategory_id());
                return new ApiResponse("category has been acticated");
            }
            else 
                throw new ApiException(HttpStatus.BAD_REQUEST, "category already exist");
        }
        repo.createCategory(category.getCategory());
        return new ApiResponse("category created");
	}

	@Override
	public ApiResponse updateCategory(Integer category_id, Category category) {
	    Category caregorySaved= (Category)repo.findByCategoryId(category_id);
        if(caregorySaved == null)
            throw new ApiException(HttpStatus.NOT_FOUND, "category does not exist");
        else{
            if(caregorySaved.getStatus() == 0) 
                throw new ApiException(HttpStatus.BAD_REQUEST, "category is not active");
            else {
                caregorySaved= (Category)repo.findByCategory(category.getCategory());
                if(caregorySaved != null)
                    throw new ApiException(HttpStatus.BAD_REQUEST, "category alredy exist");
               repo.updateCategory(category_id, category.getCategory());
               return new ApiResponse("category updated");
            }
        }
	}

	@Override
	public ApiResponse deleteCategory(Integer category_id) {
	    Category caregorySaved= (Category)repo.findByCategoryId(category_id);
        if(caregorySaved == null)
            throw new ApiException(HttpStatus.NOT_FOUND, "category does not exist");
        else{    
            repo.deleteById(category_id);
            return new ApiResponse("category removed");
        
        }
	}

}
