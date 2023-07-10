package it.uniroma3.prs.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.prs.model.Work;
import it.uniroma3.prs.repository.WorkRepository;

@Component
public class WorkValidator implements Validator {
	@Autowired
	private WorkRepository workRepository;
	
	@Override
	public void validate(Object o, Errors errors) {
		Work work = (Work)o;
		if (work.getName()!=null && work.getDate()!=null
	    		&& workRepository.existsByNameAndDate(work.getName(), work.getDate())) {
	        errors.reject("work.duplicate");
	    }
		if(work.getName().length()>100) {
			errors.rejectValue("name", "work.Size");
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
	    return Work.class.equals(aClass);
	}
	
}
