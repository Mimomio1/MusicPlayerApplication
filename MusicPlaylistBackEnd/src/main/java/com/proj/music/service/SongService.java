package com.proj.music.service;

import java.util.List;

import com.proj.music.entity.Songs;

import se.michaelthelin.spotify.model_objects.specification.Track;

public interface SongService {
	String updateSongById(long id, Songs song);

	String deleteSongById(long id);

	Songs getSongById(long id);

	String addSong(Track song, String userId);

	List<Songs> getSongs();
	
	String deleteBySpotifyId(String spotifyId);
	
	Boolean existsBySpotifyId(String spotifyId);

	Songs getSongBySpotifyId(String spotifyId);
}
