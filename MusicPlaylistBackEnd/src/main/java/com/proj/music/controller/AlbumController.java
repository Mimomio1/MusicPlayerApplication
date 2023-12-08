package com.proj.music.controller;

import java.io.IOException;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.neovisionaries.i18n.CountryCode;
import com.proj.music.entity.Users;
import com.proj.music.service.AlbumService;
import com.proj.music.service.SpotifyAuthService;
import com.proj.music.service.UserService;
import com.proj.music.spotify.config.SpotifyConfiguration;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.SavedAlbum;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumRequest;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.albums.GetSeveralAlbumsRequest;
import se.michaelthelin.spotify.requests.data.browse.GetListOfNewReleasesRequest;
import se.michaelthelin.spotify.requests.data.library.CheckUsersSavedAlbumsRequest;
import se.michaelthelin.spotify.requests.data.library.GetCurrentUsersSavedAlbumsRequest;
import se.michaelthelin.spotify.requests.data.library.RemoveAlbumsForCurrentUserRequest;
import se.michaelthelin.spotify.requests.data.library.SaveAlbumsForCurrentUserRequest;

@RestController
@RequestMapping("/api")
public class AlbumController {

	@Autowired
	private AlbumService albumService;

	@Autowired
	private UserService userService;

	@Autowired
	private SpotifyConfiguration spotifyConfiguration;

	@Autowired
	private SpotifyApi spotifyApi;

	@Autowired
	private SpotifyAuthService spotifyService;

	// Get album By Id
	@GetMapping(value = "/albums/{albumId}")
	public ResponseEntity<Album> getAlbumById(@PathVariable String albumId, @RequestParam String userId)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the user from queried
		Users userDetails = userService.findRefById(userId);
		// Check if the access token is still valid
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());
		// Then it gets the album
		final GetAlbumRequest getAlbumRequest = spotifyApi.getAlbum(albumId).build();
		// creates a album object
		Album album = getAlbumRequest.execute();
		// returns a http response back with album being created and converted into
		// json
		return new ResponseEntity<Album>(album, HttpStatus.OK);
	}

	@GetMapping(value = "/albums/{albumId}/tracks")
	@ResponseStatus(value = HttpStatus.OK)
	public TrackSimplified[] getAlbumTracks(@PathVariable String albumId, @RequestParam String userId)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the user from queried
		Users userDetails = userService.findRefById(userId);
		// Check if the access token is still valid
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());
		// sends an access token to the spotify api to get album track
		GetAlbumsTracksRequest getAlbumTrackRequet = spotifyApi.getAlbumsTracks(albumId).limit(10).offset(1)
				.market(CountryCode.US).build();
		// Creates a paging for the tracks
		Paging<TrackSimplified> getTracks = getAlbumTrackRequet.execute();
		// Gets the Tracks in the paging object
		return getTracks.getItems();
	}

	@GetMapping(value = "/user/albums")
	@ResponseStatus(value = HttpStatus.OK)
	public SavedAlbum[] getUserSavedAlbums(@RequestParam String userId)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the user from queried
		Users userDetails = userService.findRefById(userId);
		// Check if the access token is still valid
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());

		GetCurrentUsersSavedAlbumsRequest getUserSaveAlbumRequest = spotifyApi.getCurrentUsersSavedAlbums().limit(10)
				.market(CountryCode.US).offset(0).build();
		try {
			Paging<SavedAlbum> savedAlbum = getUserSaveAlbumRequest.execute();
			return savedAlbum.getItems();
		} catch (Exception e) {
			System.out.println("Exception occured while fetching top songs: " + e);
		}
		return new SavedAlbum[0];
	}

	@PutMapping(value = "/user/albums")
	@ResponseStatus(value = HttpStatus.CREATED)
	public String saveAlbumsForCurrentUsers(@RequestParam String userId, @RequestParam String... albumIds)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the user from queried
		Users userDetails = userService.findRefById(userId);
		// Check if the access token is still valid
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());
		// sends an access token to the spotify api to save albums current users
		SaveAlbumsForCurrentUserRequest saveAlbumsForCurrUser = spotifyApi.saveAlbumsForCurrentUser(albumIds).build();
		String newSavedAlbumForCurrUser = saveAlbumsForCurrUser.execute();

		for (String id : albumIds) {
			GetAlbumRequest albumRequest = spotifyApi.getAlbum(id).build();
			Album newAlbum = albumRequest.execute();
			albumService.addAlbum(newAlbum, userId);
		}

		return newSavedAlbumForCurrUser;
	}

	@DeleteMapping(value = "/user/albums")
	@ResponseStatus(value = HttpStatus.OK)
	public String deleteUserSavedAlbums(@RequestParam String userId, @RequestParam String... albumIds)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the user from queried
		Users userDetails = userService.findRefById(userId);
		// Check if the access token is still valid
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());
		// sends an access token to the spotify api to remove saved albums
		RemoveAlbumsForCurrentUserRequest removeUserSavedAlbums = spotifyApi.removeAlbumsForCurrentUser(albumIds)
				.build();

		String deleteAlbumForCurrUser = removeUserSavedAlbums.execute();
		for (String id : albumIds) {
			albumService.deleteAlbumBySpotifyId(id);
		}
		return deleteAlbumForCurrUser;
	}

	@DeleteMapping(value = "/user/albums/contains")
	@ResponseStatus(value = HttpStatus.OK)
	public Boolean[] checkUserSavedAlbums(@RequestParam String userId) {
		// first its gets the user from queried
		Users userDetails = userService.findRefById(userId);
		// Check if the access token is still valid
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());
		// sends an access token to the spotify api to check user saved albums
		CheckUsersSavedAlbumsRequest checkUserSavedAlbumsRequest = spotifyApi.checkUsersSavedAlbums(userId).build();

		try {
			Boolean[] isSavedAlbumsExist = checkUserSavedAlbumsRequest.execute();
			return isSavedAlbumsExist;
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		return new Boolean[0];
	}

	@GetMapping(value = "/browse/new_release")
	@ResponseStatus(value = HttpStatus.OK)
	public AlbumSimplified[] getNewReleases(@RequestParam String userId) {
		// first its gets the user from queried
		Users userDetails = userService.findRefById(userId);
		// Check if the access token is still valid
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());
		// then it gets the new releases
		GetListOfNewReleasesRequest getListOfNewReleases = spotifyApi.getListOfNewReleases().build();

		Paging<AlbumSimplified> albumSimplified;
		try {
			albumSimplified = getListOfNewReleases.execute();
			return albumSimplified.getItems();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		return new AlbumSimplified[0];
	}

	// Get several albums
	@GetMapping(value = "/albums")
	@ResponseStatus(value = HttpStatus.OK)
	public Album[] getSeveralAlbums(@RequestParam String userId, @RequestParam String... albumIds) {
		// first its gets the user from queried
		Users userDetails = userService.findRefById(userId);
		// Check if the access token is still valid
		if (spotifyService.isTokenExpired(userDetails.getExpiresAt())) {
			// If expired, refresh the access token
			spotifyService.refreshAccessToken(userDetails);
		}
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());
		// Then it gets the album
		final GetSeveralAlbumsRequest getAlbumRequest = spotifyApi.getSeveralAlbums(albumIds).build();

		try {
			Album[] myAlbum = getAlbumRequest.execute();
			return myAlbum;
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		return new Album[0];
	}
}
