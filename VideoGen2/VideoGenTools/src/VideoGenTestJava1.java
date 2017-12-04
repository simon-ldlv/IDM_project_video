import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.junit.Test;
import org.xtext.example.mydsl.videoGen.AlternativesMedia;
import org.xtext.example.mydsl.videoGen.MandatoryMedia;
import org.xtext.example.mydsl.videoGen.Media;
import org.xtext.example.mydsl.videoGen.MediaDescription;
import org.xtext.example.mydsl.videoGen.OptionalMedia;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;

public class VideoGenTestJava1 {
	
	

	
	@Test
	public void testInJava1() {
		String 	myCmdVlc = "ffmpeg -i 'concat:";
		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI("example1.videogen"));
		assertNotNull(videoGen);
		// and then visit the model
		// eg access video sequences: 
		EList<Media> medias = videoGen.getMedias();
		for(Media med : medias) {
			if (med instanceof AlternativesMedia) {
				AlternativesMedia altMed = (AlternativesMedia) med;
				int randomNum = ThreadLocalRandom.current().nextInt(0, altMed.getMedias().size());
				MediaDescription mediaDescAlt = altMed.getMedias().get(randomNum);
				//myCmdVlc += " "+mediaDescAlt.getLocation();
				myCmdVlc = myCmdVlc+mediaDescAlt.getLocation()+"|";

				
			} else if ( med instanceof MandatoryMedia) {
				MandatoryMedia mandMed = (MandatoryMedia) med;
				myCmdVlc = myCmdVlc+((MandatoryMedia) med).getDescription().getLocation()+"|";
				//myCmdVlc += " "+((MandatoryMedia) med).getDescription().getLocation();
				


			} else if ( med instanceof OptionalMedia){
				OptionalMedia optMed = (OptionalMedia) med;
				int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
				if(randomNum==1) {
					//myCmdVlc = " "+optMed.getDescription().getLocation();
					myCmdVlc = myCmdVlc+ optMed.getDescription().getLocation()+"|";
				}
			}
		}	
		myCmdVlc = myCmdVlc.substring(0, myCmdVlc.length() - 1);
		myCmdVlc += "' -codec copy output.mp4";
		System.out.println("COMMAND : "+myCmdVlc);
	    try {
			Process child = Runtime.getRuntime().exec(myCmdVlc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}


