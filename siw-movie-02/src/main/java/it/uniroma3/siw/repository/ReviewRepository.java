package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;

public interface ReviewRepository extends CrudRepository<Review, Long> {
	
	public List<Review> findByMovie(Movie movie);
	public List<Review> findByUser(User user);
	
	public boolean existsByUserAndMovie(User user, Movie movie);
	
	public void deleteByMovie(Movie movie);
	public void deleteByUser(User user);
	
}
