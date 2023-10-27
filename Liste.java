package com;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Liste implements Serializable{

	private static final long serialVersionUID = 1921416915397766648L;
	private String date;
	private String nom;
	ArrayList<ModeleVideo> liste = new ArrayList<>();

	public Liste(String nom) {
		SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
		date = format.format(new Date());
		this.nom = nom;
	}

	public String getDate() {
		return date;
	}

	public ArrayList<ModeleVideo> getListe() {
		return liste;
	}

	public void setListe(ArrayList<ModeleVideo> liste) {
		this.liste = liste;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

}
