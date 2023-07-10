package it.uniroma3.prs.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Work {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@NotBlank
	private String name;
	@NotNull
	@Max(2023)
	private Integer date;
	
	private String image;
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@ManyToOne
	private Artist artist;
	@ManyToOne
	private Movement movement;
	
	
	
	public Long getId() {
		return id;
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
	
	public Integer getDate() {
		return this.date;
	}
	public void setDate(Integer date) {
		this.date = date;
	}
	
	public String getImage() {
		return this.image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Artist getArtist() {
		return this.artist;
	}
	public void setArtist(Artist artist) {
		this.artist = artist;
	}
	
	public Movement getMovement() {
		return this.movement;
	}
	public void setMovement(Movement movement) {
		this.movement = movement;
	}
	
	@Transient
    public String getImagePath() {
        if (this.image == null || this.id == null)
        	return null;
        else
            return "/src/main/resources/static/uploaded-images/works/" + this.id + "/" + this.image;
    }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Work work = (Work) obj;
		return this.name == work.getName();
	}
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
}
