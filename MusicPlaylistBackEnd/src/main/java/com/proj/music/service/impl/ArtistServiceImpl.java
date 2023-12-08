package com.proj.music.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proj.music.entity.Artists;
import com.proj.music.exceptions.ResourceNotFoundException;
import com.proj.music.repository.ArtistRepository;
import com.proj.music.service.ArtistService;

import se.michaelthelin.spotify.model_objects.specification.Artist;

@Service
public class ArtistServiceImpl implements ArtistService {

	@Autowired
	private ArtistRepository artistRepository;

	@Override
	public String updateArtistById(long id, Artist artist) {

		Optional<Artists> artist1 = artistRepository.findById(id);

		if (artist1.isPresent()) {
			artist1.get().setName(artist.getName());
			artist1.get().setGenres(artist.getGenres());
			artist1.get().setHref(artist.getHref());
			artist1.get().setImages(artist.getImages());
			artist1.get().setPopularity(artist.getPopularity());
			artist1.get().setUri(artist.getUri());
		}

		return "Artist has been updated";
	}

	@Override
	public String deleteArtistById(long id) {
		Optional<Artists> artist1 = artistRepository.findById(id);

		if (artist1.isPresent()) {
			artistRepository.deleteById(id);
		}
		return "Artist has been deleted";
	}

	@Override
	public Artists getArtistById(long id) {
		return artistRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Artist has not been found"));
	}

	@Override
	public String addArtist(Artist artist) {
		Artists newArtist = new Artists();
		newArtist.setName(artist.getName());
		newArtist.setSpotifyId(artist.getId());
		newArtist.setPopularity(artist.getPopularity());
		newArtist.setUri(artist.getUri());
		newArtist.setHref(artist.getHref());
		newArtist.setGenres(artist.getGenres());
		newArtist.setImages(artist.getImages());
		artistRepository.save(newArtist);
		return "Artist has been added";
	}

	@Override
	public List<Artists> getArtists() {
		return artistRepository.findAll();
	}

	@Override
	public Artists findArtistBySpotifyId(String id) {
		return artistRepository.findArtistBySpotifyId(id);
	}

	@Override
	public String deleteArtistBySpotifyId(String id) {
		artistRepository.deleteArtistBySpotifyId(id);
		return "Artist has been deleted";
	}
}
