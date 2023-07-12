package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;

@Controller
public class ReviewController {
	
	@Autowired ReviewService reviewService;
	@Autowired CredentialsService credentialsService;
	
	@GetMapping("userReviews/{username}")
	public String userReviews(@PathVariable("username") String username, Model model) {
		User user = this.credentialsService.getCredentials(username).getUser();
		
		model.addAttribute("reviews", this.reviewService.getReviews(user));
		model.addAttribute("username", username);
		return "userReviews.html";
	}
	
	@PostMapping("/deleteReview/{idReview}/{username}")
    public String deleteReview(@PathVariable("idReview") Long idReview, @PathVariable("username") String username, Model model) {
		this.reviewService.deleteReview(this.reviewService.getReview(idReview));
		User user = this.credentialsService.getCredentials(username).getUser();
		
		model.addAttribute("reviews", this.reviewService.getReviews(user));
		model.addAttribute("username", username);
		return "userReviews.html";
	}
	
}
