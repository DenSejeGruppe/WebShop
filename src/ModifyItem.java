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

@ManagedBean(name="modify")			//Bean med navnet "modify"
@SessionScoped
public class ModifyItem {
	
	private String modify;				//Elementernes variable
	private String name;
	private String id;
	private String price;
	private String URL;
	private String description;
	
	public void modify(){
		
		String SHOPKEY = "3040F78236D8073C8C692612";
		Namespace ns = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");
		
	try{		//Laver rodelementet modifyItem og dets børneelementer.
		Element modifyItem = new Element("modifyItem", ns);
		Element shopKey = new Element("shopKey", ns);			
		Element itemID = new Element("itemID", ns);
		Element itemName = new Element("itemName", ns);
		Element itemPrice = new Element("itemPrice", ns);
		Element itemURL = new Element("itemURL", ns);
		Element document = new Element("document", ns);
		Element itemDescription = new Element("itemDescription", ns);
		
		modifyItem.setText(modify);
		shopKey.setText(SHOPKEY);		//Indsætter variable i vores elementer.
		itemID.setText(id);
		itemName.setText(name);
		itemPrice.setText(price);
		itemURL.setText(URL);
		document.setText(description);
		itemDescription.addContent(document);
		
		Document d = new Document();
		
		d.setRootElement(modifyItem);					//Sætter modifyItem som rodelement og de resterende
		d.getRootElement().addContent(shopKey);			//elementer sættes som modifyItems børn. 
		d.getRootElement().addContent(itemID);
		d.getRootElement().addContent(itemName);
		d.getRootElement().addContent(itemPrice);
		d.getRootElement().addContent(itemURL);
		d.getRootElement().addContent(itemDescription);

		//Her kaldes til den relevante URL på cloudserveren.
		URL cloud = new URL("http://services.brics.dk/java4/cloud/modifyItem");
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

		System.out.println(responseNumCode);		// Responskoden fra clouden udskrives.
		
	} catch (IOException i) { i.printStackTrace(); }
	catch (Exception e){ e.printStackTrace(); }
}

	public String getMod() {		//settere og gettere .....
		return modify;
	}

	public void setMod(String mod) {
		this.modify = mod;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}


