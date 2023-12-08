package com.proj.music.controller;

import java.io.IOException;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.proj.music.service.SongService;
import com.proj.music.service.SpotifyAuthService;
import com.proj.music.service.UserService;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.AudioAnalysis;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.library.CheckUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.library.GetUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.library.RemoveUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.library.SaveTracksForUserRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioAnalysisForTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForSeveralTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetSeveralTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

@RestController
@RequestMapping(value = "/api")
public class SongController {

	@Autowired
	private UserService userService;

	@Autowired
	private SpotifyApi spotifyApi;

	@Autowired
	private SongService songService;

	@Autowired
	private SpotifyAuthService spotifyService;

	@GetMapping(value = "/songs/{songId}")
	@ResponseStatus(value = HttpStatus.OK)
	public Track getSongById(@PathVariable String songId, @RequestParam String userId) {
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

		final GetTrackRequest getTrackRequest = spotifyApi.getTrack(songId).build();

		Track getTrack = null;
		try {
			getTrack = getTrackRequest.execute();
		} catch (Exception e) {
			System.out.println("Exception occured while fetching top songs: " + e);
		}
		songService.getSongBySpotifyId(songId);
		return getTrack;
	}

	@GetMapping(value = "/songs")
	@ResponseStatus(value = HttpStatus.OK)
	public Track[] getSeveralSongs(@RequestParam String userId, @RequestParam String... songId) {
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

		final GetSeveralTracksRequest getSeveralTrackRequest = spotifyApi.getSeveralTracks(songId)
				.market(CountryCode.US).build();

		Track[] getSeveralTrack = null;

		try {
			getSeveralTrack = getSeveralTrackRequest.execute();
		} catch (Exception e) {
			System.out.println("Exception occured while fetching top songs: " + e);
		}
		return getSeveralTrack;
	}

	@GetMapping(value = "/user/songs")
	@ResponseStatus(value = HttpStatus.OK)
	public SavedTrack[] getUserSavedSongs(@RequestParam String userId) {
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

		final GetUsersSavedTracksRequest getUserSavedSongsRequest = spotifyApi.getUsersSavedTracks().limit(10).offset(1)
				.market(CountryCode.US).build();

		Paging<SavedTrack> getUserSavedTracks = null;

		try {
			getUserSavedTracks = getUserSavedSongsRequest.execute();
		} catch (Exception e) {
			System.out.println("Exception occured while fetching top songs: " + e);
		}
		return getUserSavedTracks.getItems();
	}

	@PutMapping(value = "/user/songs")
	@ResponseStatus(value = HttpStatus.CREATED)
	public String saveSongForCurrUser(@RequestParam String userId, @RequestParam String... songId)
			throws ParseException, SpotifyWebApiException, IOException {
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

		for (String s1 : songId) {
			GetTrackRequest getTrackRequest = spotifyApi.getTrack(s1).build();

			Track t1 = getTrackRequest.execute();

			songService.addSong(t1, userId);
		}
		// Saved tracks for user
		final SaveTracksForUserRequest saveTrackForUserRequest = spotifyApi.saveTracksForUser(songId).build();

		try {
			saveTrackForUserRequest.execute();
		} catch (Exception e) {
			System.out.println("Exception occured while saving songs: " + e);
		}
		return "Track has been saved";
	}

	@DeleteMapping(value = "/user/songs")
	private String removeSongsForUser(@RequestParam String userId, @RequestParam String... songId) {
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
		for (String s1 : songId) {
			songService.deleteBySpotifyId(s1);
		}
		// Remove user save tracks request to spotify api
		RemoveUsersSavedTracksRequest removeSavedTracks = spotifyApi.removeUsersSavedTracks(songId).build();
		// Removes Song for the user
		try {
			removeSavedTracks.execute();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		return "Track has been deleted";
	}

	@GetMapping(value = "/user/song/contains")
	private Boolean[] checkUserSavedTrack(@RequestParam String userId, @RequestParam String... songId)
			throws ParseException, SpotifyWebApiException, IOException {
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

		for (String s1 : songId) {
			songService.existsBySpotifyId(s1);
		}

		// Sends a request to spotifyapi to check user saved tracks
		CheckUsersSavedTracksRequest checkUsers = spotifyApi.checkUsersSavedTracks(songId).build();
		Boolean[] isUserTrackSaved = null;
		try {
			isUserTrackSaved = checkUsers.execute();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		return isUserTrackSaved;
	}

	@GetMapping(value = "/user/song/features")
	private AudioFeatures[] getSongAudioFeatures(@RequestParam String userId, @RequestParam String... songId)
			throws ParseException, SpotifyWebApiException, IOException {
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
		// Sends a request to spotifyapi to check user saved tracks
		GetAudioFeaturesForSeveralTracksRequest getTracksAudioFeatures = spotifyApi
				.getAudioFeaturesForSeveralTracks(songId).build();
		AudioFeatures[] severalSongFeaturesTrackSaved = null;
		try {
			severalSongFeaturesTrackSaved = getTracksAudioFeatures.execute();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		return severalSongFeaturesTrackSaved;
	}

	@GetMapping(value = "/user/song/features/{songId}")
	private AudioFeatures getAudioFeatureForSong(@RequestParam String userId, @PathVariable String songId)
			throws ParseException, SpotifyWebApiException, IOException {
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
		// Sends a request to spotifyapi to check user saved tracks
		GetAudioFeaturesForTrackRequest getSongAudioFeature = spotifyApi.getAudioFeaturesForTrack(songId).build();
		AudioFeatures audioFeatureTrackSaved = null;
		try {
			audioFeatureTrackSaved = getSongAudioFeature.execute();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		return audioFeatureTrackSaved;
	}

	@GetMapping(value = "/user/song/analysis/{songId}")
	private AudioAnalysis getAudioAnalysisTrack(@RequestParam String userId, @PathVariable String songId) throws ParseException, SpotifyWebApiException, IOException {
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
		// Sends a request to spotifyapi to check user saved tracks
		GetAudioAnalysisForTrackRequest getSongAudioAnalysis = spotifyApi.getAudioAnalysisForTrack(songId).build();
		AudioAnalysis audioAnalysisTrack = null;
		try {
			audioAnalysisTrack = getSongAudioAnalysis.execute();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		return audioAnalysisTrack;
	}

	@GetMapping(value = "/recommendations")
	private Recommendations getSongRecommendations(@RequestParam String userId) throws ParseException, SpotifyWebApiException, IOException {
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
		// Sends a request to spotifyapi to check user saved tracks
		GetRecommendationsRequest getRecommendations = spotifyApi.getRecommendations().limit(10).market(CountryCode.US).build();
		Recommendations recommendations = null;
		try {
			recommendations = getRecommendations.execute();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		return recommendations;
	}
}
