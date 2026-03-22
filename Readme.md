# 保單管理系統

這是一個基於 **Spring Boot 3** 與 **Vue 3** 構建的輕量級壽險核心管理系統。
<br>本專案支援 Docker 一鍵啟動，無需安裝本地開發環境。

## 技術棧

- **後端**: Java 17, Spring Boot 4.0.3, MyBatis, Spring Security
- **資料庫**: H2 Database
- **前端**: Vue 3, Pinia, Vite
- **部署**: Docker, Docker Compose

---
1. **確保已安裝 Docker 與 Docker Compose**。(可以直接到 https://docs.docker.com/desktop/setup/install/windows-install/ 下載 Docker Desktop for Windows - x86_64
2. 在 D槽建立一個資料夾(insurance-demo)並在裡面建立docker-compose-insurance-v1.yml<br>(D:\insurance-demo\docker-compose-insurance-v1.yml)<br>
檔案內容:
```
services:
  h2-db:
    image: oscarfonts/h2:2.3.232
    environment:
      - H2_OPTIONS=-ifNotExists -tcpAllowOthers -webAllowOthers
    ports:
      - "1521:1521"
      - "81:81"
    volumes:
      - ./h2-data:/opt/h2-data
  spring-backend:
    image: becomegooddevloper/insurance-backend:v1.0
    ports:
      - "99:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:h2:tcp://h2-db:1521//opt/h2-data/insurance_db
    depends_on:
      - h2-db
  insurance-frontend:
    image: becomegooddevloper/insurance-frontend:v1.0
    ports:
      - "88:80"
    depends_on:
      - spring-backend
```
3.**執行啟動指令**:<br>
docker-compose -f docker-compose-insurance-v1.yml up -d

4.**啟動後即可進行接下來的測試**

5.**測試完畢的結束指令**:<br>
為了避免伺服器占用資源，請輸入 docker-compose -f docker-compose-insurance-v1.yml down -v 以關閉容器

---

## 服務入口

1.**前端介面:** http://localhost:88

2.**後端 API 文檔 (Swagger):** http://localhost:99/swagger-ui/index.html

3.**H2 資料庫管理:** http://localhost:81

---
## 資料庫

系統包含兩張核心資料表：

1. **Customer (客戶表)**: 儲存客戶基本資料。
2. **Policy (保單表)**: 儲存保單資訊，透過 `customer_id` 與客戶關聯。

可直接連線 http://localhost:81/ 進入 H2 db網頁
<img width="1469" height="441" alt="image" src="https://github.com/user-attachments/assets/47466831-ab56-44a4-8c49-ea5f156cf240" />


- **Driver Class**: org.h2.Driver
- **JDBC URL**: jdbc:h2:tcp://localhost:1521//opt/h2-data/insurance_db
- **User Name**: sa
- **Password**: test


- DDL:
 ```
 CREATE TABLE Customer (
   customer_id NUMBER PRIMARY KEY,
   name VARCHAR(50) NOT NULL,
   id_no VARCHAR(20) NOT NULL UNIQUE,
   email VARCHAR(100),
   phone VARCHAR(20),
   status VARCHAR(10) ,
   created_at TIMESTAMP
);

CREATE TABLE Policy (
   policy_id NUMBER PRIMARY KEY,
   customer_id NUMBER NOT NULL,
   policy_no VARCHAR(30) NOT NULL UNIQUE,
   policy_type VARCHAR(20),
   premium NUMBER(12,2),
   start_date DATE,
   end_date DATE,
   status VARCHAR(20),
   created_at TIMESTAMP,
   CONSTRAINT fk_policy_customer FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);
 ```
 - DML:
 ```
 新增客戶
INSERT INTO Customer (customer_id, name, id_no, email, phone, status, created_at)
    VALUES (2026032218214218,'測試二','A123451112','tester2@abc.com','0987654321','ACTIVE',CURRENT_TIMESTAMP())

新增保單
INSERT INTO Policy (policy_id, customer_id, policy_no, policy_type, premium, start_date, end_date, status, created_at)
    VALUES (2026032218325517, 2026032218214218, 'TEST-111111', 'LIFE', 100000, '2026-05-01', '2026-05-31', 'PENDING', CURRENT_TIMESTAMP())
    
依客戶id查詢保單
SELECT * FROM Policy WHERE customer_id = 2026032218214218
    
依保單id查詢保單
SELECT * FROM Policy WHERE policy_id = 2026032218325517
    
更新保單狀態
UPDATE Policy SET status = 'ACTIVE WHERE policy_id = 2026032218325517
 ```


---

##  API 測試流程 (依序執行)

本系統 API 均受 JWT 保護，<br>測試前請先執行 **登入** 並於 Header 加入 `Authorization: Bearer <Token>`。(token使用期限預設 **60min**)

Postman打 http://localhost:99/login

### 1. 登入
- **API**: `POST /login`
- **功能**: 驗證帳密並取得 Token。
- **範例**: `{"username": "tester", "password": "test123"}`

### 2. 客戶開戶
- **API**: `POST /customers`
- **功能**: 建立新客戶。
- **限制**: 姓名必填，身分證需符合格式。
- **範例**: 
```
輸入
{
    "name": "測試三",
    "idNo": "A123452222",
    "email": "xiaoming@example.com",
    "phone": "0912222222",
}

回傳
{
    "createdAt": "2026-03-22T23:42:12.3740684",
    "customerId": 2026032223421234,
    "email": "xiaoming@example.com",
    "idNo": "A123452222",
    "name": "測試三",
    "phone": "0912222222",
    "status": "ACTIVE"
}
```

### 3. 新增保單
- **API**: `POST /policies`
- **功能**: 為指定客戶建立保單。
- **欄位**: `customerId`, `policyNo`, `premium`, `startDate`, `endDate`...
- **範例**: 
```
輸入(customerId的範例為上一個新增客戶的id)
{
    "customerId": 2026032223421234,
    "policyNo": "LIFE-20260318",
    "policyType": "LIFE",
    "premium": 100000,
    "startDate": "2026-05-01",
    "endDate": "2026-05-31"
}

回傳
{
    "createdAt": "2026-03-22T23:43:09.9939809",
    "customerId": 2026032223421234,
    "endDate": "2026-05-31",
    "policyId": 2026032223430925,
    "policyNo": "LIFE-20260318",
    "policyType": "LIFE",
    "premium": 100000,
    "startDate": "2026-05-01",
    "status": "PENDING"
}
```

### 4. 查詢保單清單
- **API**: `GET /api/customers/{customerId}/policies`
- **功能**: 列出該客戶名下所有保單。
- **範例**: 
```
CALL
http://localhost:99/api/customers/2026032223421234/policies

回傳
[
    {
        "createdAt": "2026-03-22T23:43:09.993981",
        "customerId": 2026032223421234,
        "endDate": "2026-05-31",
        "policyId": 2026032223430925,
        "policyNo": "LIFE-20260318",
        "policyType": "LIFE",
        "premium": 100000.00,
        "startDate": "2026-05-01",
        "status": "PENDING"
    }
]
```

### 5. 變更保單狀態
- **API**: `PUT /api/policies/{policyId}/status`
- **功能**: 模擬核保流程（PENDING -> ACTIVE）。
- **範例**: 
```
CALL
http://localhost:99/api/policies/2026032223430925/status
輸入BODY
{
    "status": "ACTIVE"
}

回傳
更新成功
```

---

## 前端畫面
進入前端頁面 http://localhost:88/login 後，請依照以下步驟測試功能：

1.**系統登入:**

    帳號：tester
    密碼：test123

<img width="1867" height="920" alt="image" src="https://github.com/user-attachments/assets/f4779bfc-edb6-43e6-858a-36445b97fff9" />


2.**新增客戶:** 登入後點擊「完成開戶」按鈕即可新增客戶資料。
<img width="1840" height="933" alt="image" src="https://github.com/user-attachments/assets/198aab34-2aa1-465c-a508-0819ba4acede" />

3.**建立保單:** 新增客戶完成後，系統會自動將 客戶 ID 帶入下方的保單申請表單，確認保單資料無誤後點擊「提交保單」。
<img width="1804" height="938" alt="image" src="https://github.com/user-attachments/assets/89d7ffaa-281b-4509-86dd-d6ce9a48002a" />

4.**保單管理:** 透過查詢功能找到保單，並可針對該保單進行 狀態變更（如：核保、作廢）。
<img width="1259" height="432" alt="image" src="https://github.com/user-attachments/assets/8d10dbd2-8a35-47a4-bc8c-205f70e6b250" />
