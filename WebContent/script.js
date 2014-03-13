var loggedin = false;

//Run this function when we have loaded the HTML document
window.onload = function () {
	alert("Please accept the webbroweser's request or you won't be able to login in or create a new account");
	document.getElementById("logge").style.visibility="hidden";

	//This code is called when the body element has been loaded and the application starts
	//Request items from the server. The server expects no request body, so we set it to null
	sendRequest("GET", "rest/shop/items", null, function (itemsText) {
		//This code is called when the server has sent its data
		var items = JSON.parse(itemsText);
		addItemsToTable(items);
	});

	function getLocation()
	{
		if (navigator.geolocation)
		{
			navigator.geolocation.getCurrentPosition(showPosition);
		}
		else{alert("Geolocation is not supported by this browser.");}
	}
	var lat;
	var long;
	function showPosition(position)
	{
		lat = position.coords.latitude;
		long= position.coords.longitude; 
	}
	getLocation();

	var createButton = document.getElementById("createButton");

	addEventListener(createButton,"click", function(){
		var username = document.getElementById("username").value;
		var password = document.getElementById("password").value;

		if(55 < lat < 58 && 8 < long < 11 && long !== null && lat !== null){
			var pack = ("username="+username+"&"+"password="+password);
			sendRequest("POST", "rest/shop/createCustomer", pack, function(createCustomerResponse){
				if(createCustomerResponse === "Success!"){
					loggedin = true;
					logge();
					alert(username +" er blevet oprettet. Velkommen kammerat!");
				}else if(createCustomerResponse ==="Existing username")
				{
					alert(username + " er allerede taget!");
				}else{
					"Der er sket en fejl";
				}
			});
		}else{alert("Du er for langt væk fra Aarhus kammerat!")};
	});
	var loginButton = document.getElementById("login");
	addEventListener(loginButton,"click", function(){
		var username = document.getElementById("username").value;
		var password = document.getElementById("password").value;

		if(55 < lat < 58 && 8 < long < 11 && long !== null && lat !== null){
			var pack = ("username="+username+"&"+"password="+password);
			sendRequest("POST", "rest/shop/login", pack, function(loginResponse){
				if(loginResponse === "Success!"){
					loggedin = true;
					logge();
					alert("Velkommen " + username);
				}else{
					alert("Der er sket en fejl under loginfasen");
				}
			});
		}else{alert("Du er for langt væk fra Aarhus kammerat!")};
	});
};

function logge()
{
	if(loggedin === true){
		document.getElementById("createButton").style.visibility="hidden";
		document.getElementById("login").style.visibility="hidden";
		document.getElementById("username").style.visibility="hidden";
		document.getElementById("password").style.visibility="hidden";
		document.getElementById("logge").style.visibility="visible";
		document.getElementById("logge").innerHTML="Du er nu logget in som " + document.getElementById("username").value;

	}else{
		document.getElementById("createButton").style.visibility="visible";
		document.getElementById("login").style.visibility="visible";
		document.getElementById("username").style.visibility="visible";
		document.getElementById("password").style.visibility="visible";
		document.getElementById("logge").style.visibility="hidden";

	}
}

function loop(item, tableBody){
	//Create a new line for this item
	var tr = document.createElement("tr");

	var nameCell = document.createElement("td");
	nameCell.textContent = item.Name;
	tr.appendChild(nameCell);

	var priceCell = document.createElement("td");
	priceCell.textContent = item.Price;
	tr.appendChild(priceCell);

	var DescriptionCell = document.createElement("td");
	DescriptionCell.textContent = item.Description;
	tr.appendChild(DescriptionCell);

	var stockCell = document.createElement("td");
	stockCell.textContent = item.Stock;
	tr.appendChild(stockCell);

	var basketCell = document.createElement("td");
	basketCell.textContent = 0;
	tr.appendChild(basketCell);

	var inBasketCell = document.createElement("td");
	var buyButton = document.createElement("button");
	var t = document.createTextNode("Add student to cart");
	buyButton.appendChild(t);
	inBasketCell.appendChild(buyButton);
	tr.appendChild(inBasketCell);
	addEventListener(buyButton, "click", function(){
		if(loggedin){
			addToBasket(stockCell, basketCell);
		}else{
			alert("Log in kammerat!");
		}
	});

	tableBody.appendChild(tr);
}

function addItemsToTable(items) {
	//Get the table body we we can add items to it
	var tableBody = document.getElementById("itemtablebody");
	//Remove all contents of the table body (if any exist)
	tableBody.innerHTML = "";

	//Loop through the items from the server
	for (var i = 0; i < items.length; i++) {
		loop(items[i], tableBody);
	};
}

function addToBasket(stock, basket){

	var stockValue = stock.childNodes[0].data;
	var basketValue = basket.childNodes[0].data;
	console.log(stockValue + basketValue);

	if(parseInt(stockValue)>parseInt(basketValue)){
		basket.childNodes[0].data = parseInt(basketValue) + 1;
	}else{
		alert("Der er ikke flere udgaver af denne studerende!")
	}

}



/////////////////////////////////////////////////////
//Code from slides
/////////////////////////////////////////////////////

/**
 * A function that can add event listeners in any browser
 */
function addEventListener(myNode, eventType, myHandlerFunc) {
	if (myNode.addEventListener)
		myNode.addEventListener(eventType, myHandlerFunc, false);
	else
		myNode.attachEvent("on" + eventType,
				function (event) {
			myHandlerFunc.call(myNode, event);
		});
}

var http;
if (!XMLHttpRequest)
	http = new ActiveXObject("Microsoft.XMLHTTP");
else
	http = new XMLHttpRequest();

function sendRequest(httpMethod, url, body, responseHandler) {
	http.open(httpMethod, url);
	if (httpMethod == "POST") {
		http.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	}
	http.onreadystatechange = function () {
		if (http.readyState == 4 && http.status == 200) {
			responseHandler(http.responseText);
		}
	};
	http.send(body);
}

