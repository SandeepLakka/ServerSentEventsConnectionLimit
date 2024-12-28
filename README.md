# Browser Connection Limit and Server Sent Events (SSEs)
Project to check Maximum # of parallel connections from browser to server with SSEs functionality.

## SSE and Spring Boot

This project demonstrates a realtime stock ticker update system using **Server-Sent Events (SSE)** with **Spring Boot** for the backend and a simple HTML/JavaScript frontend. It includes features like subscribing to a stock ticker, fetching additional information about the ticker, and displaying the subscriber count.

---

## **Features**
1. **Realtime Updates**:
    - Subscribe to a stock ticker and receive realtime updates using SSE.
2. **Additional APIs**:
    - Fetch information about the subscribed ticker.
    - Get a list of all available tickers.
    - Fetch the subscriber count for a specific ticker.
3. **Concurrency Support**:
    - Backend uses `ConcurrentHashMap` and `CopyOnWriteArrayList` to handle multiple subscribers efficiently.
4. **Health Check**:
    - A scheduled task continuously monitors the status of the API and reports whether it is alive or not.

---

## **Technologies Used**
- **Backend**: Spring Boot (Web, Non-Reactive)
- **Frontend**: HTML, JavaScript
- **Protocols**: HTTP/2 (In Implementation, but can easily switch to HTTP/1.1), SSE

---

## **Problem Statement**

### **HTTP/1.1 Connection Limitations**
In **HTTP/1.1**, browsers impose a limit on the number of **concurrent connections** to the same origin. For example:
- **Chrome**: 6 connections per origin.

This limitation can cause issues in applications that rely on **long-lived connections** (e.g., SSE) or make multiple API calls simultaneously. When the connection limit is reached, additional requests are queued, leading to delays or failures.

#### **Significance in HTTP/2**
**HTTP/2** addresses this issue by introducing **multiplexing**, and increasing the concurrent connection to 100(as per reference provided in [EventSource Mozilla document](https://developer.mozilla.org/en-US/docs/Web/API/EventSource)) which allows multiple requests and responses to be sent over a single connection. This significantly improves performance and reduces latency, especially for applications with many concurrent connections.

---

## **Key Concepts**

### **Server-Sent Events (SSE)**
SSE is a server push technology that enables the server to send realtime updates to the client over a single HTTP connection. Unlike WebSockets, SSE is unidirectional (server-to-client) and is ideal for applications like stock tickers, notifications, and live feeds.

Learn more about SSE: [MDN Web Docs - EventSource](https://developer.mozilla.org/en-US/docs/Web/API/EventSource)

### **HTTP/2 Multiplexing**
HTTP/2 allows multiple requests and responses to be multiplexed over a single connection, eliminating the need for multiple connections to the same origin. This improves performance and reduces resource usage.

Learn more about HTTP/2: [Understanding Max Parallel HTTP Connections in Browsers](https://medium.com/@sathyanvimala1995/understanding-max-parallel-http-connections-in-browsers-d949f18ef8eb)

---

## **Setup and Run**

### **Prerequisites**
- Java 17 or higher
- Maven 3.x
- Git

### **Steps to Run**
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/stock-ticker-sse.git
   cd stock-ticker-sse
   ```

2. **Build the Project**:
   ```bash
   mvn clean install
   ```

3. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the Frontend**:
   Open `index.html` in your browser.

---

## **Backend APIs**
1. **Subscribe to a Ticker**:
   ```
   GET /api/stock/subscribe/{ticker}
   ```

2. **Fetch Ticker Information**:
   ```
   GET /api/stock/info/{ticker}
   ```

3. **Get All Tickers**:
   ```
   GET /api/stock/all
   ```

4. **Fetch Subscriber Count**:
   ```
   GET /api/stock/subscribers/{ticker}
   ```

5. **Health Check**:
   ```
   GET /api/health
   ```

---

## **Frontend**
The frontend is a single-page application that:
- Subscribes to a stock ticker using SSE.
- Displays realtime updates.
- Fetches additional information about the ticker.
- Displays the subscriber count.

---

## **Health Check**
The backend includes a scheduled task that periodically checks the status of the API (`https://localhost:8080/api/stock/subscribers/AAPL`) and logs whether it is alive or not.

---

## **Troubleshooting**

### **SSL/TLS Certificate Issues**
If you encounter SSL/TLS certificate validation errors (e.g., `PKIX path building failed`), follow these steps:
1. **Add the Server's Certificate to the JRE Truststore**:
   Export the server's certificate and import it into the JRE's truststore (`cacerts`).

2. **Disable SSL Validation (For Development)**:
   Configure `RestTemplate` to ignore SSL errors. See the [HealthCheckService](#health-check) for details.

---

## **References**
1. [MDN Web Docs - EventSource](https://developer.mozilla.org/en-US/docs/Web/API/EventSource)
2. [Understanding Max Parallel HTTP Connections in Browsers](https://medium.com/@sathyanvimala1995/understanding-max-parallel-http-connections-in-browsers-d949f18ef8eb)

---

## **Contributing**
Contributions are welcome! Please open an issue or submit a pull request.

---

## **License**
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## 🤘 SL