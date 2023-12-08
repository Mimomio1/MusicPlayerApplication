package com.proj.music.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.music.entity.Playlists;

public interface PlaylistRepository extends JpaRepository<Playlists, Long> {
	
//	Playlists findPlaylistById(String spotifyPlaylistId);
	
	Optional<Playlists> findPlaylistBySpotifyId(String spotifyId);
	
	Boolean existsBySpotifyId(String spotifyId);
}
