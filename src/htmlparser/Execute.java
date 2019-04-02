package htmlparser;
import java.io.IOException;

import com.mashape.unirest.http.exceptions.UnirestException;
public class Execute {


	public static void main(String[] args) throws IOException, UnirestException {
		//File file = new File("speiseplan_woche/Deutsche Welle Intranet  Wochenansicht Bonn.html");
		
		Parser parser = new Parser();
		parser.getMeal();
		
		
	}
	
	 
}
