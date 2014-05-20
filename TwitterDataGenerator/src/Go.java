import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Date;
import java.util.Locale;

import org.json.simple.JSONObject;

import au.com.bytecode.opencsv.*;

public class Go {
	
	public static void main(String args[]) {
		
		TwitterTestDataGenerator twitGen = new TwitterTestDataGenerator();
		
		CSVReader reader;
		
		try {
			reader = new CSVReader(new FileReader("data/boosters_en.csv"));
			
			String[] nextLine;
			Integer weight;
			
			reader.readNext(); // skip header line
			
			while ((nextLine = reader.readNext()) != null) {
				twitGen.boosters.putWordAndWeight( nextLine[0], Integer.parseInt(nextLine[1]) );
			}
			
			reader = new CSVReader(new FileReader("data/words_en.csv"));
			
			reader.readNext(); // skip header line
			
			while ((nextLine = reader.readNext()) != null) {
				twitGen.words.putWordAndWeight( nextLine[0], Integer.parseInt(nextLine[1]) );
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		twitGen.brands.addAll( Arrays.asList("Apple","Samsung","RIM","HTC","Sony") );
		try {
			twitGen.startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2005-01-02");
		} catch (ParseException e) {}
		
	    try {
		    System.out.println("--- Tweet Generator started. Saving tweets to 'output.json'");
	    	PrintWriter writer = new PrintWriter("output.json", "UTF-8");
			String tweet;
			for (int i = 0; i < 1000000; i++) {
				tweet = twitGen.generateRandomTweet().toString();
				writer.println(tweet);
				if (i%10000 == 0 ) System.out.println("Tweet no. "+i+": "+tweet);  // print every 100 tweet for monitoring
			}
		    writer.close();
		    System.out.println("--- Tweet Generator stopped.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
