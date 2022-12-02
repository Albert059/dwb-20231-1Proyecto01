package com.Product.api.repository;


import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Product.api.entity.Product;

@Repository
public interface RepoProduct extends JpaRepository<Product, Integer>{
	
	// 3. Implementar la firma de un método que permita consultar un producto por su código GTIN y con estatus 1
    
    @Query(value = "SELECT * FROM product WHERE gtin= :gtin AND status = 1", nativeQuery = true)
    Product findByProductGtin(@Param("gtin") String gtin);
    
    @Query(value = "SELECT * FROM product WHERE product_id= :product_id ", nativeQuery = true)
    Product findByProductId(@Param("product_id") Integer product_id);
    
    // este Query es para buscar el product por el gtin pero no importa el status
    @Query(value = "SELECT * FROM product WHERE gtin= :gtin", nativeQuery = true)
    Product findByGtin(@Param("gtin") String gtin);
    
    
	    
	@Modifying
	@Transactional
	@Query(value ="UPDATE product "
					+ "SET gtin = :gtin, "
						+ "product = :product, "
						+ "description = :description, "
						+ "price = :price, "
						+ "stock = :stock, "
						+ "status = 1, "
						+ "category_id = :category_id "
					+ "WHERE product_id = :product_id", nativeQuery = true)
	Integer updateProduct(
			@Param("product_id") Integer product_id,
			@Param("gtin") String gtin, 
			@Param("product") String product, 
			@Param("description") String description, 
			@Param("price") Double price, 
			@Param("stock") Integer stock,
			@Param("category_id") Integer category_id
		);
	
	@Modifying
	@Transactional
	@Query(value ="UPDATE product SET status = 0 WHERE product_id = :product_id AND status = 1", nativeQuery = true)
	Integer deleteProduct(@Param("product_id") Integer product_id);
	
	@Modifying
	@Transactional
	@Query(value ="UPDATE product SET stock = :stock WHERE gtin = :gtin AND status = 1", nativeQuery = true)
	Integer updateProductStock(@Param("gtin") String gtin, @Param("stock") Integer stock);

	@Transactional
    @Modifying
    @Query(value = "UPDATE product SET category_id = :category_id WHERE gtin = :gtin",
            nativeQuery = true)
    void updateProductCategory(@Param(value = "gtin") String gtin,
                               @Param(value = "category_id") Integer categoryId);
}