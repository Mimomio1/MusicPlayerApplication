package com.proj.music.service;

import com.proj.music.entity.Users;

import se.michaelthelin.spotify.model_objects.specification.User;

public interface UserService {

	public String createUser(User spotifyUser, String accessToken, String refreshToken);

//
//	public User getUserById(long id);
//
//	public List<User> getUsers();

	public Users insertOrUpdateUserDetails(User user, String accessToken, String refreshToken);
	
	public  Users findRefById(String refId);
	
	public Users findUserById(long id);
	
	public Boolean userExistByRefId(String id);
}
