package it.uniroma3.siw.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Movie {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@NotBlank
	@Size(max=30)
    private String title;
	@NotNull
	@Min(1888)
	@Max(2030)
    private Integer year;
	
	private String cover;
    private List<String> images;
    
    @ManyToOne
    private Artist director;
    @ManyToMany
    private List<Artist> actors;
    
    
    
    public Long getId() {
    	return this.id;
    }
    public void setId(Long id) {
    	this.id = id;
    }
    
    public String getTitle() {
    	return this.title;
    }
    public void setTitle(String title) {
    	this.title = title;
    }
    
    public Integer getYear() {
    	return this.year;
    }
    public void setYear(Integer year) {
    	this.year = year;
    }
    
    public String getCover() {
    	return this.cover;
    }
    public void setCover(String cover) {
    	this.cover = cover;
    }
    
    public List<String> getImages() {
    	if(this.images==null)
    		this.images = new ArrayList<String>();
    	return this.images;
    }
    public void setImages(List<String> images) {
    	this.images = images;
    }
    
    public Artist getDirector() {
    	return this.director;
    }
    public void setDirector(Artist director) {
    	this.director = director;
    }
    
    public List<Artist> getActors() {
    	return this.actors;
    }
    public void setActors(List<Artist> actors) {
    	this.actors = actors;
    }
    
    @Transient
    public String getCoverPath() {
        if (this.cover == null || this.id == null)
        	return null;
        else
            return "/src/main/resources/static/uploaded-images/movies/" + this.id + "/" + this.cover;
    }
    @Transient
    public List<String> getImagePaths() {
        if (this.images == null || this.id == null)
        	return null;
        else {
        	List<String> imagePaths = new ArrayList<String>();
        	for(String image : this.images) 
        	    imagePaths.add("/src/main/resources/static/uploaded-images/movies/" + this.id + "/" + image);
            return imagePaths;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie movie = (Movie) obj;
    	return (this.title==movie.getTitle()) && (this.year==movie.getYear());
    }
    @Override
    public int hashCode() {
    	return this.title.hashCode()+this.year;
    }
    
}