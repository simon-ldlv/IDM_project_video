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
	
	
	@Test
	public void testGenerateVideo2LongestAndGif() {
	
		String configVideogen = "example2.videogen";
		
		String outVideogenConcat = VideoGenStation.genererLongestVideo(configVideogen);
		
		File f = new File(outVideogenConcat);
		assertTrue(f.exists());
		assertTrue(!f.isDirectory());
		
		String outVideoGif = VideoGenStation.genererGifWithVideo(outVideogenConcat);

		File fileGifPalette = new File(outVideoGif);
		assertTrue(fileGifPalette.exists());
		assertTrue(!fileGifPalette.isDirectory());
		
	}
	
	@Test
	public void testGenerateVideo1Longest() {
	
		String configVideogen = "example1.videogen";
		
		String outVideogenConcat = VideoGenStation.genererLongestVideo(configVideogen);
		
		File f = new File(outVideogenConcat);
		assertTrue(f.exists());
		assertTrue(!f.isDirectory());
		
	}
	
	@Test
	public void testGenerateVideo2Longest() {
	
		String configVideogen = "example2.videogen";
		
		String outVideogenConcat = VideoGenStation.genererLongestVideo(configVideogen);
		
		File f = new File(outVideogenConcat);
		assertTrue(f.exists());
		assertTrue(!f.isDirectory());
		
	}
	
	
	@Test
	public void testGenerateVideo3Longest() {
	
		String configVideogen = "example3.videogen";
		
		String outVideogenConcat = VideoGenStation.genererLongestVideo(configVideogen);
		
		File f = new File(outVideogenConcat);
		assertTrue(f.exists());
		assertTrue(!f.isDirectory());
		
		/*Process child;
		try {
			child = Runtime.getRuntime().exec("vlc "+f.getAbsolutePath());
			child.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	@Test
	public void testGenerateVideo1Random() {
		String configVideogen = "example1.videogen";
		
		String outVideogenConcat = VideoGenStation.genererVideoRandom(configVideogen);
		
		File f = new File(outVideogenConcat);
		assertTrue(f.exists());
		assertTrue(!f.isDirectory());
 
	}
	
	@Test
	public void testGenerateVideo1RandomAndGif() {
		String configVideogen = "example1.videogen";

		String outVideogenConcat = VideoGenStation.genererVideoRandom(configVideogen);
		
		File f = new File(outVideogenConcat);
		assertTrue(f.exists());
		assertTrue(!f.isDirectory());
		
		String outVideoGif = VideoGenStation.genererGifWithVideo(outVideogenConcat);

		File fileGifPalette = new File(outVideoGif);
		assertTrue(fileGifPalette.exists());
		assertTrue(!fileGifPalette.isDirectory());

	}
	
	@Test
	public void testGenerateVideo4RandomAndGif() {
		String configVideogen = "example4.videogen";

		String outVideogenConcat = VideoGenStation.genererVideoRandom(configVideogen);
		
		File f = new File(outVideogenConcat);
		assertTrue(f.exists());
		assertTrue(!f.isDirectory());
		
		String outVideoGif = VideoGenStation.genererGifWithVideo(outVideogenConcat);

		File fileGifPalette = new File(outVideoGif);
		assertTrue(fileGifPalette.exists());
		assertTrue(!fileGifPalette.isDirectory());

		
		Process child;
		try {
			child = Runtime.getRuntime().exec("vlc "+f.getAbsolutePath());
			child.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testGenerateVideo5RandomAndGif() {
		String configVideogen = "example5.videogen";

		String outVideogenConcat = VideoGenStation.genererVideoRandom(configVideogen);
		
		File f = new File(outVideogenConcat);
		assertTrue(f.exists());
		assertTrue(!f.isDirectory());
		
		String outVideoGif = VideoGenStation.genererGifWithVideo(outVideogenConcat);

		File fileGifPalette = new File(outVideoGif);
		assertTrue(fileGifPalette.exists());
		assertTrue(!fileGifPalette.isDirectory());

		
		Process child;
		try {
			child = Runtime.getRuntime().exec("vlc "+f.getAbsolutePath());
			child.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
		
	
	@Test
	public void testGenerateVideo5LongestAndGif() {
		String configVideogen = "example5.videogen";

		String outVideogenConcat = VideoGenStation.genererLongestVideo(configVideogen);
		
		File f = new File(outVideogenConcat);
		assertTrue(f.exists());
		assertTrue(!f.isDirectory());
		
		String outVideoGif = VideoGenStation.genererGifWithVideo(outVideogenConcat);

		File fileGifPalette = new File(outVideoGif);
		assertTrue(fileGifPalette.exists());
		assertTrue(!fileGifPalette.isDirectory());

		
		Process child;
		try {
			child = Runtime.getRuntime().exec("vlc "+f.getAbsolutePath());
			child.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
		
	@Test
	public void testAllCombinaisonCsv() {

		
	}

	@Test
	public void testGenererAllVignettes() {

		
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

