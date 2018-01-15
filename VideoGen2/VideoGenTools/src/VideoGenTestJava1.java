import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
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
	public void testGenerateMostLongVideo() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String 	cmdFfmpeg_1 = "ffmpeg -f concat -safe 0 -i ";
		String 	cmdFfmpeg_2 = " -framerate 30 -vcodec libx264 -acodec aac -ac 2 -strict -2 -c copy ";
		String configVideogen = "example1.videogen";
		String outputVideogenFile = "output_"+timestamp.getTime()+".txt";
		String outVideogenConcat = "output/videogen_"+timestamp.getTime()+".mp4";
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
						toWrite = "file '"+longestVideoAlt.getLocation()+"'";
						outWriter.write(toWrite);
						outWriter.write("\n");

					}
					
				} else if ( med instanceof MandatoryMedia) {
					toWrite = "file '"+((MandatoryMedia) med).getDescription().getLocation()+"'";
					outWriter.write(toWrite);
					outWriter.write("\n");
	
	
				} else if ( med instanceof OptionalMedia){
					OptionalMedia optMed = (OptionalMedia) med;
					toWrite = "file '"+optMed.getDescription().getLocation()+"'";
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
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String 	cmdFfmpeg_1 = "ffmpeg -f concat -safe 0 -i ";
		String 	cmdFfmpeg_2 = " -framerate 30 -vcodec libx264 -acodec aac -ac 2 -strict -2 -c copy ";
		String configVideogen = "example1.videogen";
		String outputVideogenFile = "output_"+timestamp.getTime()+".txt";
		String outVideogenConcat = "output/videogen_"+timestamp.getTime()+".mp4";

		try {
			VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(configVideogen));
			File fileOutput = new File(outputVideogenFile);
			fileOutput.createNewFile();
			FileWriter outWriter;
			
			//System.out.println(fileOutput.getAbsolutePath());
		
			outWriter = new FileWriter(fileOutput);		
			assertNotNull(videoGen);
			
			EList<Media> medias = videoGen.getMedias();
			for(Media med : medias) {
				String toWrite = "";
				if (med instanceof AlternativesMedia) {
					AlternativesMedia altMed = (AlternativesMedia) med;
					int randomNum = ThreadLocalRandom.current().nextInt(0, altMed.getMedias().size());
					MediaDescription mediaDescAlt = altMed.getMedias().get(randomNum);
					toWrite = "file '"+mediaDescAlt.getLocation()+"'";
					outWriter.write(toWrite);
					outWriter.write("\n");
					
				} else if ( med instanceof MandatoryMedia) {
					toWrite = "file '"+((MandatoryMedia) med).getDescription().getLocation()+"'";
					outWriter.write(toWrite);
					outWriter.write("\n");
	
	
				} else if ( med instanceof OptionalMedia){
					OptionalMedia optMed = (OptionalMedia) med;
					int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
					if(randomNum==1) {
						toWrite = "file '"+optMed.getDescription().getLocation()+"'";
						outWriter.write(toWrite);
						outWriter.write("\n");
					}
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
	public void testGenerateGif() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String 	cmdFfmpeg_1 = "ffmpeg -f concat -safe 0 -i ";
		String 	cmdFfmpeg_2 = " -framerate 30 -vcodec libx264 -acodec aac -ac 2 -strict -2 -c copy ";
		String configVideogen = "example1.videogen";
		String outputVideogenFile = "output_"+timestamp.getTime()+".txt";
		String outVideogenConcat = "output/videogen_"+timestamp.getTime()+".mp4";
		
		String cmdGifPalette1 = "ffmpeg -y -i ";
		String cmdGifPalette2 = " -vf fps=10,scale=320:-1:flags=lanczos,palettegen ";
		
		String gifFileNamePalette = outVideogenConcat+".palette.png";
		String gifFileName = outVideogenConcat+".gif";
		
		String cmdGifGenerate1 = "ffmpeg -i ";
		String cmdGifGenerate2 = " -i ";
		String cmdGifGenerate3 =" -filter_complex 'fps=10,scale=320:-1:flags=lanczos[x];[x][1:v]paletteuse' ";
				
		try {
			VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(configVideogen));
			File fileOutput = new File(outputVideogenFile);
			fileOutput.createNewFile();
			FileWriter outWriter;
			
			//System.out.println(fileOutput.getAbsolutePath());
		
			outWriter = new FileWriter(fileOutput);		
			assertNotNull(videoGen);
			
			EList<Media> medias = videoGen.getMedias();
			for(Media med : medias) {
				String toWrite = "";
				if (med instanceof AlternativesMedia) {
					AlternativesMedia altMed = (AlternativesMedia) med;
					int randomNum = ThreadLocalRandom.current().nextInt(0, altMed.getMedias().size());
					MediaDescription mediaDescAlt = altMed.getMedias().get(randomNum);
					toWrite = "file '"+mediaDescAlt.getLocation()+"'";
					outWriter.write(toWrite);
					outWriter.write("\n");
					
				} else if ( med instanceof MandatoryMedia) {
					toWrite = "file '"+((MandatoryMedia) med).getDescription().getLocation()+"'";
					outWriter.write(toWrite);
					outWriter.write("\n");
	
	
				} else if ( med instanceof OptionalMedia){
					OptionalMedia optMed = (OptionalMedia) med;
					int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
					if(randomNum==1) {
						toWrite = "file '"+optMed.getDescription().getLocation()+"'";
						outWriter.write(toWrite);
						outWriter.write("\n");
					}
				}
				//System.out.println(toWrite);
			}
			outWriter.close();
			String cmdProcess = cmdFfmpeg_1+fileOutput.getAbsolutePath()+cmdFfmpeg_2+outVideogenConcat;
			//System.out.println(cmdProcess);
			Process child = Runtime.getRuntime().exec(cmdProcess);
			child.waitFor();
			File fileVideo = new File(outVideogenConcat);
			assertTrue(fileVideo.exists());
			assertTrue(!fileVideo.isDirectory());
			
			String cmdGifPalette = cmdGifPalette1+outVideogenConcat+cmdGifPalette2+gifFileNamePalette;
			Process processGifPalette = Runtime.getRuntime().exec(cmdGifPalette);
			processGifPalette.waitFor();
			File fileGifPalette = new File(gifFileNamePalette);
			assertTrue(fileGifPalette.exists());
			assertTrue(!fileGifPalette.isDirectory());
			
			String cmdGif = cmdGifGenerate1+outVideogenConcat+cmdGifGenerate2+gifFileNamePalette+cmdGifGenerate3+gifFileName;
			Process processGif = Runtime.getRuntime().exec(cmdGif);
			processGif.waitFor();
			System.out.println(cmdGif);

			File fileGif = new File(gifFileName);
			assertTrue(fileGif.exists());
			assertTrue(!fileGif.isDirectory());
			
			
			
			
		} catch (/*IO*/Exception e) {
			e.printStackTrace();
		}   
	}

		

		
	@Test
	public void testAllCombinaisonCsv() {

		
	}

	@Test
	public void testGenererVignettes() {

		
		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI("example1.videogen"));
		assertNotNull(videoGen);
		
		
		String cmdVignette_1 = "ffmpeg -ss 00:00:01 -i ";
		String cmdVignette_2 = " -vframes 1 -q:v 2 ";

		EList<Media> medias = videoGen.getMedias();
		for(Media med : medias) {
			
			if (med instanceof AlternativesMedia) {
				AlternativesMedia altMed = (AlternativesMedia) med;
				for(MediaDescription desc : altMed.getMedias()) {
					auxGenererVignettes(cmdVignette_1+desc.getLocation()+cmdVignette_2, desc.getLocation()+".jpg");
				}
			} else if ( med instanceof MandatoryMedia) {
				MandatoryMedia mandMed = (MandatoryMedia) med;
				auxGenererVignettes(cmdVignette_1+mandMed.getDescription().getLocation()+cmdVignette_2, mandMed.getDescription().getLocation()+".jpg");
				
			} else if ( med instanceof OptionalMedia){
				OptionalMedia optMed = (OptionalMedia) med;
				auxGenererVignettes(cmdVignette_1+optMed.getDescription().getLocation()+cmdVignette_2, optMed.getDescription().getLocation()+".jpg");
			}	
		}
	}
	
	private void auxGenererVignettes(String cmd, String outImg) {
		Process child;
		File f = null;
		
		f = new File(outImg);
		if(!f.exists()) {
			try {
				child = Runtime.getRuntime().exec(cmd+outImg);
				System.out.println(outImg+" BEGIN");
				child.waitFor();
				System.out.println(outImg+" END");
	
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			f = new File(outImg);
			assertTrue(f.exists());
			assertTrue(!f.isDirectory());
		} 
	}
}

