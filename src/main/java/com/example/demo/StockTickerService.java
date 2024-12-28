package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class StockTickerService {
    private final Map<String, List<SseEmitter>> tickerEmitters = new ConcurrentHashMap<>();
    public final static AtomicLong numConn = new AtomicLong(0);
    public SseEmitter subscribe(String ticker) {
        long start = System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        tickerEmitters.computeIfAbsent(ticker, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> removeEmitter(ticker, emitter));
        emitter.onTimeout(() -> removeEmitter(ticker, emitter));
        long end = System.currentTimeMillis();
        doOp(true);
        try{
            emitter.send(SseEmitter.event().comment("Connection Established"));
        }catch (IOException e) {
            e.printStackTrace();
            //removeEmitter(ticker, emitter);
        }
        log.info("Subscribed {} client in {} millis.", numConn.get(), (end-start));
        return emitter;
    }

    public void sendEvent(String ticker, String eventData) {
        List<SseEmitter> emitters = tickerEmitters.get(ticker);
        if (emitters != null) {
            emitters.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event().data(eventData));
                } catch (IOException e) {
                    emitter.complete();
                    removeEmitter(ticker, emitter);
                }
            });
        }
    }


    private void removeEmitter(String ticker, SseEmitter emitter) {
        List<SseEmitter> emitters = tickerEmitters.get(ticker);
        if (emitters != null) {
            emitters.remove(emitter);
            doOp(false);
        }
    }

    public int getSubscriberCount(String ticker) {
        List<SseEmitter> emitters = tickerEmitters.get(ticker);
        return emitters != null ? emitters.size() : 0;
    }

    public void clearEmitters(String ticker){
        if(tickerEmitters.containsKey(ticker)){
            int count = tickerEmitters.get(ticker).size();
            tickerEmitters.remove(ticker);
            log.info("{} clients are removed.", count);
        }
    }

    public synchronized void doOp(boolean isIncrease){
        if(isIncrease){
            numConn.getAndIncrement();
        }else{
            numConn.getAndDecrement();
        }
        log.info("Client count : {}", numConn.get());
    }
}