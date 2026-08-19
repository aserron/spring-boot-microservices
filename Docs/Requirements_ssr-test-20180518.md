# Technical Test: SSR Test (2018/05/17)

## Objective
The test will consist of developing 2 applications that expose simple REST services, and a Batch process[cite: 3]. (The specifications will be detailed below)[cite: 3].

## Deliverables
* A ZIP file containing both projects[cite: 3].
* A script to start both applications[cite: 3].
* A short document explaining the solution[cite: 3].

## Technologies for all applications
* Java 8[cite: 3]
* Spring 5[cite: 3]
* Mysql 5.X[cite: 3]
* Maven[cite: 3]
* Embedded Jetty server[cite: 3]
* Other technologies if applicable[cite: 3]

---

## Applications

### Merchant Application

**Services:**

#### 1. CHECK
Given the `merchant_id`, it returns a 200 if the merchant exists and a 404 otherwise[cite: 3].

---

### PM (Payment Manager) Application

**Services:**

#### 1. SALE
It must register a sale in the database[cite: 3]. The amount must always be saved in USD in the database (using the `http://fixer.io/` service to obtain the currency exchange rate)[cite: 3].

**a. Request Body:**[cite: 3]
```json
{ 
  "currency": "String", 
  "amount": "Decimal", 
  "transaction_id": "String", 
  "merchant_id": "String" 
} 
```

**Response Body:**[cite: 3]
```json
{ 
  "id": "String" 
} 
```

**b. Restrictions:**
* This service must be idempotent regarding the `transaction_id` for the same `merchant_id`[cite: 3]. That is, the service must return the same "id" for the (`merchant_id`, `transaction_id`) tuple[cite: 3].
* The id is auto-generated and unique within the platform[cite: 3].
* The fixer.io service must not be consumed more than once every 30 seconds[cite: 3].
* All transactions start in "PENDING" status[cite: 3].
* The `merchant_id` must exist, validating it against the "CHECK" service of the Merchants application[cite: 3].

#### 2. STATUS
A service to get the status of a charge based on the id from the "SALE" service response[cite: 3].

**a. Request:**[cite: 3]
*(Implied GET request with ID)*

**Response Body:**[cite: 3]
```json
{ 
  "id": "String", 
  "date": "String", // creation date 
  "transaction_id": "String", 
  "merchant_id": "String", 
  "amount_usd": "Decimal", 
  "status": "String" // PAID, PENDING, REJECTED 
} 
```

#### 3. BALANCE
It must return the accumulated amount (in USD) of PENDING, PAID, and REJECTED statuses, given a merchant and a date range[cite: 3].

**a. Request:**[cite: 3]
*(Implied GET request with parameters)*

**Response Body:**[cite: 3]
```json
{ 
  "total_paid": "Decimal", 
  "total_pending": "Decimal", 
  "total_rejected": "Decimal" 
} 
```

---

## Batch Process
A job that must take all transactions in PENDING status and, with a 0.7 probability, transition the transaction to PAID, and the rest to REJECTED[cite: 3]. It must run every 30 seconds[cite: 3].

---

## Testing
Tests must be implemented in both applications[cite: 3].