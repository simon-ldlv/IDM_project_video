package istic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.xtext.example.mydsl.videoGen.AlternativesMedia;
import org.xtext.example.mydsl.videoGen.MandatoryMedia;
import org.xtext.example.mydsl.videoGen.Media;
import org.xtext.example.mydsl.videoGen.MediaDescription;
import org.xtext.example.mydsl.videoGen.OptionalMedia;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;

public class VideoGenStation {
	
	private String folderVideo;
	
	private String folderVideogen;
	
	private  String folderVignette;
	
	private  String folderGif;
	
	private  String folderPalette;
	
	private  String folderArgs;
	

	
	
	public VideoGenStation(String folderVideo, String folderVideogen, String folderVignette, String folderGif,
			String folderPalette, String folderArgs) {
		super();
		this.folderVideo = folderVideo;
		this.folderVideogen = folderVideogen;
		this.folderVignette = folderVignette;
		this.folderGif = folderGif;
		this.folderPalette = folderPalette;
		this.folderArgs = folderArgs;
	}

	public VideoGenStation() {
		super();
		this.folderVideo = "mp4lol";
		this.folderVideogen = "output/videogen";
		this.folderVignette = "output/vignette";
		this.folderGif = "output/gif";
		this.folderPalette  = "output/palette";;
		this.folderArgs = "ffmpeg_args";

	}
	
	
	
	// NE MARCHE PAS 
	public String genererCustomVideo(String pathModel, HashMap<String, String> alter, ArrayList<String> option){
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String 	cmdFfmpeg_1 = "ffmpeg -f concat -safe 0 -i ";
		String 	cmdFfmpeg_2 = " -framerate 30 -vcodec libx264 -acodec aac -ac 2 -strict -2 -c copy ";
		String outputVideogenFile = folderArgs+"/videogen_"+timestamp.getTime()+".txt";
		String outVideogenConcat = folderVideogen+"/videogen_"+timestamp.getTime()+".mp4";

		try {
			VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(pathModel));
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
					System.out.println(optMed.getDescription().toString());
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
	
	
	public String genererLongestVideo(String pathModel){
		

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String 	cmdFfmpeg_1 = "ffmpeg -f concat -safe 0 -i ";
		String 	cmdFfmpeg_2 = " -framerate 30 -vcodec libx264 -acodec aac -ac 2 -strict -2 -c copy ";
		String outputVideogenFile = folderArgs+"/videogen_"+timestamp.getTime()+".txt";
		String outVideogenConcat = folderVideogen+"/videogen_"+timestamp.getTime()+".mp4";
		
		String cmdInfoVideo ="ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 ";
		MediaDescription longestVideoAlt = null;


		try {
			VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(pathModel));
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
					double maxDuration=-1;

					for(MediaDescription media : mediaDescAlt) {
						Process ps = Runtime.getRuntime().exec(cmdInfoVideo+new File(media.getLocation()).getAbsolutePath());  
						ps.waitFor();

						BufferedReader in = new BufferedReader(new InputStreamReader(ps.getInputStream()));
						String line;
						
						while ((line = in.readLine()) != null) {
							double currentDuration =  Double.parseDouble(line);
							

							if(currentDuration > maxDuration) {
								
								maxDuration = currentDuration;
								longestVideoAlt = media;
							}
						}
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
			}
			outWriter.close();
			
			String cmdProcess = cmdFfmpeg_1+fileOutput.getAbsolutePath()+cmdFfmpeg_2+outVideogenConcat;
			Process child = Runtime.getRuntime().exec(cmdProcess);
			child.waitFor();
			System.out.println(cmdProcess);
		} catch (/*IO*/Exception e) {
			e.printStackTrace();
		}   
		
		return outVideogenConcat;
		
	}
	
	
	public String genererVideoRandom(String pathModel){
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String 	cmdFfmpeg_1 = "ffmpeg -f concat -safe 0 -i ";
		String 	cmdFfmpeg_2 = " -framerate 30 -vcodec libx264 -acodec aac -ac 2 -strict -2 -c copy ";
		String outputVideogenFile = folderArgs+"/videogen_"+timestamp.getTime()+".txt";
		String outVideogenConcat = folderVideogen+"/videogen_"+timestamp.getTime()+".mp4";

		try {
			VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(pathModel));
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

	public String genererGifWithVideo(String pathVideo)  {
		File fVid = new File(pathVideo);
		
		if(!fVid.exists() || fVid.isDirectory() ) {
			throw new IllegalArgumentException("ERREUR : "+pathVideo+" n'est pas un fichier valide");
		}
		
	
		String cmdGifPalette1 = "ffmpeg -y -i ";
		String cmdGifPalette2 = " -vf fps=5,scale=160:-1:flags=lanczos,palettegen ";
		
		String gifFileNamePalette = folderPalette+"/"+fVid.getName()+".palette.png";
		String gifFileName = folderGif+"/"+fVid.getName()+".gif";
		
		String cmdGifGenerate1 = "ffmpeg -i ";
		String cmdGifGenerate2 = " -i ";
		String cmdGifGenerate3 =" -filter_complex fps=5,scale=160:-1:flags=lanczos[x];[x][1:v]paletteuse ";
				
		String cmdGifPalette = cmdGifPalette1+pathVideo+cmdGifPalette2+gifFileNamePalette;
		System.out.println(cmdGifPalette);

		Process processGifPalette = null;
		try {
			processGifPalette = Runtime.getRuntime().exec(cmdGifPalette);
		
			processGifPalette.waitFor();
	
			File fileGifPalette = new File(gifFileNamePalette);
			assertTrue(fileGifPalette.exists());
			assertTrue(!fileGifPalette.isDirectory());
			
			String cmdGif = cmdGifGenerate1+pathVideo+cmdGifGenerate2+gifFileNamePalette+cmdGifGenerate3+gifFileName;
			Process processGif = Runtime.getRuntime().exec(cmdGif);
		
			processGif.waitFor();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return gifFileName;
	}
	
	
	public ArrayList<String> genererVignette(String pathModel){
		
		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(pathModel));
		assertNotNull(videoGen);
		
		ArrayList<String> listVignettePath = new ArrayList<String>();
		
		String cmdVignette_1 = "ffmpeg -ss 00:00:01 -i ";
		String cmdVignette_2 = " -vframes 1 -q:v 2 ";

		File folder = new File (folderVideo);
		for (File fileEntry : folder.listFiles()) {
			 
		        if (!fileEntry.isDirectory() && FilenameUtils.getExtension(fileEntry.getAbsolutePath()).equals("mp4")){
		        	String outVignette = folderVignette+"/"+fileEntry.getName()+".jpg";
		        	auxGenererVignettes(cmdVignette_1 + fileEntry.getAbsolutePath() + cmdVignette_2,outVignette);
		        	listVignettePath.add(outVignette);
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
