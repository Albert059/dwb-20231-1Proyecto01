package com.Product.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Product.api.dto.DtoProduct;


@Repository
public interface RepoProductList extends JpaRepository<DtoProduct, Integer> {
    
    @Query(value = "SELECT * FROM product WHERE category_id= :category_id", nativeQuery = true)
    List<DtoProduct> listProducts(@Param(value = "category_id") Integer categoryId);

}
