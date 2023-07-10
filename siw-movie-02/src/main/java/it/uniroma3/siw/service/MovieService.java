package it.uniroma3.siw.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.util.FileUploadUtil;

@Service
public class MovieService {
	
	@Autowired MovieRepository movieRepository;
	@Autowired MovieValidator movieValidator;
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Get Movie(s)
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public Iterable<Movie> getAll() {
		return this.movieRepository.findAll();
	}

	@Transactional
	public Movie getMovie(Long id) {
		return this.movieRepository.findById(id).get();
	}

	@Transactional
	public Iterable<Movie> searchMovie(String title) {
		return this.movieRepository.findByTitleContainingIgnoreCase(title);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// New/Update Movie
    //'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public BindingResult validateMovie(Movie movie, BindingResult bindingResult) {
		this.movieValidator.validate(movie, bindingResult);
		return bindingResult;
	}
	
	// Data
	///////

	@Transactional
	public void updateTitle(Movie movie, String title) {
    	movie.setTitle(title);
    	this.movieRepository.save(movie);
	}

	@Transactional
	public void updateYear(Movie movie, Integer year) {
		movie.setYear(year);
    	this.movieRepository.save(movie);
	}
	
	// Cover/Images
	///////////////

	@Transactional
	public void uploadCover(Movie movie, MultipartFile multipartFile) throws IOException {
		// Nome del file
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		
		// Aggiornato il film
        movie.setCover(fileName);
        movie.getImages().add(fileName);
        this.movieRepository.save(movie);
 
        // Upload immagine su folder apposito
        String uploadDir = "src/main/resources/static/uploaded-images/movies/" + movie.getId();
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	}

	@Transactional
	public void updateCover(Movie movie, String coverPath) {
		movie.setCover(coverPath.replace("/src/main/resources/static/uploaded-images/movies/" + movie.getId() + "/", ""));
		this.movieRepository.save(movie);
	}

	@Transactional
	public void addImage(Movie movie, MultipartFile multipartFile) throws IOException {
		// Nome del file
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
    	
    	// Check che il file non sia gia' nella directory
    	if(!movie.getImages().contains(fileName)) {
    		// Aggiornato il film
            movie.getImages().add(fileName);
            this.movieRepository.save(movie);
            
            // Upload immagine su folder apposito
            String uploadDir = "src/main/resources/static/uploaded-images/movies/" + movie.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    	}
	}
	@Transactional
	public void deleteImage(Movie movie, String imagePath) throws IOException {
		// Rimuovo il nome dell'immagine dal campo "images" del film
    	movie.getImages().remove(imagePath.replace("/src/main/resources/static/uploaded-images/movies/" + movie.getId() + "/", ""));
    	
    	// Se l'immagine e' anche la copertina allora la sostituisco con un'altra
    	if (movie.getCoverPath().equals(imagePath)) {
    		for(String newCoverPath : movie.getImagePaths()) {
    			if(newCoverPath!=imagePath) {
    				movie.setCover(newCoverPath.replace("/src/main/resources/static/uploaded-images/movies/" + movie.getId() + "/", ""));
    			}
    		}
    	}
    	
    	this.movieRepository.save(movie);
    	
    	// Cancello il file
    	Path deleteMe = Paths.get(imagePath.replace("/src","src"));
    	Files.deleteIfExists(deleteMe);
	}
	
	// Director/Actors
	//////////////////

	@Transactional
	public void updateDirector(Movie movie, Artist artist) {
		movie.setDirector(artist);
		this.movieRepository.save(movie);
	}
	@Transactional
	public void removeDirector(Movie movie, Artist artist) {
		movie.setDirector(null);
		this.movieRepository.save(movie);
	}

	@Transactional
	public void addActor(Movie movie, Artist artist) {
		movie.getActors().add(artist);
		this.movieRepository.save(movie);
	}
	@Transactional
	public void removeActor(Movie movie, Artist artist) {
		movie.getActors().remove(artist);
		this.movieRepository.save(movie);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Delete Movie
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public void deleteMovie(Movie movie) throws IOException {
		// Cancello la directory del film
    	FileUtils.deleteDirectory(new File("src/main/resources/static/uploaded-images/movies/" + movie.getId()));
    	
    	// Cancello il film dal repository
    	this.movieRepository.delete(movie);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Others
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	// Per cancellare un'artista dal sistema
	@Transactional
	public void removeArtistFromMovies(Artist artist) {
		// Rimuovo l'artista dai campi director e actors dei film
    	Iterable<Movie> movies = this.movieRepository.findAll();
    	for(Movie movie : movies) {
    		if(movie.getDirector()==artist)
    			movie.setDirector(null);
    		if(movie.getActors()!=null && movie.getActors().contains(artist))
    			movie.getActors().remove(artist);
    	}
	}
	
}
