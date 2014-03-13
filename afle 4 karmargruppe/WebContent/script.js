var itemsToBuy = 0;
var products = [];
var productIDs = [];
var buyingProducts = [];
var indexArray = [];
var login = false;
var ID = 0;
var totalPrice = 0;
var prices = [];
var bought = false;

//her kaldes load() når vinduet læses
window.onload = load();


function load() {
	
	//her sender vi et request til vores server uden body da denne del af serveren ikke kræver body
	sendRequest("GET", "rest/shop/items", null, function (itemsText) {
		//This code is called when the server has sent its data
		var items = JSON.parse(itemsText);
		addItemsToTable(items);
	});


};
/*
 * denne metode får værdien fra vores username og password og sender det til login vha en "post"
 * og den har en body med da det er krævet af login clouden
 */
function accept(form){
	var usernamet = encodeURIComponent(form.user.value);
	var passwordet = encodeURIComponent(form.pass.value);
	var body = "username="+usernamet+"&"+"password="+passwordet;
	
	sendRequest("POST", "rest/shop/loginen", body, function (loginText) {
		//dette gør brug af svaret fra serveren til at tjekke om det er korrekt og så logge ind
		//ellers så smider det besked om du ikke er logget ind
		var loginAnswer = JSON.parse(loginText);
		var loginTrue = loginAnswer[0].correct;
		if(loginTrue==="true"){
			ID = loginAnswer[0].customerID;
			login = true;
			if(login){
				document.getElementById("loginForm").style.display = "none";
				var div = document.getElementById("login-succes");
				div.innerHTML = "Du er logged in som " + decodeURIComponent(usernamet);
				document.getElementById("login-succes").style.display = "initial";


				
			}
		}
		else{
			login = false;
			var fail = document.getElementById("loginFail");
			fail.innerHTML = "wrong user or password";
			document.getElementById("loginFail").style.display = "initial";
		}

	});
}


/*
 * denne metode kaldes af vores load og er den der opretter vores liste
 */

function addItemsToTable(items) {
	
	var tableBody = document.getElementById("itemtablebody");
	
	tableBody.innerHTML = "";

	
	var index = 0;

	//vi kører items igennem en forløkke for alle items og opretter elementer til en liste 
	items.forEach(function (item) {
		
		var stocken = item.stock;
		var itemsInBasket = 0;
		
		var tr = document.createElement("tr");

		var idCell = document.createElement("td");
		idCell.textContent = item.id;
		productIDs.push(idCell);
		tr.appendChild(idCell);

		var nameCell = document.createElement("td");
		nameCell.textContent = item.name;
		tr.appendChild(nameCell);

		var pictureCell = document.createElement("td");
		var lol = document.createElement("img");
		lol.setAttribute("src", item.url);
		pictureCell.appendChild(lol);
		tr.appendChild(pictureCell);

		var priceCell = document.createElement("td");
		priceCell.textContent = item.price;
		prices.push(priceCell);
		tr.appendChild(priceCell);

		var stockCell = document.createElement("td");
		stockCell.textContent = stocken;
		products.push(stockCell);
		tr.appendChild(stockCell);

		var itemsIn = document.createElement("td");
		itemsIn.textContent = itemsInBasket;
		buyingProducts.push(itemsIn);
		tr.appendChild(itemsIn);

		var buttonAdd = document.createElement("td");
		var buttonAtri = document.createElement("button");

		var indexRef = function(){
			
		};
		indexRef.index = index;
		indexArray.push(indexRef);

		buttonAtri.textContent = "Add to cart";
		//her laves en knap der trækker 1 fra stock og ligger en til itemsInBasket og øger totalPrice
		buttonAtri.addEventListener('click', function () {
			if(login){
				var et = products[indexRef.index];
				var to = buyingProducts[indexRef.index];
				var tre = prices[indexRef.index];
				var valueEt = parseInt(et.innerHTML);
				if(valueEt>0){
					valueEt--;
					et.textContent = valueEt;
					var valueTo = parseInt(to.innerHTML);
					valueTo++;
					to.textContent = valueTo;
					totalPrice += parseInt(tre.innerHTML);
					var pur = document.getElementById("purchased");
					pur.innerHTML = "Pris " + totalPrice;
				} else{
					window.alert("No stock!");
				}
			}
			else{
				window.alert("LOG the FUCK IN!!!!");
			}
		});
		buttonAdd.appendChild(buttonAtri);
		tr.appendChild(buttonAdd);


		tableBody.appendChild(tr);
		index++;

	});
	itemsToBuy = items.length;

}


//denne metode tjekker om man er logget ind og så sender den til serveren med den body der kræves af 
//sellItems clouden
function purchase(){
	if(login){

		for(var i=0 ; i<itemsToBuy ; i++){
			var lorte = parseInt(buyingProducts[i].innerHTML);


			if(lorte>0){

				var itemID = encodeURIComponent(parseInt(productIDs[i].innerHTML));
				var customerID = encodeURIComponent(ID);
				var itemsToSell = encodeURIComponent(parseInt(buyingProducts[i].innerHTML));
				var body = "itemByID="+itemID+"&customerByID="+customerID+"&itemSellAmount="+itemsToSell;
				sendRequest("POST", "rest/shop/purchaseren", body, function (createText) {
					

					if(createText==="lol"){
						bought=true;

					}
					else{
						window.alert("error");
					}

				});
			}
		}
		if(bought&totalPrice>0){
			var pur = document.getElementById("purchased");
			pur.innerHTML = "Du har kÃ¸bt ind for " + totalPrice + " INGEN FORTYDELSESRET!";
			productIDs = [];
			buyingProducts=[];
			products = [];

			load();
			
			totalPrice = 0;
		}
		
	}


	else{
		window.alert("log in fucking noob!");
	}
}
/*
function sleep(milliseconds) {
	var start = new Date().getTime();
	for (var i = 0; i < 1e7; i++) {
		if ((new Date().getTime() - start) > milliseconds){
			break;
		}
	}
}
*/

/*
 * denne metode får værdien fra vores username og password og sender det til createCustomer vha en "post"
 * og den har en body med da det er krævet af createCustomer clouden
 */
function createCustomer(form){
	var usernamet = encodeURIComponent(form.user.value);
	var passwordet = encodeURIComponent(form.pass.value);
	var body = "username="+usernamet+"&"+"password="+passwordet;
	sendRequest("POST", "rest/shop/createren", body, function (createText) {
		

		if(createText==="created"){
			var created = document.getElementById("createdUser");
			created.innerHTML = "user created with the name: " + usernamet + "please log in to purchase";
		}
		else{
			window.alert("error try again with new name");
			window.location.reload();
		}

	});
}

/*
function addEventListener(myNode, eventType, myHandlerFunc) {
	if (myNode.addEventListener)
		myNode.addEventListener(eventType, myHandlerFunc, false);
	else
		myNode.attachEvent("on" + eventType,
				function (event) {
			myHandlerFunc.call(myNode, event);
		});
}
*/

//denne metode der bruger når vi sender til serveren
function sendRequest(httpMethod, url, body, responseHandler) {
	var http;
	if (!XMLHttpRequest){
		http = new ActiveXObject("Microsoft.XMLHTTP");}
	else{
		http = new XMLHttpRequest();}
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

