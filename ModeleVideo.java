package com;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModeleVideo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7144809486634871375L;
	private String nom;
	private String chemin;
	private String dateModification;
	private int taille;
	private int largeur;
	private int hauteur;
	private String duration;

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public ModeleVideo(String nom, String chemin, String dateModification, int taille, int largeur, int hauteur,
			String duration) {
		super();
		this.nom = nom;
		this.chemin = chemin;
		this.dateModification = dateModification;
		this.taille = taille;
		this.largeur = largeur;
		this.hauteur = hauteur;
		this.duration = duration;
	}

	public String getDuration() {
		return duration;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getChemin() {
		return chemin;
	}

	public void setChemin(String chemin) {
		this.chemin = chemin;
	}

	public String getDateModification() {
		return dateModification;
	}

	public void setDateModification(String dateModification) {
		this.dateModification = dateModification;
	}
	public Date getDate() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			return format.parse(dateModification);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getTaille() {
		return taille;
	}

	public void setTaille(int taille) {
		this.taille = taille;
	}

	public int getLargeur() {
		return largeur;
	}

	public void setLargeur(int largeur) {
		this.largeur = largeur;
	}

	public int getHauteur() {
		return hauteur;
	}

	public void setHauteur(int hauteur) {
		this.hauteur = hauteur;
	}
}
