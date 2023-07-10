package it.uniroma3.prs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.prs.model.Artist;
import it.uniroma3.prs.model.Comment;
import it.uniroma3.prs.model.Movement;
import it.uniroma3.prs.model.User;
import it.uniroma3.prs.model.Work;
import it.uniroma3.prs.service.ArtistService;
import it.uniroma3.prs.service.CommentService;
import it.uniroma3.prs.service.CredentialsService;
import it.uniroma3.prs.service.MovementService;
import it.uniroma3.prs.service.UserService;
import it.uniroma3.prs.service.WorkService;

@Controller
public class CommentController {
	
	@Autowired private CommentService commentService;
	@Autowired private CredentialsService credentialsService;
	@Autowired private UserService userService;
	
	@Autowired private ArtistService artistService;
	@Autowired private MovementService movementService;
	@Autowired private WorkService workService;

	@GetMapping("userComments/{username}")
	public String userComments(@PathVariable("username") String username, Model model) {
		model.addAttribute("username", username);
		model.addAttribute("user", credentialsService.getCredentials(username).getUser());
		model.addAttribute("comments", this.commentService.getComments(username));
		return "comments.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Aggiungi Commento
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@PostMapping("addCommentToArtist/{username}/{idObject}")
	public String addCommentToArtist(@PathVariable("username") String username, 
			                         @PathVariable("idObject") Long idObject,
			                         @RequestParam("text") String text, Model model) {
		User user  = this.credentialsService.getCredentials(username).getUser();
		Artist artist = this.artistService.getArtist(idObject);
		
		Comment comment = this.commentService.newComment(username, user, text, "artist", idObject, artist.getName());
	    this.userService.addComment(user, comment);
		
		// Ridireziona alla pagina dell'artista
		model.addAttribute("comments", this.commentService.getComments("artist", idObject));
		model.addAttribute("favorites", user.getFavorites().getArtists());
		model.addAttribute("artist", artist);
		return "artist.html";
	}
	
	@PostMapping("addCommentToMovement/{username}/{idObject}")
	public String addCommentToMovement(@PathVariable("username") String username, 
			                         @PathVariable("idObject") Long idObject,
			                         @RequestParam("text") String text, Model model) {
		User user  = this.credentialsService.getCredentials(username).getUser();
		Movement movement = this.movementService.getMovement(idObject);
		
		Comment comment = this.commentService.newComment(username, user, text, "movement", idObject, movement.getName());
		this.userService.addComment(user, comment);
		
		// Ridireziona alla pagina dell'artista
		model.addAttribute("comments", this.commentService.getComments("movement", idObject));
		model.addAttribute("favorites", user.getFavorites().getMovements());
		model.addAttribute("movement", movement);
		return "movement.html";
	}
	
	@PostMapping("addCommentToWork/{username}/{idObject}")
	public String addCommentToWork(@PathVariable("username") String username, 
			                         @PathVariable("idObject") Long idObject,
			                         @RequestParam("text") String text, Model model) {
		User user  = this.credentialsService.getCredentials(username).getUser();
		Work work = this.workService.getWork(idObject);
		
		Comment comment = this.commentService.newComment(username, user, text, "work", idObject, work.getName());
		this.userService.addComment(user, comment);
		
		// Ridireziona alla pagina dell'artista
		model.addAttribute("comments", this.commentService.getComments("work", idObject));
		model.addAttribute("favorites", user.getFavorites().getWorks());
		model.addAttribute("work", work);
		return "work.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Rimuovi Commento
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@PostMapping("removeCommentFromList/{idComment}/{username}")
	public String removeCommentFromList(@PathVariable("idComment") Long idComment, @PathVariable("username") String username, Model model) {
		User user  = this.credentialsService.getCredentials(username).getUser();
		Comment comment = this.commentService.getComment(idComment);
		
		// Cancello il commento
		this.userService.removeComment(user, comment);
		this.commentService.deleteComment(comment);
		
		// Rimando alla lista
		model.addAttribute("username", username);
		model.addAttribute("user", credentialsService.getCredentials(username).getUser());
		model.addAttribute("comments", this.commentService.getComments(username));
		return "comments.html";
	}
	
	@PostMapping("removeCommentFromArtist/{idComment}/{idEntity}")
	public String removeCommentFromArtist(@PathVariable("idComment") Long idComment, @PathVariable("idEntity") Long idEntity, Model model) {
		Comment comment = this.commentService.getComment(idComment);
		User user  = this.credentialsService.getCredentials(comment.getUsername()).getUser();
		
		// Cancello il commento
		this.userService.removeComment(user, comment);
		this.commentService.deleteComment(comment);
		
		// Rimando all'artista
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    List<Artist> favoriteArtists = credentialsService.getCredentials(userDetails.getUsername()).getUser().getFavorites().getArtists();
	    
		model.addAttribute("artist", this.artistService.getArtist(idEntity));
		model.addAttribute("favorites", favoriteArtists);
		model.addAttribute("comments", this.commentService.getComments("artist", idEntity));
		return "artist.html";
	}
	
	@PostMapping("removeCommentFromMovement/{idComment}/{idEntity}")
	public String removeCommentFromMovement(@PathVariable("idComment") Long idComment, @PathVariable("idEntity") Long idEntity, Model model) {
		Comment comment = this.commentService.getComment(idComment);
		User user  = this.credentialsService.getCredentials(comment.getUsername()).getUser();
		
		// Cancello il commento
		this.userService.removeComment(user, comment);
		this.commentService.deleteComment(comment);
		
		// Rimando al movimento
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    List<Movement> favoriteMovements = credentialsService.getCredentials(userDetails.getUsername()).getUser().getFavorites().getMovements();
	    
		model.addAttribute("movement", this.movementService.getMovement(idEntity));
		model.addAttribute("favorites", favoriteMovements);
		model.addAttribute("comments", this.commentService.getComments("movement", idEntity));
		return "movement.html";
	}
	
	@PostMapping("removeCommentFromWork/{idComment}/{idEntity}")
	public String removeCommentFromWork(@PathVariable("idComment") Long idComment, @PathVariable("idEntity") Long idEntity, Model model) {
		Comment comment = this.commentService.getComment(idComment);
		User user  = this.credentialsService.getCredentials(comment.getUsername()).getUser();
		
		// Cancello il commento
		this.userService.removeComment(user, comment);
		this.commentService.deleteComment(comment);
		
		// Rimando all'opera
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    List<Work> favoriteWorks = credentialsService.getCredentials(userDetails.getUsername()).getUser().getFavorites().getWorks();
	    
		model.addAttribute("work", this.workService.getWork(idEntity));
		model.addAttribute("favorites", favoriteWorks);
		model.addAttribute("comments", this.commentService.getComments("work", idEntity));
		return "work.html";
	}
	
}
