import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONObject;

class SentimentWeightedWordList extends HashMap<Integer,List<String>> {
	
	public static Integer SENTIMENT_NEGATIVE = 1;
	public static Integer SENTIMENT_NEUTRAL = 0;
	public static Integer SENTIMENT_POSITIVE = 2;	
	
	public Map<Integer,ArrayList<Integer>> weights;

	public SentimentWeightedWordList() {
		super();
		this.weights = new HashMap<Integer,ArrayList<Integer>>();
		
		this.weights.put(SENTIMENT_NEGATIVE, new ArrayList<Integer>());
		this.weights.put(SENTIMENT_POSITIVE, new ArrayList<Integer>());
		this.weights.put(SENTIMENT_NEUTRAL, new ArrayList<Integer>());
	}
	public void putWordAndWeight(String word, Integer weight) {
		if (!this.containsKey(weight))  {
			this.put(weight, new ArrayList<String>());
		}
		this.get(weight).add(word);
		if (weight>0 && !this.weights.get(SENTIMENT_POSITIVE).contains(weight)) {
			this.weights.get(SENTIMENT_POSITIVE).add(weight);
		} else if (weight<0 && !this.weights.get(SENTIMENT_NEGATIVE).contains(weight)) {
			this.weights.get(SENTIMENT_NEGATIVE).add(weight);
		} else if ( weight==0 && !this.weights.get(SENTIMENT_NEUTRAL).contains(weight)) {
			this.weights.get(SENTIMENT_NEUTRAL).add(weight);
		} 
	}
}

public class TwitterTestDataGenerator {

	public static int SENTIMENT_INCONSISTENT = -1;

	public SentimentWeightedWordList words;
	public SentimentWeightedWordList boosters;
	
	public List<String> brands;
	
	public Date startDate;
	public Date endDate;
	public int sentiment;
	
	public TwitterTestDataGenerator() {
		super();
		boosters = new SentimentWeightedWordList();
		words = new SentimentWeightedWordList();
		brands = new ArrayList<String>();
	}
	
	public JSONObject generateRandomTweet () {
		return complexTweet();
	}
	
	private JSONObject complexTweet() {
		Random r = new Random();
		
		String twitBrand = brands.get(r.nextInt(brands.size() - 1));
		Integer twitRandLength = r.nextInt(140 - twitBrand.length() - 1) + twitBrand.length() + 20;
		
		Integer rSentiment = r.nextInt(100);
		
		// Sentiment distribution
		if (rSentiment < 80 || (twitBrand == "Samsung" && rSentiment <= 90)) {
			sentiment = SentimentWeightedWordList.SENTIMENT_POSITIVE;
		} else {
			sentiment = SentimentWeightedWordList.SENTIMENT_NEGATIVE;
		}
		
		ArrayList<String> message = new ArrayList<String>();
		message.add(twitBrand);
		
		String msg;
		List<String> wordList;
		Integer msgLength = twitBrand.length();
		Integer randBooster;
		Integer weightKey;
		
		while (msgLength <= twitRandLength) {
			msg = "";
			randBooster = r.nextInt(100);
			if (sentiment == SENTIMENT_INCONSISTENT) {
				// Random sentiment for each word in tweet
				if (randBooster < 95) {
					wordList = words.get(words.keySet().toArray()[r.nextInt(words.size())]);
					msg = wordList.get(r.nextInt(wordList.size()));
				} else {
					wordList = boosters.get(boosters.keySet().toArray()[r.nextInt(boosters.size())]);
					msg = wordList.get(r.nextInt(wordList.size()));
				}
			} else {
				if (randBooster < 95) {
					weightKey = words.weights.get(sentiment).get( r.nextInt( words.weights.get(sentiment).size()) );
					wordList = words.get( weightKey );
					msg = wordList.get(r.nextInt(wordList.size()));
				} else if (randBooster>=95 && randBooster <= 96 ) { 
					// Neutral Sentiment is seldom (according to word data set), therefore just 1% neutral words should be included
					weightKey = words.weights.get(SentimentWeightedWordList.SENTIMENT_NEUTRAL).
							get( r.nextInt( words.weights.get(SentimentWeightedWordList.SENTIMENT_NEUTRAL).size()) );
					wordList = words.get( weightKey );
					msg = wordList.get(r.nextInt(wordList.size()));
				} else {
					if (sentiment == SentimentWeightedWordList.SENTIMENT_NEUTRAL) {
						weightKey = boosters.weights.get(SentimentWeightedWordList.SENTIMENT_POSITIVE).
								get( r.nextInt( boosters.weights.get(SentimentWeightedWordList.SENTIMENT_POSITIVE).size() ) );
					} else {
						weightKey = boosters.weights.get(sentiment).get( r.nextInt( boosters.weights.get(sentiment).size() ) );
					}
					wordList = boosters.get( weightKey );
					msg = wordList.get(r.nextInt(wordList.size()));
				}
			}
			msgLength += msg.length() + 1; // calculating the length of the tweet with the new word and a space character
			if (msgLength <= twitRandLength) {
				message.add(msg);
			}
		}
	
		String[] msgArray = message.toArray(new String[message.size()]);
		shuffleArray(msgArray);
		msg = strJoin(msgArray, " ");
		
		Date d = new Date();
		if (endDate == null) {
			endDate = new Date();
		}
		if (startDate != null) {
			long diff = endDate.getTime()-startDate.getTime() + 1;
			d = new Date(startDate.getTime() + (long)(Math.random() * diff));
		}
		
		JSONObject obj = new JSONObject();
		obj.put("id", (int)( Math.random() * 1000000));
		obj.put("created_at", d.toString());
		obj.put("message", msg);
		
		return obj;
	}
	 static void shuffleArray(Object[] ar)
	  {
	    Random r = new Random();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = r.nextInt(i + 1);
	      Object a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }
	 public static String strJoin(String[] aArr, String sSep) {
		    StringBuilder sbStr = new StringBuilder();
		    for (int i = 0, il = aArr.length; i < il; i++) {
		        if (i > 0)
		            sbStr.append(sSep);
		        sbStr.append(aArr[i]);
		    }
		    return sbStr.toString();
	}
	private JSONObject simpleTweet() {
		double rBrand = Math.random();
		double rSentiment = Math.random();
		String brand;
		String sentiment;
		if (rBrand < 0.25) {
			brand = "Samsung";
			if (rSentiment < 0.33) {
				sentiment = "bad";
			} else if (rSentiment > 0.66) {
				sentiment = "ok";
			} else {
				sentiment = "cool";
			}
		} else if (rBrand > 0.66) {
			brand = "Apple";
			if (rSentiment < 0.25) {
				sentiment = "bad";
			} else if (rSentiment > 0.50) {
				sentiment = "ok";
			} else {
				sentiment = "cool";
			}
		} else {
			brand = "RIM";
			if (rSentiment < 0.50) {
				sentiment = "bad";
			} else if (rSentiment > 0.45) {
				sentiment = "ok";
			} else {
				sentiment = "cool";
			}
		}
		JSONObject obj = new JSONObject();
		obj.put("id", (int)( Math.random() * 1000000));
		Date d = new Date();
		obj.put("created_at", d.toString());
		obj.put("message", "Hey guys, I think " + brand + " is "
				+ sentiment);
		
		return obj;
	}
}
