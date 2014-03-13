import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.*;

public class SAXModifier {

	public static void main(String[] args) 
	{ 
		try 
		{ 
			SAXBuilder b = new SAXBuilder(); 
			Document d = b.build(new File("C://Users//Magnus//WebTek//webshop//WebContent//item2.xml"));  
			d.getRootElement().getChild("modifyItem");

			URL cloud = new URL("http://services.brics.dk/java4/cloud/modifyItem");
			HttpURLConnection connection = (HttpURLConnection) cloud.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);

			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat()); 
			outputter.output(d, connection.getOutputStream());
			outputter.output(d, System.out);

			int responseNumCode = connection.getResponseCode();
			BufferedReader input = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			String output;
			while((output = input.readLine()) != null)
			{
				System.out.println(output);
			}
		} catch (IOException i) { i.printStackTrace(); }
		catch (Exception e){ e.printStackTrace(); }
		


	}

}