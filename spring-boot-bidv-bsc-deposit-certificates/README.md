# BIDV-BSC Secondary Deposit Certificate Issuance Flow

This Spring Boot application implements the secondary issuance flow between BIDV bank and BSC securities company for deposit certificates as described in the Vietnamese business requirements.

## 📋 Business Requirements Implementation

This application implements the 4-step workflow described in the requirements:

### Step 1: Business Declaration of Primary Deposit Certificates
- **API**: `POST /api/deposit-certificates/certificates`
- **Implemented**: ✅ Complete with validation
- **Features**: Certificate creation with serial, terms, face value, interest rate, etc.

### Step 2: Securities Company Information Declaration  
- **API**: `POST /api/deposit-certificates/companies`
- **Implemented**: ✅ Complete with CIF management
- **Features**: Company registration with bank account details

### Step 3: BSC Purchase Registration
- **API**: `POST /api/deposit-certificates/purchase`
- **Implemented**: ✅ Online registration simulation
- **Features**: Purchase quantity validation, buyer information capture

### Step 4: Contract Creation and Processing
- **Implemented**: ✅ Complete with retry logic
- **Features**: 
  - Automatic contract generation
  - Core banking integration with accounting simulation
  - BSC system update with retry mechanism
  - Automatic rollback on failure

## 🚀 Quick Start

### Prerequisites
- Java 17
- Maven 3.6+

### Running the Application

1. **Clone and navigate to the module:**
   ```bash
   cd spring-boot-bidv-bsc-deposit-certificates
   ```

2. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the application:**
   - API Base URL: `http://localhost:8080/api`
   - H2 Database Console: `http://localhost:8080/api/h2-console`
     - JDBC URL: `jdbc:h2:mem:depositcert`
     - Username: `sa`
     - Password: (empty)

## 📚 API Documentation

### 1. Create Primary Deposit Certificate (Step 1)
```http
POST /api/deposit-certificates/certificates
Content-Type: application/json

{
    "serial": "CERT-001",
    "certificateName": "Chứng chỉ tiền gửi 12 tháng",
    "primaryTermDays": 365,
    "faceValue": 1000000.00,
    "quantity": 100,
    "totalAmount": 100000000.00,
    "primaryInterestRate": 0.0650,
    "interestPaymentPeriod": "Hàng tháng"
}
```

### 2. Register Securities Company (Step 2)
```http
POST /api/deposit-certificates/companies
Content-Type: application/json

{
    "cif": "BSC001",
    "partnerName": "Công ty Chứng khoán BSC",
    "bankAccountNumber": "1234567890",
    "bankName": "BIDV",
    "contactEmail": "contact@bsc.com.vn",
    "contactPhone": "024-3974-7979",
    "address": "5 Tôn Thất Thuyết, Cầu Giấy, Hà Nội"
}
```

### 3. Process Certificate Purchase (Steps 3 & 4)
```http
POST /api/deposit-certificates/purchase
Content-Type: application/json

{
    "companyFif": "BSC001",
    "certificateSerial": "CERT-001",
    "buyerCif": "BUYER001",
    "buyerName": "Nguyễn Văn A",
    "purchaseQuantity": 10
}
```

### 4. Get Available Certificates
```http
GET /api/deposit-certificates/certificates/available
```

### 5. Get Company Contracts
```http
GET /api/deposit-certificates/contracts/{cif}
```

## 🔧 Technical Implementation

### Architecture
- **Framework**: Spring Boot 2.7.17 (Java 17 compatible)
- **Database**: H2 (in-memory for demo)
- **ORM**: Spring Data JPA / Hibernate
- **Validation**: Bean Validation (JSR-303)
- **Retry Logic**: Spring Retry

### Key Features

#### 1. Retry Mechanism
- **Core Banking Service**: 3 retry attempts with 1-second delay
- **BSC Service**: 3 retry attempts with 1-second delay  
- **Automatic Rollback**: If BSC update fails after retries, transaction is reversed

