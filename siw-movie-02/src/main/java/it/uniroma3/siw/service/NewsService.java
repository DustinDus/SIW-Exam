package it.uniroma3.siw.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.News;
import it.uniroma3.siw.repository.NewsRepository;
import it.uniroma3.siw.util.FileUploadUtil;

@Service
public class NewsService {

	@Autowired NewsRepository newsRepository;
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Get News( list)
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public Iterable<News> getAll() {
		return this.newsRepository.findAllByOrderByTimeDesc();
	}

	@Transactional
	public News getNews(Long id) {
		return this.newsRepository.findById(id).get();
	}

	@Transactional
	public Iterable<News> searchNews(String headline) {
		return this.newsRepository.findByHeadlineContainingIgnoreCase(headline);
	}
	
	// Che includono film taggati
	@Transactional
	public Iterable<News> getAllWithTaggedMovies() {
		return this.newsRepository.findAllByTaggedMoviesNotNullOrderByTimeDesc();
	}
	@Transactional
	public Iterable<News> searchNewsWithTaggedMovies(String headline) {
		return this.newsRepository.findByTaggedMoviesNotNullAndHeadlineContainingIgnoreCase(headline);
	}
	
	// Che includono artisti taggati
	@Transactional
	public Iterable<News> getAllWithTaggedArtists() {
		return this.newsRepository.findAllByTaggedArtistsNotNullOrderByTimeDesc();
	}
	@Transactional
	public Iterable<News> searchNewsWithTaggedArtists(String headline) {
		return this.newsRepository.findByTaggedArtistsNotNullAndHeadlineContainingIgnoreCase(headline);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// New/Update News
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public void uploadImage(News news, MultipartFile multipartFile) throws IOException {
		// Nome del file
	    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
	     // Aggiornata la notizia
        news.setImage(fileName); 
	    this.newsRepository.save(news);
	
	    // Upload immagine su folder apposito
	    String uploadDir = "src/main/resources/static/uploaded-images/news/" + news.getId();
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	}

	@Transactional
	public void saveNews(News news) {
		this.newsRepository.save(news);
	}

	@Transactional
	public void updateHeadline(News news, String headline) {
		news.setHeadline(headline);
		this.newsRepository.save(news);
	}

	@Transactional
	public void updateText(News news, String text) {
		news.setText(text);
		this.newsRepository.save(news);
	}

	@Transactional
	public void addMovie(News news, Movie movie) {
		List<Movie> taggedMovies = news.getTaggedMovies();
		if(!taggedMovies.contains(movie)) {
			taggedMovies.add(movie);
			this.newsRepository.save(news);
		}
	}
	@Transactional
	public void removeMovie(News news, Movie movie) {
		List<Movie> taggedMovies = news.getTaggedMovies();
		if(taggedMovies.contains(movie)) {
			taggedMovies.remove(movie);
			this.newsRepository.save(news);
		}
	}

	@Transactional
	public void addArtist(News news, Artist artist) {
		List<Artist> taggedArtists = news.getTaggedArtists();
		if(!taggedArtists.contains(artist)) {
			taggedArtists.add(artist);
			this.newsRepository.save(news);
		}
	}
	@Transactional
	public void removeArtist(News news, Artist artist) {
		List<Artist> taggedArtists = news.getTaggedArtists();
		if(taggedArtists.contains(artist)) {
			taggedArtists.remove(artist);
			this.newsRepository.save(news);
		}
	}

	@Transactional
	public void deleteImage(News news, MultipartFile multipartFile) throws IOException {
		String imagePath = news.getImagePath();
	    Path deleteMe = Paths.get(imagePath.replace("/src","src"));
	    Files.deleteIfExists(deleteMe);
	    news.setImage(null);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Delete News
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public void deleteNews(News news) throws IOException {
		// Cancello la directory della notizia
		FileUtils.deleteDirectory(new File("src/main/resources/static/uploaded-images/news/" + news.getId()));
		
		// Cancello la notizia
		this.newsRepository.delete(news);
	}
	
}
