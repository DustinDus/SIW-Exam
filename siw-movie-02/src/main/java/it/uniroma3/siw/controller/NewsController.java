package it.uniroma3.siw.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.News;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.NewsService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class NewsController {
	
	@Autowired NewsService newsService;
	@Autowired MovieService movieService;
	@Autowired ArtistService artistService;
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Visualizza Notizie
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@GetMapping("/news")
	public String getNewsList(Model model) {
		model.addAttribute("newsList", this.newsService.getAll());
		return "newsList.html";
	}
	
	@GetMapping("/news/{id}")
	public String getNews(@PathVariable("id") Long id, Model model) {
		model.addAttribute("news", this.newsService.getNews(id));
		return "news.html";
	}
	
	@PostMapping("/searchNews")
	public String searchNews(@RequestParam("input") String headline, Model model) {
		model.addAttribute("newsList", this.newsService.searchNews(headline));
		return "newsList.html";
	}
	
	// Che includono film
	@GetMapping("/movieNews")
	public String getMovieNews(Model model) {
		model.addAttribute("newsList", this.newsService.getAllWithTaggedMovies());
		model.addAttribute("moviesOnly", "moviesOnly");
		return "newsList.html";
	}
	@PostMapping("/searchMovieNews")
	public String searchMovieNews(@RequestParam("input") String headline, Model model) {
		model.addAttribute("newsList", this.newsService.searchNewsWithTaggedMovies(headline));
		model.addAttribute("moviesOnly", "moviesOnly");
		return "newsList.html";
	}
	
	// Che includono artisti
	@GetMapping("/artistNews")
	public String getArtistNews(Model model) {
		model.addAttribute("newsList", this.newsService.getAllWithTaggedArtists());
		model.addAttribute("artistsOnly", "artistsOnly");
		return "newsList.html";
	}
	@PostMapping("/searchArtistNews")
	public String searchArtistNews(@RequestParam("input") String headline, Model model) {
		model.addAttribute("newsList", this.newsService.searchNewsWithTaggedArtists(headline));
		model.addAttribute("artistsOnly", "artistsOnly");
		return "newsList.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Aggiungi Notizia
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@GetMapping("/formNewNews")
	public String formNewNews(Model model) {
		model.addAttribute("news", new News());
		return "formNewNews.html";
	}
	
	@PostMapping("/newNews")
	public String newNews(@Valid @ModelAttribute("news") News news, BindingResult bindingResult, 
                          @RequestParam("fileImage") MultipartFile multipartFile, Model model) throws IOException {
		
		if(bindingResult.hasErrors())
			return "formNewNews.html";
		
		news.setTime(LocalDate.now());
		
		if(!multipartFile.isEmpty())
		    this.newsService.uploadImage(news, multipartFile);
		else
			this.newsService.saveNews(news);
	    
		model.addAttribute("news", news);
		return "news.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Gestisci Notizia
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@GetMapping("/manageNews")
	public String manageNews(Model model) {
		model.addAttribute("newsList", this.newsService.getAll());
		return "manageNews.html";
	}
	
	@GetMapping("/formUpdateNews/{id}")
	public String formUpdateNews(@PathVariable("id") Long id, Model model) {
		model.addAttribute("news", this.newsService.getNews(id));
		return "formUpdateNews.html";
	}
	
	@PostMapping("/searchManageNews")
	public String searchManageNews(@RequestParam("input") String headline, Model model) {
		model.addAttribute("news", this.newsService.searchNews(headline));
		return "manageNews.html";
	}
	
	// Dati
	///////
	
	@GetMapping("/changeNewsData/{id}")
	public String changeNewsData(@PathVariable("id") Long id, Model model) {
		model.addAttribute("news", this.newsService.getNews(id));
		return "changeNewsData.html";
	}
	
	@PostMapping("/setNewsHeadline/{id}")
	public String setNewsHeadline(@RequestParam("headline") String headline, @PathVariable("id") Long id, Model model) {
		News news = this.newsService.getNews(id);
		
		this.newsService.updateHeadline(news, headline);
		
		model.addAttribute("news", news);
		return "changeNewsData.html";
	}
	
	@PostMapping("/setNewsText/{id}")
	public String setNewsText(@RequestParam("text") String text, @PathVariable("id") Long id, Model model) {
		News news = this.newsService.getNews(id);
		
		this.newsService.updateText(news, text);
		
		model.addAttribute("news", news);
		return "changeNewsData.html";
	}
	
	// Entita' correlate
	////////////////////
	
	// Film
	@GetMapping("/moviesToTag/{id}")
	public String moviesToTag(@PathVariable("id") Long id, Model model) {
		model.addAttribute("news", this.newsService.getNews(id));
		model.addAttribute("movies", this.movieService.getAll());
		return "moviesToTag.html";
	}
	
	@PostMapping("/addMovieToNews/{idMovie}/{idNews}")
	public String addMovieToNews(@PathVariable("idMovie") Long idMovie, @PathVariable("idNews") Long idNews, Model model) {
		Movie movie = this.movieService.getMovie(idMovie);
		News news = this.newsService.getNews(idNews);
		
		this.newsService.addMovie(news, movie);
		
		model.addAttribute("news", news);
		model.addAttribute("movies", this.movieService.getAll());
		return "moviesToTag.html";
	}
	
	@PostMapping("/removeMovieFromNews/{idMovie}/{idNews}")
	public String removeMovieFromNews(@PathVariable("idMovie") Long idMovie, @PathVariable("idNews") Long idNews, Model model) {
		Movie movie = this.movieService.getMovie(idMovie);
		News news = this.newsService.getNews(idNews);
		
		this.newsService.removeMovie(news, movie);
		
		model.addAttribute("news", news);
		model.addAttribute("movies", this.movieService.getAll());
		return "moviesToTag.html";
	}
	
	// Artisti
	@GetMapping("/artistsToTag/{id}")
	public String artistsToTag(@PathVariable("id") Long id, Model model) {
		model.addAttribute("news", this.newsService.getNews(id));
		model.addAttribute("artists", this.artistService.getAll());
		return "artistsToTag.html";
	}
	
	@PostMapping("/addArtistToNews/{idArtist}/{idNews}")
	public String addArtistToNews(@PathVariable("idArtist") Long idArtist, @PathVariable("idNews") Long idNews, Model model) {
		Artist artist = this.artistService.getArtist(idArtist);
		News news = this.newsService.getNews(idNews);
		
		this.newsService.addArtist(news, artist);
		
		model.addAttribute("news", news);
		model.addAttribute("artists", this.artistService.getAll());
		return "artistsToTag.html";
	}
	
	@PostMapping("/removeArtistFromNews/{idArtist}/{idNews}")
	public String removeArtistFromNews(@PathVariable("idArtist") Long idArtist, @PathVariable("idNews") Long idNews, Model model) {
		Artist artist = this.artistService.getArtist(idArtist);
		News news = this.newsService.getNews(idNews);
		
		this.newsService.removeArtist(news, artist);
		
		model.addAttribute("news", news);
		model.addAttribute("artists", this.artistService.getAll());
		return "artistsToTag.html";
	}
	
	// Immagine
	///////////
	
	@GetMapping("/changeNewsImage/{id}")
	public String changeNewsImage(@PathVariable("id") Long id, Model model) {
		model.addAttribute("news", this.newsService.getNews(id));
		return "changeNewsImage.html";
	}
	
	@PostMapping("/setNewsImage/{id}")
	public String setNewsImage(@RequestParam("fileImage") MultipartFile multipartFile, @PathVariable("id") Long id, Model model) throws IOException {
		News news = this.newsService.getNews(id);
		
		if(news.getImage()!=null)
		    this.newsService.deleteImage(news, multipartFile);
		
		if(!multipartFile.isEmpty())
		    this.newsService.uploadImage(news, multipartFile);
		else
			this.newsService.saveNews(news);
		
		model.addAttribute("news", news);
		return "changeNewsImage.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Cancella Notizia
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@Transactional
	@PostMapping("/deleteNews/{id}")
	public String deleteNews(@PathVariable("id") Long id, Model model) throws IOException {
		News news = this.newsService.getNews(id);
		
		this.newsService.deleteNews(news);
		
		model.addAttribute("newsList", this.newsService.getAll());
		return "manageNews.html";
	}
	
}
