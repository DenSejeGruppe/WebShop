package dk.cs.dwebtek;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;



@Path("shop")
public class ShopService {
	
	/*
	 * Denne metode kalder vores itemCreator og returnere en String med vores itemListe
	 */
	
	@GET
	@Path("items")
	public String returner(){
		for(int i = 0; i< array.length(); i++){
			array.remove(i);
		}
		itemCreater();
		return array.toString();

	}
	
	@Context HttpSession session;


	
	
	Namespace ns = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");
	Namespace html = Namespace.getNamespace("http://www.w3.org/TR/html4/");
	JSONArray array = new JSONArray();

	public void itemCreater(){

		/*
		 * Vi konstruere vores XMLOutputter
		 */
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		SAXBuilder b = new SAXBuilder();
		HttpURLConnection yc = null;
		HttpURLConnection delete = null;

		try{
			/*her opretter vi en forbindelse til listItem og deletedItem*/
			URL url = new URL("http://services.brics.dk/java4/cloud/listItems?shopID=281");
			URL urldeleted = new URL("http://services.brics.dk/java4/cloud//listDeletedItemIDs?shopID=281");
			yc = (HttpURLConnection) url.openConnection();
			yc.setRequestMethod("GET");
			yc.setDoOutput(true);
			yc.setDoInput(true);
			yc.connect();

			delete = (HttpURLConnection) urldeleted.openConnection();
			delete.setRequestMethod("GET");
			delete.setDoOutput(true);
			delete.setDoInput(true);
			delete.connect();


			try{
				/*vi bygger vores svar fra serveren ind i to dokumenter og tager rootElement til det.
				 * De elementer tager vi alle childs til og ligger i to lister*/
				Document response = b.build(yc.getInputStream());
				Document deleted = b.build(delete.getInputStream());
				Element deletedE = deleted.getRootElement();
				List<Element> deletedList = deletedE.getChildren();

				Element ele = response.getRootElement();
				List<Element> listen = ele.getChildren();
				/*Her sortere vi filerne fra listen ved at tage itemId fra både list og deletedList og sammenligner.
				 * De elementer for ItemId der ikke eksitere i deletedItem smidder vi ind igennem vores ItemKlasse
				 * før de tilføjes til vores ItemList
				 */
				for(Element e:listen){
					Element clon = e.clone();
					boolean deletedBoo = false;
					String cloneId = clon.getChild("itemID", ns).getText();
					for(Element dele:deletedList){

						String deleId = dele.getText();

						if(cloneId.equals(deleId)){
							deletedBoo = true;
							break;
						}

					}
					if(!deletedBoo){

						getItems(clon);




					}

				}

			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			yc.disconnect();
		}
	}




	/*
	 * Denne metode opretter et JSONObject og tilføjer de ting det skal indeholde for vores liste
	 * og tilføjer dem til vores array
	 */

	public void getItems(Element e) {


		JSONObject jsonObject1 = new JSONObject();
		jsonObject1.put("id", (String) e.getChild("itemID", ns).getText());
		jsonObject1.put("name", (String) e.getChild("itemName", ns).getText());
		jsonObject1.put("url", (String) e.getChild("itemURL", ns).getText());
		jsonObject1.put("price", (String) e.getChild("itemPrice", ns).getText());
		jsonObject1.put("stock", (String) e.getChild("itemStock", ns).getText());
		array.put(jsonObject1);


	}

	private String customerIDet = "";
	JSONArray loginReturn = new JSONArray();

	/*
	 * Denne metode kalder vores checklogin som tjekker om brugeren findes på clouden
	 */
	
	@POST
	@Path("loginen")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String loginReturn(@FormParam("username") String name , @FormParam("password") String pass){
		
		if(checkLogin(name, pass)){
			JSONObject loginText = new JSONObject();
			loginText.put("customerID", (String) customerIDet);
			loginText.put("correct", (String) "true");
			loginReturn.put(loginText);
			return loginReturn.toString();

		}
		else{
			JSONObject loginText = new JSONObject();
			loginText.put("customerID", (String) customerIDet);
			loginText.put("correct", (String) "false");
			loginReturn.put(loginText);
			return loginReturn.toString();

		}
	}


	public boolean checkLogin(String name , String pass){


		Namespace ns = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");

		/*
		 * Vi opretter et dokument med input værdierne
		 */
		Document xml = new Document();

		Element customerName = new Element("customerName", ns);
		Element customerPass = new Element("customerPass", ns);
		customerName.setText(name);

		customerPass.setText(pass);

		xml.setRootElement(new Element("login", ns));
		xml.getRootElement().addContent("\n");	

		xml.getRootElement().addContent(customerName);
		xml.getRootElement().addContent("\n");
		xml.getRootElement().addContent(customerPass);

		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		SAXBuilder b = new SAXBuilder();
		HttpURLConnection yc = null;
		int responseCode = 0;
		String responseMessage = "";
		/*
		 * Opretter forbindelse til serveren
		 * og sender vores dokument afsted til clouden, 
		 * som acceptere hvis username med tilhørende password eksistere på serveren
		 */
		try{
			URL url = new URL("http://services.brics.dk/java4/cloud/login");
			yc = (HttpURLConnection) url.openConnection();
			yc.setRequestMethod("POST");
			yc.setDoOutput(true);
			yc.setDoInput(true);
			yc.connect();

			outputter.output(xml, yc.getOutputStream());
			responseCode = yc.getResponseCode();
			responseMessage = yc.getResponseMessage();

			try{
				Document response = b.build(yc.getInputStream());
				customerIDet = response.getRootElement().getChildText("customerID", ns);


			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			/*
			 * Dette bliver ikke printet ud men vi for en response code. er coden 200 bliver "POST" accepteres og vi logger ind,
			 * ellers for vi en code 400 og bliver ikke logget ind
			 */
			yc.disconnect();
			System.out.println("The response code from login was: " + responseCode
					+ ". Response message: " + responseMessage);
			if(responseCode==400||responseCode==403){
				System.out.println("wrong password");
			}


		}
		if(responseCode==200){
			return true;
		}
		else return false;


	}
	
	/*
	 * denne metode kalder to andre metoder. Den første tjekker om det navn man prøver at oprette 
	 * til en ny user allerede findes og den anden metode opretter en ny bruger
	 */

	@POST
	@Path("createren")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String createReturn(@FormParam("username") String name , @FormParam("password") String pass){

		if(userExist(name)){

			if(create(name, pass)){
				return "created";
			}
			else{
				return "fejl";
			}
		}
		else{
			return "fejl";
		}
	}
	public boolean create(String name, String pass){

		String Shopkey = "A295A896F613866F0E7CE070";
		Namespace ns = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");

		/*
		 * Her oprettes et Dokument, der indeholder de ting som en Customer skal indeholde.
		 */
		Document xml = new Document();
		Element shopKey = new Element("shopKey", ns);
		Element customerName = new Element("customerName", ns);
		Element customerPass = new Element("customerPass", ns);
		customerName.setText(name);
		shopKey.setText(Shopkey);
		customerPass.setText(pass);

		xml.setRootElement(new Element("createCustomer", ns));
		xml.getRootElement().addContent("\n");	
		xml.getRootElement().addContent(shopKey);
		xml.getRootElement().addContent("\n");
		xml.getRootElement().addContent(customerName);
		xml.getRootElement().addContent("\n");
		xml.getRootElement().addContent(customerPass);

		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		SAXBuilder b = new SAXBuilder();
		HttpURLConnection yc = null;
		int responseCode = 0;
		String responseMessage = "";
		/*
		 * Her oprettes forbindelse til serveren, og oploader vores Dokument XML og henter responsen fra serveren
		 */
		try{
			URL url = new URL("http://services.brics.dk/java4/cloud/createCustomer");
			yc = (HttpURLConnection) url.openConnection();
			yc.setRequestMethod("POST");
			yc.setDoOutput(true);
			yc.setDoInput(true);
			yc.connect();

			outputter.output(xml, yc.getOutputStream());
			responseCode = yc.getResponseCode();
			responseMessage = yc.getResponseMessage();
			try{
				Document response = b.build(yc.getInputStream());
				System.out.println(response.getRootElement().getValue());


			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			yc.disconnect();
			System.out.println("The response code from createUser was: " + responseCode
					+ ". Response message: " + responseMessage);
		}		
		if(responseCode==200){
			return true;
		}
		else{
			return false;
		}
	}    
	
	/*
	 * denne metode kalder og returnere svaret fra purchaser metoden.
	 */

	@POST
	@Path("purchaseren")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String purchaseItems(@FormParam("itemByID") String item , @FormParam("customerByID") String customer , @FormParam("itemSellAmount") String itemsToBuy){
		return purchaser(item, customer, itemsToBuy);
	}

	public String purchaser(String itemIDet, String customeren, String itemsSold){

		String Shopkey = "A295A896F613866F0E7CE070";
		Namespace ns = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");
		Document xml = new Document();
		Element shopKey = new Element("shopKey", ns);
		Element itemID = new Element("itemID", ns);
		Element customerID = new Element("customerID", ns);
		Element saleAmount = new Element("saleAmount", ns);
		itemID.setText(itemIDet);
		shopKey.setText(Shopkey);
		customerID.setText(customeren);
		saleAmount.setText(itemsSold);
		
		/*
		 * vi opretter her det dokument vi sender til clouden
		 */

		xml.setRootElement(new Element("sellItems", ns));
		xml.getRootElement().addContent("\n");	
		xml.getRootElement().addContent(shopKey);
		xml.getRootElement().addContent("\n");
		xml.getRootElement().addContent(itemID);
		xml.getRootElement().addContent("\n");
		xml.getRootElement().addContent(customerID);
		xml.getRootElement().addContent(saleAmount);

		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		SAXBuilder b = new SAXBuilder();
		HttpURLConnection yc = null;
		int responseCode = 0;
		String responseMessage = "";
		/*
		 * Her oprettes forbindelse til serveren, og oploader vores Dokument XML og henter responsen fra serveren
		 */
		try{
			URL url = new URL("http://services.brics.dk/java4/cloud/sellItems");
			yc = (HttpURLConnection) url.openConnection();
			yc.setRequestMethod("POST");
			yc.setDoOutput(true);
			yc.setDoInput(true);
			yc.connect();

			outputter.output(xml, yc.getOutputStream());
			
			responseCode = yc.getResponseCode();
			responseMessage = yc.getResponseMessage();
			try{
				Document response = b.build(yc.getInputStream());
				System.out.println(response.getRootElement().getValue());


			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			yc.disconnect();
			System.out.println("The response code from sellItems was: " + responseCode
					+ ". Response message: " + responseMessage);
		}
		return "lol";
	}




	public boolean userExist(String name){

		/*
		 * Vi konstruere vores XMLOutputter
		 */
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		SAXBuilder b = new SAXBuilder();
		HttpURLConnection yc = null;
		
		boolean existing = false;
		try{
			/*
			 * her opretter vi en forbindelse til listCustomers
			*/
			URL url = new URL("http://services.brics.dk/java4/cloud/listCustomers");
			yc = (HttpURLConnection) url.openConnection();
			yc.setRequestMethod("GET");
			yc.setDoOutput(true);
			yc.setDoInput(true);
			yc.connect();


			try{
				/*vi bygger vores svar fra serveren ind i et dokumenter og tager rootElement til det.
				 * Det element tager vi alle childs til og ligger i en liste*/
				Document response = b.build(yc.getInputStream());

				Element ele = response.getRootElement();
				List<Element> listen = ele.getChildren();
				/*
				 * her tjekkes om customeren allerede findes
				 */
				for(Element e:listen){
					Element clon = e.clone();
					String cloneId = clon.getChild("customerName", ns).getText();
					if(cloneId.equals(name)){
						existing = true;
					}

				}

			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			yc.disconnect();
		}
		if(existing){
			return false;
		}
		else{
			return true;
		}
	}
}
