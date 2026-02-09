package com.bookItNow.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatLockService {

    private final RedissonClient redissonClient;
    private static final String LOCK_PREFIX = "seat_lock:";

    public boolean lockSeats(List<Integer> seatIds){
        //sort IDs to prevent deadlocks
        Collections.sort(seatIds);

        //Use a MultiLock to ensure we get ALL requested seats or NONE
        List<RLock> locks = seatIds.stream()
                .map(id -> redissonClient.getLock(LOCK_PREFIX+id))
                .toList();

        RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));

        try{
            //wait up to 5 second to get the lock, lease for 10 minutes
            boolean isLocked = multiLock.tryLock(5, 600, TimeUnit.SECONDS);
            if(isLocked){
                log.info("Successfully locked seats: {}", seatIds);
            }
            return isLocked;
        }catch (InterruptedException ex){
            Thread.currentThread().interrupt();
            return false;
        }
    }


    public void unlockSeats(List<Integer> seatIds){
        List<RLock> locks = seatIds.stream()
                .map(id -> redissonClient.getLock(LOCK_PREFIX + id))
                .toList();
        RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));

        if (multiLock.isHeldByCurrentThread()) {
            multiLock.unlock();
            log.info("Unlocked seats: {}", seatIds);
        }
    }
}
