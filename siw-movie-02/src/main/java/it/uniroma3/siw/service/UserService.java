package it.uniroma3.siw.service;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.util.FileUploadUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired UserRepository userRepository;

    //,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Get User(s)
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''
    
    @Transactional
    public User getUser(Long id) {
        Optional<User> result = this.userRepository.findById(id);
        return result.orElse(null);
    }
    
    @Transactional
    public List<User> getAllUsers() {
        List<User> result = new ArrayList<>();
        Iterable<User> iterable = this.userRepository.findAll();
        for(User user : iterable)
            result.add(user);
        return result;
    }

    //,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Update User
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
    public void updateAvatar(User user, MultipartFile multipartFile) throws IOException {
    	// Cancello il vecchio avatar dalla directory dell'utente (se esiste)
    	String avatarPath = user.getAvatarPath();
    	if(avatarPath!=null) {
    	    Path deleteMe = Paths.get(avatarPath.replace("/src","src"));
       	    Files.deleteIfExists(deleteMe);
    	}
    			
       	// Aggiorno la String "avatar" dell'utente
   		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
    	user.setAvatar(fileName);
    	        
        // Carico la nuova immagine su sistema
        this.userRepository.save(user);
        String uploadDir = "src/main/resources/static/uploaded-images/users/" + user.getId();
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    }
    
    @Transactional
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }
    
    @Transactional
    public void addReview(User user, Review review) {
    	user.getReviews().add(review);
    }
    @Transactional
    public void removeReview(User user, Review review) {
    	user.getReviews().remove(review);
    }
    
    //,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
  	// Delete User
  	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
    public void deleteUser(User user) throws IOException {
    	// Cancello la directory dell'utente
    	FileUtils.deleteDirectory(new File("src/main/resources/static/uploaded-images/users/" + user.getId()));
    	
    	// Cancello utente e credenziali dai repository
    	this.userRepository.delete(user);
    }

}
