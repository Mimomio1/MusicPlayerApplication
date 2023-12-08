package com.proj.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proj.music.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
	 Users findByRefId(String refId);
	 	
	 Boolean existsByRefId(String refId);
}
