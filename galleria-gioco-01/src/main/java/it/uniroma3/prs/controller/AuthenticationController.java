package it.uniroma3.prs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.prs.model.Credentials;
import it.uniroma3.prs.model.Favorites;
import it.uniroma3.prs.model.User;
import it.uniroma3.prs.service.CredentialsService;
import it.uniroma3.prs.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {
	
	@Autowired private CredentialsService credentialsService;
    @Autowired private UserService userService;
	
	@GetMapping("/register") 
	public String showRegisterForm (Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "formRegisterUser";
	}
	
	@GetMapping("/login") 
	public String showLoginForm (Model model) {
		return "formLogin";
	}

	@GetMapping("/") 
	public String index(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
	        return "index.html";
		}
		else {		
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
			if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
				return "indexAdmin.html";
			}
		}
        return "index.html";
	}
		
    @GetMapping("/success")
    public String defaultAfterLogin(Model model) {
        
    	UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
            return "indexAdmin.html";
        }
        return "index.html";
    }

	@PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                 BindingResult userBindingResult, @Valid
                 @ModelAttribute("credentials") Credentials credentials,
                 BindingResult credentialsBindingResult,
                 Model model) {
		
		// check username
		if(this.credentialsService.getCredentials(credentials.getUsername())!=null) 
			credentialsBindingResult.addError(new FieldError("UniqueUsername", "username", "Username non disponibile"));
		
		// se user e credential hanno entrambi contenuti validi, memorizza User e the Credentials nel DB
		else if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
        	user.setFavorites(new Favorites());
            userService.saveUser(user);
            credentials.setUser(user);
            credentialsService.saveCredentials(credentials);
            model.addAttribute("user", user);
            return "registrationSuccessful";
        }
        return "formRegisterUser";
    }
	
}