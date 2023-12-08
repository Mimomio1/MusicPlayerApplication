package com.proj.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.proj.music.entity.ImageModel;
import com.proj.music.entity.Playlists;
import com.proj.music.entity.Users;
import com.proj.music.repository.ImageRepository;
import com.proj.music.service.ImageService;
import com.proj.music.service.PlaylistService;
import com.proj.music.service.SpotifyAuthService;
import com.proj.music.service.UserService;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.special.FeaturedPlaylists;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.browse.GetCategorysPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.browse.GetListOfFeaturedPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.follow.UnfollowPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.ChangePlaylistsDetailsRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistCoverImageRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.playlists.RemoveItemsFromPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.UploadCustomPlaylistCoverImageRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.hc.core5.http.ParseException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200") // Add your frontend URL
public class PlaylistController {

	@Autowired
	private UserService userService;

	@Autowired
	private PlaylistService playlistService;

	@Autowired
	private SpotifyAuthService spotifyService;
	
	@Autowired
	private ImageService imageService;

	@Autowired
	private SpotifyApi spotifyApi;

	@PostMapping("/create-playlist/users/{userId}/playlists")
	public ResponseEntity<String> createPlaylist(@RequestBody Playlist myPlaylist, @PathVariable String userId) {
		System.out.println("Received request to create playlist");

		// Retrieve the Users object from the repository
		Users users = userService.findRefById(userId);

		if (users != null) {
			// Check if the access token is still valid
			if (spotifyService.isTokenExpired(users.getExpiresAt())) {
				// If expired, refresh the access token
				spotifyService.refreshAccessToken(users);
			}

			// sets the token given by the user
			spotifyApi.setAccessToken(users.getAccessToken());
			spotifyApi.setRefreshToken(users.getRefreshToken());

			// Create a playlist on Spotify
			final CreatePlaylistRequest.Builder playlistBuilder = spotifyApi.createPlaylist(userId, myPlaylist.getName()).description(myPlaylist.getDescription());

			final CreatePlaylistRequest playlistRequest = playlistBuilder.build();
			Playlist newPlaylist = null;
			try {
				newPlaylist = playlistRequest.execute();
			} catch (ParseException | SpotifyWebApiException | IOException e) {
				e.printStackTrace();
			}
			// Saves playlist to database table
			playlistService.addPlaylist(newPlaylist, userId);

			return new ResponseEntity<>("Playlist has been created", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		}
	}

	// Delete a playlist
	@DeleteMapping("/delete-playlist/users/{playlistId}")
	public ResponseEntity<String> deletePlaylist(@PathVariable String playlistId, @RequestParam String userId) {
		System.out.println("Im inside the delete method");
		// Retrieve the Users object from the repository
		Users users = userService.findRefById(userId);

		if (users != null) {
			// Check if the access token is still valid
			if (spotifyService.isTokenExpired(users.getExpiresAt())) {
				// If expired, refresh the access token
				spotifyService.refreshAccessToken(users);
			}

			spotifyApi.setAccessToken(users.getAccessToken());
			spotifyApi.setRefreshToken(users.getRefreshToken());

			// Delete a playlist on Spotify
			UnfollowPlaylistRequest deleteBuilder = spotifyApi.unfollowPlaylist(playlistId).build();

			String deleteRequest = null;
			try {
				deleteRequest = deleteBuilder.execute();
			} catch (ParseException | SpotifyWebApiException | IOException e) {
				e.printStackTrace();
			}

			// Delete playlist in database table
			playlistService.deletePlaylistBySpotifyId(playlistId);

			return new ResponseEntity<>(deleteRequest, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		}

	}

	// Get a list of users playlists
	@GetMapping("/getPlaylists/users/playlists")
	public ResponseEntity<Object> getPlaylistSongById(@RequestParam String userId, @RequestParam String songId) {

		// Retrieve the Users object from the repository
		Users users = userService.findRefById(userId);

		if (users != null) {
			// Check if the access token is still valid
			if (spotifyService.isTokenExpired(users.getExpiresAt())) {
				// If expired, refresh the access token
				spotifyService.refreshAccessToken(users);
			}

			spotifyApi.setAccessToken(users.getAccessToken());
			spotifyApi.setRefreshToken(users.getRefreshToken());

			// Delete a playlist on Spotify
			GetListOfUsersPlaylistsRequest playlistBuilder = spotifyApi.getListOfUsersPlaylists(songId).build();

			Paging<PlaylistSimplified> getPlaylistsRequest = null;
			try {
				getPlaylistsRequest = playlistBuilder.execute();
			} catch (ParseException | SpotifyWebApiException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Delete playlist in database table

			return new ResponseEntity<>(getPlaylistsRequest, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		}
	}

	// Update a Playlist Name
	@PutMapping("/user/playlist/{playlistId}")
	public ResponseEntity<String> updatePlaylistDetails(@RequestParam String userId, @PathVariable String playlistId,
			@RequestBody Playlist playlist) throws ParseException, SpotifyWebApiException, IOException {

		// Retrieve the Users object from the repository
		Users users = userService.findRefById(userId);

		if (users != null) {
			// Check if the access token is still valid
			if (spotifyService.isTokenExpired(users.getExpiresAt())) {
				// If expired, refresh the access token
				spotifyService.refreshAccessToken(users);
			}

			spotifyApi.setAccessToken(users.getAccessToken());
			spotifyApi.setRefreshToken(users.getRefreshToken());

			// Delete a playlist on Spotify
			ChangePlaylistsDetailsRequest.Builder updatePlaylistDetailBuilder = spotifyApi
					.changePlaylistsDetails(playlistId);

			ChangePlaylistsDetailsRequest changePlaylistDetails = updatePlaylistDetailBuilder.name(playlist.getName())
					.description(playlist.getDescription()).build();

			String newDetails = changePlaylistDetails.execute();

			playlistService.updatePlaylistById(playlistId, playlist);

			return new ResponseEntity<>(newDetails, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		}
	}

	// Get album By Id
	@GetMapping(value = "/playlists/{playlistId}")
	public ResponseEntity<Playlist> getPlaylistById(@PathVariable String playlistId, @RequestParam String userId)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the user
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
		final GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(playlistId).build();
		// creates a album object
		Playlist playlist = getPlaylistRequest.execute();
		// returns a http response back with album being created and converted into
		// json
		return new ResponseEntity<Playlist>(playlist, HttpStatus.OK);
	}

	// Get album By Id
	@GetMapping(value = "/playlists/{playlistId}/tracks")
	@ResponseStatus(value = HttpStatus.OK)
	public PlaylistTrack[] getPlaylistItems(@PathVariable String playlistId, @RequestParam String userId)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the user
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
		final GetPlaylistsItemsRequest getPlaylistItemsRequest = spotifyApi.getPlaylistsItems(playlistId).build();
		try {
			// creates a album object
			Paging<PlaylistTrack> playlist = getPlaylistItemsRequest.execute();
			// returns a http response back with album being created and converted into
			return playlist.getItems();
		} catch (Exception e) {
			System.out.println("Exception occured while fetching: " + e);
		}
		return new PlaylistTrack[0];
	}

	// Get album By Id
	@GetMapping(value = "/me/playlists")
	@ResponseStatus(value = HttpStatus.OK)
	public PlaylistSimplified[] getCurrUsersPlaylist(@RequestParam String userId)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the user
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
		// Then it gets the playlist for users
		final GetListOfCurrentUsersPlaylistsRequest getCurrUserPlaylistRequest = spotifyApi
				.getListOfCurrentUsersPlaylists().build();
		try {
			// creates a playlistSimplified object
			Paging<PlaylistSimplified> playlist = getCurrUserPlaylistRequest.execute();

			// returns a http response back with playlist being created and converted into
			return playlist.getItems();
		} catch (Exception e) {
			System.out.println("Exception occured while fetching: " + e);
		}
		return new PlaylistSimplified[0];
	}

	// Get users playlist
	@GetMapping(value = "/users/{userId}/playlists")
	@ResponseStatus(value = HttpStatus.OK)
	public PlaylistSimplified[] getUserPlaylist(@PathVariable String userId)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the user
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
		// Then it gets the playlist for users
		final GetListOfUsersPlaylistsRequest getListOfUserPlaylistRequest = spotifyApi.getListOfUsersPlaylists(userId)
				.build();
		try {
			// creates a playlistSimplified object
			Paging<PlaylistSimplified> playlist = getListOfUserPlaylistRequest.execute();
			// returns a http response back with playlist being created and converted into
			return playlist.getItems();
		} catch (Exception e) {
			System.out.println("Exception occured while fetching: " + e);
		}
		return new PlaylistSimplified[0];
	}

	// Get Feature playlist
	@GetMapping(value = "/browse/featured-playlist")
	@ResponseStatus(value = HttpStatus.OK)
	public FeaturedPlaylists getFeaturePlaylist(@RequestParam String userId)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the user
		Users userDetails = userService.findRefById(userId);
		// Then it pass the access token for the user to do the spotify api request
		spotifyApi.setAccessToken(userDetails.getAccessToken());
		// Then it refreshes the token for the user to the spotify api request
		spotifyApi.setRefreshToken(userDetails.getRefreshToken());
		// Then it send an accesstoken to spotify to get list of featured playlist;i0
		final GetListOfFeaturedPlaylistsRequest getListOfFeaturedRequest = spotifyApi.getListOfFeaturedPlaylists()
				.build();
		// creates a featuedplaylist object
		FeaturedPlaylists featuredPlaylist = getListOfFeaturedRequest.execute();
		// returns a http response back with featureplalyist
		return featuredPlaylist;
	}

