var websocket = null;

var websocketUrl = (window.document.location.protocol === 'https:' ? 'wss:' : 'ws:')
    + "//" + window.document.location.host + "/junkyplace/room";
var healthUrl = window.document.location.protocol + "//" + window.document.location.host + "/junkyplace/health";
var metricsUrl = window.document.location.protocol + "//" + window.document.location.host + "/junkyplace/metrics";

console.log("%o %o -> %o %o %o", window.document.location.protocol, window.document.location.host,
    websocketUrl, healthUrl, metricsUrl);


var inputMessage = document.getElementById("inputmessage");
var connectButton = document.getElementById("connectButton");
var disconnectButton = document.getElementById("disconnectButton");
var helloButton = document.getElementById("helloButton");
var goodbyeButton = document.getElementById("goodbyeButton");
var joinButton = document.getElementById("joinButton");
var partButton = document.getElementById("partButton");
var roomId = document.getElementById("roomId");
var response = document.getElementById("response");

console.log("buttons %o %o %o %o %o %o %o",
  inputMessage, connectButton, disconnectButton, helloButton, goodbyeButton, joinButton, partButton);

document.getElementById("socketUrl").innerHTML = websocketUrl;
document.getElementById("healthUrl").innerHTML = healthUrl;
document.getElementById("metricsUrl").innerHTML = metricsUrl;

function connect() {
  console.log("connect %o", websocket);

  if ( websocket === null ) {
    response.innerHTML = "";
    connectButton.disabled = true;

    websocket = new WebSocket(websocketUrl);
    websocket.onerror = function(event) {
      response.innerHTML += "Error: " + event.data + "<br />";
    };

    websocket.onopen = function(event) {
      response.innerHTML += "Connection established<br />";

      disconnectButton.disabled = false;
      helloButton.disabled = false;
      goodbyeButton.disabled = false;
      joinButton.disabled = false;
      partButton.disabled = false;
      inputMessage.disabled = false;
    };

    websocket.onclose = function(event) {
      websocket = null;
      response.innerHTML += "Connection closed: " + event.code + "<br />";
      connectButton.disabled = false;
      disconnectButton.disabled = true;
      helloButton.disabled = true;
      goodbyeButton.disabled = true;
      joinButton.disabled = true;
      partButton.disabled = true;
      inputMessage.disabled = true;
    };

    websocket.onmessage = function(event) {
      response.innerHTML += "&larr; " + event.data + "<br />";
      var isScrolledToBottom = response.scrollHeight - response.clientHeight <= response.scrollTop + 1;
      console.log("isScrolledToBottom: %o", isScrolledToBottom);
      if(!isScrolledToBottom) {
        response.scrollTop = response.scrollHeight - response.clientHeight;
      }
    };
  }
}

function sendSocket(payload) {
  console.log("sendSocket %o, %o", websocket, payload);
  if ( websocket !== null ) {
    response.innerHTML += "&rarr; " + payload + "<br />";
    websocket.send(payload);
  }
}

function disconnect() {
  console.log("disconnect %o", websocket);

  if ( websocket !== null ) {
    websocket.close();

    disconnectButton.disabled = true;
    helloButton.disabled = true;
    goodbyeButton.disabled = true;
    joinButton.disabled = true;
    partButton.disabled = true;
    response.disabled = true;
  }
}

function roomHello() {
  console.log("hello %o", websocket);
  //    roomHello,<roomId>,{
  //        "username": "username",
  //        "userId": "<userId>",
  //        "version": 1|2
  //    }
  var helloMsg = {
    "username": "webtest",
    "userId": "dummyId",
    "version": 2
  };

  sendSocket("roomHello," + roomId.value + "," + JSON.stringify(helloMsg));
}

function roomGoodbye() {
  console.log("goodbye %o", websocket);
  //    roomGoodbye,<roomId>,{
  //        "username": "username",
  //        "userId": "<userId>"
  //    }
  var goodbyeMsg = {
    "username": "webtest",
    "userId": "dummyId"
  };

  sendSocket("roomGoodbye," + roomId.value + "," + JSON.stringify(goodbyeMsg));
}

function roomJoin() {
  console.log("join %o", websocket);
  //    roomJoin,<roomId>,{
  //        "username": "username",
  //        "userId": "<userId>",
  //        "version": 2
  //    }
  var joinMsg = {
    "username": "webtest",
    "userId": "dummyId",
    "version": 2
  };

  sendSocket("roomJoin," + roomId.value + "," + JSON.stringify(joinMsg));
}

function roomPart() {
  console.log("part %o", websocket);
  //    roomPart,<roomId>,{
  //        "username": "username",
  //        "userId": "<userId>"
  //    }
  var partMsg = {
    "username": "webtest",
    "userId": "dummyId"
  };

  sendSocket("roomPart," + roomId.value + "," + JSON.stringify(partMsg));
}

function emulateClient() {
  console.log("emulateClient %o", websocket);
  //    room,<roomId>,{
  //        "username": "username",
  //        "userId": "<userId>"
  //        "content": "<message>"
  //    }
  var message = {
    "username": "webtest",
    "userId": "dummyId"
  };
  var txt = inputMessage.value;
  inputMessage.value="";

  if ( txt.indexOf("clear") >= 0) {
    response.innerHTML="";
  } else {
    message.content = txt;
    sendSocket("room," + roomId.value + "," + JSON.stringify(message));
  }
}

function submit(event) {
    event.preventDefault();
    emulateClient();
}
document.getElementById("simpleForm").addEventListener("submit", submit, false);
