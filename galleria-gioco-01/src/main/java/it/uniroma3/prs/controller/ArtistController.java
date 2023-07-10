package it.uniroma3.prs.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.prs.service.ArtistService;
import it.uniroma3.prs.service.CommentService;
import it.uniroma3.prs.service.CredentialsService;
import it.uniroma3.prs.service.MovementService;
import it.uniroma3.prs.service.UserService;
import it.uniroma3.prs.service.WorkService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import it.uniroma3.prs.model.Artist;
import it.uniroma3.prs.model.Movement;
import it.uniroma3.prs.model.User;
import it.uniroma3.prs.model.Work;

@Controller
public class ArtistController {
	
	@Autowired private ArtistService artistService;
	@Autowired private MovementService movementService;
	@Autowired private WorkService workService;
	
	@Autowired private CommentService commentService;
	@Autowired private CredentialsService credentialsService;
	@Autowired private UserService userService;
	
	@GetMapping("/artists")
	public String getArtists(Model model) {
		model.addAttribute("artists", this.artistService.getAllByBirth());
		return "artists.html";
	}
	
	@GetMapping("/artists/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model, Principal principal) {
		// Info utente per aggiungere/rimuovere preferiti
		if(principal!=null) {
		    UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		    List<Artist> favoriteArtists = credentialsService.getCredentials(userDetails.getUsername()).getUser().getFavorites().getArtists();
		    model.addAttribute("favorites", favoriteArtists);
		}
		
		model.addAttribute("artist", this.artistService.getArtist(id));
		model.addAttribute("comments", this.commentService.getComments("artist", id));
		return "artist.html";
	}
	
	@PostMapping("/searchArtists")
	public String searchArtists(@RequestParam(name = "input") String input, Model model) {
		model.addAttribute("artists", this.artistService.searchArtists(input));
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
        
		if(!multipartFile.isEmpty())
		    this.artistService.uploadImage(artist, multipartFile);
		else
			this.artistService.saveArtist(artist);
            
    	UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	List<Artist> favoriteArtists = credentialsService.getCredentials(userDetails.getUsername()).getUser().getFavorites().getArtists();
    	model.addAttribute("favorites", favoriteArtists);
        model.addAttribute("artist", artist);
        model.addAttribute("comments", this.commentService.getComments("artist", artist.getId()));
        return "artist.html";
            
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Gestisci Artista
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
		
	// Movimenti dell'artista
	@PostMapping("/manageArtistMovements/{id}")
	public String manageArtistMovements(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistService.getArtist(id));
		model.addAttribute("movements", this.movementService.getAllByStart());
		return "manageArtistMovements.html";
	}
	
	// Aggiungi un movimento all'artista
	@PostMapping("/addMovementToArtist/{idMovement}/{idArtist}")
	public String addMovementToArtist(@PathVariable("idMovement") Long idMovement, @PathVariable("idArtist") Long idArtist, Model model) {
		Artist artist = this.artistService.getArtist(idArtist);
		Movement movement = this.movementService.getMovement(idMovement);
		
		if(!artist.getMovements().contains(movement)) {
		    // Aggiorno l'artista
			this.artistService.addMovement(artist, movement);
			
			// Aggiorno il movimento
		    this.movementService.addArtist(movement, artist);
		}
		
		model.addAttribute("artist", artist);
		model.addAttribute("movements", this.movementService.getAllByStart());
		return "manageArtistMovements.html";
	}
	
	// Rimuovi un movimento dall'artista
	@PostMapping("/removeMovementFromArtist/{idMovement}/{idArtist}")
	public String removeMovementFromArtist(@PathVariable("idMovement") Long idMovement, @PathVariable("idArtist") Long idArtist, Model model) {
		Artist artist = this.artistService.getArtist(idArtist);
		Movement movement = this.movementService.getMovement(idMovement);
		
		if(artist.getMovements().contains(movement)) {
			// Aggiorno l'artista
		    this.artistService.removeMovement(artist, movement);
		    
		    // Aggiorno il movimento
		    this.movementService.removeArtist(movement, artist);
		}
		
		model.addAttribute("artist", artist);
		model.addAttribute("movements", this.movementService.getAllByStart());
		return "manageArtistMovements.html";
	}
	
