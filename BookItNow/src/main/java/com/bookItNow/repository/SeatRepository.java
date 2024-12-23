package com.bookItNow.repository;

import com.bookItNow.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

    List<Seat> findAllBySeatNumberInAndSectionId(List<String> seatNumbers, int sectionId);
}
