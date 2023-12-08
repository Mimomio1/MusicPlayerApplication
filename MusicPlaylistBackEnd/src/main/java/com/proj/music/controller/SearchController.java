package com.proj.music.controller;

import java.io.IOException;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.neovisionaries.i18n.CountryCode;
import com.proj.music.entity.Users;
import com.proj.music.service.SpotifyAuthService;
import com.proj.music.service.UserService;
import com.proj.music.spotify.config.SpotifyConfiguration;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.requests.data.search.SearchItemRequest;

@RestController
@RequestMapping("/api")
public class SearchController {

	@Autowired
	private UserService userService;

	@Autowired
	private SpotifyConfiguration spotifyConfig;

	@Autowired
	private SpotifyAuthService spotifyService;

	@Autowired
	private SpotifyApi spotifyApi;

	@GetMapping(value = "/search")
	@ResponseStatus(value = HttpStatus.OK)
	ResponseEntity<SearchResult> searchItems(@RequestParam String q, @RequestParam String type,
			@RequestParam String userId) throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the user
		Users userDetails = userService.findRefById(userId);
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());

		SearchItemRequest searchItemsRequest = spotifyApi.searchItem(q, type)
				.limit(20)
				.offset(0)
				.market(CountryCode.US)
				.build();
		
		SearchResult search = searchItemsRequest.execute();
		
		return ResponseEntity.ok().body(search);
	}
}
