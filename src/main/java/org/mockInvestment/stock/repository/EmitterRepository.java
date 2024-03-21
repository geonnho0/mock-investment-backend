package org.mockInvestment.stock.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EmitterRepository {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> stockIdMappingMemberIds = new ConcurrentHashMap<>();

    public void createSubscription(long memberId, long stockId) {
        SseEmitter emitter = getOrCreateEmitter(memberId);
        emitters.put(memberId, emitter);
        Set<Long> memberIds = stockIdMappingMemberIds.getOrDefault(stockId, new HashSet<>());
        memberIds.add(memberId);
        stockIdMappingMemberIds.put(stockId, memberIds);
    }

    public void deleteSseEmitterByMemberId(long memberId) {
        emitters.remove(memberId);
        for (Long stockId : stockIdMappingMemberIds.keySet()) {
            Set<Long> memberIds = stockIdMappingMemberIds.get(stockId);
            memberIds.remove(stockId);
        }
    }

    public Optional<SseEmitter> getSseEmitterByMemberId(long memberId) {
        return Optional.ofNullable(emitters.get(memberId));
    }

    public Set<Long> getMemberIdsByStockId(long stockId) {
        return stockIdMappingMemberIds.getOrDefault(stockId, new HashSet<>());
    }

    private SseEmitter getOrCreateEmitter(long memberId) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter == null) {
            emitter = new SseEmitter(DEFAULT_TIMEOUT);
            emitter.onCompletion(() -> deleteSseEmitterByMemberId(memberId));
            emitter.onTimeout(() -> deleteSseEmitterByMemberId(memberId));
            emitter.onError((error) -> deleteSseEmitterByMemberId(memberId));
        }
        return emitter;
    }

}
