package htmlparser;
import java.io.File;
import java.io.IOException;
public class htmlparser {


	public static void main(String[] args) throws IOException {
		File file = new File("speiseplan_woche/Deutsche Welle Intranet  Wochenansicht Bonn.html");
		String link = "intranet.dw.com";
		Days days = new Days(file);
		days.GetSpeiseplan("Speiseplan.json");
	}
	
	 
}
