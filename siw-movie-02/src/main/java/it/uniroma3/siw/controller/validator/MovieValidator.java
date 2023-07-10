package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.*;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;

@Component
public class MovieValidator implements Validator {
	@Autowired
	private MovieRepository movieRepository;

	@Override
	public void validate(Object o, Errors errors) {
	    Movie movie = (Movie)o;
	    if (movie.getTitle()!=null && movie.getYear()!=null 
	    		&& movieRepository.existsByTitleAndYear(movie.getTitle(), movie.getYear())) {
	        errors.reject("artist.duplicate");
	    }
	}
	  
	@Override
	public boolean supports(Class<?> aClass) {
	    return Movie.class.equals(aClass);
	}
}
