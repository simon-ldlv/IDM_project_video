package istic;

import java.util.HashMap;

public class Ligne {

	HashMap<String,Boolean> value ;
	int taille;
	
	public Ligne() {
		this.value=new HashMap<String,Boolean>();
		this.taille=0;
	}

	public HashMap<String, Boolean> getValue() {
		return value;
	}

	public void setValue(HashMap<String, Boolean> value) {
		this.value = value;
	}

	public int getTaille() {
		return taille;
	}

	public void setTaille(int taille) {
		this.taille = taille;
	}
	
	public void add(String key, boolean bool) {
		this.value.put(key, bool);
	}
	
	public void addTaille(int i) {
		this.taille = taille + i;
	}
	
	
	
}
