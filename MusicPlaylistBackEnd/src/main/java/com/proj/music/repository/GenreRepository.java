package com.proj.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.music.entity.Genres;

public interface GenreRepository extends JpaRepository<Genres, Long> {
	Genres findGenreByGenreSpotifyId(String genreSpotifyId);
}
