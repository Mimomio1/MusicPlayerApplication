package com.proj.music.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.music.entity.Songs;

public interface SongRepository extends JpaRepository<Songs, Long> {
	void deleteBySpotifyId(String spotifyId);
	
	Boolean existsBySpotifyId(String spotifyId);

	List<Songs> getSongsByUris(String uris);
	
	Optional<Songs> findSongBySpotifyId(String spotifyId);
	
//	List<Songs> findByNameContainingAndType(String name, String type);
}
