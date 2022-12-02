package com.Product.api.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.Product.api.dto.ApiResponse;
import com.Product.api.dto.DtoProduct;
import com.Product.api.entity.Category;
import com.Product.api.entity.Product;
import com.Product.api.repository.RepoCategory;
import com.Product.api.repository.RepoProduct;
import com.Product.api.repository.RepoProductList;
import com.Product.exception.ApiException;

@Service
public class SvcProductImp implements SvcProduct {

	@Autowired
	RepoProduct repo;
	
	@Autowired
	RepoProductList repoProductList;
	
	@Autowired
	RepoCategory repoCategory;

	@Override
	public Product getProduct(String gtin) {
		Product product = repo.findByProductGtin(gtin); // sustituir null por la llamada al método implementado en el repositorio
		if (product != null) {
			product.setCategory(repoCategory.findByCategoryId(product.getCategory_id()));
			return product;
		}else
			throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");
	}

	/*
	 * 4. Implementar el método createProduct considerando las siguientes validaciones:
  		1. validar que la categoría del nuevo producto exista
  		2. el código GTIN y el nombre del producto son únicos
  		3. si al intentar realizar un nuevo registro ya existe un producto con el mismo GTIN pero tiene estatus 0, 
  		   entonces se debe cambiar el estatus del producto existente a 1 y actualizar sus datos con los del nuevo registro
	 */
	@Override
	public ApiResponse createProduct(Product in) {
	    Category category = repoCategory.findByCategoryId(in.getCategory_id());
	    if(category != null) {
	        Product product = repo.findByGtin(in.getGtin());
	        if(product != null) {
	            updateProduct(in,product.getProduct_id());
	            return new ApiResponse("product activated");
	        }else {
	            try {
	                in.setStatus(1);
	                repo.save(in);
	            }catch (DataIntegrityViolationException e) {
	                if (e.getLocalizedMessage().contains("gtin"))
	                    throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");
	                if (e.getLocalizedMessage().contains("product"))
	                    throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exist");
	            }
	            return new ApiResponse("Product created");
	        }
	    }else
	        throw new ApiException(HttpStatus.NOT_FOUND, "Category not found");
	}

	@Override
	public ApiResponse updateProduct(Product in, Integer id) {
		Integer updated = 0;
		try {
			updated = repo.updateProduct(id, in.getGtin(), in.getProduct(), in.getDescription(), in.getPrice(), in.getStock(), in.getCategory_id());
		}catch (DataIntegrityViolationException e) {
			if (e.getLocalizedMessage().contains("gtin"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");
			if (e.getLocalizedMessage().contains("product"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exist");
			if (e.contains(SQLIntegrityConstraintViolationException.class))
				throw new ApiException(HttpStatus.BAD_REQUEST, "category not found");
		}
		if(updated == 0)
			throw new ApiException(HttpStatus.BAD_REQUEST, "product cannot be updated");
		else
			return new ApiResponse("product updated");
	}

	@Override
	public ApiResponse deleteProduct(Integer id) {
		if (repo.deleteProduct(id) > 0)
			return new ApiResponse("product removed");
		else
			throw new ApiException(HttpStatus.BAD_REQUEST, "product cannot be deleted");
	}

	@Override
	public ApiResponse updateProductStock(String gtin, Integer stock) {
		Product product = getProduct(gtin);
		Integer actualstock= product.getStock(); 
		if(stock > product.getStock())
			throw new ApiException(HttpStatus.BAD_REQUEST, "stock to update is invalid");
	
		repo.updateProductStock(gtin, (actualstock-stock));
		return new ApiResponse("product stock updated");
	}

    @Override
    public ApiResponse updateProductCategory(String gtin, Integer category_id) {
        Product foundProduct = repo.findByGtin(gtin);

        if(foundProduct== null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "product category cannot be updated");
        } else {
            if(foundProduct.getStatus() == 1) {
                if(repoCategory.findByCategoryId(category_id) == null) {
                    throw new ApiException(HttpStatus.NOT_FOUND, "category not found");
                }else {
                    if(foundProduct.getCategory_id() == category_id) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "product category cannot be updated");
                    }else {
                        repo.updateProductCategory(gtin, category_id);
                        return new ApiResponse("product category updated");
                    }
                }
            } else {
                throw new ApiException(HttpStatus.BAD_REQUEST, "product category cannot be updated");
            }
        }
    }

    @Override
    public List<DtoProduct> listProducts(Integer categoryId) {

        return repoProductList.listProducts(categoryId);
    }
}
