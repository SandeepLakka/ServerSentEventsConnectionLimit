package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

@Service
@Slf4j
public class HealthCheckService {

    private final RestTemplate restTemplate = getRestTemplateWithDisabledSSL();

    private boolean isApiAlive = false;


    // Schedule the health check to run every 1 second
    @Scheduled(fixedRate = 10)
    public void checkApiStatus() {
        String url = "https://localhost:8080/api/stock/subscribers/AAPL";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                isApiAlive = true;
                log.warn("API is Alive at {} client count", StockTickerService.numConn.get());
            } else {
                isApiAlive = false;
                log.warn("API is Not Alive at {} client count", StockTickerService.numConn.get());
            }
        } catch (HttpStatusCodeException e) {
            isApiAlive = false;
            log.warn("API is Not Alive at {} client count : {}", StockTickerService.numConn.get(), e.getStatusCode());
        } catch (Exception e) {
            isApiAlive = false;
            log.warn("API is Not Alive at {} client count : {}", StockTickerService.numConn.get(), e.getMessage());
        }
    }

    public RestTemplate getRestTemplateWithDisabledSSL() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Create a hostname verifier that allows all hostnames
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            // Create a RestTemplate with the custom SSL configuration
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            return new RestTemplate(requestFactory);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RestTemplate with disabled SSL validation", e);
        }
    }

    // Method to get the current status of the API
    public boolean isApiAlive() {
        return isApiAlive;
    }
}