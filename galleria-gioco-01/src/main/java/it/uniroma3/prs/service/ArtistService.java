package it.uniroma3.prs.service;

import java.io.File;
import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.prs.controller.validator.ArtistValidator;
import it.uniroma3.prs.repository.ArtistRepository;
import it.uniroma3.prs.util.FileUploadUtil;
import it.uniroma3.prs.model.Artist;
import it.uniroma3.prs.model.Movement;
import it.uniroma3.prs.model.Work;

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
	public Iterable<Artist> searchArtists(String name) {
		return this.artistRepository.findByNameContainingIgnoreCase(name);
	}

	@Transactional
	public Iterable<Artist> getAllByBirth() {
		return this.artistRepository.findAllByOrderByBirthAsc();
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
	public void saveArtist(Artist artist) {
		this.artistRepository.save(artist);
	}

	@Transactional
	public void addMovement(Artist artist, Movement movement) {
		artist.getMovements().add(movement);
	    this.artistRepository.save(artist);
	}
	@Transactional
	public void removeMovement(Artist artist, Movement movement) {
		artist.getMovements().remove(movement);
	    this.artistRepository.save(artist);
	}

	@Transactional
	public void addWork(Artist artist, Work work) {
		artist.getWorks().add(work);
	    this.artistRepository.save(artist);
	}
	@Transactional
	public void removeWork(Artist artist, Work work) {
		artist.getWorks().remove(work);
	    this.artistRepository.save(artist);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Delete Artist
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public void deleteArtist(Artist artist) throws IOException {
		// Cancello la directory dell'artista
    	FileUtils.deleteDirectory(new File("src/main/resources/static/uploaded-images/artists/" + artist.getId()));
		
		// Rimuovo l'artista dal repository
		this.artistRepository.delete(artist);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Others
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	// Rimuove il movimento dal campo movements degli artisti
	@Transactional
	public void deleteMovement(Movement movement) {
		Iterable<Artist> artists = this.artistRepository.findAll();
		for(Artist artist : artists) {
			artist.getMovements().remove(movement);
		}
	}
	
	// Rimuove l'opera dal campo works degli artisti
	@Transactional
	public void deleteWork(Work work) {
		Iterable<Artist> artists = this.artistRepository.findAll();
		for(Artist artist : artists) {
			artist.getWorks().remove(work);
		}
	}
	
	// Gioco
	////////
	
	// Valuta se e' possibile giocare
	@Transactional
	public boolean enoughArtists() {
		return this.artistRepository.count()>3;
	}
	// Artista random
	@Transactional
	public Artist getRandomArtist() {
		return this.artistRepository.findRandomArtist().get(0);
	}
	
}
