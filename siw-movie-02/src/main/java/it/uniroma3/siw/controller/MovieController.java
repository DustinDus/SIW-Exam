package it.uniroma3.siw.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller 
public class MovieController {
	
	@Autowired MovieService movieService;
	@Autowired ArtistService artistService;
    
	@Autowired UserService userService;
    @Autowired CredentialsService credentialsService;
    @Autowired ReviewService reviewService;
    
    @Autowired MessageSource messageSource;
    
    //,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Visualizza/Cerca Film
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
    
    @GetMapping("/movies")
    public String getMovies(Model model) {
        model.addAttribute("movies", this.movieService.getAll());
        return "movies.html";
    }
    
    @GetMapping("/movies/{id}")
    public String getMovie(@PathVariable("id") Long id, Model model, Principal principal) {
    	Movie movie = this.movieService.getMovie(id);
    	
    	if(principal!=null) {
    		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    		User user = this.credentialsService.getCredentials(userDetails.getUsername()).getUser();
        	model.addAttribute("hasReviewed", this.reviewService.userHasReviewed(user, movie));
    	}
    	model.addAttribute("reviews", this.reviewService.getReviews(movie));
        model.addAttribute("movie", movie);
        return "movie.html";
    }
    
    @PostMapping("/searchMovies")
    public String searchMovies(@RequestParam("input") String title, Model model) {
        model.addAttribute("movies", this.movieService.searchMovie(title));
        return "movies.html";
    }
    
    //,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Recensioni
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
    
    @PostMapping("/addReview/{username}/{idMovie}")
    public String addReview(@PathVariable("username") String username, @PathVariable("idMovie") Long idMovie,
    		                @RequestParam("headline") String headline, @RequestParam("rating") Integer rating, @RequestParam("text") String text,
                            Model model) {
    	
    	User user = this.credentialsService.getCredentials(username).getUser();
    	Movie movie = this.movieService.getMovie(idMovie);
    	
    	model.addAttribute("hasReviewed", true);
    	model.addAttribute("movie", movie);
    	
    	boolean hasReviewed = this.reviewService.userHasReviewed(user, movie);
    	if(hasReviewed) {
    		model.addAttribute("reviews", this.reviewService.getReviews(movie));
    		return "movie.html";
    	}
    	else {
    	    this.reviewService.newReview(user, movie, username, headline, rating, text);
    	    model.addAttribute("reviews", this.reviewService.getReviews(movie));
		    return "movie.html";
    	}
    }
    
    @PostMapping("/deleteReviewFromMovie/{idReview}/{idObject}")
    public String deleteReviewFromMovie(@PathVariable("idReview") Long idReview, @PathVariable("idObject") Long idObject, Model model) {
    	
    	Movie movie = this.movieService.getMovie(idObject);
    	model.addAttribute("movie", movie);
    	
    	UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = this.credentialsService.getCredentials(userDetails.getUsername()).getUser();
    	
    	if(!this.reviewService.reviewExists(idReview)) {
    		model.addAttribute("hasReviewed", this.reviewService.userHasReviewed(user, movie));
    		model.addAttribute("reviews", this.reviewService.getReviews(movie));
    		return "movie.html";
    	}
    	else {
    		Review review = this.reviewService.getReview(idReview);
    		this.userService.removeReview(user, review);
    	    this.reviewService.deleteReview(review);
    	    model.addAttribute("hasReviewed", this.reviewService.userHasReviewed(user, movie));
    	    model.addAttribute("reviews", this.reviewService.getReviews(movie));
		    return "movie.html";
    	}
    }
    
    //,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Aggiungi Film
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
    
    @GetMapping("/formNewMovie")
    public String formNewMovie(Model model) {
        model.addAttribute("movie", new Movie());
        return "formNewMovie.html";
    }

