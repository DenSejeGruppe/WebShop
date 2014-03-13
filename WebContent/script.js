var loggedin = false;
var studentInBasketPrice = 0;

//Run var loggedin = false;
var studentInBasketPrice = 0;

//Run this function when we have loaded the HTML document
window.onload = function () {
	document.getElementById("logge").style.visibility="hidden";
	var buyField = document.getElementById("buyField"); 

	//This code is called when the body element has been loaded and the application starts
	//Request items from the server. The server expects no request body, so we set it to null
	sendRequest("GET", "rest/shop/items", null, function (itemsText) {
		//This code is called when the server has sent its data
		var items = JSON.parse(itemsText);
		addItemsToTable(items);
	});

	/////////////////////////////////////////////////////////
	//					**CREATE USER**
	// This creates a new user when you have typed in  some
	// information in the fields on the website and checks
	// that the user does not exist
	/////////////////////////////////////////////////////////
	var createButton = document.getElementById("createButton");
	addEventListener(createButton,"click", function(){
		var username = document.getElementById("username").value;
		var password = document.getElementById("password").value;

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
	});


	/////////////////////////////////////////////////////////
	//					**LOG IN**
	// Logs the user in if their username and password match
	// and if the user exists.
	/////////////////////////////////////////////////////////
	var loginButton = document.getElementById("login");
	addEventListener(loginButton,"click", function(){
		var username = document.getElementById("username").value;
		var password = document.getElementById("password").value;

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
	});

	////////////////////////////////////////////////////////
	//				**FAILED BUY BUTTON**
	//Unfortunatly we did not achieve any functionality on
	//this button. :( - But this was our take on it
	////////////////////////////////////////////////////////

	/*	var buyButton = document.getElementById("buyButton");
	addEventListener(buyButton, "click", function(){
		var itemID;
		var customerID;
		var itemsToSell;
		var pack2 = "itemByID="+itemID+"&customerByID="+customerID+"&itemSellAmount="+itemsToSell;
		sendRequest("POST", "rest/shop/buy", pack2, function (salesResponse) {

		});
});*/
};


/**
* This function takes the fields and makes them invisible on the 
* webpage when the user has been successfully logged on to our site
*/
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

/*
* This function takes all our JSON elements and creates a table
* row, and a column for everything
*/
function loop(item, tableBody){
	//Create a new line for this item
	var tr = document.createElement("tr");

	//Makes the cell for the title of the element
	var nameCell = document.createElement("td");
	nameCell.textContent = item.Name;
	tr.appendChild(nameCell);

	//Makes a cell for the price of the element
	var priceCell = document.createElement("td");
	priceCell.textContent = item.Price;
	tr.appendChild(priceCell);

	//Makes a cell for the Description of the element
	var DescriptionCell = document.createElement("td");
	DescriptionCell.textContent = item.Description;
	tr.appendChild(DescriptionCell);

	//Makes a cell for the Stock of the element
	var stockCell = document.createElement("td");
	stockCell.textContent = item.Stock;
	tr.appendChild(stockCell);

	//Makes a cell for the basket amount of the element
	var basketCell = document.createElement("td");
	basketCell.textContent = 0;
	tr.appendChild(basketCell);

	//Makes a cell and a button to add items to the basket
	//and checks if you're logged in, so you are allowed to
	//add items to the basket
	var inBasketCell = document.createElement("td");
	var basketButton = document.createElement("button");
	var t = document.createTextNode("Add student to cart");
	basketButton.appendChild(t);
	inBasketCell.appendChild(basketButton);
	tr.appendChild(inBasketCell);
	addEventListener(basketButton, "click", function(){
		if(loggedin){
			addToBasket(stockCell, basketCell, priceCell);
		}else{
			alert("Log ind kammerat!");
		}
	});

	tableBody.appendChild(tr);
}

/*
* This functions loops our loop() function to add all items from our JSON array
*/
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


/*
* When this function is called we add items to the basket
* and add the price of the collected items together
* so we can make out how much we have to pay
*/
function addToBasket(stock, basket, price){

	var stockValue = stock.childNodes[0].data;
	var basketValue = basket.childNodes[0].data;
	var priceValue = price.childNodes[0].data;
	console.log(stockValue + basketValue);

	if(parseInt(stockValue)>parseInt(basketValue)){
		basket.childNodes[0].data = parseInt(basketValue) + 1;
		studentInBasketPrice = parseInt(studentInBasketPrice) + parseInt(priceValue);
		buyField.innerHTML = "Den samlede pris er " + studentInBasketPrice + " bajere! (sorry, men purchase virker altså ikke )): )";
	}else{
		alert("Der er ikke flere udgaver af denne studerende!");
	}

}

/////////////////////////////////////////////////////
//Code from slides								   //
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

