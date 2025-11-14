let stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);

        $("#name").prop("disabled", connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    const username = $("#name").val().trim();
    if (!username) {
        alert('이름을 입력하세요!');
        return;
    }
    console.log("보낼 이름 (생성자에 전달):", username);

    stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/ws',
        connectHeaders: {
            'username': username
        },


    });
    stompClient.onConnect = (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/greetings', (greeting) => {
            showGreeting(JSON.parse(greeting.body).content);
        });
        enterChatRoom();
    };

    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };

    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
    };

    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    stompClient.publish({
        destination: "/app/hello",
        body: JSON.stringify({'message': $("#message").val()})
    });
}
function enterChatRoom(){
    stompClient.publish({
        destination: "/app/send"
    })
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendMessage());
});