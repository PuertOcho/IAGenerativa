package com.nuka.nuka_server.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.nuka.nuka_server.models.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

	@Query("{email: ?0, password: ?1}")
	List<User> getUserLogin(String email, String password);
	
	@Query("{'userNick': { $in: ?0 }}")
	List<User> findByUsers(List<String> usernames);
	
	@Query("{email: ?0}")
	List<User> findByEmail(String email);
	
	@Query("{userNick: ?0}")
	User findByUserNick(String userNick);
}
