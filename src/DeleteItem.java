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

@ManagedBean(name="delete")			//Bean med navnet "delete"
@SessionScoped
public class DeleteItem {
	
	private String id;

	public void sletItem()
	{
		String SHOPKEY = "3040F78236D8073C8C692612";
		Namespace ns = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");
			
		//Laver rodelementet deleteItem og dets child-elementer.
		Element deleteItem = new Element("deleteItem",ns);
		Element shopKey = new Element("shopKey", ns);
		Element itemID = new Element("itemID", ns);
		
		shopKey.setText(SHOPKEY);			//Indsætter variable i vores elementer.
		itemID.setText(id);

		Document d = new Document();
			
	d.setRootElement(deleteItem);					//Sætter deleteItem som rodelement og de resterende
		d.getRootElement().addContent(shopKey);		//elementer sættes som deleteItems børn. 
		d.getRootElement().addContent(itemID);
		
		try{
			//Her kaldes til den relevante URL på cloudserveren
			URL cloud = new URL("http://services.brics.dk/java4/cloud/deleteItem");
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

			System.out.println(responseNumCode);	// Responskoden fra clouden udskrives.
				
		} catch (IOException i) { i.printStackTrace(); }
		catch (Exception e){ e.printStackTrace(); }
		}

	public String getId() {			//Gettere og settere.
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
}



