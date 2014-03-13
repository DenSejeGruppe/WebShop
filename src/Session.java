import java.io.Serializable;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
@ManagedBean(name="shop")	//bean med navnet "shop"
@SessionScoped
public class Session implements Serializable
{	
	private String username = "admin";			//Originale password og username hardcoded som feltvariable.
	private String password = "password";
	private String namespace = "ns";
	

	
	public String goback(){
		return "GOBACK";
	}
	
	public String delete(){
		return "DELETE";
	}
	public String checkCredentials(){
		if(username.equals("admin") && password.equals("password")){
			return "GRANTED";				//Kontrollerer om det givne password giver adgang til admin siden. 
		}else{	
			return "DENIED";
		}
	}
	
	public String getUsername() {			//Setter & getters
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
		
}