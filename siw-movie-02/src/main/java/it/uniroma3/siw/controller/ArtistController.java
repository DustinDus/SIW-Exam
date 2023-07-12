package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.MovieService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class ArtistController {
    
	@Autowired ArtistService artistService;
	@Autowired MovieService movieService;
	
	@Autowired MessageSource messageSource;
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Visualizza/Cerca Artisti
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@GetMapping("/artists")
    public String getArtists(Model model) {
        model.addAttribute("artists", this.artistService.getAll());
        return "artists.html";
    }
	
	@GetMapping("/artists/{id}")
    public String getArtist(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artist", this.artistService.getArtist(id));
        return "artist.html";
    }
	
	@PostMapping("/searchArtists")
    public String searchArtists(@RequestParam("input") String name, Model model) {
        model.addAttribute("artists", this.artistService.searchArtists(name));
        return "artists.html";
    }
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Aggiungi Artista
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@GetMapping("/formNewArtist")
    public String formNewArtist(Model model) {
        model.addAttribute("artist", new Artist());
        return "formNewArtist.html";
    }
	
	@PostMapping("/newArtist")
    public String newArtist(@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, 
    		                @RequestParam("fileImage") MultipartFile multipartFile, Model model) throws IOException {
		
        if (this.artistService.validateArtist(artist, bindingResult).hasErrors())
        	return "formNewArtist.html";
        
        this.artistService.uploadImage(artist, multipartFile);
    	
        model.addAttribute("artist", artist);
        return "artist.html";
    }
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Gestisci Artista
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@GetMapping("/manageArtists")
    public String manageArtists(Model model) {
    	model.addAttribute("artists", this.artistService.getAll());
    	return "manageArtists.html";
    }
	
	@GetMapping("/formUpdateArtist/{id}")
    public String formUpdateArtist(@PathVariable("id") Long id, Model model) {
    	model.addAttribute("artist", this.artistService.getArtist(id));
    	return "formUpdateArtist.html";
    }
	
	@PostMapping("/searchManageArtists")
    public String searchManageArtists(@RequestParam("input") String name, Model model) {
        model.addAttribute("artists", this.artistService.searchArtists(name));
        return "manageArtists.html";
    }
	
	// Dati
	///////
	
	@GetMapping("/changeArtistData/{id}")
    public String changeArtistData(@PathVariable("id") Long id, Model model) {
    	model.addAttribute("artist", this.artistService.getArtist(id));
    	return "changeArtistData.html";
    }
	
	@PostMapping("setArtistName/{id}")
	public String setArtistName(@RequestParam("name") String name, @PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.getArtist(id);
		this.artistService.updateName(artist, name);
		
		model.addAttribute("artist", artist);
		return "changeArtistData.html";
	}
	
	@PostMapping("setArtistSurname/{id}")
	public String setArtistSurname(@RequestParam("surname") String surname, @PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.getArtist(id);
		this.artistService.updateSurname(artist, surname);
		
		model.addAttribute("artist", artist);
		return "changeArtistData.html";
	}
	
	@PostMapping("setArtistBirth/{id}")
	public String setArtistBirth(@RequestParam("birth") Integer birth, @PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.getArtist(id);
		
		// Check errori
		if(birth<1888) {
			model.addAttribute("birth_error", this.messageSource.getMessage("Min.birth", null, Locale.getDefault()));
			model.addAttribute("artist", artist);
			return "changeArtistData.html";
		}
		if(birth>2023) {
			model.addAttribute("birth_error", this.messageSource.getMessage("Max.birth", null, Locale.getDefault()));
			model.addAttribute("artist", artist);
			return "changeArtistData.html";
		}
		if(artist.getDeath()!=null && birth>artist.getDeath()) {
			model.addAttribute("birth_error", this.messageSource.getMessage("artist.undead", null, Locale.getDefault()));
			model.addAttribute("artist", artist);
			return "changeArtistData.html";
		}
		
		this.artistService.updateBirth(artist, birth);
		
		model.addAttribute("artist", artist);
		return "changeArtistData.html";
	}
	
	@PostMapping("setArtistDeath/{id}")
	public String setArtistDeath(@RequestParam(value="death", required=false) Integer death, @PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.getArtist(id);
		
		// Check null immediato per evitare NullPointerException
		if(death==null) {
			this.artistService.updateDeath(artist, death);
			
			model.addAttribute("artist", artist);
			return "changeArtistData.html";
		}
		
		// Check errori
		if(death>2023) {
			model.addAttribute("death_error", this.messageSource.getMessage("Max.death", null, Locale.getDefault()));
			model.addAttribute("artist", artist);
			return "changeArtistData.html";
		}
		if(artist.getBirth()>death) {
			model.addAttribute("death_error", this.messageSource.getMessage("artist.undead", null, Locale.getDefault()));
			model.addAttribute("artist", artist);
			return "changeArtistData.html";
		}
		
		this.artistService.updateDeath(artist, death);
		
		model.addAttribute("artist", artist);
		return "changeArtistData.html";
	}
	
	// Immagine
	///////////
	
	@GetMapping("/changeArtistImage/{id}")
	public String changeArtistImage(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistService.getArtist(id));
    	return "changeArtistImage.html";
	}
	
	@PostMapping("/setArtistImage/{id}")
	public String setArtistImage(@RequestParam("fileImage") MultipartFile multipartFile, @PathVariable("id") Long id, Model model) throws IOException {
		Artist artist = this.artistService.getArtist(id);
		
		// Cancello la vecchia immagine dalla directory dell'artista
		this.artistService.deleteImage(artist, multipartFile);
        
		// Carico la nuova immagine su sistema
        this.artistService.uploadImage(artist, multipartFile);
    	
        model.addAttribute("artist", artist);
        return "changeArtistImage.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Cancella Artista
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@Transactional
	@PostMapping("/deleteArtist/{id}")
	public String deleteArtist(@PathVariable("id") Long id, Model model) throws IOException {
		Artist artist = this.artistService.getArtist(id);
    	
    	// Rimuovo l'artista dai campi director e actors dei film
    	this.movieService.removeArtistFromMovies(artist);
    	
		// Cancello artista e directory immagini
		this.artistService.deleteArtist(artist);
		
		model.addAttribute("artists", this.artistService.getAll());
		return "manageArtists.html";
	}
	
}
