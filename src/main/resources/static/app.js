// 1. 전역 상태 변수
let stompClient = null;
let currentSubscription = null;
let currentRoomId = null; // 방에 입장하기 전에는 null

// 2. DOM 로드 완료 시 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', () => {

    // --- 폼 기본 동작(새로고침) 방지 ---
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', (e) => e.preventDefault());
    });

    // --- 로그인/등록 관련 버튼 ---
    document.getElementById('login-btn').addEventListener('click', login);
    document.getElementById('logout-btn').addEventListener('click', logout);
    document.getElementById('register-btn').addEventListener('click', toggleRegisterForm);
    document.getElementById('cancel-register-btn').addEventListener('click', toggleRegisterForm);
    document.getElementById('submit-register-btn').addEventListener('click', submitRegister);

    // --- 채팅 관련 버튼 ---
    document.getElementById('create-room-form').addEventListener('submit', createRoom);
    document.getElementById('message-form').addEventListener('submit', sendMessage);

    // --- 방 목록 클릭 (이벤트 위임) ---
    document.getElementById('room-list').addEventListener('click', (e) => {
        if (e.target && e.target.classList.contains('list-group-item')) {
            e.preventDefault();
            const roomId = e.target.dataset.roomId; // data-room-id
            const roomName = e.target.textContent;
            switchRoom(roomId, roomName);
        }
    });
});

// 3. UI 상태 변경 함수
function setConnected(connected) {
    // 로그인 폼 <-> 로그아웃 폼 전환
    document.getElementById('login-form').style.display = connected ? 'none' : 'block';
    document.getElementById('logout-form').style.display = connected ? 'block' : 'none';

    // 로그인/등록 버튼 비활성화
    document.getElementById('email').disabled = connected;
    document.getElementById('pwd').disabled = connected;
    document.getElementById('login-btn').disabled = connected;
    document.getElementById('register-btn').disabled = connected;

    // 채팅 UI 활성화/비활성화
    document.getElementById('room-area').style.display = connected ? 'block' : 'none';
    document.getElementById('message-form').style.display = connected ? 'flex' : 'none';

    if (connected) {
        // 로그인 성공 시 환영 메시지
        const username = document.getElementById('email').value;
        document.getElementById('login-username').textContent = username;
    } else {
        // 로그아웃 시 채팅창 비우기
        document.getElementById('greetings').innerHTML = '<tr><td>로그인 후 채팅방에 입장해 주세요.</td></tr>';
        document.getElementById('room-list').innerHTML = ''; // 방 목록 비우기
        document.getElementById('current-room-name').textContent = 'Lobby';
    }
}

// 4. 회원가입 폼 토글
function toggleRegisterForm() {
    const form = document.getElementById('register-form');
    // slideToggle/slideUp 대신 display 속성으로 토글
    if (form.style.display === 'none' || form.style.display === '') {
        form.style.display = 'block';
    } else {
        form.style.display = 'none';
    }
}

// 5. 회원가입 (Fetch)
function submitRegister() {
    const name = document.getElementById('register-name').value;
    const pwd = document.getElementById('register-pwd').value;
    const email = document.getElementById('register-email').value;

    if (!name || !pwd) {
        alert("ID와 비밀번호는 필수입니다.");
        return;
    }

    const registerData = {
        name: name, // (서버 API에 맞게 수정: 'name' 또는 'username')
        pwd: pwd,
        email: email
    };

    console.log("Registering...", registerData);

    fetch("/members/member", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(registerData)
    })
        .then(response => {
            if (!response.ok) {
                // (서버가 에러 메시지를 JSON으로 보낸다고 가정)
                return response.json().then(err => { throw new Error(err.message || '회원가입 실패'); });
            }
            return response.text(); // "회원가입 성공" 텍스트
        })
        .then(message => {
            alert(message); // "회원가입 성공"
            toggleRegisterForm(); // 폼 닫기
        })
        .catch(error => {
            console.error("Register Error:", error);
            alert(error.message);
        });
}

// 6. 로그인 (Fetch)
function login() {
    const email = document.getElementById('email').value.trim();
    const pwd = document.getElementById('pwd').value.trim();

    if (!email || !pwd) {
        alert("ID와 비밀번호를 입력하세요.");
        return;
    }

    // (참고: fetch는 POST 요청 시 폼 데이터를 URLSearchParams로 보내는 게 편함)
    const loginData = new URLSearchParams();
    loginData.append('email', email);
    loginData.append('pwd', pwd);

    fetch("/login", {
        method: "POST",
        headers: {
            // (LoginController가 @RequestParam을 받으므로 json이 아님)
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: loginData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("HTTP " + response.status);
            }
            return response.text(); // "로그인 성공: [이름]"
        })
        .then(text => {
            console.log("Login Success:", text);
            // ★ 1. HTTP 로그인 성공

            // ★ 2. WebSocket 연결 시작 (세션이 생성됐으므로 헤더 필요 없음)
            activateWebSocket();
        })
        .catch(error => {
            console.error("Login failed:", error);
            alert("로그인에 실패했습니다. ID 또는 비밀번호를 확인하세요.");
        });
}

// 7. 로그아웃 (Fetch)
function logout() {
    fetch("/logout", { method: "POST" })
        .then(() => {
            console.log("Logout Success");
            deactivateWebSocket(); // WebSocket 연결 해제
        })
        .catch(error => {
            console.error("Logout failed:", error);
            deactivateWebSocket(); // 실패해도 일단 연결은 끊음
        });
}

