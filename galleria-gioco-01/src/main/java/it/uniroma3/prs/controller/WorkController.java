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
public class WorkController {

	@Autowired private MovementService movementService;
	@Autowired private ArtistService artistService;
	@Autowired private WorkService workService;
	
	@Autowired private CommentService commentService;
	@Autowired private CredentialsService credentialsService;
	@Autowired private UserService userService;
	
	@GetMapping("/works")
	public String getWorks(Model model) {
		model.addAttribute("works", this.workService.getAll());
		return "works.html";
	}
	
	@GetMapping("/works/{id}")
	public String getWork(@PathVariable("id") Long id, Model model, Principal principal) {
		// Info utente per aggiungere/rimuovere preferiti
		if(principal!=null) {
		    UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		    List<Work> favoriteWorks = credentialsService.getCredentials(userDetails.getUsername()).getUser().getFavorites().getWorks();
		    model.addAttribute("favorites", favoriteWorks);
		}
		
		model.addAttribute("work", this.workService.getWork(id));
		model.addAttribute("comments", this.commentService.getComments("work", id));
		return "work.html";
	}
	
	@PostMapping("/searchWorks")
	public String searchWorks(@RequestParam(name = "input") String input, Model model) {
		model.addAttribute("works", this.workService.searchWorks(input));
		return "works.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Aggiungi Opera
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@GetMapping("/formNewWork")
	public String formNewWork(Model model) {
		model.addAttribute("work", new Work());
		return "formNewWork.html";
	}
	
	@PostMapping("/newWork")
	public String newWork(@Valid @ModelAttribute("work") Work work, BindingResult bindingResult, 
                          @RequestParam("fileImage") MultipartFile multipartFile, Model model) throws IOException {
		
		if (this.workService.validateWork(work, bindingResult).hasErrors())
			return "formNewWork.html";
		
		this.workService.uploadImage(work, multipartFile);
            
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Work> favoriteWorks = credentialsService.getCredentials(userDetails.getUsername()).getUser().getFavorites().getWorks();
		model.addAttribute("favorites", favoriteWorks);
        model.addAttribute("work", work);
        model.addAttribute("comments", this.commentService.getComments("work", work.getId()));
        return "work.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Gestisci Opera
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	// Artisti dell'opera
	@PostMapping("/manageWorkArtists/{id}")
	public String manageWorkArtists(@PathVariable("id") Long id, Model model) {
		model.addAttribute("work", this.workService.getWork(id));
		model.addAttribute("artists", this.artistService.getAll());
		return "manageWorkArtists.html";
	}
	
	// Aggiungi un artista all'opera
	@PostMapping("/addArtistToWork/{idArtist}/{idWork}")
	public String addArtistToWork(@PathVariable("idArtist") Long idArtist, @PathVariable("idWork") Long idWork, Model model) {
		Work work = this.workService.getWork(idWork);
		Artist artist = this.artistService.getArtist(idArtist);
		
		if(!work.getMakers().contains(artist)) {
			// Aggiorno l'opera
		    this.workService.addArtist(work, artist);
		    
		    // Aggiorno l'artista
		    this.artistService.addWork(artist, work);
		}
		
		model.addAttribute("work", work);
		model.addAttribute("artists", this.artistService.getAll());
		return "manageWorkArtists.html";
	}
	
	// Rimuovi un artista dall'opera
	@PostMapping("/removeArtistFromWork/{idArtist}/{idWork}")
	public String removeArtistFromWork(@PathVariable("idArtist") Long idArtist, @PathVariable("idWork") Long idWork, Model model) {
		Work work = this.workService.getWork(idWork);
		Artist artist = this.artistService.getArtist(idArtist);
		
		if(work.getMakers().contains(artist)) {
			// Aggiorno l'opera
		    this.workService.removeArtist(work, artist);
		    
		    // Aggiorno l'artista
		    this.artistService.removeWork(artist, work);
		}
		
		model.addAttribute("work", work);
		model.addAttribute("artists", this.artistService.getAll());
		return "manageWorkArtists.html";
	}
	
	// Movimento dell'opera
	@PostMapping("/changeWorkMovement/{id}")
	public String changeWorkMovement(@PathVariable("id") Long id, Model model) {
		model.addAttribute("work", this.workService.getWork(id));
		model.addAttribute("movements", this.movementService.getAll());
		return "changeWorkMovement";
	}
	
	// Cambia il movimento dell'opera
	@PostMapping("/setMovementToWork/{idMovement}/{idWork}")
	public String setMovementToWork(@PathVariable("idMovement") Long idMovement, @PathVariable("idWork") Long idWork, Model model) {
		Work work = this.workService.getWork(idWork);
		Movement movement = this.movementService.getMovement(idMovement);
		
		if(work.getMovement()!=movement) {
			// Aggiorno l'opera
		    this.workService.addMovement(work, movement);
		    
		    // Aggiorno il movimento
		    this.movementService.addWork(movement, work);
		}
		
		model.addAttribute("work", work);
		model.addAttribute("movements", this.movementService.getAll());
		return "changeWorkMovement";
	}
	
	// Rimuovi il movimento da un'opera
	@PostMapping("/removeMovementFromWork/{idMovement}/{idWork}")
	public String removeMovementFromWork(@PathVariable("idMovement") Long idMovement, @PathVariable("idWork") Long idWork, Model model) {
		Work work = this.workService.getWork(idWork);
		Movement movement = this.movementService.getMovement(idMovement);
		
		if(work.getMovement()==movement) {
			// Aggiorno l'opera
		    this.workService.removeMovement(work);
		    
		    // Aggiorno il movimento
		    this.movementService.removeWork(movement, work);
		}
		
		model.addAttribute("work", work);
		model.addAttribute("movements", this.movementService.getAll());
		return "changeWorkMovement";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Gestisci Preferiti
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
		
	@PostMapping("/addWorkToFavorites/{id}/{username}")
	public String addWorkToFavorites(@PathVariable("id") Long id, @PathVariable("username") String username, Model model) {
		Work work = this.workService.getWork(id);
		User user = this.credentialsService.getCredentials(username).getUser();
		
		if(!user.getFavorites().getWorks().contains(work)) {
			user.getFavorites().getWorks().add(work);
			this.userService.saveUser(user);
		}
		
	    model.addAttribute("favorites", user.getFavorites().getWorks());
		model.addAttribute("work", work);
		model.addAttribute("comments", this.commentService.getComments("work", id));
		model.addAttribute("message", "Opera aggiunta alle preferite!");
		return "work.html";
	}
	
	@PostMapping("/removeWorkFromFavorites/{id}/{username}")
	public String removeWorkFromFavorites(@PathVariable("id") Long id, @PathVariable("username") String username, Model model) {
		Work work = this.workService.getWork(id);
		User user = this.credentialsService.getCredentials(username).getUser();
		
		if(user.getFavorites().getWorks().contains(work)) {
			user.getFavorites().getWorks().remove(work);
			this.userService.saveUser(user);
		}
		
	    model.addAttribute("favorites", user.getFavorites().getWorks());
		model.addAttribute("work", work);
		model.addAttribute("comments", this.commentService.getComments("work", id));
		model.addAttribute("message", "Opera rimossa dalle preferite!");
	    return "work.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Cancellazione
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@Transactional
	@PostMapping("/deleteWork/{id}")
	public String deleteWork(@PathVariable("id") Long id, Model model) throws IOException {
		
		Work work = this.workService.getWork(id);
		
		// Rimuovo l'opera dal campo works degli artisti
		this.artistService.deleteWork(work);
		
		// Rimuovo l'opera dal campo works dei movimenti
		this.movementService.deleteWork(work);
	    
		// Rimuovo l'opera dai preferiti di tutti gli utenti
		List<User> users = this.userService.getAllUsers();
		for(User user : users) {
			user.getFavorites().getWorks().remove(work);
		}
		
		// Rimuovo i commenti relativi all'opera
		this.commentService.deleteComments("work", id);
		
		// Cancello la directory e l'opera
    	this.workService.deleteWork(work);
		
		model.addAttribute("works", this.workService.getAll());
		return "works.html";
	}
	
}
