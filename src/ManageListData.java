import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


@ManagedBean(name="o")
@SessionScoped

public class ManageListData {

private String ID;
private String name;
private String URL;
private String description;
private String stock;

public String getID() {
	return ID;
}
public void setID(String iD) {
	ID = iD;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
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
public String getStock() {
	return stock;
}
public void setStock(String stock) {
	this.stock = stock;
}
	
}