// 8. WebSocket 연결 활성화 (로그인 성공 후 호출)
function activateWebSocket() {
    stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/ws'
        // ★ (중요) connectHeaders가 필요 없습니다.
        // /login 성공으로 생긴 HTTP 세션을
        // 서버의 HandshakeInterceptor가 읽어서 처리합니다.
    });

    stompClient.onConnect = (frame) => {
        console.log('WebSocket Connected: ' + frame);
        setConnected(true); // ★ UI를 '로그인됨' 상태로 변경

        // ★ 연결되자마자 방 목록 로드
        loadRooms();
    };

    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
        setConnected(false); // 에러 시 UI 롤백
    };

    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        setConnected(false); // 에러 시 UI 롤백
    };

    stompClient.activate();
}

// 9. WebSocket 연결 해제 (로그아웃 시 호출)
function deactivateWebSocket() {
    if (stompClient) {
        stompClient.deactivate();
    }
    stompClient = null;
    currentSubscription = null;
    currentRoomId = null;
    setConnected(false); // ★ UI를 '로그아웃됨' 상태로 변경
}

// 10. (신규) 방 목록 로드 (Fetch)
function loadRooms() {
    document.getElementById('room-list').innerHTML = ''; // 기존 목록 비우기

    fetch("/chat/rooms") // (이 API는 서버에 만드셔야 합니다)
        .then(response => response.json())
        .then(rooms => {
            // rooms는 [ {id: "1", name: "개발팀"}, {id: "2", name: "기획팀"} ] 형태
            rooms.forEach(room => {
                addRoomToList(room.name, room.id);
            });

        })
        .catch(error => console.error("Error loading rooms:", error));
}

// 11. (신규) 방 생성 (Fetch)
function createRoom(event) {
    event.preventDefault();
    const roomName = document.getElementById('room-name').value.trim();
    if (!roomName) return;

// 👇 이렇게 한 줄로 줄일 수 있습니다.
    const nameData = new URLSearchParams({ roomName });

    fetch("/chat/room", { // (이 API는 서버에 만드셔야 합니다)
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: nameData// (서버가 {name: "..."}을 받는다고 가정)
    })
        .then(response => response.json())
        .then(createdRoom => {
            // createdRoom은 {id: "3", name: "새 방"} 형태
            addRoomToList(createdRoom.name, createdRoom.id);
            document.getElementById('room-name').value = '';

        })
        .catch(error => console.error("Error creating room:", error));
}

// 12. (신규) 방 목록에 <a> 태그 추가 (헬퍼 함수)
function addRoomToList(name, id) {
    const roomList = document.getElementById('room-list');
    console.log(name);
    const link = document.createElement('a');
    link.href = '#';
    link.className = 'list-group-item';
    link.dataset.roomId = id; // data-room-id="id"
    link.textContent = name;

    roomList.appendChild(link);
}

// 13. (신규) 방 전환 (Fetch + Subscribe)
function switchRoom(roomId, roomName) {
    if (roomId === currentRoomId || !stompClient || !stompClient.connected) {
        return; // 같은 방이거나, 연결이 안 됐으면 무시
    }

    // 1. 기존 구독 해제
    if (currentSubscription) {
        currentSubscription.unsubscribe();
    }

    // 2. 채팅창 비우기 및 UI 업데이트
    document.getElementById('greetings').innerHTML = '<tr><td>Loading history...</td></tr>';
    document.getElementById('current-room-name').textContent = roomName;

    // 3. (모든 방 링크에서 'active' 클래스 제거)
    document.querySelectorAll('#room-list .list-group-item').forEach(el => {
        el.classList.remove('active');
    });
    // 4. (클릭한 방에 'active' 클래스 추가)
    document.querySelector(`#room-list [data-room-id="${roomId}"]`).classList.add('active');

    // 5. ★ (Fetch) 과거 메시지 내역 불러오기
    fetch(`/chat/room/${roomId}/messages`)
        .then(response => response.json())
        .then(messages => {
            document.getElementById('greetings').innerHTML = ''; // 'Loading' 메시지 지우기
            messages.forEach(msg => showGreeting(msg)); // (서버가 ChatMessage 객체 리스트 반환)

            // 6. ★ (Subscribe) 과거 내역 로드 후, 새 방 구독 시작
            currentSubscription = stompClient.subscribe(`/topic/room/${roomId}`, (greeting) => {
                const chatMessage = JSON.parse(greeting.body);
                showGreeting(chatMessage);
            });

            currentRoomId = roomId; // 현재 방 ID 갱신
        })
        .catch(error => {
            console.error("Error loading messages:", error);
            document.getElementById('greetings').innerHTML = '<tr><td>Error loading messages.</td></tr>';
        });
}

// 14. 메시지 전송 (STOMP)
function sendMessage(event) {
    event.preventDefault();
    const messageContent = document.getElementById('message').value.trim();

    if (messageContent && stompClient && currentRoomId) {
        stompClient.publish({
            destination: `/app/chat/${currentRoomId}`, // 동적 방 ID
            body: JSON.stringify({'content': messageContent}) // 서버가 content만 받는 DTO
        });
        document.getElementById('message').value = '';
    }
}

// 15. 채팅창에 메시지 표시 (헬퍼 함수)
function showGreeting(message) {
    const greetingsTbody = document.getElementById('greetings');

    const tr = document.createElement('tr');
    const td = document.createElement('td');

    // message가 (과거 내역) 객체일 수도, (실시간) JSON 문자열일 수도 있음
    // 서버 로직에 따라 이 부분을 통일해야 함
    // (여기서는 message가 {sender: "...", content: "..."} 객체라고 가정)
    td.textContent = message.sender + ": " + message.content;

    tr.appendChild(td);
    greetingsTbody.appendChild(tr);

    // 스크롤을 맨 아래로
    const wrapper = document.getElementById('conversation-wrapper');
    wrapper.scrollTop = wrapper.scrollHeight;
}