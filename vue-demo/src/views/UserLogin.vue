<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import axios from 'axios'; // 登入通常直接用 axios，不需要攔截器

const router = useRouter();
const authStore = useAuthStore();

const username = ref('tester'); // 預設值，方便測試
const password = ref('test123');
const errorMessage = ref('');
const isLoading = ref(false);

const handleLogin = async () => {
    isLoading.value = true;
    errorMessage.value = '';
    try {
        // 注意：這裡直接使用 axios (透過 Nginx 8082)
        const response = await axios.post('/api/login', {
            username: username.value,
            password: password.value
        });

        // 假設你後端現在直接回傳字串 Token，或是 { token: '...' }
        // 請根據你後端 ResponseEntity 回傳的內容調整
        const token = response.data.token || response.data; 

        if (token) {
            authStore.setAuth(token); // 呼叫新的 setAuth
            router.push('/policies');
        }

    } catch (error) {
        console.error('Login failed', error);
        errorMessage.value = '登入失敗，請檢查帳號密碼。';
    } finally {
        isLoading.value = false;
    }
};
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">壽險系統登入</h2>
      
      <div class="form-group">
        <label>帳號</label>
        <input v-model="username" type="text" placeholder="輸入帳號" />
      </div>

      <div class="form-group">
        <label>密碼</label>
        <input v-model="password" type="password" placeholder="輸入密碼" />
      </div>

      <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>

      <button @click="handleLogin" :disabled="isLoading" class="login-btn">
        {{ isLoading ? '登入中...' : '確認登入' }}
      </button>
    </div>
  </div>
</template>

<style scoped>
/* 簡單的樣式，使畫面美觀 */
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f4f7f6;
}
.login-box {
    background: white;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    width: 350px;
}
.title { text-align: center; margin-bottom: 1.5rem; color: #333; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.3rem; color: #666; }
.form-group input { width: 100%; padding: 0.5rem; border: 1px solid #ddd; border-radius: 4px; }
.login-btn { width: 100%; padding: 0.7rem; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 1rem; }
.login-btn:hover { background-color: #0056b3; }
.login-btn:disabled { background-color: #ccc; cursor: not-allowed; }
.error-msg { color: red; font-size: 0.9rem; text-align: center; }
</style>