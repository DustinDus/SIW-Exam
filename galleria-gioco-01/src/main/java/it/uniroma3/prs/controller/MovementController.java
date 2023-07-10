package it.uniroma3.prs.controller;

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

import it.uniroma3.prs.model.Artist;
import it.uniroma3.prs.model.Movement;
import it.uniroma3.prs.model.User;
import it.uniroma3.prs.model.Work;
import it.uniroma3.prs.service.ArtistService;
import it.uniroma3.prs.service.CommentService;
import it.uniroma3.prs.service.CredentialsService;
import it.uniroma3.prs.service.MovementService;
import it.uniroma3.prs.service.UserService;
import it.uniroma3.prs.service.WorkService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class MovementController {
	
	@Autowired private MovementService movementService;
	@Autowired private ArtistService artistService;
	@Autowired private WorkService workService;
	
	@Autowired private CommentService commentService;
	@Autowired private CredentialsService credentialsService;
	@Autowired private UserService userService;
	
	@GetMapping("/movements")
	public String getMovements(Model model) {
		model.addAttribute("movements", this.movementService.getAll());
		return "movements.html";
	}
	
	@GetMapping("/movements/{id}")
	public String getMovement(@PathVariable("id") Long id, Model model, Principal principal) {
		// Info utente per aggiungere/rimuovere preferiti
		if(principal!=null) {
		    UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		    List<Movement> favoriteMovements = credentialsService.getCredentials(userDetails.getUsername()).getUser().getFavorites().getMovements();
		    model.addAttribute("favorites", favoriteMovements);
		}
		
		model.addAttribute("movement", this.movementService.getMovement(id));
		model.addAttribute("comments", this.commentService.getComments("movement", id));
		return "movement.html";
	}
	
	@PostMapping("/searchMovements")
	public String searchMovements(@RequestParam(name = "input") String input, Model model) {
		model.addAttribute("movements", this.movementService.searchMovements(input));
		return "movements.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Aggiungi Movimento
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@GetMapping("/formNewMovement")
	public String formNewMovement(Model model) {
		model.addAttribute("movement", new Movement());
		return "formNewMovement.html";
	}
	
	@PostMapping("/newMovement")
	public String newMovement(@Valid @ModelAttribute("movement") Movement movement, BindingResult bindingResult, Model model) {
		
		if (this.movementService.validateMovement(movement, bindingResult).hasErrors())
			return "formNewMovement.html";
			
        this.movementService.saveMovement(movement);
            
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Movement> favoriteMovements = credentialsService.getCredentials(userDetails.getUsername()).getUser().getFavorites().getMovements();
	    model.addAttribute("favorites", favoriteMovements);
        model.addAttribute("movement", movement);
        model.addAttribute("comments", this.commentService.getComments("movement", movement.getId()));
        return "movement.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Gestisci Movimento
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
		
	// Opere del movimento
	@PostMapping("/manageMovementWorks/{id}")
	public String manageMovementWorks(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movement", this.movementService.getMovement(id));
		model.addAttribute("works", this.workService.getAll());
		return "manageMovementWorks.html";
	}
		
	// Aggiungi un'opera al movimento
	@PostMapping("/addWorkToMovement/{idWork}/{idMovement}")
	public String addWorkToMovement(@PathVariable("idWork") Long idWork, @PathVariable("idMovement") Long idMovement, Model model) {
		Work work = this.workService.getWork(idWork);
		Movement movement = this.movementService.getMovement(idMovement);
		
		if(!movement.getWorks().contains(work)) {
		    // Aggiorno il movimento
			this.movementService.addWork(movement, work);
			
			// Aggiorno l'opera
		    this.workService.addMovement(work, movement);
		}
		
		model.addAttribute("movement", movement);
		model.addAttribute("works", this.workService.getAll());
		return "manageMovementWorks.html";
	}
		
	// Rimuovi un'opera dal movimento
	@PostMapping("/removeWorkFromMovement/{idWork}/{idMovement}")
	public String removeWorkFromMovement(@PathVariable("idWork") Long idWork, @PathVariable("idMovement") Long idMovement, Model model) {
		Work work = this.workService.getWork(idWork);
		Movement movement = this.movementService.getMovement(idMovement);
		
		if(movement.getWorks().contains(work)) {
			// Aggiorno il movimento
		    this.movementService.removeWork(movement, work);
		    
		    // Aggiorno l'opera
		    this.workService.removeMovement(work);
		}
		
		model.addAttribute("movement", movement);
		model.addAttribute("works", this.workService.getAll());
	    return "manageMovementWorks.html";
	}
	
	// Aggiungi un'opera principale al movimento
	@PostMapping("/addMainWorkToMovement/{idWork}/{idMovement}")
	public String addMainWorkToMovement(@PathVariable("idWork") Long idWork, @PathVariable("idMovement") Long idMovement, Model model) {
		Work work = this.workService.getWork(idWork);
		Movement movement = this.movementService.getMovement(idMovement);
		
		if(!movement.getMainWorks().contains(work)) {
			// Massimo 3 opere principali
		    if(movement.getMainWorks()!=null && movement.getMainWorks().size()>=3) {
			    model.addAttribute("err", "Max 3 Opere Principali");
			    return "manageMovementWorks.html";
		    }
		    this.movementService.addMainWork(movement, work);
		}
		
		model.addAttribute("movement", movement);
		model.addAttribute("works", this.workService.getAll());
		return "manageMovementWorks.html";
	}
	
	// Rimuovi un'opera principale dal movimento
	@PostMapping("/removeMainWorkFromMovement/{idWork}/{idMovement}")
	public String removeMainWorkFromMovement(@PathVariable("idWork") Long idWork, @PathVariable("idMovement") Long idMovement, Model model) {
		Work work = this.workService.getWork(idWork);
		Movement movement = this.movementService.getMovement(idMovement);
		
		if(movement.getMainWorks().contains(work)) {
		    this.movementService.removeMainWork(movement, work);
		}
		
		model.addAttribute("movement", movement);
		model.addAttribute("works", this.workService.getAll());
		return "manageMovementWorks.html";
	}
		
	// Artisti del movimento
	@PostMapping("/manageMovementArtists/{id}")
	public String manageMovementArtists(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movement", this.movementService.getMovement(id));
		model.addAttribute("artists", this.artistService.getAll());
		return "manageMovementArtists.html";
	}
		
	// Aggiungi un'artista al movimento
	@PostMapping("/addArtistToMovement/{idArtist}/{idMovement}")
	public String addArtistToMovement(@PathVariable("idArtist") Long idArtist, @PathVariable("idMovement") Long idMovement, Model model) {
		Artist artist = this.artistService.getArtist(idArtist);
		Movement movement = this.movementService.getMovement(idMovement);
		
		if(!movement.getArtists().contains(artist)) {
			// Aggiorno il movimento
		    this.movementService.addArtist(movement, artist);
	   	    
	   	    // Aggiorno l'artista
		    this.artistService.addMovement(artist, movement);
		}
		
		model.addAttribute("movement", movement);
		model.addAttribute("artists", this.artistService.getAll());
		return "manageMovementArtists.html";
	}
		
	// Rimuovi un'artista dal movimento
	@PostMapping("/removeArtistFromMovement/{idArtist}/{idMovement}")
	public String removeArtistFromMovement(@PathVariable("idArtist") Long idArtist, @PathVariable("idMovement") Long idMovement, Model model) {
		Artist artist = this.artistService.getArtist(idArtist);
		Movement movement = this.movementService.getMovement(idMovement);
		
		if(movement.getArtists().contains(artist)) {
			// Aggiorno il movimento
		    this.movementService.removeArtist(movement, artist);
	   	    
	   	    // Aggiorno l'artista
		    this.artistService.removeMovement(artist, movement);
		}
		
		model.addAttribute("movement", movement);
		model.addAttribute("artists", this.artistService.getAll());
		return "manageMovementArtists.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Gestisci Preferiti
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@PostMapping("/addMovementToFavorites/{id}/{username}")
	public String addMovementToFavorites(@PathVariable("id") Long id, @PathVariable("username") String username, Model model) {
		Movement movement = this.movementService.getMovement(id);
		User user = this.credentialsService.getCredentials(username).getUser();
		
		if(!user.getFavorites().getMovements().contains(movement)) {
			user.getFavorites().getMovements().add(movement);
			this.userService.saveUser(user);
		}
		
	    model.addAttribute("favorites", user.getFavorites().getMovements());
	    model.addAttribute("movement", movement);
	    model.addAttribute("comments", this.commentService.getComments("movement", id));
	    model.addAttribute("message", "Movimento aggiunto ai preferiti!");
		return "movement.html";
	}
	
	@PostMapping("/removeMovementFromFavorites/{id}/{username}")
	public String removeMovementFromFavorites(@PathVariable("id") Long id, @PathVariable("username") String username, Model model) {
		Movement movement = this.movementService.getMovement(id);
		User user = this.credentialsService.getCredentials(username).getUser();
		
		if(user.getFavorites().getMovements().contains(movement)) {
			user.getFavorites().getMovements().remove(movement);
			this.userService.saveUser(user);
		}
		
	    model.addAttribute("favorites", user.getFavorites().getMovements());
	    model.addAttribute("movement", movement);
	    model.addAttribute("comments", this.commentService.getComments("movement", id));
	    model.addAttribute("message", "Movimento rimosso dai preferiti!");
		return "movement.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Cancellazione
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	@Transactional
	@PostMapping("/deleteMovement/{id}")
	public String deleteMovement(@PathVariable("id") Long id, Model model) {
		Movement movement = this.movementService.getMovement(id);
		
		// Rimuovo il movimento dal campo movements degli artisti
		this.artistService.deleteMovement(movement);
		
		// Rimuove il movimento dal campo movement delle opere
		this.workService.deleteMovement(movement);
		
		// Rimuovo il movimento dai preferiti di tutti gli utenti
		List<User> users = this.userService.getAllUsers();
		for(User user : users) {
			user.getFavorites().getMovements().remove(movement);
		}
		
		// Rimuovo i commenti relativi al movimento
		this.commentService.deleteComments("movement", id);
		
		// Rimuovo il movimento dal repository
		this.movementService.deleteMovement(movement);
		
		model.addAttribute("movements", this.movementService.getAll());
		return "movements.html";
	}
	
}
