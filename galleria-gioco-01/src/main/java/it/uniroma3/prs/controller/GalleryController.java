package it.uniroma3.prs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.uniroma3.prs.model.Favorites;
import it.uniroma3.prs.model.Work;
import it.uniroma3.prs.service.ArtistService;
import it.uniroma3.prs.service.CredentialsService;
import it.uniroma3.prs.service.MovementService;
import it.uniroma3.prs.service.WorkService;

@Controller
public class GalleryController {

	@Autowired private ArtistService artistService;
	@Autowired private MovementService movementService;
	@Autowired private WorkService workService;
	
	@Autowired private CredentialsService credentialsService;
	
	@GetMapping("/gallery")
	public String gallery(Model model) {
		model.addAttribute("movements", this.movementService.getAllByStart());
		
		model.addAttribute("artists", this.artistService.getAllByBirth());
		
		model.addAttribute("areThereOutsiders", this.workService.existsByUnknownArtist());
		model.addAttribute("works", this.workService.getAllByDate());
		return "gallery.html";
	}
	
	@GetMapping("/userGallery/{username}")
	public String userGallery(@PathVariable("username") String username, Model model) {
		Favorites favorites = this.credentialsService.getCredentials(username).getUser().getFavorites();
		
		model.addAttribute("movements", favorites.getMovements());
		
		model.addAttribute("artists", favorites.getArtists());
		
		List<Work> works = favorites.getWorks();
		boolean areThereOutsiders = false;
		for(Work work: works) {
			if(work.getMovement()==null) {
				areThereOutsiders = true;
				break;
			}
		}
		model.addAttribute("areThereOutsiders", areThereOutsiders);
		model.addAttribute("works", works);
		
		return "gallery.html";
	}
	
}