#### 2. Error Handling
- Comprehensive validation on all inputs
- Quantity availability checking
- Graceful error responses with Vietnamese messages
- Detailed logging for debugging

#### 3. Transaction Management
- @Transactional for data consistency
- Proper rollback handling
- Contract status tracking through the entire lifecycle

#### 4. Simulation Services
- **CoreBankingService**: Simulates BIDV core banking with random failures (20% failure rate)
- **BscService**: Simulates BSC system updates with random failures (15% failure rate)

### Database Schema

#### Tables
1. **primary_deposit_certificates**: Stores certificate definitions
2. **securities_companies**: Stores company information
3. **purchase_contracts**: Stores purchase contracts and their processing status

#### Contract Status Flow
```
PENDING → ACCOUNTING_SUCCESS → COMPLETED
        ↓                   ↓
    ACCOUNTING_FAILED    BSC_UPDATE_FAILED
        ↓                   ↓
     FAILED              REVERSED (after retries)
```

## 🧪 Testing Examples

### Complete Workflow Test
```bash
# Step 1: Create certificate
curl -X POST http://localhost:8080/api/deposit-certificates/certificates \
  -H "Content-Type: application/json" \
  -d '{"serial":"CERT-001","certificateName":"Chứng chỉ tiền gửi 12 tháng","primaryTermDays":365,"faceValue":1000000.00,"quantity":100,"totalAmount":100000000.00,"primaryInterestRate":0.0650,"interestPaymentPeriod":"Hàng tháng"}'

# Step 2: Register company  
curl -X POST http://localhost:8080/api/deposit-certificates/companies \
  -H "Content-Type: application/json" \
  -d '{"cif":"BSC001","partnerName":"Công ty Chứng khoán BSC","bankAccountNumber":"1234567890","bankName":"BIDV"}'

# Step 3&4: Process purchase
curl -X POST http://localhost:8080/api/deposit-certificates/purchase \
  -H "Content-Type: application/json" \
  -d '{"companyFif":"BSC001","certificateSerial":"CERT-001","buyerCif":"BUYER001","buyerName":"Nguyễn Văn A","purchaseQuantity":10}'

# Check results
curl http://localhost:8080/api/deposit-certificates/certificates/available
```

## 📊 Monitoring & Logging

The application provides detailed logging at different levels:
- **DEBUG**: Business logic flow and retry attempts
- **INFO**: Successful operations and transaction completion
- **ERROR**: Failures and retry exhaustion

Log examples:
```
2025-09-10 10:45:55.593 DEBUG - Bước 3 & 4: Xử lý đăng ký mua CCTG
2025-09-10 10:45:57.891 INFO  - Hạch toán thành công cho hợp đồng: CONTRACT-7574BF87
2025-09-10 10:45:58.539 INFO  - Cập nhật BSC thành công cho hợp đồng: CONTRACT-7574BF87
```

## 🏗️ Development Notes

### Design Decisions
1. **No Lombok**: Removed for Java 17 compatibility in this environment
2. **Spring Boot 2.7.17**: Balance between features and compatibility
3. **H2 Database**: For easy demonstration without external dependencies
4. **Simulation Services**: Random failures to demonstrate retry logic

### Future Enhancements
- Add authentication and authorization
- Implement proper external service integration
- Add comprehensive test coverage
- Enhance error handling with specific error codes
- Add audit logging
- Implement proper production database

## 📝 Vietnamese Business Context

This implementation follows Vietnamese banking terminology and business practices:
- **Chứng chỉ tiền gửi (CCTG)**: Deposit Certificate
- **Phát hành thứ cấp**: Secondary Issuance  
- **Hạch toán**: Accounting/Transaction Processing
- **CIF**: Customer Information File
- **BSC**: Bảo Sản Chứng khoán (Securities Company)
- **BIDV**: Ngân hàng TMCP Đầu tư và Phát triển Việt Nam

The application maintains Vietnamese language for user-facing messages while using English for technical implementation.