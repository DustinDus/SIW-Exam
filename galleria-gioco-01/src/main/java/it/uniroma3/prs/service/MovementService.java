package it.uniroma3.prs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import it.uniroma3.prs.controller.validator.MovementValidator;
import it.uniroma3.prs.repository.MovementRepository;
import it.uniroma3.prs.model.Artist;
import it.uniroma3.prs.model.Movement;
import it.uniroma3.prs.model.Work;

@Service
public class MovementService {

	@Autowired MovementRepository movementRepository;
	@Autowired MovementValidator movementValidator;
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Get Movement(s)
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public Iterable<Movement> getAll() {
		return this.movementRepository.findAll();
	}

	@Transactional
	public Movement getMovement(Long id) {
		return this.movementRepository.findById(id).get();
	}

	@Transactional
	public Iterable<Movement> searchMovements(String name) {
		return this.movementRepository.findByNameContainingIgnoreCase(name);
	}

	@Transactional
	public Iterable<Movement> getAllByStart() {
		return this.movementRepository.findAllByOrderByStartDateAsc();
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// New/Update Movement
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public BindingResult validateMovement(Movement movement, BindingResult bindingResult) {
		this.movementValidator.validate(movement, bindingResult);
		return bindingResult;
	}

	@Transactional
	public void saveMovement(Movement movement) {
		this.movementRepository.save(movement);
	}	

	@Transactional
	public void addArtist(Movement movement, Artist artist) {
		movement.getArtists().add(artist);
	    this.movementRepository.save(movement);
	}
	@Transactional
	public void removeArtist(Movement movement, Artist artist) {
		movement.getArtists().remove(artist);
	    this.movementRepository.save(movement);
	}

	@Transactional
	public void addWork(Movement movement, Work work) {
		movement.getWorks().add(work);
   	    this.movementRepository.save(movement);
	}
	@Transactional
	public void removeWork(Movement movement, Work work) {
		movement.getWorks().remove(work);
		
	    // Se era anche un'opera principale
		if(movement.getMainWorks().contains(work))
		 	movement.getMainWorks().remove(work);
		
   	    this.movementRepository.save(movement);
	}

	@Transactional
	public void addMainWork(Movement movement, Work work) {
		movement.getMainWorks().add(work);
	    this.movementRepository.save(movement);
	}
	@Transactional
	public void removeMainWork(Movement movement, Work work) {
		movement.getMainWorks().remove(work);
	    this.movementRepository.save(movement);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Delete Movement
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public void deleteMovement(Movement movement) {
		this.movementRepository.delete(movement);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Others
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	// Rimuove l'artista dal campo artists dei movimenti
	@Transactional
	public void deleteArtist(Artist artist) {
		Iterable<Movement> movements = this.movementRepository.findAll();
		for(Movement movement : movements) {
			movement.getArtists().remove(artist);
		}
	}
	
	// Rimuove l'opera dal campo works dei movimenti
	@Transactional
	public void deleteWork(Work work) {
		Iterable<Movement> movements = this.movementRepository.findAll();
		for(Movement movement : movements) {
			movement.getWorks().remove(work);
		}
	}
	
	// Gioco
	////////
	
	// Valuta se e' possibile giocare
	@Transactional
	public boolean enoughMovements() {
		return this.movementRepository.count()>3;
	}
	// Movimento random
	@Transactional
	public Movement getRandomMovement() {
		return this.movementRepository.findRandomMovement().get(0);
	}
	
}