	// Get Feature playlist
	@GetMapping(value = "/browse/categories/{categoryId}/playlists")
	@ResponseStatus(value = HttpStatus.OK)
	public PlaylistSimplified[] getCategoryPlaylist(@RequestParam String userId, @PathVariable String categoryId)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the users
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
		// Then it send an accesstoken to spotify to get list of featured playlist;i0
		final GetCategorysPlaylistsRequest getCategoryPlaylistsRequest = spotifyApi.getCategorysPlaylists(categoryId)
				.build();
		// creates a featuedplaylist object
		Paging<PlaylistSimplified> categoryPlaylist = getCategoryPlaylistsRequest.execute();
		// returns a http response back with featureplalyist
		return categoryPlaylist.getItems();
	}

	// Get Feature playlist
	@GetMapping(value = "/playlists/{playlistId}/images")
	@ResponseStatus(value = HttpStatus.OK)
	public Image[] getPlaylistCoverImage(@PathVariable String playlistId, @RequestParam String userId)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the users
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
		// Then it send an accesstoken to spotify to get list of featured playlist;i0
		final GetPlaylistCoverImageRequest getPlaylistsCoverImageRequest = spotifyApi.getPlaylistCoverImage(playlistId)
				.build();
		// creates a featuedplaylist object
		Image[] playlistCoverImage = getPlaylistsCoverImageRequest.execute();
		// returns a http response back with featureplalyist
		return playlistCoverImage;
	}

	@PostMapping(value = "/playlists/{playlistId}/songs")
	@ResponseStatus(value = HttpStatus.CREATED)
	public SnapshotResult addSongsToPlaylist(@PathVariable String playlistId, @RequestParam String userId,
			@RequestParam String... songUri) throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the users
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
		// It send access token to spotify to add songs to playlist

		final AddItemsToPlaylistRequest addSongsPlaylistRequest = spotifyApi.addItemsToPlaylist(playlistId, songUri)
				.build();

		SnapshotResult songSavedToPlaylist = addSongsPlaylistRequest.execute();

		for (String uri : songUri) {
			String[] parts = uri.split(":");
			String songId = parts[parts.length - 1];
			GetTrackRequest getTrackRequest = spotifyApi.getTrack(songId).build();
			Track newTrack = getTrackRequest.execute();

			// Save information about the added track to your database
			playlistService.addSongToPlaylist(playlistId, newTrack);
		}
		return songSavedToPlaylist;
	}

	// Deletes Songs From Playlist
	@DeleteMapping(value = "/playlists/{playlistId}/songs")
	public ResponseEntity<SnapshotResult> removePlaylistSongs(@PathVariable String playlistId,
			@RequestParam String userId, @RequestParam String... songUri)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the users
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
		// Retrieve details about the added tracks from Spotify by iterating through
		// each URI
		for (String uri : songUri) {
			String[] parts = uri.split(":");
			String songId = parts[parts.length - 1];
			// Save information about the added track to your database
			playlistService.deleteSongFromPlaylist(songId);
		}

		List<String> songUris = Arrays.asList(songUri);

		List<JsonObject> newTrackObjects = songUris.stream().map((x) -> {
			JsonObject newtrackObject = new JsonObject();
			newtrackObject.addProperty("uri", x);
			return newtrackObject;
		}).collect(Collectors.toList());

		// Convert song URIs to a list of JSON objects
