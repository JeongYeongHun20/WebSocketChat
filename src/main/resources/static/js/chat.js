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

// ★★★ [수정됨] 카카오톡 스타일 UI 적용 ★★★
function showGreeting(chatMessage) {
    const greetingsTbody = document.getElementById('greetings');

    // 테이블 구조 유지를 위한 tr, td 생성
    const tr = document.createElement('tr');
    const td = document.createElement('td');

    // 기존 테이블 스타일 간섭 제거
    td.style.border = 'none';
    td.style.padding = '5px 0'; // 위아래 간격만 살짝 줌

    // 1. "나"인지 확인
    // login-username 요소는 auth.js의 setConnected에서 값이 채워집니다.
    // 서버에서 주는 sender 값이 이메일인지 이름인지에 따라 비교 대상이 달라질 수 있습니다.
    const myName = document.getElementById('login-username').textContent;
    const senderName = chatMessage.sender || chatMessage.memberId || 'Unknown';
    const isMe = (myMemberId === chatMessage.memberId);
    console.log(myMemberId+" "+chatMessage.memberId)
    // 2. 전체 컨테이너 (Flex Row)
    const rowDiv = document.createElement('div');
    // 내가 보냈으면 'mine', 남이면 'other' 클래스 붙이기 -> CSS에서 방향 결정
    rowDiv.className = isMe ? 'chat-row mine' : 'chat-row other';

    // 3. 메시지 말풍선 (Bubble)
    const bubbleDiv = document.createElement('div');
    bubbleDiv.className = 'chat-bubble';

    // 남이 보낸 메시지일 때만 이름 표시
    if (!isMe) {
        const nameDiv = document.createElement('div');
        nameDiv.style.fontSize = '0.75em';
        nameDiv.style.marginBottom = '2px';
        nameDiv.style.color = '#666';
        nameDiv.textContent = senderName;
        bubbleDiv.appendChild(nameDiv);
    }

    const contextSpan = document.createElement('span');
    contextSpan.textContent = chatMessage.context; // 서버 필드명 확인 (context vs content)
    bubbleDiv.appendChild(contextSpan);

    // 4. 시간 표시
    const timeSpan = document.createElement('span');
    timeSpan.className = 'chat-time';
    const dateObj = chatMessage.createdAt ? new Date(chatMessage.createdAt) : new Date();
    timeSpan.textContent = dateObj.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

    if(isMe){
        rowDiv.appendChild(timeSpan);
        rowDiv.appendChild(bubbleDiv);
    }else{
        rowDiv.appendChild(bubbleDiv);
        rowDiv.appendChild(timeSpan);
    }


    td.appendChild(rowDiv);
    tr.appendChild(td);
    greetingsTbody.appendChild(tr);

    // 스크롤 맨 아래로
    const wrapper = document.getElementById('conversation-wrapper');
    if (wrapper) wrapper.scrollTop = wrapper.scrollHeight;
}