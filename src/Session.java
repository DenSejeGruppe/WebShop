import java.io.Serializable;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="shop")
@SessionScoped
public class Session implements Serializable
{	
	private String username;
	private String password;
	
	public String checkCredentials(){
		if(getUsername() == "admin" && getPassword() == "password"){
			return "GRANTED";
		}else{
			return "FUCK OFF";
		}
	
	}
	
	public String getUsername() {
		return "admin";
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return "password";
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}