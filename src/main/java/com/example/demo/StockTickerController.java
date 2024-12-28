package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/stock")
public class StockTickerController {

    @Autowired
    private StockTickerService stockTickerService;

    @GetMapping("/subscribe/{ticker}")
    public SseEmitter subscribe(@PathVariable String ticker) {
        return stockTickerService.subscribe(ticker);
    }

    @GetMapping("/publish/{ticker}")
    public void publish(@PathVariable String ticker, @RequestParam String eventData) {
        stockTickerService.sendEvent(ticker, eventData);
    }

    @GetMapping("/info/{ticker}")
    public String getTickerInfo(@PathVariable String ticker) {
        return "Information about " + ticker;
    }

    @GetMapping("/all")
    public String getAllTickers() {
        return "AAPL, GOOGL, MSFT, AMZN";
    }

    @GetMapping("/subscribers/{ticker}")
    public int getSubscriberCount(@PathVariable String ticker) {
        return stockTickerService.getSubscriberCount(ticker);
    }
}