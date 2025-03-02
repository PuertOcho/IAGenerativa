package com.nuka.nuka_server.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nuka.nuka_server.models.ShoppingList;

@Repository
public interface ShoppingListRepository extends MongoRepository<ShoppingList, String> {
    
    
    @Query(value = "{ 'isFinished' : ?0 }", sort = "{ 'datePurchase' : -1 }")
    List<ShoppingList> findByIsFinished(@Param("isFinished") Boolean isFinished, Pageable pageable);

    @Query(value = "{'isFinished': ?0}", count = true)
    long countByIsFinished(@Param("isFinished") Boolean isFinished);
	
}