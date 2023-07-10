package it.uniroma3.prs.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import it.uniroma3.prs.model.Artist;
import it.uniroma3.prs.model.Movement;
import it.uniroma3.prs.model.Work;

@Service
public class GameService {

	@Autowired private WorkService workService;
	@Autowired private ArtistService artistService;
	@Autowired private MovementService movementService;
	
	// Valuta se e' possibile giocare
	@Transactional
	public boolean enoughEntities() {
		return (this.workService.enoughWorks() && this.artistService.enoughArtists() && this.movementService.enoughMovements());
	}
	
	// Prende un'opera e valuta come e' possibile giocarci
	@Transactional
	public void newRound(Model model) {
		// Immagine
		Work work = this.workService.getRandomWork();
		model.addAttribute("image", work.getImagePath());
		
		// Valutazione
		//////////////
		
		Random r = new Random();
		
		// Opera senza artista e movimento
		if( work.getMovement()==null && work.getArtist()==null ) {
			this.guessTheName(work,model);
		}
		
		// Opera senza artista o movimento
		else if( work.getMovement()==null || work.getArtist()==null ) {
			int n = r.nextInt(2);
			
			if(work.getMovement()==null) {
				switch(n) {
			        case 0:
			        	this.guessTheName(work,model);
			        	break;
			        case 1:
			        	this.guessTheArtist(work,model);
			        	break;
			    }
			}
			else {
				switch(n) {
			        case 0:
			        	this.guessTheName(work,model);
			        	break;
			        case 1:
			        	this.guessTheMovement(work,model);
			        	break;
			    }
			}
		}
		
		// Opera con artista e movimento
		else {
			int n = r.nextInt(3);
			
			switch(n) {
			    case 0:
			    	this.guessTheName(work,model);
			    	break;
			    case 1:
			    	this.guessTheArtist(work,model);
			    	break;
			    case 2:
			    	this.guessTheMovement(work,model);
			    	break;
			}
		}
		
	}
	
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Metodi per giocare
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public void guessTheName(Work work, Model model) {
		// Soluzione
		List<String> options = new ArrayList<String>();
		String option = this.nameOption(work);
		model.addAttribute("solution", option);
		
		// Opzioni tra cui scegliere
		options.add(option);
		for(int i=0 ; i<3 ; i++) {
			
			// Evita duplicati
			while(options.contains(option))
				option = this.nameOption(this.workService.getRandomWork());
			
			// Opzione aggiunta
			options.add(option);
			
		}
		Collections.shuffle(options);
		model.addAttribute("options", options);
		
		// Consegna
		model.addAttribute("question", "Che opera e' questa?");
	}

	@Transactional
	public void guessTheArtist(Work work, Model model) {
		// Soluzione
		List<String> options = new ArrayList<String>();
		String option = this.artistOption(work.getArtist());
		model.addAttribute("solution", option);
			
		// Opzioni tra cui scegliere
	    options.add(option);
		for(int i=0 ; i<3 ; i++) {
					
			// Evita duplicati
			while(options.contains(option))
				option = this.artistOption(this.artistService.getRandomArtist());
					
			// Opzione aggiunta
			options.add(option);
					
		   }
		Collections.shuffle(options);
		model.addAttribute("options", options);
		
		// Consegna
		model.addAttribute("question", "Quale di questi artisti e' artifice di quest'opera?");
	}

	@Transactional
	public void guessTheMovement(Work work, Model model) {
		// Soluzione
		List<String> options = new ArrayList<String>();
		String option = this.movementOption(work.getMovement());
		model.addAttribute("solution", option);
	
		// Opzioni tra cui scegliere
	    options.add(option);
		for(int i=0 ; i<3 ; i++) {
			
			// Evita duplicati
			while(options.contains(option))
				option = this.movementOption(this.movementService.getRandomMovement());
			
			// Opzione aggiunta
			options.add(option);
			
    	}
		Collections.shuffle(options);
		model.addAttribute("options", options);
		
		// Consegna
		model.addAttribute("question", "A quale di questi movimenti appartiene l'opera?");
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Funzioni ausiliarie per tenere in ordine il codice
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	// Restituisce una stringa opzione per guessTheName
	@Transactional
	private String nameOption(Work work) {
		return work.getName() + " (" + work.getDate() + ")";
	}
	
	// Idem per guessTheArtist
	@Transactional
	private String artistOption(Artist artist) {
		return artist.getName() + " (n." + artist.getBirth() + ")";
	}
	
	// Idem per guessTheMovement
	@Transactional
	private String movementOption(Movement movement) {
		return movement.getName() + " (i." + movement.getStartDate() + ")";
	}
	
}
