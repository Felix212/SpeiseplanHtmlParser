package htmlparser;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class Parser {
	private Pattern p = Pattern.compile("(\\D*)(\\s)((\\d*,\\d\\d\\s*-*\\s*)*€?)(.*)");
	private Document doc;
	//Login
	String username = "_ADPassword";
	String password = "_ADPassword2016";
	String login = username + ":" + password;
	String base64login = new String(Base64.getEncoder().encode(login.getBytes()));
	//Menues
	private Elements elementDay;
	private int tagesSuppe = 0;
	private int veggie = 1;
	private int dessert = 2;
	private Elements elementWeek;
	private int proFunkMenue = 0;
	private int lunchMenue = 5;
	private int gourmetMenue = 10;
	private int menueBase = 6;
	public Parser() throws IOException, UnirestException
	{
		//WeekParser
		HttpResponse<String> htmlWeek = Unirest.get("http://intranet.dw.com/top-menu/speiseplan/speiseplan-bonn/tagesansicht/wochenansicht-bonn.html").header("Content-Type",
				"application/json")
				.basicAuth("_ADPassword", "_ADPassword2016")
				.asString();
		doc = Jsoup.parse(htmlWeek.getBody());
		elementWeek = doc.getElementsByClass("ppw-speiseplan-woche");
		//DayParser
		HttpResponse<String> htmlDay = Unirest.get("http://intranet.dw.com/top-menu/speiseplan/speiseplan-bonn/tagesansicht.html").header("Content-Type",
				"application/json")
				.basicAuth("_ADPassword", "_ADPassword2016")
				.asString();
		doc = Jsoup.parse(htmlDay.getBody());
		elementDay = doc.getElementsByClass("ppw-speiseplan-text");		
	}
	public POJO_Day.Plan getDay()
	{
		JsonObject TagesPlan = new JsonObject();
		Matcher m;
		m = p.matcher(elementDay.get(tagesSuppe).text());
		m.find();
		TagesPlan.addProperty("SuppeBeschreibung", m.group(1));
		TagesPlan.addProperty("SuppePreis", m.group(3));
		m = p.matcher(elementDay.get(veggie).text());
		m.find();
		TagesPlan.addProperty("VeggieBeschreibung", m.group(1));
		TagesPlan.addProperty("VeggiePreis", m.group(3));
		m = p.matcher(elementDay.get(dessert).text());
		m.find();
		TagesPlan.addProperty("DessertBeschreibung", m.group(1));
		TagesPlan.addProperty("DessertPreis", m.group(3));
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.fromJson(TagesPlan, POJO_Day.Plan.class);
	}
	public POJO_Week.Plan getWeek() throws IOException
	{
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(creatWeekJSONObject(), POJO_Week.Plan.class);
	}
	private JsonObject getFood(DayOfWeek day)
	{
		JsonObject data = new JsonObject();
		Matcher m;
		//Funk
		
		m = p.matcher(elementWeek.get(((menueBase)+(day.getValue()-1)+proFunkMenue)).getElementsByTag("div").text());
		m.find();
		data.addProperty("FunkBeschreibung" , m.group(1));
		data.addProperty("FunkPreis",m.group(3));
		
		//Lunch
		
		m = p.matcher(elementWeek.get(((menueBase)+(day.getValue()-1)+lunchMenue)).getElementsByTag("div").text());
		m.find();
		data.addProperty("LunchBeschreibung" ,m.group(1));
		data.addProperty("LunchPreis",m.group(3));

		//Gourmet
		
		m = p.matcher(elementWeek.get(((menueBase)+(day.getValue()-1)+gourmetMenue)).getElementsByTag("div").text());
		m.find();
		data.addProperty("GourmetBeschreibung",m.group(1));
		data.addProperty("GourmetPreis",m.group(3));
		
		return data;
		
	}
	private JsonObject creatWeekJSONObject()
	{
		JsonObject week = new JsonObject();
		week.add("Monday", getFood(DayOfWeek.MONDAY));
		week.add("Tuesday", getFood(DayOfWeek.TUESDAY));	
		week.add("Wednesday",getFood(DayOfWeek.WEDNESDAY));	
		week.add("Thursday",getFood(DayOfWeek.THURSDAY));	
		week.add("Friday", getFood(DayOfWeek.FRIDAY));
		return week;
	}
}
