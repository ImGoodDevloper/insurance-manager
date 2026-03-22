import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useAuthStore = defineStore('auth', () => {
    const accessToken = ref(localStorage.getItem('access_token') || '');
    const username = ref(localStorage.getItem('username') || '');
    const authorities = ref(localStorage.getItem('authorities') || '');

    const isLoggedIn = computed(() => !!accessToken.value);

    // 登入成功後，只需傳入 accessToken 字串
    function setAuth(token) {
        accessToken.value = token;
        localStorage.setItem('access_token', token);

        // 從 Token 中提取 username (後端 Claims 裡放的 key 是 "username")
        const payload = decodeJwt(token);
        if (payload && payload.username && payload.authorities) {
            username.value = payload.username;
            authorities.value = payload.authorities;
            localStorage.setItem('username', payload.username);
            localStorage.setItem('authorities', payload.authorities);
        }
    }

    function logout() {
        accessToken.value = undefined;
        username.value = undefined;
        authorities.value = undefined;
        localStorage.clear();
        window.location.href = '/login';
    }

    return { accessToken, username, authorities, isLoggedIn, setAuth, logout };
});

function decodeJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(c => 
            '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
        ).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) { return null; }
}
