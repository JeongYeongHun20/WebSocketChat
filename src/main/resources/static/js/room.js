// 방 목록, 생성, 입장, 퇴장 로직

function loadRooms() {
    document.getElementById('room-list').innerHTML = '';
    fetch("/chat/rooms")
        .then(response => response.json())
        .then(rooms => {
            rooms.forEach(room => addRoomToList(room.name, room.id));
        })
        .catch(error => console.error("Error loading rooms:", error));
}

function createRoom(event) {
    event.preventDefault();
    const roomName = document.getElementById('room-name').value.trim();
    if (!roomName) return;

    const nameData = new URLSearchParams({ roomName });

    fetch("/chat/room", {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: nameData
    })
        .then(response => response.json())
        .then(createdRoom => {
            addRoomToList(createdRoom.name, createdRoom.id);
            document.getElementById('room-name').value = '';
        })
        .catch(error => console.error("Error creating room:", error));
}

function addRoomToList(name, id) {
    const roomList = document.getElementById('room-list');
    const wrapper = document.createElement('div');
    wrapper.className = 'list-group-item room-item-row';

    const link = document.createElement('a');
    link.href = '#';
    link.className = 'room-link-item';
    link.dataset.roomId = id;
    link.textContent = name;

    const delBtn = document.createElement('button');
    delBtn.className = 'btn btn-danger room-delete-btn';
    delBtn.dataset.roomId = id;
    delBtn.textContent = 'Exit';

    wrapper.appendChild(link);
    wrapper.appendChild(delBtn);
    roomList.appendChild(wrapper);
}

function switchRoom(roomId, roomName) {
    if (roomId === currentRoomId || !stompClient || !stompClient.connected) return;

    if (currentSubscription) currentSubscription.unsubscribe();

    document.getElementById('greetings').innerHTML = '<tr><td>Loading history...</td></tr>';
    document.getElementById('current-room-name').textContent = roomName;

    // UI 하이라이트 변경
    document.querySelectorAll('.room-link-item').forEach(el => el.classList.remove('active'));
    const activeLink = document.querySelector(`.room-link-item[data-room-id="${roomId}"]`);
    if(activeLink) activeLink.classList.add('active');

    // 과거 메시지 로드
    fetch(`/chat/rooms/${roomId}/messages`)
        .then(response => response.json())
        .then(messages => {
            document.getElementById('greetings').innerHTML = '';
            messages.forEach(msg => showGreeting(msg));

            // 새 방 구독 (chat.js의 subscribeRoom 함수 활용)
            subscribeRoom(roomId);
            currentRoomId = roomId;
        })
        .catch(error => {
            console.error("Error loading messages:", error);
            document.getElementById('greetings').innerHTML = '<tr><td>Error loading messages.</td></tr>';
        });
}

function leaveRoom(roomId) {
    fetch(`/chat/rooms/${roomId}`, { method: 'DELETE' })
        .then(response => {
            if (response.ok) {
                alert("방에서 퇴장했습니다.");
                if (currentRoomId === roomId) {
                    document.getElementById('greetings').innerHTML = '';
                    document.getElementById('current-room-name').textContent = 'Lobby';
                    currentRoomId = null;
                    if(currentSubscription) currentSubscription.unsubscribe();
                }
                loadRooms();
            } else {
                alert("퇴장 실패");
            }
        })
        .catch(error => console.error("Error leaving room:", error));
}

function join(roomId, roomName) {
    fetch(`/chat/rooms/${roomId}`)
        .then(response => {
            if (!response.ok) throw new Error("방을 찾을 수 없습니다.");
            return response.json();
        })
        .then(chatRoom => {
            addRoomToList(chatRoom.name, chatRoom.id);
            switchRoom(roomId, roomName || chatRoom.name);
        })
        .catch(error => {
            console.error("Error joining room:", error);
            alert("방 입장에 실패했습니다.");
        });
}