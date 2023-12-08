package com.proj.music.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.proj.music.entity.Users;
import com.proj.music.exceptions.ResourceNotFoundException;
import com.proj.music.repository.UserRepository;
import com.proj.music.service.UserService;

import se.michaelthelin.spotify.model_objects.specification.User;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public String createUser(User spotifyUser, String accessToken,
			String refreshToken) {

		// Create a new User object
		Users user = new Users();

		// Set the properties of the User object
		user.setEmail(spotifyUser.getEmail()); // Use the email from the Spotify user
		user.setUserName(spotifyUser.getDisplayName()); // Use the username from the Spotify user
		user.setAccessToken(accessToken);
		Instant expiresAt = Instant.now().plus(Duration.ofHours(1));
		user.setExpiresAt(expiresAt);
		user.setRefreshToken(refreshToken);
		user.setRefid(spotifyUser.getId());
		// Save the user to the database
		userRepository.save(user);

		return "User has been saved";
	}

	@Override
	public Users insertOrUpdateUserDetails(User user, String accessToken, String refreshToken) {
		Users userDetails = userRepository.findByRefId(user.getId());
		
		if(Objects.isNull(userDetails))
		{
			userDetails = new Users();
		}
		
		userDetails.setUserName(user.getDisplayName());
		userDetails.setRefreshToken(refreshToken);
		userDetails.setAccessToken(accessToken);
		userDetails.setRefid(user.getId());
		userDetails.setEmail(user.getEmail());
		return userRepository.save(userDetails);
	}
	
	public Users findRefById(String refid)
	{
		return userRepository.findByRefId(refid);
	}
//
//	@Override
//	public User getUserById(long id) {
//		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
//	}
//
//	@Override
//	public List<User> getUsers() {
//		return userRepository.findAll();
//	}
//
//	@Override
//	public String updateUserById(User user, long id) {
//		Optional<User> findUser = userRepository.findById(id);
//
//		if (findUser.isPresent()) {
//			findUser.get().setId(user.getId());
//			findUser.get().setUserName(user.getUserName());
//		} else {
//			throw new ResourceNotFoundException("User +" + id + "+Not Found");
//		}
//
//		return "User has been updated";
//	}
//
//	@Override
//	public String deleteUserById(long id) {
//		Optional<User> findUser = userRepository.findById(id);
//		if (findUser.isPresent()) {
//			userRepository.deleteById(id);
//		}
//		return "User has been deleted";
//	}

	@Override
	public Users findUserById(long id) {	
		return userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cant Find User"));
	}

	@Override
	public Boolean userExistByRefId(String id) {
		return userRepository.existsByRefId(id);
	}

	

//	@Override
//	public se.michaelthelin.spotify.model_objects.specification.User getUserById(long id) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String updateUserById(se.michaelthelin.spotify.model_objects.specification.User user, long id) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/*
	 * @Override public User findUserByUsername(String userName) { return
	 * userRepository.findUserByUsername(userName); }
	 */

}