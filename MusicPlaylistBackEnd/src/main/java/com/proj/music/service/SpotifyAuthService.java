package com.proj.music.service;

import java.time.Instant;

import com.proj.music.entity.Users;

public interface SpotifyAuthService {
	boolean isTokenExpired(Instant expiresAt);
	void refreshAccessToken(Users users);
}
