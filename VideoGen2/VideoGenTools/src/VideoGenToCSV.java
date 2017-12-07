import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.junit.Test;
import org.xtext.example.mydsl.videoGen.AlternativesMedia;
import org.xtext.example.mydsl.videoGen.MandatoryMedia;
import org.xtext.example.mydsl.videoGen.Media;
import org.xtext.example.mydsl.videoGen.MediaDescription;
import org.xtext.example.mydsl.videoGen.OptionalMedia;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;

public class VideoGenToCSV {
	
	
	public List<Ligne> createListLigne(VideoGeneratorModel videoGen) {
		
		assertNotNull(videoGen);	
		List<Ligne> listLigne = new ArrayList<Ligne>();
		// and then visit the model
		// eg access video sequences: 
		EList<Media> medias = videoGen.getMedias();
		for(Media med : medias) {
			if (med instanceof AlternativesMedia) {
				AlternativesMedia altMed = (AlternativesMedia) med;
				listLigne = addAlternative(listLigne, altMed);
				
			} else if ( med instanceof MandatoryMedia) {
				MandatoryMedia mandMed = (MandatoryMedia) med;
				for(int i=0; i<listLigne.size(); i++) {
					listLigne.get(i).add(mandMed.getDescription().toString(), true);
					listLigne.get(i).addTaille(100);
				}

			} else if ( med instanceof OptionalMedia){
				OptionalMedia optMed = (OptionalMedia) med;
				List<Ligne> newlistLigne = listLigne;
				for(int i=0; i<newlistLigne.size(); i++) {
					newlistLigne.get(i).add(optMed.getDescription().toString(), true);
					newlistLigne.get(i).addTaille(150);
				}
				for(int i=0; i<listLigne.size(); i++) {
					Ligne currentLigne = listLigne.get(i);
					currentLigne.add(optMed.getDescription().toString(), false);
					currentLigne.addTaille(150);
					newlistLigne.add(currentLigne);
				}
				listLigne = newlistLigne;

			}
		}
		return listLigne;
	}
	
	private List<Ligne> addAlternative (List<Ligne> listLigne, AlternativesMedia altMed){
		int tailleListOrigine = listLigne.size();
		int taille = altMed.getMedias().size();
		int y = 0;
		List<Ligne> listLigneXcopie = copieXfoisListInside(listLigne, taille);
			for(int i =0; i<listLigneXcopie.size(); i++) {
				listLigneXcopie.get(i).add(altMed.getMedias().get(y).toString(), true);		
				listLigneXcopie.get(i).addTaille(1000);
				tailleListOrigine--;
				if(tailleListOrigine==0) {
					y++;
					tailleListOrigine = listLigne.size();
				}	
			}
		
		
		return listLigne;
	}
	private List<Ligne> copieXfoisListInside (List<Ligne> listLigne, int x){
		List<Ligne> copieListLigne = listLigne;
		for(int i=0; i<x-1; i++) {
			for(Ligne ligne : copieListLigne) {
				listLigne.add(ligne);
			}
		}
		return listLigne;
	}
	private String toCSV(List<Ligne> listLigne) {
		String result = "";
		int j=0;
		int taille = 0;
		for(Ligne ligne : listLigne) {
			taille ++;
		}
		
		for(int i=0; i<taille; i++) {
			result += "video"+i;
			if(i!=taille-1) {
				result += ", ";
			}
			else {
				result += ", TOTAL\n";
			}
		}
		for(Ligne ligne : listLigne) {
			for(boolean bool : ligne.getValue().values()) {
				if(bool) {
					result += "T";
				}
				else {
					result += "F";
				}
				if(j!=taille-1) {
					result += ", ";
				}
				else {
					result += ligne.getTaille() +"\n";
					j=0;
				}
				j++;
			}
		}
		return result;
	}
	
	@Test
	public void testCSV() {
		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI("example1.videogen"));
		assertNotNull(videoGen);
		List<Ligne> listLigne = createListLigne(videoGen);
		String reponse = toCSV(listLigne);
		System.out.println("la reponse \n"+reponse);
	}
}

