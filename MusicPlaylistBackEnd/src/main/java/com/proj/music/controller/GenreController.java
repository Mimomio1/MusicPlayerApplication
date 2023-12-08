package com.proj.music.controller;

import java.io.IOException;
import java.util.List;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.proj.music.entity.Genres;
import com.proj.music.entity.Users;
import com.proj.music.service.GenreService;
import com.proj.music.service.SpotifyAuthService;
import com.proj.music.service.UserService;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.data.browse.miscellaneous.GetAvailableGenreSeedsRequest;

@RestController
@RequestMapping(value = "/api")
public class GenreController {

	@Autowired
	private UserService userService;

	@Autowired
	private SpotifyApi spotifyApi;

	@Autowired
	private GenreService genreService;

	@Autowired
	private SpotifyAuthService spotifyService;
	
	@GetMapping(value = "/recommendations/get-genre-seeds")
	@ResponseStatus(value = HttpStatus.OK)
	public String[] getGenreSeeds(@RequestParam String userId) {
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
		
		GetAvailableGenreSeedsRequest getGenreSeeds = spotifyApi.getAvailableGenreSeeds().build();
		
		String [] genreSeeds = null;
		try {
			genreSeeds = getGenreSeeds.execute();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return genreSeeds;
	}
	
	@GetMapping(value = "/genre/{genreId}")
	@ResponseStatus(value = HttpStatus.OK)
	public Genres getGenreById(@RequestParam String userId, @PathVariable String genreId)
	{
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
		
		return genreService.findGenreBySpotifyId(genreId);
	}
	
	@PostMapping(value = "/genre")
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<String> addGenre(@RequestParam String userId, Genres genre)
	{
		return new ResponseEntity<String>(genreService.addGenre(genre),HttpStatus.CREATED);
	}
	
	@GetMapping(value = "/genres")
	public ResponseEntity<List<Genres>> getAllGenres()
	{		
		return ResponseEntity.ok().body(genreService.getGenres());
	}
}
