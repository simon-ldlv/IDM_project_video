import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
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
	
	//@Test
	public void testGenerateMostLongVideo() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String 	cmdFfmpeg_1 = "ffmpeg -f concat -safe 0 -i ";
		String 	cmdFfmpeg_2 = " -framerate 30 -vcodec libx264 -acodec aac -ac 2 -strict -2 -c copy ";
		String configVideogen = "example1.videogen";
		String outputVideogenFile = "ffmpeg_args/videogen_"+timestamp.getTime()+".txt";
		String outVideogenConcat = "output/videogen/videogen_"+timestamp.getTime()+".mp4";
		
		String cmdInfoVideo ="ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 ";
		MediaDescription longestVideoAlt = null;

		try {
			VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(configVideogen));
			File fileOutput = new File(outputVideogenFile);
			fileOutput.createNewFile();
			FileWriter outWriter;
					
			outWriter = new FileWriter(fileOutput);		
			assertNotNull(videoGen);
			
			EList<Media> medias = videoGen.getMedias();
			for(Media med : medias) {
				String toWrite = "";
				if (med instanceof AlternativesMedia) {
					AlternativesMedia altMed = (AlternativesMedia) med;
					EList<MediaDescription> mediaDescAlt = altMed.getMedias();
					
					for(MediaDescription media : mediaDescAlt) {
						Process ps = Runtime.getRuntime().exec(cmdInfoVideo+new File(media.getLocation()).getAbsolutePath());  
						BufferedReader in = new BufferedReader(new InputStreamReader(ps.getInputStream()));
						String line;
						double maxDuration=-1;
						
						while ((line = in.readLine()) != null) {
							double currentDuration =  Double.parseDouble(line);
							if(currentDuration > maxDuration) {
								maxDuration = currentDuration;
								longestVideoAlt = media;
							}
						}
						ps.waitFor();
					}
					if(longestVideoAlt!=null) {
						toWrite = "file '../"+longestVideoAlt.getLocation()+"'";
						outWriter.write(toWrite);
						outWriter.write("\n");

					}
					
				} else if ( med instanceof MandatoryMedia) {
					toWrite = "file '../"+((MandatoryMedia) med).getDescription().getLocation()+"'";
					outWriter.write(toWrite);
					outWriter.write("\n");
	
	
				} else if ( med instanceof OptionalMedia){
					OptionalMedia optMed = (OptionalMedia) med;
					toWrite = "file '../"+optMed.getDescription().getLocation()+"'";
					outWriter.write(toWrite);
					outWriter.write("\n");
					
				}
				//System.out.println(toWrite);
			}
			outWriter.close();
			String cmdProcess = cmdFfmpeg_1+fileOutput.getAbsolutePath()+cmdFfmpeg_2+outVideogenConcat;
			//System.out.println(cmdProcess);
			Process child = Runtime.getRuntime().exec(cmdProcess);
			child.waitFor();
			File f = new File(outVideogenConcat);
			assertTrue(f.exists());
			assertTrue(!f.isDirectory());
			
		} catch (/*IO*/Exception e) {
			e.printStackTrace();
		}   
	}
	
	@Test
	public void testGenerateVideogen1() {
		String configVideogen = "example1.videogen";
		
		String outVideogenConcat = VideoGenStation.genererVideo(configVideogen);
		
		File f = new File(outVideogenConcat);
		assertTrue(f.exists());
		assertTrue(!f.isDirectory());
 
	}
	
	@Test
	public void testGenerateVideoAndGif() {
		String configVideogen = "example1.videogen";

		String outVideogenConcat = VideoGenStation.genererVideo(configVideogen);
		
		File f = new File(outVideogenConcat);
		assertTrue(f.exists());
		assertTrue(!f.isDirectory());
		
		String outVideoGif = VideoGenStation.genererGifWithVideo(outVideogenConcat);

		File fileGifPalette = new File(outVideoGif);
		assertTrue(fileGifPalette.exists());
		assertTrue(!fileGifPalette.isDirectory());

	}
		
	
		
	@Test
	public void testAllCombinaisonCsv() {

		
	}

	@Test
	public void testGenererVignettes() {

		
		String configModel = "example1.videogen";

		ArrayList<String> listVignettes = VideoGenStation.genererVignette(configModel);
		
		listVignettes.forEach(vignPath -> {
			System.out.println(vignPath);
			File f = new File(vignPath);
			assertTrue(f.exists());
			assertTrue(!f.isDirectory());
		});
	}
	
}

