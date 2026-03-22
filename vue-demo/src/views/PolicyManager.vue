<script setup>
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import api from '@/api'; 

const authStore = useAuthStore();

// --- 狀態變數 ---
const searchCustomerId = ref(); 
const policies = ref([]);
const isLoading = ref(false);
const errorMessage = ref('');

// --- 客戶相關 ---
const initialCustomer = {
    name: '測試一',
    idNo: 'A123451111',
    email: 'tester@abc.com',
    phone: '0912345678'
};
const newCustomer = ref({ ...initialCustomer });

// --- 保單相關 (補齊所有 Entity 欄位) ---
const initialPolicy = {
    customerId: '', // 新增保單時指定的客戶 ID
    policyNo: 'TEST-111111',
    policyType: 'LIFE',
    premium: 100000,
    startDate: '2026-05-01', // yyyy-MM-dd
    endDate: '2026-05-30',   // yyyy-MM-dd
    status: ''
};
const newPolicy = ref({ ...initialPolicy });

// --- 工具函式 ---

const translateStatus = (status) => {
    if (!status) return '未知';
    const s = status.toUpperCase().trim();
    const statusMap = {
        'PENDING': '審核中',
        'ACTIVE': '已生效',
        'EXPIRED': '到期',
        'CANCELLED': '已終止'
    };
    return statusMap[s] || status;
};

const translateType = (type) => {
    if (!type) return '未知';
    const t = type.toUpperCase();
    const typeMap = {
        'LIFE': '壽險',
        'HEALTH': '健康險',
        'CAR': '車險'
    };
    return typeMap[t] || type;
};

const formatCurrency = (value) => {
    return new Intl.NumberFormat('zh-TW').format(value);
};

// --- API 呼叫函式 ---

const handleAddCustomer = async () => {
    if (!newCustomer.value.name || !newCustomer.value.idNo) {
        return alert('姓名與身分證字號為必填');
    }
    try {
        const response = await api.post('/customers', newCustomer.value);
        alert(`客戶開戶成功！系統 ID：${response.data.customerId}`);
        // 自動帶入到查詢與保單新增的 ID 中
        searchCustomerId.value = response.data.customerId.toString();
        newPolicy.value.customerId = response.data.customerId;
        newCustomer.value = { ...initialCustomer };
    } catch (error) {
        console.error('Add customer failed', error);
        alert(error.response?.data?.message || '開戶失敗');
    }
};

const fetchPolicies = async () => {
    if (!searchCustomerId.value) {
        alert('請輸入客戶 ID');
        return;
    }
    isLoading.value = true;
    errorMessage.value = '';
    try {
        const response = await api.get(`/customers/${searchCustomerId.value}/policies`);
        policies.value = response.data; 
    } catch (error) {
        console.error('Fetch failed', error);
        errorMessage.value = '查詢失敗，請確認該客戶是否有保單資料。';
        policies.value = [];
    } finally {
        isLoading.value = false;
    }
};

const handleAddPolicy = async () => {
    // 驗證必填項
    if (!newPolicy.value.customerId) return alert('請輸入客戶 ID');
    if (!newPolicy.value.policyNo) return alert('請輸入保單號碼');
    if (!newPolicy.value.startDate || !newPolicy.value.endDate) return alert('請輸入完整合約日期');

    try {
        await api.post('/policies', {
            ...newPolicy.value,
            customerId: Number(newPolicy.value.customerId) // 確保傳給後端的是數字
        });
        alert('保單新增成功！');
        
        // 紀錄目前的客戶 ID，清空後再帶回，方便連續輸入
        const currentId = newPolicy.value.customerId;
        newPolicy.value = { ...initialPolicy, customerId: currentId }; 
        
        // 如果新增的保單剛好是目前查詢的客戶，則重新整理列表
        if (currentId.toString() === searchCustomerId.value) {
            fetchPolicies();
        }
    } catch (error) {
        console.error('Add policy failed', error);
        alert(error.response.data.message);
    }
};

