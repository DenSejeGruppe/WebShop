import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

@ManagedBean(name="adjust")		//Bean med navnet "adjust"
@SessionScoped
public class AdjustItem {	
	private int newStock;			//Elementernes variable
	private String id;
	
	public void adjustItem(){
		
		String SHOPKEY = "3040F78236D8073C8C692612";
		Namespace ns = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");
		
		//Laver rodelementet adjustItem og dets child elementer.
		Element adjustItem = new Element("adjustItemStock", ns);
		Element itemID = new Element("itemID", ns);
		Element shopKey = new Element("shopKey", ns);
		Element adjustment = new Element("adjustment", ns);
		
		shopKey.setText(SHOPKEY);					//Indsætter variable i vores elementer.
		itemID.setText(id);
		adjustment.setText(Integer.toString(newStock));
		
		Document d = new Document();
		d.setRootElement(adjustItem);				//Sætter adjustItem som rodelement og de resterende
		d.getRootElement().addContent(shopKey);		//elementer sættes som adjustItems børn. 
		d.getRootElement().addContent(itemID);
		d.getRootElement().addContent(adjustment);
		
		try{
			//Her kaldes til den relevante URL på cloudserveren.
			URL cloud = new URL("http://services.brics.dk/java4/cloud/adjustItemStock");
			HttpURLConnection connection = (HttpURLConnection) cloud.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.connect();
			
			//Her sendes vores XML-dokument til cloudserveren. 
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat()); 
			outputter.output(d, connection.getOutputStream());
			outputter.output(d, System.out);	
			
			int responseNumCode = connection.getResponseCode();
			connection.disconnect();
			
			System.out.println(responseNumCode);  // Responskoden fra clouden udskrives.
		} catch (IOException i) { i.printStackTrace(); }
		catch (Exception e){ e.printStackTrace(); }
	}
	public int getNewStock() {				//gettere og settere.
		return newStock;
	}
	public void setNewStock(int newStock) {
		this.newStock = newStock;
	}
	public String getId() {
		return id;
	}
	public void setItemID(String id) {
		this.id = id;
	}
}