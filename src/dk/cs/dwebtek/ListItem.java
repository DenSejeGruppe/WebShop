package dk.cs.dwebtek;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.imageio.ImageIO;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("shop")
public class ListItem {

	ArrayList<ManageListData> theList = new ArrayList<ManageListData>();
	Namespace ns = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");
	Namespace nas = Namespace.getNamespace("http://www.w3.org/TR/html4");
	JSONArray array = new JSONArray();
	String username;
	String password;

	@GET
	@Path("items")
	public String listItem(){

		try{
			// Opening two connections to the cloud server
			URL cloud = new URL("http://services.brics.dk/java4/cloud/listItems?shopID=277");
			URL deleted = new URL("http://services.brics.dk/java4/cloud//listDeletedItemIDs?shopID=277");
			HttpURLConnection connection = (HttpURLConnection) cloud.openConnection();
			HttpURLConnection connection2 = (HttpURLConnection) deleted.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();

			connection2.setRequestMethod("GET");
			connection2.setDoInput(true);
			connection2.setDoOutput(true);
			connection2.connect();


			// Reading the input from the server
			SAXBuilder b = new SAXBuilder();
			Document d = b.build(connection.getInputStream());
			Document del = b.build(connection2.getInputStream());

			Element delE = del.getRootElement();
			List<Element> delList = delE.getChildren();

			Element root = d.getRootElement();
			List<Element> list = root.getChildren();

			// Goes through all the Elements in list and makes deleted items disappear from the admin site
			for(Element e : list){
				Element klon = e.clone();
				boolean booldel = false;
				String klonId = klon.getChild("itemID", ns).getText();
				for(Element delle: delList){
					String delleId = delle.getText();

					if(klonId.equals(delleId)){
						booldel = true;
						break;
					}
				}
				if(!booldel){			
					ManageListData mld = new ManageListData(klon);
					theList.add(mld);
				}
			}

			for(ManageListData e: theList){
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("ID", e.getID());
				jsonObject1.put("Name", e.getName());
				jsonObject1.put("Price", e.getPrice());
				jsonObject1.put("Description", e.getDescription());
				jsonObject1.put("Stock", e.getStock());
				jsonObject1.put("InBasket", 0);

				array.put(jsonObject1);
			}

		} catch (IOException i) {i.printStackTrace();}
		catch (Exception e){e.printStackTrace();}
		return array.toString();
	}

	@POST
	@Path("createCustomer")
	public String createCustomer(@FormParam("username") String username,
			@FormParam("password") String password){

		String SHOPKEY = "3040F78236D8073C8C692612";
		Namespace ns = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");

		try{		
			SAXBuilder b = new SAXBuilder(); 
			Element createCustomer = new Element("createCustomer", ns);		
			Element shopKey = new Element("shopKey", ns);
			Element customerName = new Element("customerName", ns);
			Element customerPass = new Element("customerPass", ns);

			shopKey.setText(SHOPKEY);
			customerName.setText(username);
			customerPass.setText(password);

			Document d = new Document();

			d.setRootElement(createCustomer);		//Sætter createCustomer som rodelement og de resterende
			d.getRootElement().addContent(shopKey);		//elementer sættes som createItems børn. 
			d.getRootElement().addContent(customerName);
			d.getRootElement().addContent(customerPass);


			//Her kaldes til den relevante URL på cloudserveren.
			URL cloud = new URL("http://services.brics.dk/java4/cloud//createCustomer");
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
			outputter.output(response, System.out);

			if(response.getRootElement().getChild("usernameTaken", ns) != null){
				return "Existing username";
			}else if(responseNumCode == 200)
			{
				return "Success!";
			}else{
				return "fail";
			}

		} catch (IOException i) { i.printStackTrace(); }
		catch (Exception e){ e.printStackTrace(); }
		return "done";
	}


