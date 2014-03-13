package dk.cs.dwebtek;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

@Path("")
public class ShopService {

    @Context HttpSession session;

    private static int priceChange = 0;

    @GET
    @Path("")
    public String getItems() {
        //You should get the items from the cloud server.
        //In the template we just construct some simple data as an array of objects
    	
    	JSONArray array = new JSONArray();

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("ID", 1);
        jsonObject1.put("Name", "Stetson hat");
        jsonObject1.put("Price", 200 + priceChange);
        jsonObject1.put("URL", 200 + priceChange);
        jsonObject1.put("Description", 200 + priceChange);
        jsonObject1.put("Stock", 200 + priceChange);
        array.put(jsonObject1);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("id", 2);
        jsonObject2.put("name", "Rifle");
        jsonObject2.put("price", 500 + priceChange);
        array.put(jsonObject2);

        priceChange++;

        //You can create a MessageBodyWriter so you don't have to call toString() every time
        return array.toString();
    }
}
