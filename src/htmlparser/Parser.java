package htmlparser;
import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class Parser {
	private Pattern p = Pattern.compile("(\\D*)(\\s)((\\d*,\\d\\d\\s*-*\\s*){1,2}€?)(.*)");
	private Pattern p2 = Pattern.compile("(\\d{1,2})(.)(\\D{3})(\\D*)(\\d{4})(.*)");
	private Pattern p3 = Pattern.compile("");
	private Document docWeek;
	private Document docDay;
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
	JsonObject Speiseplan = new JsonObject();
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
		elementDay = docWeek.getElementsByClass("ppw-speiseplan-text");	
		
	}
	public Parser(String link) throws UnirestException
	{
		docWeek = getHtmlElement(link);
		elementWeek = docWeek.getElementsByClass("ppw-speiseplan-aussen-woche");
	}
	private Document getHtmlElement(String link) throws UnirestException
	{
		HttpResponse<String> htmlWeek = Unirest.get(link)
				.header("Content-Type", "application/json")
				.basicAuth("_ADPassword", "_ADPassword2016")
				.asString();
		Document htmldoc = Jsoup.parse(htmlWeek.getBody());
		return htmldoc;
	}
	public int getPlanTime() throws ParseException
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
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.WEEK_OF_YEAR);
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
	public JsonObject getWeekFood()
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
		System.out.println(gson.toJson(Speiseplan));
		return Speiseplan;
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
			case 1: Speiseplan.add("Monday", o);
					break;
			case 2: Speiseplan.add("Tueday", o);
					break;
			case 3: Speiseplan.add("Wednesday", o);
					break;
			case 4: Speiseplan.add("Thursday", o);
					break;
			case 5: Speiseplan.add("Friday", o);
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
	 
	public String getValidWeekLinks() throws UnirestException
	{
		Parser tmpParser = new Parser(docWeek.select("th.ppw-speiseplan-funktionen-rechts > a").attr("href").toString());
		if(tmpParser.isLinkValid())
		{
			tmpParser.getValidWeekLinks();
			
		}
		else
		{
			
		}
		return base64login;
	}
	public boolean isLinkValid()
	{
		Matcher m;
		m = p3.matcher(elementWeek.select("table").get(0).select("tr").get(1).select("td").get(1).text()); 
		return m.find();
	}
}