    @PostMapping("/newMovie")
    public String newMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult,
    		               @RequestParam("fileImage") MultipartFile multipartFile, Model model) throws IOException {
    	
    	if (this.movieService.validateMovie(movie, bindingResult).hasErrors())
    		return "formNewMovie.html";
    	
    	this.movieService.uploadCover(movie, multipartFile);
    	
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = this.credentialsService.getCredentials(userDetails.getUsername()).getUser();
    	model.addAttribute("hasReviewed", this.reviewService.userHasReviewed(user, movie));
        model.addAttribute("reviews", this.reviewService.getReviews(movie));
        model.addAttribute("movie", movie);
        return "movie.html";
    }
    
    //,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Gestisci Film
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
    
    @GetMapping("/manageMovies")
    public String manageMovies(Model model) {
    	model.addAttribute("movies", this.movieService.getAll());
    	return "manageMovies.html";
    }
    
    @GetMapping("/formUpdateMovie/{id}")
    public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
    	model.addAttribute("movie", this.movieService.getMovie(id));
    	return "formUpdateMovie.html";
    }
    
    @PostMapping("/searchManageMovies")
    public String searchManageMovies(@RequestParam("input") String title, Model model) {
        model.addAttribute("movies", this.movieService.searchMovie(title));
        return "manageMovies.html";
    }
    
    // Dati
    ///////
    
    @GetMapping("/changeMovieData/{id}")
    public String changeMovieData(@PathVariable("id") Long id, Model model) {
    	model.addAttribute("movie", this.movieService.getMovie(id));
    	return "changeMovieData.html";
    }
    
    @PostMapping("/setMovieTitle/{id}")
    public String setMovieTitle(@RequestParam("title") String title, @PathVariable("id") Long id, Model model) {
    	Movie movie = this.movieService.getMovie(id);
    	this.movieService.updateTitle(movie, title);
    	
    	model.addAttribute("movie", movie);
    	return "changeMovieData.html";
    }
    
    @PostMapping("/setMovieYear/{id}")
    public String setMovieYear(@RequestParam("year") Integer year, @PathVariable("id") Long id, Model model) {
    	Movie movie = this.movieService.getMovie(id);
    	
    	// Check errori
    	if(year<1888) {
    		model.addAttribute("year_error", this.messageSource.getMessage("Min.year", null, Locale.getDefault()));
    		model.addAttribute("movie", movie);
        	return "changeMovieData.html";
    	}
    	if(year>2023) {
    		model.addAttribute("year_error", this.messageSource.getMessage("Max.year", null, Locale.getDefault()));
    		model.addAttribute("movie", movie);
        	return "changeMovieData.html";
    	}
    	
    	this.movieService.updateYear(movie, year);
    	
    	model.addAttribute("movie", movie);
    	return "changeMovieData.html";
    }
    
    // Immagini
    ///////////
    
    @GetMapping("/manageMovieImages/{id}")
	public String manageMovieImages(@PathVariable("id") Long id, Model model) {
    	Movie movie = this.movieService.getMovie(id);
    	
    	model.addAttribute("hasSeveralImages", movie.getImages().size()>1);
		model.addAttribute("movie", movie);
    	return "manageMovieImages.html";
	}
    
    @PostMapping("/setMovieCover/{id}")
	public String setMovieCover(@RequestParam("coverPath") String coverPath, @PathVariable("id") Long id, Model model) throws IOException {
		Movie movie = this.movieService.getMovie(id);
		this.movieService.updateCover(movie, coverPath);
    	
		model.addAttribute("hasSeveralImages", movie.getImages().size()>1);
        model.addAttribute("movie", movie);
        return "manageMovieImages.html";
	}
    
    @PostMapping("/addImageToMovie/{id}")
	public String addImageToMovie(@RequestParam("fileImage") MultipartFile multipartFile, @PathVariable("id") Long id, Model model) throws IOException {
    	Movie movie = this.movieService.getMovie(id);
    	this.movieService.addImage(movie, multipartFile);
        
        model.addAttribute("hasSeveralImages", movie.getImages().size()>1);
        model.addAttribute("movie", movie);
        return "manageMovieImages.html";
    }
    
    @PostMapping("/deleteImageFromMovie/{id}")
    public String deleteImageFromMovie(@RequestParam("imagePath") String imagePath, @PathVariable("id") Long id, Model model) throws IOException {
    	Movie movie = this.movieService.getMovie(id);
    	this.movieService.deleteImage(movie, imagePath);
    	
    	model.addAttribute("hasSeveralImages", movie.getImages().size()>1);
    	model.addAttribute("movie", movie);
        return "manageMovieImages.html";
    }
    
    // Direttore del Film
    /////////////////////
    
    @GetMapping("/updateDirectorOfMovie/{id}")
    public String updateDirectorOfMovie(@PathVariable("id") Long id, Model model) {
    	model.addAttribute("movie", this.movieService.getMovie(id));
    	model.addAttribute("artists", this.artistService.getAll());
    	return "directorsToAdd.html";
    }
    
    @PostMapping("/setDirectorToMovie/{idDirector}/{idMovie}")
    public String setDirectorToMovie(@PathVariable("idDirector") Long idDirector, @PathVariable("idMovie") Long idMovie, Model model) {
    	Movie movie = this.movieService.getMovie(idMovie);
    	Artist artist = this.artistService.getArtist(idDirector);
    	
    	// Aggiorno il film
    	this.movieService.updateDirector(movie, artist);
    	
    	// Aggiorno l'artista
    	this.artistService.addMovieToArtistsDirected(movie, artist);
    	
    	model.addAttribute("movie", movie);
    	return "formUpdateMovie.html";
    }
    
    @PostMapping("/removeDirectorFromMovie/{idDirector}/{idMovie}")
    public String removeDirectorFromMovie(@PathVariable("idDirector") Long idDirector, @PathVariable("idMovie") Long idMovie, Model model) {
    	Movie movie = this.movieService.getMovie(idMovie);
    	Artist artist = this.artistService.getArtist(idDirector);
    	
    	// Aggiorno il film
    	this.movieService.removeDirector(movie, artist);
    	
    	// Aggiorno l'artista
    	this.artistService.removeMovieFromArtistDirected(movie, artist);
    	
    	model.addAttribute("movie", movie);
    	return "formUpdateMovie.html";
    }
    
    // Attori del Film
    //////////////////
    
    @GetMapping("/updateActorsOnMovie/{id}")
    public String updateActorsOnMovie(@PathVariable("id") Long id, Model model) {
    	model.addAttribute("movie", this.movieService.getMovie(id));
    	model.addAttribute("artists", this.artistService.getAll());
    	return "actorsToAdd.html";
    }
    
    @PostMapping("/addActorToMovie/{idActor}/{idMovie}")
    public String addActorToMovie(@PathVariable("idActor") Long idActor, @PathVariable("idMovie") Long idMovie, Model model) {
    	Movie movie = this.movieService.getMovie(idMovie);
    	Artist artist = this.artistService.getArtist(idActor);
    	
    	if(!movie.getActors().contains(artist)) {
    	    // Aggiorno il film
    		this.movieService.addActor(movie, artist);
    		
    		// Aggiorno l'artista
    		this.artistService.addMovieToArtistActed(movie, artist);
    	}
    	
    	model.addAttribute("movie", movie);
    	model.addAttribute("artists", this.artistService.getAll());
    	return "actorsToAdd.html";
    }
    
    @PostMapping("/removeActorFromMovie/{idActor}/{idMovie}")
    public String removeActorFromMovie(@PathVariable("idActor") Long idActor, @PathVariable("idMovie") Long idMovie, Model model) {
    	Movie movie = this.movieService.getMovie(idMovie);
    	Artist artist = this.artistService.getArtist(idActor);
    	
    	if(movie.getActors().contains(artist)) {
    	    // Aggiorno il film
    		this.movieService.removeActor(movie, artist);
    		
    		// Aggiorno l'artista
    		this.artistService.removeMovieFromArtistActed(movie, artist);
    	}
    	
    	model.addAttribute("movie", movie);
    	model.addAttribute("artists", this.artistService.getAll());
    	return "actorsToAdd.html";
    }
    
    //,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Cancella Film
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
    @Transactional
	@PostMapping("/deleteMovie/{id}")
	public String deleteMovie(@PathVariable("id") Long id, Model model) throws IOException {
		Movie movie = this.movieService.getMovie(id);
    	
		// Cancello le recensioni del film dal repository
    	this.reviewService.deleteReviews(movie);
		
    	// Rimuovo il film dai campi directed e acted degli artisti
    	this.artistService.removeMovieFromArtists(movie);
    	
		// Cancello film e directory immagini
		this.movieService.deleteMovie(movie);
		
		model.addAttribute("movies", this.movieService.getAll());
		return "manageMovies.html";
	}

}