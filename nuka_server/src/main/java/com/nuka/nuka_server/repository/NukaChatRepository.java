package com.nuka.nuka_server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.nuka.nuka_server.models.NukaChat;

@Repository
public interface NukaChatRepository extends MongoRepository<NukaChat, String> {

	@Query("{ 'visible' : ?0 }")
	List<NukaChat> findByVisible(Boolean visible);
	
	@Query("{ 'visible' : ?0, 'usuarioId' : ?1 }")
	List<NukaChat> findByVisibleAndUsuarioId(Boolean visible, String usuarioId);
	
	@Query("{ 'visible' : true, 'id':  { $in: ?0 }}")
	List<NukaChat> findByIds(List<String> ids);

	@Query("{ 'visible' : true, 'id': ?0 }")
	Optional<NukaChat> findById(String id);

	@Query("{ 'visible' : ?0, 'usuarioId' : ?1, 'tags' : { $in: ?2 } }")
	List<NukaChat> findByVisibleAndUsuarioIdAndFilter(Boolean visible, String usuarioId, List<String> valores);
	
	
}
