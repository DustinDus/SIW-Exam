package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

@Component
public class ArtistValidator implements Validator {
    @Autowired
    private ArtistRepository artistRepository;
    
	@Override
	public void validate(Object o, Errors errors) {
	    Artist artist = (Artist)o;
	    if (artist.getName()!=null && artist.getSurname()!=null && artist.getBirth()!=null 
	    		&& artistRepository.existsByNameAndSurnameAndBirth(artist.getName(), artist.getSurname(), artist.getBirth())) {
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
