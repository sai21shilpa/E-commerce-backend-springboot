package com.ecommerce.backend.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.backend.entity.Product;

@Repository
public interface ProductDao extends CrudRepository<Product,Integer> {

	public List<Product> findAll(Pageable pageable);
	
	public List<Product>  findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCase(
			String key1, String key2, Pageable pageable);
	

}
