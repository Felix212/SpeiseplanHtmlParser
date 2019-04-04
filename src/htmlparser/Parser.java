package htmlparser;
import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Base64;
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
	JsonObject Speiseplan = new JsonObject();
	public Parser(File in) throws IOException
	{
		doc = Jsoup.parse(in, "UTF-8");
		elementWeek = doc.getElementsByClass("ppw-speiseplan-aussen-woche");
	}
	public Parser() throws IOException, UnirestException
	{
		//WeekParser
		HttpResponse<String> htmlWeek = Unirest.get("http://intranet.dw.com/top-menu/speiseplan/speiseplan-bonn/tagesansicht/wochenansicht-bonn.html?tx_ppwlunchmenu_pi1%5Bforward%5D=1&tx_ppwlunchmenu_pi1%5Btime%5D=1555574400&tx_ppwlunchmenu_pi1%5Blanguage%5D=de&cHash=570aa9026b534cefd2417cc10ca401a8").header("Content-Type",
				"application/json")
				.basicAuth("_ADPassword", "_ADPassword2016")
				.asString();
		doc = Jsoup.parse(htmlWeek.getBody());
		elementWeek = doc.getElementsByClass("ppw-speiseplan-aussen-woche");
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
        
        return null;
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
}