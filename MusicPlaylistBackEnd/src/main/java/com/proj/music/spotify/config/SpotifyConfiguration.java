package com.proj.music.spotify.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

@Service
public class SpotifyConfiguration {
	
	public static String url = "";
	
	@Value("${redirect.server.ip}")
	private String customIp;
	
	@Bean
	public SpotifyApi getSpotifyObject() {
		URI redirectedURL = SpotifyHttpManager.makeUri(customIp + "/api/get-user-code/");

		return new SpotifyApi
				.Builder()
				.setClientId("168ee3507de24e17ab803a85aa541fcb")
				.setClientSecret("d89ff093928a4061bb7cfc6d5c753633")
				.setRedirectUri(redirectedURL)
				.build();
	}
}