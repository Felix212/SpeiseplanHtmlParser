package SpeiseplanParser;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import POJO_Weekplan.Friday;
import POJO_Weekplan.Monday;
import POJO_Weekplan.Plan;
import POJO_Weekplan.Thursday;
import POJO_Weekplan.Tuesday;
import POJO_Weekplan.Wednesday;


public class Parser {
	private Pattern p = Pattern.compile("(^.*)(\\s)+((\\d*,\\d\\d\\s*-?\\s*){1,2}\\s?€?)");
	private Pattern p2 = Pattern.compile("(\\d{1,2})(.)(\\D{3})(\\D*)(\\d{4})(.*)");
	private Pattern p3 = Pattern.compile("Es wurde noch keine Speise eingetragen");
	private Pattern p4 = Pattern.compile("(\\D*)(\\d{1,2})(.*)");
	private Document docWeek;
	private Document docDay;
	//Login
	String username = "_ADPassword";
	String password = "_ADPassword2016";
	String login = username + ":" + password;
	String base64login = new String(Base64.getEncoder().encode(login.getBytes()));
	
	//Menues
	private Elements elementDay;
	private Elements elementDayDate;
	private int tagesSuppe = 0;
	private int veggie = 1;
	private int dessert = 2;
	private Elements elementWeek;
	JsonObject Speiseplan = new JsonObject();
	public String Link;
	public String JsonSpeiseplan;
	Monday mon = new Monday();
	Tuesday die = new Tuesday();
	Wednesday mit = new Wednesday();
	Thursday don = new Thursday();
	Friday fre = new Friday();
	Plan Speiseplanobj = new Plan();
	POJO_Dayplan.Plan Tagesplanobj = new POJO_Dayplan.Plan();
	public Parser(File in) throws IOException
	{
		docWeek = Jsoup.parse(in, "UTF-8");
		elementWeek = docWeek.getElementsByClass("ppw-speiseplan-aussen-woche");
	}
	public Parser() throws IOException, UnirestException
	{
		
		//WeekParser
		docWeek = getHtmlElement("http://intranet.dw.com/top-menu/speiseplan/speiseplan-bonn/tagesansicht/wochenansicht-bonn.html");
		elementWeek = docWeek.getElementsByClass("ppw-speiseplan-aussen-woche");
		//DayParser
		docDay = getHtmlElement("http://intranet.dw.com/top-menu/speiseplan/speiseplan-bonn/tagesansicht.html");
		elementDay = docDay.getElementsByClass("ppw-speiseplan-text");	
		elementDayDate = docDay.getElementsByClass("csc-header csc-header-n13");	
		
	}
	public Parser(String link) throws UnirestException
	{
		docWeek = getHtmlElement(link);
		elementWeek = docWeek.getElementsByClass("ppw-speiseplan-aussen-woche");
		Link = link;
	}
	private Document getHtmlElement(String link) throws UnirestException
	{
		HttpResponse<String> htmlWeek = Unirest.get(link)
				.header("Content-Type",
						"application/json")
						.basicAuth("_ADPassword", "_ADPassword2016")
						.asString();
		Document htmldoc = Jsoup.parse(htmlWeek.getBody());
		return htmldoc;
	}
	public DateTime getPlanTime() throws ParseException
	{
		
		Matcher m = p2.matcher(docWeek.getElementsByClass("ppw-speiseplan-kopf-rechts").text());
		m.find();
		DateTimeFormatter parser = DateTimeFormatter.ofPattern("MMM").withLocale(Locale.GERMAN);
		TemporalAccessor accessor = parser.parse(m.group(3));
		int month = accessor.get(ChronoField.MONTH_OF_YEAR);
		int year = Integer.parseInt(m.group(5));
		int day = Integer.parseInt(m.group(1));
		String ms = String.format("%02d", month);
		String ds = String.format("%02d", day);
		String ys = Integer.toString(year);
		String dateString = ds+ms+ys;
		DateFormat df = new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH);
		Date date = df.parse(dateString);
		DateTime dt = new DateTime(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return dt;
		
	}
	public POJO_Dayplan.Plan getDayFood()
	{
		Matcher m;
		m = p.matcher(elementDay.get(tagesSuppe).text());
		m.find();
		try {
			Tagesplanobj.setSuppeBeschreibung(m.group(1));
			Tagesplanobj.setSuppePreis(m.group(3));
		} catch (Exception e) {
			System.out.println("Could not find Suppe");
			System.out.println(e);
		}
		
		
		m = p.matcher(elementDay.get(veggie).text());
		m.find();
		try {
			Tagesplanobj.setVeggieBeschreibung(m.group(1));
			Tagesplanobj.setVeggiePreis(m.group(3));
		} catch (Exception e) {
			System.out.println("Could not find Veggie");
			System.out.println(e);
		}
		
		m = p.matcher(elementDay.get(dessert).text());
		m.find();
		try {
			Tagesplanobj.setDessertBeschreibung(m.group(1));
			Tagesplanobj.setDessertPreis(m.group(3));
		} catch (Exception e) {
			System.out.println("Could not find Dessert");
			System.out.println(e);
		}
		
		return Tagesplanobj;
	}
	public boolean TagesPlanUpdated() 
	{
		Matcher m;
		m = p4.matcher(elementDayDate.select("h1").text());
		m.find();
		int dateIntranet = Integer.valueOf(m.group(2));
		DateTime dt = new DateTime();
		return dt.getDayOfMonth() == dateIntranet;
	}
	public POJO_Weekplan.Plan getWeekFood()
	{
		Element table = elementWeek.select("table").get(0);
		Elements rows = table.select("tr");
		Matcher m;	
		int fehlendertag = 0;
		int[] feierTage =  checkFeiertage(rows);
		for (int i = 1; i < 6; i++) {
			JsonObject data = new JsonObject();
			for (int j = 1; j < rows.size(); j++) 
			{
				Elements tmp = rows.get(j).select("td");
				if(feierTage[i-1] == 1)
				{
					fehlendertag = 1;
				}
				//Wenn erste Zeile --> Feiertag überspringen
				if(rows.get(j).select("td").size() == 6 && feierTage[i-1] == 1 && rows.get(j).select("td").get(i).attr("rowspan").contains("3"))
				{
					break;
				}
				else
				{
					Element row = rows.get(j);
					Elements cols = row.select("td");
					//Fallunterscheidung für Zeilen mit 5 tr Elementen und Zeilen 6 tr --> vorheriges aus i auswählen wenn 5 tr Elementen
					if(tmp.size() == 5 && fehlendertag == 1)
					{
						m = p.matcher(cols.get(i-1).text());
					}
					else
					{
						m = p.matcher(cols.get(i).text());
					}
					m.find();
					data.addProperty(getMenueName(j)+"Beschreibung", m.group(1));
					data.addProperty(getMenueName(j)+"Preis", m.group(3));
				}			
			}
			addToTag(i, data);	
		}
		Speiseplanobj.setMonday(mon);
		Speiseplanobj.setTuesday(die);
		Speiseplanobj.setWednesday(mit);
		Speiseplanobj.setThursday(don);
		Speiseplanobj.setFriday(fre);
		//JsonSpeiseplan = gson.toJson(Speiseplan);
		//System.out.println(JsonSpeiseplan.toString());
		return Speiseplanobj;
	}
	private String getMenueName(int m)
	{
		String MenueNumber = "";
		switch(m)
		{
			case 1: MenueNumber = "Funk";
			break;
			case 2: MenueNumber = "Lunch";
			break;
			case 3: MenueNumber = "Gourmet";
			break;
		}
		return MenueNumber;
	}
	private void addToTag(int tag, JsonObject o)
	{
		
		switch(tag)
		{
			case 1: 
				if(o.has("FunkBeschreibung") && o.has("LunchBeschreibung") && o.has("GourmetBeschreibung"))
				{
					mon.setFunkBeschreibung(o.get("FunkBeschreibung").getAsString());
					mon.setFunkPreis(o.get("FunkPreis").getAsString());
					mon.setLunchBeschreibung(o.get("LunchBeschreibung").getAsString());
					mon.setLunchPreis(o.get("LunchPreis").getAsString());
					mon.setGourmetBeschreibung(o.get("GourmetBeschreibung").getAsString());
					mon.setGourmetPreis(o.get("GourmetPreis").getAsString());
				}
					break;
			case 2: 
				if(o.has("FunkBeschreibung") && o.has("LunchBeschreibung") && o.has("GourmetBeschreibung"))
				{
					die.setFunkBeschreibung(o.get("FunkBeschreibung").getAsString());
					die.setFunkPreis(o.get("FunkPreis").getAsString());
					die.setLunchBeschreibung(o.get("LunchBeschreibung").getAsString());
					die.setLunchPreis(o.get("LunchPreis").getAsString());
					die.setGourmetBeschreibung(o.get("GourmetBeschreibung").getAsString());
					die.setGourmetPreis(o.get("GourmetPreis").getAsString());
				}
					
					break;
			case 3:
				if(o.has("FunkBeschreibung") && o.has("LunchBeschreibung") && o.has("GourmetBeschreibung"))
				{
					mit.setFunkBeschreibung(o.get("FunkBeschreibung").getAsString());
					mit.setFunkPreis(o.get("FunkPreis").getAsString());
					mit.setLunchBeschreibung(o.get("LunchBeschreibung").getAsString());
					mit.setLunchPreis(o.get("LunchPreis").getAsString());
					mit.setGourmetBeschreibung(o.get("GourmetBeschreibung").getAsString());
					mit.setGourmetPreis(o.get("GourmetPreis").getAsString());
				}
					
					break;
			case 4: 
				if(o.has("FunkBeschreibung") && o.has("LunchBeschreibung") && o.has("GourmetBeschreibung"))
				{
					don.setFunkBeschreibung(o.get("FunkBeschreibung").getAsString());
					don.setFunkPreis(o.get("FunkPreis").getAsString());
					don.setLunchBeschreibung(o.get("LunchBeschreibung").getAsString());
					don.setLunchPreis(o.get("LunchPreis").getAsString());
					don.setGourmetBeschreibung(o.get("GourmetBeschreibung").getAsString());
					don.setGourmetPreis(o.get("GourmetPreis").getAsString());
				}
					break;
			case 5: 
					fre.setFunkBeschreibung(o.get("FunkBeschreibung").getAsString());
					fre.setFunkPreis(o.get("FunkPreis").getAsString());
					fre.setLunchBeschreibung(o.get("LunchBeschreibung").getAsString());
					fre.setLunchPreis(o.get("LunchPreis").getAsString());
					fre.setGourmetBeschreibung(o.get("GourmetBeschreibung").getAsString());
					fre.setGourmetPreis(o.get("GourmetPreis").getAsString());
					break;
		}
	}
	public int[] checkFeiertage(Elements rows)
	{
		int[] feierTage = {0,0,0,0,0};
		for (int i = 1; i < 6; i++) {
			if(rows.get(1).select("td").get(i).attr("rowspan").contains("3"))
			{
				feierTage[i-1] = 1;
			}
			
		}
		return feierTage;
	}
	 
	public String getNextLink() throws UnirestException
	{
		Parser tmpParser = new Parser("http://intranet.dw.com/" + docWeek.select("th.ppw-speiseplan-funktionen-rechts > a").attr("href").toString());
		return tmpParser.Link;
		
	}
	public boolean isLinkValid()
	{
		Matcher m = p3.matcher(elementWeek.select("table").get(0).select("tr").get(1).select("td").get(1).text()); 
		return !m.find();
	}
}