package com.proj.music.controller;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.proj.music.service.PlaylistService;
import com.proj.music.service.UserService;
import com.proj.music.spotify.config.SpotifyConfiguration;

import jakarta.servlet.http.HttpServletResponse;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

@CrossOrigin(origins = "http://localhost:4200") // Allow requests from your React app
@RestController
@RequestMapping("/api")
public class SpotifyController {

	@Value("${custom.server.ip}")
	private String customIp;

	@Autowired
	private UserService userProfileService;
	
	@Autowired
	private PlaylistService playlistService;

	@Autowired
	private SpotifyConfiguration spotifyConfiguration;

	@Autowired
	private SpotifyApi spotifyApi;

	// Method to login
	@GetMapping(value = "/login")
	public ResponseEntity<Map<String, String>> spotifyLogin() {

		SpotifyApi object = spotifyConfiguration.getSpotifyObject();

		AuthorizationCodeUriRequest authorizationCodeUriRequest = object.authorizationCodeUri().scope(
				"user-library-read, user-read-private, user-read-email, user-top-read, playlist-modify-public, playlist-modify-private, playlist-read-collaborative, playlist-read-private, user-library-modify")
				.show_dialog(true).build();

		final URI uri = authorizationCodeUriRequest.execute();

		Map<String, String> responseMap = new HashMap<>();
		responseMap.put("url", uri.toString());

		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@GetMapping("/get-user-code/")
	@ResponseStatus(value = HttpStatus.OK)
	public void getSpotifyUserCode(@RequestParam("code") String userCode, HttpServletResponse response) {

		AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(userCode).build();
		User user = null;

		try {
			final AuthorizationCodeCredentials authorizationCode = authorizationCodeRequest.execute();

			spotifyApi.setAccessToken(authorizationCode.getAccessToken());
			spotifyApi.setRefreshToken(authorizationCode.getRefreshToken());

			// Check if the access token has expired
			if (System.currentTimeMillis() > authorizationCode.getExpiresIn()) {
				// Refresh the access token
				AuthorizationCodeCredentials refreshedToken = spotifyApi.authorizationCodeRefresh().build().execute();
				spotifyApi.setAccessToken(refreshedToken.getAccessToken());

				// Log refreshed access token
				System.out.println("Refreshed Access Token: " + refreshedToken.getAccessToken());
			}

			final GetCurrentUsersProfileRequest getCurrentUsersProfile = spotifyApi.getCurrentUsersProfile().build();
			user = getCurrentUsersProfile.execute();

			if (user != null && !userProfileService.userExistByRefId(user.getId())) {

				userProfileService.createUser(user, authorizationCode.getAccessToken(),
						authorizationCode.getRefreshToken());
				System.out.println("Expires in: " + authorizationCode.getExpiresIn());
				response.sendRedirect(
						customIp + "/home?id=" + user.getId() + "&accessToken=" + authorizationCode.getAccessToken());
				addAllCurrentUserPlaylist(user.getId());
			} else if (user != null) {
				response.sendRedirect(
						customIp + "/home?id=" + user.getId() + "&accessToken=" + authorizationCode.getAccessToken());
				addAllCurrentUserPlaylist(user.getId());
			} else {
				// If the user object is null, log an error message or handle it appropriately.
				throw new RuntimeException("User object is null. The response may not contain a valid user.");
			}
		} catch (Exception e) {
			// Handle the exception and log an error message
			System.out.println("Invalid authorization code: " + userCode);
			e.printStackTrace();
		}
	}

	@GetMapping(value = "/home")
	public String home(@RequestParam String userId) {
		try {
			return userId;
		} catch (Exception e) {
			System.out.println("Exception occured while landing to home page: " + e);
		}
		return null;
	}
	
	public void addAllCurrentUserPlaylist(String userId) throws ParseException, SpotifyWebApiException, IOException
	{
		GetListOfCurrentUsersPlaylistsRequest getListOfPlaylist = spotifyApi.getListOfCurrentUsersPlaylists().build();
		PlaylistSimplified [] newPlaylist = getListOfPlaylist.execute().getItems();
		
		for(PlaylistSimplified userPlaylist : newPlaylist)
		{
			if(!playlistService.isPlaylistExist(userPlaylist.getId()))
			{
				GetPlaylistRequest getPlaylist = spotifyApi.getPlaylist(userPlaylist.getId()).build();
				Playlist myPlaylist = getPlaylist.execute();
//				GetTrackRequest getTrackForPlaylist = spotifyApi.getTrack(userPlaylist.getId()).build();
//				Track track = getTrackForPlaylist.execute();
				playlistService.addPlaylist(myPlaylist, userId); 
//				playlistService.addSongToPlaylist(userPlaylist.getId(), track);
			}
			else {
				System.out.println("Playlist has already been added");
			}
		}

	}
}