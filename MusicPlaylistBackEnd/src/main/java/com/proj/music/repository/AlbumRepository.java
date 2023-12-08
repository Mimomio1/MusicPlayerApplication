package com.proj.music.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.music.entity.Albums;

public interface AlbumRepository extends JpaRepository<Albums, Long> {
	Optional<Albums> findAlbumBySpotifyId(String spotifyId);
}
