package it.uniroma3.prs.service;

import java.io.File;
import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.prs.controller.validator.WorkValidator;
import it.uniroma3.prs.model.Artist;
import it.uniroma3.prs.model.Movement;
import it.uniroma3.prs.model.Work;
import it.uniroma3.prs.repository.WorkRepository;
import it.uniroma3.prs.util.FileUploadUtil;

@Service
public class WorkService {
	
	@Autowired WorkRepository workRepository;
	@Autowired WorkValidator workValidator;
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Get Work(s)
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@Transactional
	public Iterable<Work> getAll() {
		return this.workRepository.findAll();
	}
	
	@Transactional
	public Work getWork(Long id) {
		return this.workRepository.findById(id).get();
	}

	@Transactional
	public Iterable<Work> searchWorks(String name) {
		return this.workRepository.findByNameContainingIgnoreCase(name);
	}

	@Transactional
	public Iterable<Work> getAllByDate() {
		return this.workRepository.findAllByOrderByDateAsc();
	}

	@Transactional
	public boolean existsByUnknownArtist() {
		return this.workRepository.existsByMovementNull();
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// New/Update Work
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public BindingResult validateWork(Work work, BindingResult bindingResult) {
		this.workValidator.validate(work, bindingResult);
		return bindingResult;
	}

	@Transactional
	public void uploadImage(Work work, MultipartFile multipartFile) throws IOException {
		// Nome del file
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		
		// Aggiornato l'artista
        work.setImage(fileName); 
        this.workRepository.save(work);
 
        // Upload immagine su folder apposito
        String uploadDir = "src/main/resources/static/uploaded-images/works/" + work.getId();
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	}

	@Transactional
	public void addArtist(Work work, Artist artist) {
		work.setArtist(artist);
	    this.workRepository.save(work);
	}
	@Transactional
	public void removeArtist(Work work) {
		work.setArtist(null);
	    this.workRepository.save(work);
	}

	@Transactional
	public void addMovement(Work work, Movement movement) {
		work.setMovement(movement);
	    this.workRepository.save(work);
	}
	@Transactional
	public void removeMovement(Work work) {
		work.setMovement(null);
	    this.workRepository.save(work);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Delete Work
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public void deleteWork(Work work) throws IOException {
		// Cancello la directory e l'opera
    	FileUtils.deleteDirectory(new File("src/main/resources/static/uploaded-images/works/" + work.getId()));
		
		// Rimuovo l'opera dal repository
		this.workRepository.delete(work);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Others
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	// Rimuove l'artista dal campo artist delle opere
	@Transactional
	public void deleteArtist(Artist artist) {
		Iterable<Work> works = this.workRepository.findAll();
		for(Work work : works) {
			if(work.getArtist()==artist)
				work.setArtist(null);
		}
	}
	
	// Rimuove il movimento dal campo movement delle opere
	@Transactional
	public void deleteMovement(Movement movement) {
		Iterable<Work> works = this.workRepository.findAll();
		for(Work work : works) {
			if(work.getMovement()==movement)
			    work.setMovement(null);
		}
	}
	
	// Gioco
	////////
	
	// Valuta se e' possibile giocare
	@Transactional
	public boolean enoughWorks() {
		return this.workRepository.count()>3;
	}
	// Opera random
	@Transactional
	public Work getRandomWork() {
		return this.workRepository.findRandomWork().get(0);
	}
	
}
