package htmlparser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.google.gson.*;

public class Days {
	private Pattern p = Pattern.compile("(\\D*)((\\d*,\\d\\d\\s*-*\\s*)*€)");
	private File input;
	private Document doc;
	//Menues
	private Elements element;
	private int proFunkMenue = 0;
	private int lunchMenue = 5;
	private int gourmetMenue = 10;
	private int menueBase = 6;
	public Days(File in) throws IOException {
		input = in;
		doc = Jsoup.parse(input, "UTF-8");
		element =  doc.getElementsByClass("ppw-speiseplan-woche");
	}
	public Days(String link) throws IOException
	{
		doc = Jsoup.connect(link).get();
		element = doc.getElementsByClass("ppw-speiseplan-woche");
	}
	public void GetSpeiseplan(String filename) throws IOException
	{
		JsonObject Speiseplan = new JsonObject();
		Speiseplan.add("Speiseplan", CreatWeekJSONObject());
		FileWriter file = new FileWriter(filename);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(Speiseplan.toString());
        file.write(gson.toJson(je));
        file.flush();
        file.close();
	}
	public JsonObject GetFood(DayOfWeek day)
	{
		JsonObject data = new JsonObject();
		Matcher m;
		//Funk
		
		JsonArray funk = new JsonArray();
		funk.add(element.get((menueBase)+(day.getValue()-1)+proFunkMenue).getElementsByTag("p").text());
		m = p.matcher(element.get(((menueBase)+(day.getValue()-1)+proFunkMenue)).getElementsByTag("div").text());
		m.find();
		funk.add(m.group(2));
		
		//Lunch
		
		JsonArray lunch = new JsonArray();
		lunch.add(element.get((menueBase)+(day.getValue()-1)+lunchMenue).getElementsByTag("p").text());
		m = p.matcher(element.get(((menueBase)+(day.getValue()-1)+lunchMenue)).getElementsByTag("div").text());
		m.find();
		lunch.add(m.group(2));

		//Gourmet
		
		JsonArray gourmet = new JsonArray();
		gourmet.add(element.get((menueBase)+(day.getValue()-1)+gourmetMenue).getElementsByTag("p").text());
		m = p.matcher(element.get(((menueBase)+(day.getValue()-1)+gourmetMenue)).getElementsByTag("div").text());
		m.find();
		gourmet.add(m.group(2));
		
		//add arrays to data object
		
		data.add("funk", funk);
		data.add("lunch", lunch);
		data.add("gourmet", gourmet);
		return data;
		
	}
	public JsonObject CreatWeekJSONObject()
	{
		JsonObject week = new JsonObject();
		week.add("Monday", GetFood(DayOfWeek.MONDAY));
		week.add("Tuesday", GetFood(DayOfWeek.TUESDAY));	
		week.add("Wednesday",GetFood(DayOfWeek.WEDNESDAY));	
		week.add("Thursday",GetFood(DayOfWeek.THURSDAY));	
		week.add("Friday", GetFood(DayOfWeek.FRIDAY));
		return week;
	}
}
