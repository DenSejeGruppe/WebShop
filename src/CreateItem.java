import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.catalina.filters.CsrfPreventionFilter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

@ManagedBean(name="create")		//Bean med navnet "modify"
@SessionScoped
public class CreateItem {
	
	private String name;		//Elementernes variable

	public void lavItem()
	{
		String SHOPKEY = "3040F78236D8073C8C692612";
		Namespace ns = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");

		try{		
			SAXBuilder b = new SAXBuilder(); 	//Skaber en SAXBuilder for at modtage respons fra cloud. 
			Element createItem = new Element("createItem", ns);		//Laver rodelementet createItem og dets børneelementer.
			Element shopkey = new Element("shopKey", ns);
			Element itemName = new Element("itemName", ns);
			
			shopkey.setText(SHOPKEY);		//Indsætter variable i vores elementer.
			itemName.setText(name);

			Document d = new Document();
			
			d.setRootElement(createItem);		//Sætter createItem som rodelement og de resterende
			d.getRootElement().addContent(shopkey);		//elementer sættes som createItems børn. 
			d.getRootElement().addContent(itemName);

			//Her kaldes til den relevante URL på cloudserveren.
			URL cloud = new URL("http://services.brics.dk/java4/cloud/createItem");
			HttpURLConnection connection = (HttpURLConnection) cloud.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.connect();

			//Her sendes vores XML-dokument til cloudserveren. 
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat()); 
			outputter.output(d, connection.getOutputStream());
			outputter.output(d, System.out);	

			int responseNumCode = connection.getResponseCode();
			
			//response er det dokument der tager imod den respons vi får fra cloud.
			//Rodelementet i response(itemID) printes ud. 
			Document response = b.build(connection.getInputStream());
			System.out.println(response.getRootElement().getValue());	
			System.out.println(responseNumCode);	// Responskoden fra clouden udskrives.
				
		} catch (IOException i) { i.printStackTrace(); }
		catch (Exception e){ e.printStackTrace(); }
		}

	public String getName() {		//gettere og settere.
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}



