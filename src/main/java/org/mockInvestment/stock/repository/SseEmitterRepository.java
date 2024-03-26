package org.mockInvestment.stock.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, Set<String>> stockIdMappingKeys = new ConcurrentHashMap<>();

    public void createSubscription(String key, long stockId) {
        SseEmitter emitter = getOrCreateEmitter(key);
        emitters.put(key, emitter);
        Set<String> memberIds = stockIdMappingKeys.getOrDefault(stockId, new HashSet<>());
        memberIds.add(key);
        stockIdMappingKeys.put(stockId, memberIds);
    }

    public void deleteSseEmitterByKey(String key) {
        emitters.remove(key);
        for (Long stockId : stockIdMappingKeys.keySet()) {
            Set<String> memberIds = stockIdMappingKeys.get(stockId);
            memberIds.remove(key);
        }
    }

    public Optional<SseEmitter> getSseEmitterByKey(String key) {
        return Optional.ofNullable(emitters.get(key));
    }

    public Set<String> getMemberIdsByStockId(long stockId) {
        return stockIdMappingKeys.getOrDefault(stockId, new HashSet<>());
    }

    private SseEmitter getOrCreateEmitter(String key) {
        SseEmitter emitter = emitters.get(key);
        if (emitter == null)
            emitter = createEmitter(key);
        return emitter;
    }

    private SseEmitter createEmitter(String key) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitter.onCompletion(() -> deleteSseEmitterByKey(key));
        emitter.onTimeout(() -> deleteSseEmitterByKey(key));
        emitter.onError((error) -> deleteSseEmitterByKey(key));
        return emitter;
    }

}