const handleUpdateStatus = async (policyId, newStatus) => {
    try {
        await api.put(`/policies/${policyId}/status`, { status: newStatus });
        alert(`狀態已更新為: ${translateStatus(newStatus)}`);
        fetchPolicies();
    } catch (error) {
        alert('更新失敗。');
    }
};

</script>

<template>
  <div class="manager-container">
    <header class="header">
      <div class="brand">
        <h1>KTransact</h1>
        <span class="sub-title">壽險保單管理系統</span>
      </div>
      <div class="user-info">
        <span>登入者：<strong>{{ authStore.username }}</strong></span>
        <button @click="authStore.logout()" class="logout-btn">登出系統</button>
      </div>
    </header>

    <main class="main-content">
      <section class="add-section card customer-card">
        <h3>👤 新增客戶</h3>
        <div class="add-form">
          <div class="form-item">
            <label>客戶姓名</label>
            <input v-model="newCustomer.name" type="text" />
          </div>
          <div class="form-item">
            <label>身分證字號</label>
            <input v-model="newCustomer.idNo" type="text" />
          </div>
          <div class="form-item">
            <label>電子信箱</label>
            <input v-model="newCustomer.email" type="email" />
          </div>
          <div class="form-item">
            <label>聯絡電話</label>
            <input v-model="newCustomer.phone" type="text" />
          </div>
          <button @click="handleAddCustomer" class="add-btn primary-btn">完成開戶</button>
        </div>
      </section>

      <section class="search-section card">
        <h3>🔍 客戶保單查詢</h3>
        <div class="search-bar">
          <input v-model="searchCustomerId" @keyup.enter="fetchPolicies" placeholder="輸入客戶系統 ID" />
          <button @click="fetchPolicies" :disabled="isLoading">{{ isLoading ? '連線中...' : '執行查詢' }}</button>
        </div>
      </section>

      <section class="list-section card">
        <h3>📋 保單列表 (客戶 ID: {{ searchCustomerId }})</h3>
        <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>
        
        <div class="table-wrapper">
            <table v-if="policies.length > 0" class="policy-table">
              <thead>
                <tr>
                  <th>保單 ID</th>
                  <th>保單號碼</th>
                  <th>險種</th>
                  <th>保費 (TWD)</th>
                  <th>起始/終止日</th>
                  <th>目前狀態</th>
                  <th>管理操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="policy in policies" :key="policy.policyId || policy.policy_id">
                  <td>{{ policy.policyId || policy.policy_id }}</td>
                  <td>{{ policy.policyNo || policy.policy_no }}</td>
                  <td>{{ translateType(policy.policyType || policy.policy_type) }}</td>
                  <td>{{ formatCurrency(policy.premium) }}</td>
                  <td class="date-cell">
                    {{ policy.startDate }} <br/> 
                    <small class="text-muted">至 {{ policy.endDate }}</small>
                  </td>
                  <td>
                    <span :class="['status-badge', (policy.status || '').toUpperCase()]">
                      {{ translateStatus(policy.status) }}
                    </span>
                  </td>
                  <td>
                    <span v-if="['EXPIRED', 'CANCELLED'].includes((policy.status || '').toUpperCase())" class="text-muted">已結案</span>
                    <select 
                      v-else
                      :value="(policy.status || '').toUpperCase()" 
                      @change="e => handleUpdateStatus(policy.policyId || policy.policy_id, e.target.value)"
                      class="action-select"
                    >
                      <option :value="policy.status" disabled>— 變更狀態 —</option>
                      <option v-if="policy.status === 'PENDING'" value="ACTIVE">核保通過</option>
                      <option v-if="policy.status === 'ACTIVE'" value="EXPIRED">到期</option>
                      <option v-if="policy.status === 'ACTIVE'" value="CANCELLED">終止</option>
                    </select>
                  </td>
                </tr>
              </tbody>
            </table>
            <p v-else-if="!isLoading" class="empty-msg">查無記錄。</p>
        </div>
      </section>

      <section class="add-section card policy-card">
        <h3>➕ 新增保單</h3>
        <div class="add-form policy-form">
          <div class="form-item">
            <label>客戶 ID</label>
            <input v-model="newPolicy.customerId" type="number" placeholder="必填" />
          </div>
          <div class="form-item">
            <label>保單號碼</label>
            <input v-model="newPolicy.policyNo" type="text" placeholder="LIFE-..." />
          </div>
          <div class="form-item">
            <label>險種</label>
            <select v-model="newPolicy.policyType">
              <option value="LIFE">壽險</option>
              <option value="HEALTH">健康險</option>
              <option value="CAR">車險</option>
            </select>
          </div>
          <div class="form-item">
            <label>保費金額</label>
            <input v-model="newPolicy.premium" type="number" />
          </div>
          <div class="form-item">
            <label>生效日期</label>
            <input v-model="newPolicy.startDate" type="date" />
          </div>
          <div class="form-item">
            <label>終止日期</label>
            <input v-model="newPolicy.endDate" type="date" />
          </div>
          <button @click="handleAddPolicy" class="add-btn">提交保單</button>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.manager-container { padding: 2rem; background-color: #f8f9fa; min-height: 100vh; font-family: sans-serif; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; background: #fff; padding: 1rem 2rem; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
.brand h1 { margin: 0; color: #004a99; }
.logout-btn { background: #fff; color: #dc3545; border: 1px solid #dc3545; padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer; }
.main-content { display: grid; gap: 1.5rem; max-width: 1200px; margin: 0 auto; }
.card { background: white; padding: 1.5rem; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
.customer-card { border-top: 4px solid #004a99; }
.policy-card { border-top: 4px solid #2b8a3e; }

.search-bar { display: flex; gap: 1rem; }
.search-bar input { flex: 1; padding: 0.7rem; border: 1px solid #ddd; border-radius: 8px; }
.search-bar button { padding: 0.7rem 1.5rem; background: #004a99; color: white; border: none; border-radius: 8px; cursor: pointer; }

.policy-table { width: 100%; border-collapse: collapse; }
.policy-table th { background: #f1f3f5; padding: 1rem; text-align: left; }
.policy-table td { padding: 1rem; border-bottom: 1px solid #eee; }
.date-cell { font-size: 0.9rem; line-height: 1.2; }

.status-badge { padding: 0.3rem 0.8rem; border-radius: 20px; font-size: 0.85rem; font-weight: bold; display: inline-block;}
.status-badge.PENDING { background: #fff4e6; color: #d9480f; }
.status-badge.ACTIVE { background: #ebfbee; color: #2b8a3e; }
.status-badge.EXPIRED, .status-badge.CANCELLED { background: #fff5f5; color: #c92a2a; }

.action-select { padding: 0.4rem 0.6rem; border: 1px solid #ced4da; border-radius: 6px; }

/* 🌟 自動響應式欄位排版 */
.add-form { display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 1rem; align-items: flex-end; }
.form-item label { display: block; font-size: 0.85rem; color: #666; margin-bottom: 0.4rem; }
.form-item input, .form-item select { width: 100%; padding: 0.6rem; border: 1px solid #ddd; border-radius: 6px; }
.add-btn { grid-column: span 1; background: #2b8a3e; color: white; border: none; padding: 0.7rem; border-radius: 6px; cursor: pointer; transition: background 0.3s; height: 40px; }
.add-btn:hover { opacity: 0.9; }
.primary-btn { background: #004a99; }

.text-muted { color: #adb5bd; font-size: 0.8rem; }
.error-msg { color: #dc3545; background: #fff5f5; padding: 0.5rem; border-radius: 4px; }
</style>