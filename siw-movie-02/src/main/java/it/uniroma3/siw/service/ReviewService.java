package it.uniroma3.siw.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ReviewRepository;

@Service
public class ReviewService {
	
	@Autowired ReviewRepository reviewRepository;
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Get Review(s)
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public Review getReview(Long id) {
		return this.reviewRepository.findById(id).get();
	}

	@Transactional
	public Iterable<Review> getReviews(User user) {
		return this.reviewRepository.findByUser(user);
	}
	
	// Verifica che una recensione esista
	@Transactional
	public boolean reviewExists(Long id) {
		return this.reviewRepository.existsById(id);
	}
	
	// Controlla se un utente abbia gia' recensito un film
	@Transactional
	public boolean userHasReviewed(User user, Movie movie) {
		return this.reviewRepository.existsByUserAndMovie(user, movie);
	}
	
	// getReviews di un film
	@Transactional
	public Iterable<Review> getReviews(Movie movie) {
		return this.reviewRepository.findByMovie(movie);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// New/Update Review
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	// Aggiunge ed inizializza una nuova recensione
	@Transactional
	public void newReview(User user, Movie movie, String username, String headline, Integer rating, String text) {
		Review review = new Review();
    	review.setUsername(username);
    	
    	review.setHeadline(headline);
    	review.setRating(rating);
    	review.setText(text);
    	
    	review.setTime(LocalDate.now());
    	
    	review.setUser(user);
    	review.setMovie(movie);
    	
    	user.getReviews().add(review);
    	this.reviewRepository.save(review);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Delete Review
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public void deleteReview(Review review) {
		this.reviewRepository.delete(review);
	}
	
	// Cancella le recensioni di un film
	@Transactional
	public void deleteReviews(Movie movie) {
		this.reviewRepository.deleteByMovie(movie);
	}
	
	// Cancella le recensioni di un utente
	@Transactional
	public void deleteReviews(User user) {
		this.reviewRepository.deleteByUser(user);
	}
	
}
