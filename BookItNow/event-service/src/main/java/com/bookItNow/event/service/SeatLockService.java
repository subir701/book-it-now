package com.bookItNow.event.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SeatLockService {

    private final ConcurrentHashMap<Integer, ReentrantLock> seatLocks = new ConcurrentHashMap<>();

    public boolean lockSeat(int seatId){
        seatLocks.putIfAbsent(seatId, new ReentrantLock());
        return seatLocks.get(seatId).tryLock();
    }


    public void unlockSeat(int seatId){
        ReentrantLock lock = seatLocks.get(seatId);
        if(lock != null && lock.isHeldByCurrentThread()){
            lock.unlock();
            seatLocks.remove(seatId);
        }
    }
}
