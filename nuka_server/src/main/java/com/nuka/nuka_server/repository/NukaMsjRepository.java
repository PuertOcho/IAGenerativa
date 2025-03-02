package com.nuka.nuka_server.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nuka.nuka_server.models.NukaChat;
import com.nuka.nuka_server.models.NukaMsj;

@Repository
public interface NukaMsjRepository extends MongoRepository<NukaMsj, String> {
	@Query("{'chatId': { $in: ?0 }}")
	List<NukaMsj> findByChatIds(List<String> ids);
	
	@Query("{ 'visible' : true, 'texto': { $regex: ?0, $options: 'i' }}")
	List<NukaMsj> findByTexto(String texto);

	@Query("{ 'visible' : true, 'texto': { $regex: ?0, $options: 'i' }, 'chatId':  { $in: ?1 }}")
	List<NukaMsj> findByTextoLikeVisibleOrderByFechaDesc(@Param("texto") String texto, List<String> chatIds, Pageable pageable);

	@Query("{ 'visible' : true, 'chatId': { $in: ?0 }, 'fecha': { $lte: ?1 }}")
	List<NukaMsj> findByChatIdsAndFecha(List<String> ids, Long fecha);
	    
	@Query(value = "{ 'visible' : true, 'chatId' : { $in: ?0 }, 'fecha': { $lt: ?1 }}", sort = "{ 'fecha' : -1 }")
	List<NukaMsj> findTopNByChatIdsAndFecha(List<String> ids, Long fecha, Pageable pageable);

	@Query(value = "{ 'visible' : true, 'chatId' : { $in: ?0 } , 'fecha': { $gte: ?1, $lt: ?2 }}", sort = "{ 'fecha' : -1 }")
	List<NukaMsj> findMessagesByChatIdsAndDateRange(List<String> chatId, Long fechaStart, Long fechaEnd);
	 
	@Query("{ 'visible' : true, 'chatId': { $in: ?0 }}")
	List<NukaMsj> findByChatIds(List<String> ids, Pageable pageable);
	 
	@Query("{ 'visible' : ?0 }")
	List<NukaMsj> findByVisibility(Boolean visible);
	
	@Query("{ 'id' : { $in: ?0 } }")
	List<NukaMsj> findByIds(List<String> ids);
	
	@Query(value = "{ 'chatId' : { $in: ?0 } }", count = true)
	long countByChatIds(List<String> ids);
}
