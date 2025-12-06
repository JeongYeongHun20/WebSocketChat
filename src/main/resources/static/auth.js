// 로그인, 회원가입, UI 상태 변경 로직

function setConnected(connected) {
    document.getElementById('login-form').style.display = connected ? 'none' : 'block';
    document.getElementById('logout-form').style.display = connected ? 'block' : 'none';

    document.getElementById('email').disabled = connected;
    document.getElementById('pwd').disabled = connected;
    document.getElementById('login-btn').disabled = connected;
    document.getElementById('register-btn').disabled = connected;

    document.getElementById('room-area').style.display = connected ? 'block' : 'none';
    document.getElementById('message-form').style.display = connected ? 'flex' : 'none';

    if (connected) {
        const username = document.getElementById('email').value;
        document.getElementById('login-username').textContent = username;
    } else {
        document.getElementById('greetings').innerHTML = '<tr><td>로그인 후 채팅방에 입장해 주세요.</td></tr>';
        document.getElementById('room-list').innerHTML = '';
        document.getElementById('current-room-name').textContent = 'Lobby';
    }
}

function toggleRegisterForm() {
    const form = document.getElementById('register-form');
    if (form.style.display === 'none' || form.style.display === '') {
        form.style.display = 'block';
    } else {
        form.style.display = 'none';
    }
}

function submitRegister() {
    const name = document.getElementById('register-name').value;
    const pwd = document.getElementById('register-pwd').value;
    const email = document.getElementById('register-email').value;

    if (!name || !pwd) {
        alert("ID와 비밀번호는 필수입니다.");
        return;
    }

    const registerData = { name, pwd, email };

    fetch("/members/member", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(registerData)
    })
        .then(response => {
            if (!response.ok) return response.json().then(err => { throw new Error(err.message || '회원가입 실패'); });
            return response.text();
        })
        .then(message => {
            alert(message);
            toggleRegisterForm();
        })
        .catch(error => {
            console.error("Register Error:", error);
            alert(error.message);
        });
}

function login() {
    const email = document.getElementById('email').value.trim();
    const pwd = document.getElementById('pwd').value.trim();

    if (!email || !pwd) {
        alert("ID와 비밀번호를 입력하세요.");
        return;
    }

    const loginData = new URLSearchParams();
    loginData.append('email', email);
    loginData.append('pwd', pwd);

    fetch("/login", {
        method: "POST",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: loginData
    })
        .then(response => {
            if (!response.ok) throw new Error("HTTP " + response.status);
            return response.text();
        })
        .then(text => {
            console.log("Login Success:", text);
            activateWebSocket(); // chat.js에 있는 함수 호출
        })
        .catch(error => {
            console.error("Login failed:", error);
            alert("로그인에 실패했습니다.");
        });
}

function logout() {
    fetch("/logout", { method: "POST" })
        .then(() => {
            console.log("Logout Success");
            deactivateWebSocket(); // chat.js에 있는 함수 호출
        })
        .catch(error => {
            console.error("Logout failed:", error);
            deactivateWebSocket();
        });
}