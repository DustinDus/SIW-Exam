package it.uniroma3.prs.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.prs.model.Credentials;
import it.uniroma3.prs.model.User;
import it.uniroma3.prs.service.CredentialsService;
import it.uniroma3.prs.service.UserService;
import jakarta.transaction.Transactional;

@Controller
public class UserController {
	
	@Autowired private UserService userService;
	@Autowired private CredentialsService credentialsService;
	
	@GetMapping("userPage/{username}")
	public String userPage(@PathVariable("username") String username, Model model) {
		User chosenUser = credentialsService.getCredentials(username).getUser();
		model.addAttribute("user", chosenUser);
		model.addAttribute("username", username);
		
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		User currentUser = credentials.getUser();
		if(chosenUser==currentUser)
		    return "personalUserPage.html";
		else
			return "userPage.html";
	}
	
	@PostMapping("setAvatar/{username}")
	public String setAvatar(@RequestParam("fileImage") MultipartFile multipartFile, @PathVariable("username") String username, Model model) throws IOException {
		User user = credentialsService.getCredentials(username).getUser();
		
		this.userService.updateAvatar(user, multipartFile);
				
		model.addAttribute("user", user);
		return "personalUserPage.html";
	}
	
	@GetMapping("/userList")
	public String userList(Model model) {
		model.addAttribute("credentialsList", this.credentialsService.getCredentialsList());
		return "userList.html";
	}
	
	@Transactional
	@PostMapping("/deleteUser/{username}")
	public String deleteUser(@PathVariable("username") String username, Model model) throws IOException {
		Credentials credentials = this.credentialsService.getCredentials(username);
		User user = credentials.getUser();
		
		// Cancello utente e la sua directory immagini
		this.userService.deleteUser(user);
		
		// Cancello le credenziali dell'utente
		this.credentialsService.deleteCredentials(credentials.getId());
		
    	model.addAttribute("credentialsList", this.credentialsService.getCredentialsList());
		return "userList.html";
	}
	
}
