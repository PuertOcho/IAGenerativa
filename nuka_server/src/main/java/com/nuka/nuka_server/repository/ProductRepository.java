package com.nuka.nuka_server.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.nuka.nuka_server.models.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
	@Query("{'aliasProducto': { $in: ?0 }}")
	List<Product> findByAliasProducto(List<String> aliasProducto);
}