	// Opere dell'artista
	@PostMapping("/manageArtistWorks/{id}")
	public String manageArtistWorks(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistService.getArtist(id));
		model.addAttribute("works", this.workService.getAllByDate());
		return "manageArtistWorks.html";
	}
	
	// Aggiungi un'opera all'artista
	@PostMapping("/addWorkToArtist/{idWork}/{idArtist}")
	public String addWorkToArtist(@PathVariable("idWork") Long idWork, @PathVariable("idArtist") Long idArtist, Model model) {
		Artist artist = this.artistService.getArtist(idArtist);
		Work work = this.workService.getWork(idWork);
		
		if(!artist.getWorks().contains(work)) {
			// Aggiorno l'artista
		    this.artistService.addWork(artist, work);
		    
		    // Aggiorno l'opera
		    this.workService.addArtist(work, artist);
		}
		
		model.addAttribute("artist", artist);
		model.addAttribute("works", this.workService.getAllByDate());
		return "manageArtistWorks.html";
	}
	
	// Rimuovi un'opera dall'artista
	@PostMapping("/removeWorkFromArtist/{idWork}/{idArtist}")
	public String removeWorkFromArtist(@PathVariable("idWork") Long idWork, @PathVariable("idArtist") Long idArtist, Model model) {
		Artist artist = this.artistService.getArtist(idArtist);
		Work work = this.workService.getWork(idWork);
		
		if(artist.getWorks().contains(work)) {
			// Aggiorno l'artista
	        this.artistService.removeWork(artist, work);
		    
		    // Aggiorno l'opera
	        this.workService.removeArtist(work, artist);
		}
		
		model.addAttribute("artist", artist);
		model.addAttribute("works", this.workService.getAllByDate());
		return "manageArtistWorks.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Gestisci Preferiti
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@PostMapping("/addArtistToFavorites/{id}/{username}")
	public String addArtistToFavorites(@PathVariable("id") Long id, @PathVariable("username") String username, Model model) {
		Artist artist = this.artistService.getArtist(id);
		User user = this.credentialsService.getCredentials(username).getUser();
		
		if(!user.getFavorites().getArtists().contains(artist)) {
			user.getFavorites().getArtists().add(artist);
			this.userService.saveUser(user);
		}
	    
	    model.addAttribute("favorites", user.getFavorites().getArtists());
	    model.addAttribute("artist", artist);
	    model.addAttribute("comments", this.commentService.getComments("artist", id));
	    model.addAttribute("message", "Artista aggiunto ai preferiti!");
		return "artist.html";
	}
	
	@PostMapping("/removeArtistFromFavorites/{id}/{username}")
	public String removeArtistFromFavorites(@PathVariable("id") Long id, @PathVariable("username") String username, Model model) {
		Artist artist = this.artistService.getArtist(id);
		User user = this.credentialsService.getCredentials(username).getUser();
		
		if(user.getFavorites().getArtists().contains(artist)) {
			user.getFavorites().getArtists().remove(artist);
			this.userService.saveUser(user);
		}
		
	    model.addAttribute("favorites", user.getFavorites().getArtists());
	    model.addAttribute("artist", artist);
	    model.addAttribute("comments", this.commentService.getComments("artist", id));
	    model.addAttribute("message", "Artista rimosso dai preferiti!");
		return "artist.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Cancellazione
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@Transactional
	@PostMapping("/deleteArtist/{id}")
	public String deleteArtist(@PathVariable("id") Long id, Model model) throws IOException {
		Artist artist = this.artistService.getArtist(id);
		
		// Rimuovo l'artista dal campo artists dei movimenti
		this.movementService.deleteArtist(artist);
		
		// Rimuovo l'artista dal campo makers delle opere
		this.workService.deleteArtist(artist);
		
		// Rimuovo l'artista dai preferiti di tutti gli utenti
		List<User> users = this.userService.getAllUsers();
		for(User user : users) {
			user.getFavorites().getArtists().remove(artist);
		}
		
		// Rimuovo i commenti relativi all'artista
		this.commentService.deleteComments("artist", id);
		
		// Cancello directory ed artista
    	this.artistService.deleteArtist(artist);
		
		model.addAttribute("artists", this.artistService.getAllByBirth());
		return "artists.html";
	}
	
}