	@POST
	@Path("login")
	public String login(@FormParam("username") String username,
			@FormParam("password") String password){

		Namespace ns = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");

		try{		
			SAXBuilder b = new SAXBuilder(); 
			Element login = new Element("login", ns);		
			Element customerName = new Element("customerName", ns);
			Element customerPass = new Element("customerPass", ns);

			customerName.setText(username);
			customerPass.setText(password);

			Document d = new Document();

			d.setRootElement(login);		//Sætter createCustomer som rodelement og de resterende
			d.getRootElement().addContent(customerName);
			d.getRootElement().addContent(customerPass);


			//Her kaldes til den relevante URL på cloudserveren.
			URL cloud = new URL("http://services.brics.dk/java4/cloud/login");
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
			if(responseNumCode == 200){
				Document response = b.build(connection.getInputStream());
				System.out.println(response.getRootElement().getValue());	
				System.out.println(responseNumCode);	// Responskoden fra clouden udskrives.
				outputter.output(response, System.out);

				return "Success!";
			}else{
				return "fail";
			}

		} catch (IOException i) { i.printStackTrace(); }
		catch (Exception e){ e.printStackTrace(); }

		return "done";
	}

	@POST
	@Path("buy")
	public String buyItems(@FormParam("itemID") String itemID,
			@FormParam("customerID") String customerID, 
			@FormParam("itemSellAmount") String sellAmount){

		String SHOPKEY = "3040F78236D8073C8C692612";
		Namespace ns = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");

		try{		
			SAXBuilder b = new SAXBuilder(); 
			Element sellItems = new Element("sellItems", ns);		
			Element shopKey = new Element("shopKey", ns);
			Element itemID1 = new Element("itemID", ns);
			Element customerID1 = new Element("customerID", ns);
			Element saleAmount1 = new Element("saleAmount", ns);

			shopKey.setText(SHOPKEY);
			itemID1.setText(itemID);
			customerID1.setText(customerID);
			saleAmount1.setText(sellAmount);

			Document d = new Document();

			d.setRootElement(sellItems);		//Sætter createCustomer som rodelement og de resterende
			d.getRootElement().addContent(shopKey);		//elementer sættes som createItems børn. 
			d.getRootElement().addContent(itemID1);
			d.getRootElement().addContent(customerID1);
			d.getRootElement().addContent(saleAmount1);


			//Her kaldes til den relevante URL på cloudserveren.
			URL cloud = new URL("http://services.brics.dk/java4/cloud/sellItems");
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
			outputter.output(response, System.out);


		} catch (IOException i) { i.printStackTrace(); }
		catch (Exception e){ e.printStackTrace(); }
		return "done";
	}

	/** Makes an ArrayList with objects of the type ManageListData,
	 * clears the ArrayList containing all of our items and
	 * calls the listItem method from the ManageListData class 
	 */
	public ArrayList<ManageListData> getListItem(){
		theList.clear();
		listItem();
		return theList;
	}

	public class ManageListData {

		// Contructor for this class
		private Element e;
		public ManageListData(Element e){
			this.e = e;
		}

		// Some getters
		public String getID() {
			String returnString = (String) e.getChild("itemID", ns).getText();
			return returnString;
		}

		public String getName() {
			String returnString = (String) e.getChild("itemName", ns).getText();
			return returnString;
		}

		public String getURL() {
			String returnString = (String) e.getChild("itemURL", ns).getText();
			return returnString;
		}

		public String getDescription() {
			Element x = e.getChild("itemDescription", e.getNamespace()).clone();
			Element div = new Element("div");

			itemCheck(x);

			Document doc = new Document(div);

			for(Content c : x.getChild("document", x.getNamespace()).getContent()){
				div.addContent(c.clone());
			}
			XMLOutputter push = new XMLOutputter();
			push.setFormat(Format.getPrettyFormat());

			String returnString = push.outputString(doc);
			return returnString;
		}

		public String getStock() {
			String returnString = (String) e.getChild("itemStock", ns).getText();
			return returnString;
		}

		public String getPrice(){
			String returnString = (String) e.getChild("itemPrice", ns).getText();
			return returnString;
		}
	}


	/**
	 * Makes a list of all Elements and it's children, compares all the xml tags and 
	 * makes sure the correct HTML is passed to the server
	 */
	public void itemCheck(Element el){
		List<Element> l = el.getChildren();

		int n = l.size();

		for(int i = 0; i < n; i++){
			Element m = l.get(i);
			itemCheck(m);

			if(m.getName().equals("item")){
				m.setName("li");
				m.setNamespace(nas);
			} else if(m.getName().equals("italics")){
				m.setName("i");
				m.setNamespace(nas);
			} else if(m.getName().equals("bold")){
				m.setName("b");
				m.setNamespace(nas);
			} else if(m.getName().equals("list")){
				m.setName("ul");
				m.setNamespace(nas);
			}
		}
	}
}