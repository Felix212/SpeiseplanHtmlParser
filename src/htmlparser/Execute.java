package htmlparser;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import com.mashape.unirest.http.exceptions.UnirestException;
public class Execute {


	public static void main(String[] args) throws IOException, UnirestException {
		File file = new File("speiseplan_woche/Deutsche Welle Intranet  Wochenansicht Bonn.html");
		
		Parser parser = new Parser(file);
		try {
			parser.getPlanTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	 
}
