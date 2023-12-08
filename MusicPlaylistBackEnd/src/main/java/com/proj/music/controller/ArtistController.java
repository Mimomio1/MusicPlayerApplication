package com.proj.music.controller;

import org.springframework.http.HttpStatus;

import java.io.IOException;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.neovisionaries.i18n.CountryCode;
import com.proj.music.entity.Users;
import com.proj.music.service.ArtistService;
import com.proj.music.service.SpotifyAuthService;
import com.proj.music.service.UserService;
import com.proj.music.spotify.config.SpotifyConfiguration;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.artists.GetArtistRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsRelatedArtistsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;

@RestController
@RequestMapping("/api")
public class ArtistController {

	@Autowired
	private UserService userProfileService;

	@Autowired
	private SpotifyConfiguration spotifyConfiguration;

	@Autowired
	private ArtistService artistService;

	@Autowired
	private SpotifyAuthService spotifyService;

	private final SpotifyApi spotifyApi;

	@Autowired
	public ArtistController(SpotifyApi spotifyApi) {
		this.spotifyApi = spotifyApi;
	}

	// Get Artist By Id
	@GetMapping(value = "/artists/{artistId}")
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<Artist> getArtistById(@PathVariable String artistId, @RequestParam String userId)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the user
		Users users = userProfileService.findRefById(userId);
		if (spotifyService.isTokenExpired(users.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(users);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(users.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(users.getRefreshToken());
		// Then it gets the artist
		final GetArtistRequest getArtistRequest = spotifyApi.getArtist(artistId).build();
		// creates a artist object
		Artist artist = getArtistRequest.execute();
		// returns a http response back with artist being created and converted into
		// json
		return new ResponseEntity<Artist>(artist, HttpStatus.OK);
	}

	// Get Top Artist
	@GetMapping(value = "/user-top-artists")
	public Artist[] getUserTopArtists(@RequestParam String userId) {
		Users userDetails = userProfileService.findRefById(userId);
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		spotifyApi.setAccessToken(userDetails.getAccessToken());

		spotifyApi.setRefreshToken(userDetails.getRefreshToken());

		final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
				.time_range("medium_term").limit(10).offset(0).build();
		try {
			final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
			return artistPaging.getItems();
		} catch (Exception e) {
			System.out.println("Something went wrong!\n" + e.getMessage());
		}
		return new Artist[0];
	}

	// Get Artist Albums
	@GetMapping(value = "/artists/{artistId}/albums")
	@ResponseStatus(value = HttpStatus.OK)
	public AlbumSimplified[] getArtistAlbums(@PathVariable String artistId, @RequestParam String userId) {
		// first its gets the user
		Users userDetails = userProfileService.findRefById(userId);
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());
		// Then it gets the artist albums
		final GetArtistsAlbumsRequest getArtistsAlbumRequest = spotifyApi.getArtistsAlbums(artistId)
				.market(CountryCode.ES).limit(10).offset(1).build();
		try {
			// put albums in pages after the 10 albums
			final Paging<AlbumSimplified> albumPaging = getArtistsAlbumRequest.execute();
			return albumPaging.getItems();
		} catch (Exception e) {
			System.out.println("Something went wrong!\n" + e.getMessage());
		}
		return new AlbumSimplified[0];
	}

	@GetMapping(value = "/artist-top-songs")
	@ResponseStatus(value = HttpStatus.OK)
	public Track[] getArtistTopTracks(@PathVariable String artistId, @RequestParam String userId)
			throws ParseException, SpotifyWebApiException, IOException {

		// first its gets the user
		Users userDetails = userProfileService.findRefById(userId);
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());

		final GetArtistsTopTracksRequest getArtistTopTracksRequest = spotifyApi.getArtistsTopTracks(userId, null)
				.build();
		try {
			final Track[] topTracks = getArtistTopTracksRequest.execute();
			return topTracks;
		} catch (Exception e) {
			System.out.println("Exception occured while fetching top songs: " + e);
		}
		return new Track[0];
	}

	@GetMapping(value = "artists/{artistId}/related-artists")
	@ResponseStatus(value = HttpStatus.OK)
	public Artist[] getArtistRelatedArtist(@PathVariable String artistId, @RequestParam String userId) {
		// first its gets the user
		Users userDetails = userProfileService.findRefById(userId);
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());

		final GetArtistsRelatedArtistsRequest getArtistRelatedArtistRequest = spotifyApi
				.getArtistsRelatedArtists(artistId).build();
		try {
			final Artist[] relatedArtist = getArtistRelatedArtistRequest.execute();
			return relatedArtist;
		} catch (Exception e) {
			System.out.println("Exception occured while fetching top songs: " + e);
		}
		return new Artist[0];
	}

	@DeleteMapping(value = "/artists/{artistId}")
	@ResponseStatus(value = HttpStatus.OK)
	public String deleteArtistById(@PathVariable String artistId, @RequestParam String userId) {
		// first its gets the user
		Users userDetails = userProfileService.findRefById(userId);
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());
		return artistService.deleteArtistBySpotifyId(artistId);
	}

	@PostMapping(value = "/artists")
	@ResponseStatus(value = HttpStatus.OK)
	public String addArtist(@RequestBody Artist artist, @RequestParam String userId) {
		// first its gets the user
		Users userDetails = userProfileService.findRefById(userId);
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());

		artistService.addArtist(artist);
		return "Artist has been added";
	}
}
