package it.uniroma3.siw.model;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Artist {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@NotBlank
	@Size(max=30)
    private String name;
	@NotBlank
	@Size(max=30)
    private String surname;
	@NotNull
	@Min(1800)
	@Max(2023)
    private Integer birth;
	@Max(2023)
    private Integer death;
	
    private String image;
    
    @OneToMany(mappedBy = "director")
    private List<Movie> directed;
	@ManyToMany(mappedBy = "actors")
	private List<Movie> acted;
    
	
	
    public Long getId() {
    	return this.id;
    }
    public void setId(Long id) {
    	this.id = id;
    }
    
    public String getName() {
    	return this.name;
    }
    public void setName(String name) {
    	this.name = name;
    }
    
    public String getSurname() {
    	return this.surname;
    }
    
    public void setSurname(String surname) {
    	this.surname = surname;
    }
    
    public Integer getBirth() {
    	return this.birth;
    }
    public void setBirth(Integer birth) {
    	this.birth = birth;
    }
    
    public Integer getDeath() {
    	return this.death;
    }
    public void setDeath(Integer death) {
    	this.death = death;
    }
    
    public String getImage() {
    	return this.image;
    }
    public void setImage(String image) {
    	this.image = image;
    }
    
    public List<Movie> getDirected() { 
    	return this.directed;
    }
    public void setDirected(List<Movie> directed) {
    	this.directed = directed;
    }
    
    public List<Movie> getActed() {
    	return this.acted;
    }
    public void setActed(List<Movie> acted) {
    	this.acted = acted;
    }
    
    @Transient
    public String getImagePath() {
        if (this.image == null || this.id == null)
        	return null;
        else
            return "/src/main/resources/static/uploaded-images/artists/" + this.id + "/" + this.image;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist artist = (Artist) obj;
    	return (this.name==artist.getName()) && (this.surname==artist.getSurname()) && (this.birth==artist.getBirth());
    }
    @Override
    public int hashCode() {
    	return this.name.hashCode() + this.surname.hashCode() + this.birth;
    }
    
}
