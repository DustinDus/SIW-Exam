package it.uniroma3.siw.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.util.FileUploadUtil;

@Service
public class ArtistService {
	
	@Autowired ArtistRepository artistRepository;
	@Autowired ArtistValidator artistValidator;
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Get Artist(s)
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public Iterable<Artist> getAll() {
		return this.artistRepository.findAll();
	}

	@Transactional
	public Artist getArtist(Long id) {
		return this.artistRepository.findById(id).get();
	}

	@Transactional
	public Iterable<Artist> searchArtists(String fullName) {
		// Cerco artisti con piu' metodi differenti
		List<Artist> artists = new ArrayList<Artist>();
		artists.addAll(this.artistRepository.findByNameContainingIgnoreCase(fullName));
		artists.addAll(this.artistRepository.findBySurnameContainingIgnoreCase(fullName));
		artists.addAll(this.artistRepository.findByNameAndSurname(fullName));
		artists.addAll(this.artistRepository.findBySurnameAndName(fullName));
		
		// Elimino eventuali duplicati
		artists = new ArrayList<Artist>(new HashSet<Artist>(artists));
		
		return artists;
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// New/Update Artist
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public BindingResult validateArtist(Artist artist, BindingResult bindingResult) {
		this.artistValidator.validate(artist, bindingResult);
		return bindingResult;
	}

	@Transactional
	public void updateName(Artist artist, String name) {
		artist.setName(name);
		this.artistRepository.save(artist);
	}

	@Transactional
	public void updateSurname(Artist artist, String surname) {
		artist.setSurname(surname);
		this.artistRepository.save(artist);
	}

	@Transactional
	public void updateBirth(Artist artist, Integer birth) {
		artist.setBirth(birth);
		this.artistRepository.save(artist);
	}

	@Transactional
	public void updateDeath(Artist artist, Integer death) {
		artist.setDeath(death);
		this.artistRepository.save(artist);
	}

	@Transactional
	public void uploadImage(Artist artist, MultipartFile multipartFile) throws IOException {
		// Nome del file
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		
		// Aggiornato l'artista
        artist.setImage(fileName); 
        this.artistRepository.save(artist);
 
        // Upload immagine su folder apposito
        String uploadDir = "src/main/resources/static/uploaded-images/artists/" + artist.getId();
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	}

	@Transactional
	public void deleteImage(Artist artist, MultipartFile multipartFile) throws IOException {
		String imagePath = artist.getImagePath();
		Path deleteMe = Paths.get(imagePath.replace("/src","src"));
    	Files.deleteIfExists(deleteMe);
	}
	
	// Metodi utili a controller diversi da ArtistController
	////////////////////////////////////////////////////////
	
	// Per aggiungere un film al campo directed di un artista
	@Transactional
	public void addMovieToArtistsDirected(Movie movie, Artist artist) {
		artist.getDirected().add(movie);
		this.artistRepository.save(artist);
	}
	// Per rimuovere un film dal campo directed di un artista
	@Transactional
	public void removeMovieFromArtistDirected(Movie movie, Artist artist) {
		artist.getDirected().remove(movie);
		this.artistRepository.save(artist);
	}
	
	// Per aggiungere un film al campo acted di un artista
	@Transactional
	public void addMovieToArtistActed(Movie movie, Artist artist) {
		artist.getActed().add(movie);
		this.artistRepository.save(artist);
	}
	// Per rimuovere un film dal campo acted di un artista
	@Transactional
	public void removeMovieFromArtistActed(Movie movie, Artist artist) {
		artist.getActed().remove(movie);
		this.artistRepository.save(artist);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Delete Artist
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public void deleteArtist(Artist artist) throws IOException {
		// Cancello la directory dell'artista
    	FileUtils.deleteDirectory(new File("src/main/resources/static/uploaded-images/artists/" + artist.getId()));
    	
    	// Cancello l'artista dal repository
    	this.artistRepository.delete(artist);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Others
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	// Per cancellare un film dal sistema
	@Transactional
	public void removeMovieFromArtists(Movie movie) {
		// Rimuovo il film dai campi directed e acted degli artisti
    	Iterable<Artist> artists = this.artistRepository.findAll();
    	for(Artist artist : artists) {
    		if(artist.getDirected()!=null && artist.getDirected().contains(movie))
    			artist.getDirected().remove(movie);
    		if(artist.getActed()!=null && artist.getActed().contains(movie))
    			artist.getActed().remove(movie);
    	}
	}

}
