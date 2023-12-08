package com.proj.music.service.impl;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proj.music.entity.Users;
import com.proj.music.repository.UserRepository;
import com.proj.music.service.SpotifyAuthService;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

@Service
public class SpotifyAuthServiceImpl implements SpotifyAuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SpotifyApi spotifyApi;
	
	@Override
	public boolean isTokenExpired(Instant expiresAt) {
	       // Compare the current time with the expiration time
        return expiresAt != null && Instant.now().isAfter(expiresAt);
	}

	@Override
	public void refreshAccessToken(Users users) {
		try {
            AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
                    .refresh_token(users.getRefreshToken())
                    .build();

            AuthorizationCodeCredentials credentials = authorizationCodeRefreshRequest.execute();

            // Update the Users object with the new access token and expiration time
            users.setAccessToken(credentials.getAccessToken());
            users.setExpiresAt(Instant.now().plusSeconds(credentials.getExpiresIn()));

            // Save the updated Users object to the database
            userRepository.save(users);
        } catch (Exception e) {
            // Handle the exception, log, or throw a specific exception
            throw new RuntimeException("Failed to refresh access token: " + e.getMessage(), e);
        }
	}

}
