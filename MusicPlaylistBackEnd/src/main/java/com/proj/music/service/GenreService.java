package com.proj.music.service;

import java.util.List;

import com.proj.music.entity.Genres;

public interface GenreService {
	String updateGenreById(long id, Genres genre);

	String deleteGenreById(long id);

	Genres getGenreById(long id);

	String addGenre(Genres genre);

	List<Genres> getGenres();
	
	Genres findGenreBySpotifyId(String genreSpotifyId);
}