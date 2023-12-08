package com.proj.music.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proj.music.dto.ReviewDTO;
import com.proj.music.entity.Albums;
import com.proj.music.entity.Playlists;
import com.proj.music.entity.Reviews;
import com.proj.music.entity.Songs;
import com.proj.music.entity.Users;
import com.proj.music.exceptions.ResourceNotFoundException;
import com.proj.music.repository.AlbumRepository;
import com.proj.music.repository.PlaylistRepository;
import com.proj.music.repository.ReviewRepository;
import com.proj.music.repository.SongRepository;
import com.proj.music.repository.UserRepository;
import com.proj.music.service.ReviewService;

import jakarta.transaction.Transactional;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.Track;

@Service
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private SongRepository songRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PlaylistRepository playlistRepository;

	@Autowired
	private AlbumRepository albumRepository;

	@Override
	public String updateReviewById(long reviewId, String entityId, String entityType, Reviews review) {
		Optional<Reviews> review1 = null;
		switch (entityType) {
		case "songs":
			review1 = reviewRepository.findBySongs_SpotifyIdAndId(entityId, reviewId);
			review1.get().setName(review.getName());
			review1.get().setComment(review.getComment());
			review1.get().setDatePosted(review.getDatePosted());
			review1.get().setRating(review.getRating());
			reviewRepository.save(review1.get());
			break;

		case "albums":
			review1 = reviewRepository.findByAlbums_SpotifyIdAndId(entityId, reviewId);
			review1.get().setName(review.getName());
			review1.get().setComment(review.getComment());
			review1.get().setDatePosted(review.getDatePosted());
			review1.get().setRating(review.getRating());
			reviewRepository.save(review1.get());
			break;

		case "playlist":
			review1 = reviewRepository.findByPlaylist_SpotifyIdAndId(entityId, reviewId);
			review1.get().setName(review.getName());
			review1.get().setComment(review.getComment());
			review1.get().setDatePosted(review.getDatePosted());
			review1.get().setRating(review.getRating());
			reviewRepository.save(review1.get());
			break;

		default:
			throw new ResourceNotFoundException("Object has not been found");
		}
		return "Review for " + entityType + " has been updated";
	}

	@Override
	public String deleteReviewById(long reviewId, String entityType, String entityId) {
		Optional<Reviews> review1 = null;
		String reviewType = "";
		switch (entityType) {
		case "songs":
			review1 = reviewRepository.findBySongs_SpotifyIdAndId(entityId, reviewId);
			reviewType = "songs";
			reviewRepository.delete(review1.get());
			break;

		case "playlist":
			review1 = reviewRepository.findByPlaylist_SpotifyIdAndId(entityId, reviewId);
			reviewType = "playlist";
			reviewRepository.delete(review1.get());
			break;

		case "albums":
			review1 = reviewRepository.findByAlbums_SpotifyIdAndId(entityId, reviewId);
			reviewType = "albums";
			reviewRepository.delete(review1.get());
			break;

		default:
			System.out.println("Object could not be found to be deleted");
			throw new ResourceNotFoundException("Object could not be found");
		}
		return "Review has been deleted For " + reviewType;
	}

	@Override
	public ReviewDTO getReviewById(long reviewid, String entityType, String entityId) {
		Reviews foundReview = null;
		switch (entityType) {
		case "songs":
			foundReview = reviewRepository.findBySongs_SpotifyIdAndId(entityId, reviewid).get();
			break;
		case "playlist":
			foundReview = reviewRepository.findByPlaylist_SpotifyIdAndId(entityId, reviewid).get();
			break;
		case "albums":
			foundReview = reviewRepository.findByAlbums_SpotifyIdAndId(entityId, reviewid).get();
			break;
		default:
			throw new ResourceNotFoundException("Not the correct entity type");
		}
		ReviewDTO newReview = convertToDTO(foundReview);
		return newReview;
	}

	@Override
	public List<ReviewDTO> getReviews(String entityType, String entityId) {
		List<Reviews> foundReview = null;
		switch (entityType) {
		case "songs":
			foundReview = reviewRepository.findBySongs_SpotifyId(entityId).get();
			break;
		case "playlist":
			foundReview = reviewRepository.findByPlaylist_SpotifyId(entityId).get();
			break;
		case "albums":
			foundReview = reviewRepository.findByAlbums_SpotifyId(entityId).get();
			break;
		default:
			throw new ResourceNotFoundException("Not the correct entity type");
		}
		return foundReview.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private ReviewDTO convertToDTO(Reviews review) {
		ReviewDTO dto = new ReviewDTO();
		// Set DTO properties based on the review entity
		dto.setId(review.getReviewId());
		dto.setName(review.getName());
		dto.setComment(review.getComment());
		dto.setDatePosted(review.getDatePosted());
		dto.setRating(review.getRating());
		return dto;
	}

	@Override
	@Transactional
	public Reviews addReview(Reviews review, String entityId, String entityType, String userId, Object object) {
		Optional<Users> optionalUser = Optional.of(userRepository.findByRefId(userId));
		Optional<Songs> newSongs = null;
		Optional<Albums> newAlbums = null;
		if (optionalUser.isPresent()) {
			Reviews newReview = new Reviews();
			newReview.setUser(optionalUser.get());
			newReview.setName(review.getName());
			newReview.setRating(review.getRating());
			newReview.setComment(review.getComment());
			if(entityType.equals("songs"))
			{
				newSongs = createSongFromSpotifyData(object, optionalUser.get());
				songRepository.save(newSongs.get());
			}
			else if(entityType.equals("albums"))
			{
				newAlbums = createAlbumFromSpotifyData(object, optionalUser.get());
				albumRepository.save(newAlbums.get());
			}
			switch (entityType) {
			case "songs":

				if (newSongs.isPresent()) {
					newReview.setSongs(newSongs.get());
					newSongs.get().getReviews().add(newReview);
					reviewRepository.save(newReview);
					return newReview;
				}
				break;

			case "albums":
				if (newAlbums.isPresent()) {
					newReview.setAlbums(newAlbums.get());
					newAlbums.get().getReviews().add(newReview);
					reviewRepository.save(newReview);
					return newReview;
				}
				break;
				
			default:
				throw new ResourceNotFoundException("Object not found");
			}
		}

		return new Reviews();
	}
	
	public Optional<Songs> createSongFromSpotifyData(Object track1, Users user) {
		Track track = (Track) track1;
	    Optional<Songs> newSongs = Optional.of(new Songs());
	    newSongs.get().setName(track.getName());
	    newSongs.get().setSpotifyId(track.getId());
	    newSongs.get().setUris(track.getUri());
	    newSongs.get().setDuration(track.getDurationMs());
	    newSongs.get().setPreviewUrl(track.getPreviewUrl());
	    newSongs.get().getUsers().add(user);
	    return newSongs;
	}
	
	public Optional<Albums> createAlbumFromSpotifyData(Object album1, Users user) {
		Album album = (Album) album1;
	    Optional<Albums> newAlbums = Optional.of(new Albums());
	    newAlbums.get().setName(album.getName());
	    newAlbums.get().setSpotifyId(album.getId());
	    newAlbums.get().setReleaseDate(album.getReleaseDate());
	    newAlbums.get().setUri(album.getUri());
	    newAlbums.get().setGenres(album.getGenres());
	    newAlbums.get().getUsers().add(user);
	    return newAlbums;
	}

}

