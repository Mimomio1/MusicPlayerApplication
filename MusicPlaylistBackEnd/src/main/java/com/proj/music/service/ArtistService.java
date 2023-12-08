package com.proj.music.service;

import java.util.List;

import com.proj.music.entity.Artists;

import se.michaelthelin.spotify.model_objects.specification.Artist;

public interface ArtistService {
	String updateArtistById(long id, Artist artist);

	String deleteArtistById(long id);

	Artists getArtistById(long id);

	String addArtist(Artist artist);

	List<Artists> getArtists();
	
	Artists findArtistBySpotifyId(String id);
	
	String deleteArtistBySpotifyId(String id);
}
