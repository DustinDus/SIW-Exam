package it.uniroma3.prs.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

@Entity
public class Comment {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	private String username; // del commentatore

	private String comment;
	
	private String type; // per identificare il tipo dell'entita' commentata (work/artist/movement)
	private Long idObject; // id dell'entita'
	
	private LocalDate time;
	
	@OneToOne
	private User user;
	
	private String object; // nome dell'oggetto, per la lista dei commenti di un utente
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public User getUser() {
		return this.user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getComment() {
		return this.comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public Long getIdObject() {
		return this.idObject;
	}
	public void setIdObject(Long idObject) {
		this.idObject = idObject;
	}
	
	public LocalDate getTime() {
		return this.time;
	}
	public void setTime(LocalDate time) {
		this.time = time;
	}
	
	public String getObject() {
		return this.object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	
	@Transient
    public String getAvatarPath() {
        if (this.user == null || this.id == null)
        	return null;
        else
            return this.user.getAvatarPath();
    }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment comment = (Comment) obj;
		return this.id == comment.getId();
	}
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
}
