package com.proj.music.controller;

import java.io.IOException;
import java.util.List;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.proj.music.dto.ReviewDTO;
import com.proj.music.entity.Reviews;
import com.proj.music.entity.Users;
import com.proj.music.service.ReviewService;
import com.proj.music.service.SpotifyAuthService;
import com.proj.music.service.UserService;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.Track;

@RestController
@RequestMapping("/api")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private SpotifyAuthService spotifyService;

	@Autowired
	private SpotifyApi spotifyApi;

	@Autowired
	private UserService userService;

	@PostMapping("/review")
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<Reviews> postReview(@RequestBody Reviews review, @RequestParam String userId, @RequestParam String entityType, @RequestParam String entityId) throws ParseException, SpotifyWebApiException, IOException {
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
		entityType = entityType.toLowerCase();
		Reviews createdReview = null;
		if(entityType.equals("songs"))
		{
			Track getNewTrack = spotifyApi.getTrack(entityId).build().execute();
			createdReview = reviewService.addReview(review, entityId, entityType, userId, getNewTrack);
		}
		else if(entityType.equals("albums"))
		{
			String[] parts = entityId.split(":");
			String albumId = parts[parts.length - 1];
			Album getNewAlbum = spotifyApi.getAlbum(albumId).build().execute();
			createdReview = reviewService.addReview(review, entityId, entityType, userId, getNewAlbum);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
	}

	@PutMapping("/review")
	public ResponseEntity<String> updateReview(@RequestBody Reviews review, @RequestParam long reviewId, @RequestParam String entityId,
			@RequestParam String entityType, @RequestParam String userId) {
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
		entityType = entityType.toLowerCase();
		reviewService.updateReviewById(reviewId, entityId, entityType, review);
		return ResponseEntity.ok("Review updated successfully. Review ID: " + reviewId);
	}

	@DeleteMapping("/review")
	public ResponseEntity<String> deleteReview(@RequestParam String userId, @RequestParam String entityId,
			@RequestParam String entityType, @RequestParam long reviewId) {
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
		entityType = entityType.toLowerCase();
		reviewService.deleteReviewById(reviewId, entityType, entityId);
		return ResponseEntity.ok("Review deleted successfully. Review ID: " + reviewId);
	}

	@GetMapping("/review/{reviewId}")
	public ResponseEntity<ReviewDTO> getReviewByReviewIdForUser(@RequestParam String userId,
			@PathVariable String entityType, @PathVariable String entityId, @PathVariable long reviewId) {
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
		entityType = entityType.toLowerCase();
		ReviewDTO newReview = reviewService.getReviewById(reviewId, entityType, entityId);
		return ResponseEntity.ok().body(newReview);
	}

	@GetMapping("/reviews")
	public ResponseEntity<List<ReviewDTO>> getReviews(@RequestParam String userId, @RequestParam String entityType,
			@RequestParam String entityId) {
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
		entityType = entityType.toLowerCase();
		List<ReviewDTO> reviewDTOs = reviewService.getReviews(entityType, entityId);
		// Print the reviews just before returning them

		return ResponseEntity.ok().body(reviewDTOs);

	}

}