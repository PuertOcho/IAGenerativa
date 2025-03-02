package com.nuka.nuka_server.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.nuka.nuka_server.models.Data;

@Repository
public interface AdminRepository extends MongoRepository<Data, String> {

    @Query("{ 'idScript': { $ne: null } }")
    List<Data> findScripts();

    // List<Data> findByIdScriptIsNotNull();
    
}
