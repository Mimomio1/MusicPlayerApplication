package com.proj.music.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proj.music.entity.Genres;
import com.proj.music.exceptions.ResourceNotFoundException;
import com.proj.music.repository.GenreRepository;
import com.proj.music.service.GenreService;

@Service
public class GenreServiceImpl implements GenreService {

	@Autowired
	private GenreRepository genreRepository;
	
	@Override
	public String updateGenreById(long id, Genres genre) {
		Optional<Genres> genre1 = genreRepository.findById(id);
		
		if(genre1.isPresent())
		{
			genre1.get().setId(genre.getId());
			genre1.get().setName(genre.getName());
			genre1.get().setDescription(genre.getDescription());
		}
		
		return "Genre has been updated";
	}

	@Override
	public String deleteGenreById(long id) {
		Optional<Genres> genre1 = genreRepository.findById(id);
		
		if(genre1.isPresent())
		{
			genreRepository.deleteById(id);
		}
		
		return "Genre has been deleted";
	}

	@Override
	public Genres getGenreById(long id) {
		return genreRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Genre Object has not been found."));
	}

	@Override
	public String addGenre(Genres genre) {
		genreRepository.save(genre);
		return "Genre has been added";
	}

	@Override
	public List<Genres> getGenres() {
		return genreRepository.findAll();
	}

	@Override
	public Genres findGenreBySpotifyId(String genreSpotifyId) {
		return genreRepository.findGenreByGenreSpotifyId(genreSpotifyId);
	}

}