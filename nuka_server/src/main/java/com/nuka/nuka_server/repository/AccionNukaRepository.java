package com.nuka.nuka_server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nuka.nuka_server.models.AccionNuka;

@Repository
public interface AccionNukaRepository extends MongoRepository<AccionNuka, String> {
}
