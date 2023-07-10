package it.uniroma3.prs.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.prs.model.Comment;
import it.uniroma3.prs.model.User;
import it.uniroma3.prs.repository.CommentRepository;

@Service
public class CommentService {
	
	@Autowired CommentRepository commentRepository;
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Get Comment(s)
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public Comment getComment(Long id) {
		return this.commentRepository.findById(id).get();
	}

	@Transactional
	public Iterable<Comment> getComments(String username) {
		return this.commentRepository.findByUsernameOrderByTimeDesc(username);
	}

	@Transactional
	public Iterable<Comment> getComments(String type, Long idObject) {
		return this.commentRepository.findByTypeAndIdObjectOrderByTimeDesc(type, idObject);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// New Comment
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public Comment newComment(String username, User user, String text, String type, Long idObject, String objectName) {
		// Info dell'utente
		Comment comment = new Comment();
		comment.setUsername(username);
		comment.setUser(user);
		comment.setComment(text);
				
		// Info dell'entita'
		comment.setType(type);
		comment.setIdObject(idObject);
				
		// Data
		comment.setTime(LocalDate.now());
				
		// Nome dell'entita'
		comment.setObject(objectName);
		
		return this.commentRepository.save(comment);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Delete Comment
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public void deleteComment(Comment comment) {
		this.commentRepository.delete(comment);
	}

	@Transactional
	public void deleteComments(String type, Long id) {
		this.commentRepository.deleteByTypeAndIdObject(type, id);;
	}

}
