// 이벤트 리스너 등록 및 초기화

document.addEventListener('DOMContentLoaded', () => {
    // 폼 기본 동작 방지
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', (e) => e.preventDefault());
    });

    // auth.js 관련 이벤트
    document.getElementById('login-btn').addEventListener('click', login);
    document.getElementById('logout-btn').addEventListener('click', logout);
    document.getElementById('register-btn').addEventListener('click', toggleRegisterForm);
    document.getElementById('cancel-register-btn').addEventListener('click', toggleRegisterForm);
    document.getElementById('submit-register-btn').addEventListener('click', submitRegister);

    // room.js & chat.js 관련 이벤트
    document.getElementById('create-room-form').addEventListener('submit', createRoom);
    document.getElementById('message-form').addEventListener('submit', sendMessage);

    // 방 입장 폼 (join-room-form)
    const joinForm = document.getElementById('join-room-form');
    if (joinForm) {
        joinForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const roomIdInput = document.getElementById('join-room-id');
            const roomId = roomIdInput.value.trim();
            if (!roomId) {
                alert("참여할 방 ID를 입력하세요.");
                return;
            }
            join(roomId, "");
            roomIdInput.value = '';
        });
    }

    // 방 목록 클릭 (이벤트 위임)
    document.getElementById('room-list').addEventListener('click', (e) => {
        e.preventDefault();
        if (e.target.classList.contains('room-link-item')) {
            const roomId = e.target.dataset.roomId;
            const roomName = e.target.textContent;
            switchRoom(roomId, roomName);
        } else if (e.target.classList.contains('room-delete-btn')) {
            const roomId = e.target.dataset.roomId;
            if(confirm("정말 이 방을 나가시겠습니까?")) {
                leaveRoom(roomId);
            }
        }
    });
});