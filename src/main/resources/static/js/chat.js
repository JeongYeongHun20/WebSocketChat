// WebSocket 연결, 메시지 전송, 수신 로직

let stompClient = null;
let currentSubscription = null;
let currentRoomId = null;

function activateWebSocket() {
    stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/ws'
    });

    stompClient.onConnect = (frame) => {
        console.log('WebSocket Connected: ' + frame);
        setConnected(true); // auth.js 함수 호출
        loadRooms();        // room.js 함수 호출
    };

    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
        setConnected(false);
    };

    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        setConnected(false);
    };

    stompClient.activate();
}

function deactivateWebSocket() {
    if (stompClient) {
        stompClient.deactivate();
    }
    stompClient = null;
    currentSubscription = null;
    currentRoomId = null;
    setConnected(false);
}

function subscribeRoom(roomId) {
    currentSubscription = stompClient.subscribe(`/topic/room/${roomId}`, (greeting) => {
        const chatMessage = JSON.parse(greeting.body);
        showGreeting(chatMessage);
    });
}

function sendMessage(event) {
    event.preventDefault();
    const messageContent = document.getElementById('message').value.trim();

    if (messageContent && stompClient && currentRoomId) {
        stompClient.publish({
            destination: `/app/chat/${currentRoomId}`,
            body: JSON.stringify({'content': messageContent})
        });
        document.getElementById('message').value = '';
    }
}

function showGreeting(chatMessage) {
    const greetingsTbody = document.getElementById('greetings');
    const tr = document.createElement('tr');
    const td = document.createElement('td');

    const div = document.createElement('div');
    div.style.display = 'flex';
    div.style.justifyContent = 'space-between';
    div.style.alignItems = 'center';

    const messageSpan = document.createElement('span');
    // message.sender가 없으면 memberId 사용 (서버 DTO 확인 필요)
    const sender = chatMessage.sender || chatMessage.memberId || 'Unknown';
    messageSpan.textContent = sender + ": " + chatMessage.context;
    messageSpan.style.wordBreak = 'break-all';

    const timeSpan = document.createElement('span');
    const dateObj = chatMessage.createdAt ? new Date(chatMessage.createdAt) : new Date();
    timeSpan.textContent = dateObj.toLocaleTimeString([], {day: '2-digit', hour: '2-digit', minute: '2-digit'});
    timeSpan.style.color = '#999';
    timeSpan.style.fontSize = '0.8em';
    timeSpan.style.minWidth = '70px';
    timeSpan.style.textAlign = 'right';
    timeSpan.style.marginLeft = '10px';

    div.appendChild(messageSpan);
    div.appendChild(timeSpan);
    td.appendChild(div);
    tr.appendChild(td);
    greetingsTbody.appendChild(tr);

    const wrapper = document.getElementById('conversation-wrapper');
    if (wrapper) wrapper.scrollTop = wrapper.scrollHeight;
}