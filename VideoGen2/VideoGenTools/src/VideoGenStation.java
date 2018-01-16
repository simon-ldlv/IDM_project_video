import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.xtext.example.mydsl.videoGen.AlternativesMedia;
import org.xtext.example.mydsl.videoGen.MandatoryMedia;
import org.xtext.example.mydsl.videoGen.Media;
import org.xtext.example.mydsl.videoGen.MediaDescription;
import org.xtext.example.mydsl.videoGen.OptionalMedia;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;

public class VideoGenStation {
	
	private static String folderVideo = "./mp4lol";
	
	private static String folderVideogen = "./output/videogen";
	
	private static String folderVignette = "./output/vignette";
	
	private static String folderGif = "./output/gif";
	
	private static String folderPalette = "./output/palette";
	
	private static String folderArgs = "./ffmpeg_args";
	
	
	static String genererLongestVideo(String modelFile){
		
		return null;
	}
	
	
	static String genererVideo(String modelFile){
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String 	cmdFfmpeg_1 = "ffmpeg -f concat -safe 0 -i ";
		String 	cmdFfmpeg_2 = " -framerate 30 -vcodec libx264 -acodec aac -ac 2 -strict -2 -c copy ";
		String outputVideogenFile = folderArgs+"/videogen_"+timestamp.getTime()+".txt";
		String outVideogenConcat = folderVideogen+"/videogen_"+timestamp.getTime()+".mp4";

		try {
			VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(modelFile));
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
					int randomNum = ThreadLocalRandom.current().nextInt(0, altMed.getMedias().size());
					MediaDescription mediaDescAlt = altMed.getMedias().get(randomNum);
					toWrite = "file '../"+mediaDescAlt.getLocation()+"'";
					outWriter.write(toWrite);
					outWriter.write("\n");
					
				} else if ( med instanceof MandatoryMedia) {
					toWrite = "file '../"+((MandatoryMedia) med).getDescription().getLocation()+"'";
					outWriter.write(toWrite);
					outWriter.write("\n");
	
	
				} else if ( med instanceof OptionalMedia){
					OptionalMedia optMed = (OptionalMedia) med;
					int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
					if(randomNum==1) {
						toWrite = "file '../"+optMed.getDescription().getLocation()+"'";
						outWriter.write(toWrite);
						outWriter.write("\n");
					}
				}
			}
			outWriter.close();
			String cmdProcess = cmdFfmpeg_1+fileOutput.getAbsolutePath()+cmdFfmpeg_2+outVideogenConcat;
			Process child = Runtime.getRuntime().exec(cmdProcess);
			child.waitFor();
			
		} catch (/*IO*/Exception e) {
			e.printStackTrace();
		}   
		
		return outVideogenConcat;
	}

	static String genererGifWithVideo(String fileVideo)  {
		File fVid = new File(fileVideo);
		
		if(!fVid.exists() || fVid.isDirectory() ) {
			throw new IllegalArgumentException("ERREUR : "+fileVideo+" n'est pas un fichier valide");
		}
		
	
		String cmdGifPalette1 = "ffmpeg -y -i ";
		String cmdGifPalette2 = " -vf fps=10,scale=320:-1:flags=lanczos,palettegen ";
		
		String gifFileNamePalette = folderPalette+"/"+fVid.getName()+".palette.png";
		String gifFileName = folderGif+"/"+fVid.getName()+".gif";
		
		String cmdGifGenerate1 = "ffmpeg -i ";
		String cmdGifGenerate2 = " -i ";
		String cmdGifGenerate3 =" -filter_complex fps=10,scale=320:-1:flags=lanczos[x];[x][1:v]paletteuse ";
				
		String cmdGifPalette = cmdGifPalette1+fileVideo+cmdGifPalette2+gifFileNamePalette;
		System.out.println(cmdGifPalette);

		Process processGifPalette = null;
		try {
			processGifPalette = Runtime.getRuntime().exec(cmdGifPalette);
		
			processGifPalette.waitFor();
	
			File fileGifPalette = new File(gifFileNamePalette);
			assertTrue(fileGifPalette.exists());
			assertTrue(!fileGifPalette.isDirectory());
			
			String cmdGif = cmdGifGenerate1+fileVideo+cmdGifGenerate2+gifFileNamePalette+cmdGifGenerate3+gifFileName;
			Process processGif = Runtime.getRuntime().exec(cmdGif);
		
			processGif.waitFor();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return gifFileName;
	}
	
	
	static ArrayList<String> genererVignette(String modelFile){
		
		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(modelFile));
		assertNotNull(videoGen);
		
		ArrayList<String> listVignettePath = new ArrayList<String>();
		
		String cmdVignette_1 = "ffmpeg -ss 00:00:01 -i ";
		String cmdVignette_2 = " -vframes 1 -q:v 2 ";

		EList<Media> medias = videoGen.getMedias();
		for(Media med : medias) {
			
			if (med instanceof AlternativesMedia) {
				AlternativesMedia altMed = (AlternativesMedia) med;
				for(MediaDescription desc : altMed.getMedias()) {
					String pathVignette = (new File(desc.getLocation())).getName()+".jpg";
					auxGenererVignettes(cmdVignette_1+desc.getLocation()+cmdVignette_2, folderVignette+"/"+pathVignette);
					listVignettePath.add(folderVignette+"/"+pathVignette);
				}
			} else if ( med instanceof MandatoryMedia) {
				MandatoryMedia mandMed = (MandatoryMedia) med;
				String pathVignette = (new File(mandMed.getDescription().getLocation())).getName()+".jpg";

				auxGenererVignettes(cmdVignette_1+mandMed.getDescription().getLocation()+cmdVignette_2, folderVignette+"/"+pathVignette);
				listVignettePath.add(folderVignette+"/"+pathVignette);

			} else if ( med instanceof OptionalMedia){
				
				OptionalMedia optMed = (OptionalMedia) med;
				String pathVignette = (new File(optMed.getDescription().getLocation())).getName()+".jpg";

				auxGenererVignettes(cmdVignette_1+optMed.getDescription().getLocation()+cmdVignette_2, folderVignette+"/"+pathVignette);
				listVignettePath.add(folderVignette+"/"+pathVignette);
			}	
		}
		return listVignettePath;
	}

	private static void auxGenererVignettes(String cmd, String outImg) {
		Process child;
		File f = null;
		
		f = new File(outImg);
		if(!f.exists()) {
			try {
				System.out.println(cmd+outImg);
				child = Runtime.getRuntime().exec(cmd+outImg);
				child.waitFor();
	
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

		} 
	}
}
