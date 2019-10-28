package SpeiseplanParser;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import com.mashape.unirest.http.exceptions.UnirestException;

import POJO_Weekplan.Plan;
public class Test {


	public static void main(String[] args) throws IOException, UnirestException, ParseException {
		File file = new File("speiseplan_woche/Deutsche Welle Intranet  Wochenansicht Bonn.html");
		
		Parser parser = new Parser();
		POJO_Dayplan.Spezial day  = parser.getSpecialFood();
		System.out.println(day.getDescription());

	}
	
	 
}