//		List<JsonObject> trackObjects = new ArrayList<>();
//		for (String uri : songUris) {
//		    JsonObject trackObject = new JsonObject();
//		    trackObject.addProperty("uri", uri);
//		    trackObjects.add(trackObject);
//		}

		JsonArray jsonArray = newTrackObjects.stream().collect(JsonArray::new, JsonArray::add, JsonArray::addAll);

		// Create a JSON array from the list of track objects
//		JsonArray jsonArray = new JsonArray();
//		for (JsonObject trackObject : newTrackObjects) {
//		    jsonArray.add(trackObject);
//		}

		RemoveItemsFromPlaylistRequest removeItemsFromPlaylist = spotifyApi
				.removeItemsFromPlaylist(playlistId, jsonArray).build();

		SnapshotResult snapShot = removeItemsFromPlaylist.execute();

		return ResponseEntity.ok().body(snapShot);
	}

	@PutMapping(value = "/playlists/{playlistId}/images")
	@ResponseStatus(value = HttpStatus.OK)
	public String updateCustomImagePlaylist(@RequestParam String userId, @PathVariable String playlistId)
			throws ParseException, SpotifyWebApiException, IOException {
		// first its gets the users
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
		// It send access token to spotify for the request to update playlist customer
		final UploadCustomPlaylistCoverImageRequest uploadCustomerPlaylistCoverImageRqst = spotifyApi
				.uploadCustomPlaylistCoverImage(playlistId).build();
		String uploadPlaylistCoverImage = uploadCustomerPlaylistCoverImageRqst.execute();

		return uploadPlaylistCoverImage;
	}
	
	@PostMapping(value = "/upload")
	public BodyBuilder updateImage(@RequestParam("imageFile") MultipartFile file)
	{
		imageService.uploadImage(file);
		return ResponseEntity.status(HttpStatus.OK);
	}
	@GetMapping(path = { "/get/{imageName}" })
	public ResponseEntity<ImageModel> getImageForPlaylist(@PathVariable("imageName") String imageName) throws IOException {
		return ResponseEntity.ok().body(imageService.getImage(imageName));
	}
	
	
}