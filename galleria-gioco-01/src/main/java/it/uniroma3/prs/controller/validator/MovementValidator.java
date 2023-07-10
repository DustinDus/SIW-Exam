package it.uniroma3.prs.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.prs.model.Movement;
import it.uniroma3.prs.repository.MovementRepository;

@Component
public class MovementValidator implements Validator {
	@Autowired
	private MovementRepository movementRepository;
	
	@Override
	public void validate(Object o, Errors errors) {
	    Movement movement = (Movement)o;
	    if (movement.getName()!=null && movement.getStartDate()!=null
	    		&& movementRepository.existsByNameAndStartDate(movement.getName(), movement.getStartDate())) {
	        errors.reject("movement.duplicate");
	    }
	    if (movement.getStartDate()!=null && movement.getEndDate()!=null
	    		&& movement.getStartDate()>movement.getEndDate()) {
	    	errors.reject("movement.undead");
	    }
	}
	
	@Override
	public boolean supports(Class<?> aClass) {
	    return Movement.class.equals(aClass);
	}
	
}
