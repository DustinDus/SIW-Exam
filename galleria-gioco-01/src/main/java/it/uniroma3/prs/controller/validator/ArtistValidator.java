package it.uniroma3.prs.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.prs.model.Artist;
import it.uniroma3.prs.repository.ArtistRepository;

@Component
public class ArtistValidator implements Validator {
	@Autowired
    private ArtistRepository artistRepository;
    
	@Override
	public void validate(Object o, Errors errors) {
	    Artist artist = (Artist)o;
	    if (artist.getName()!=null && artist.getBirth()!=null
	    		&& artistRepository.existsByNameAndBirth(artist.getName(), artist.getBirth())) {
	        errors.reject("artist.duplicate");
	    }
	    if (artist.getBirth()!=null && artist.getDeath()!=null
	    		&& (artist.getBirth()>artist.getDeath())) {
	        errors.reject("artist.undead");
	    }
	}
	  
	@Override
	public boolean supports(Class<?> aClass) {
	    return Artist.class.equals(aClass);
	}
	
}
