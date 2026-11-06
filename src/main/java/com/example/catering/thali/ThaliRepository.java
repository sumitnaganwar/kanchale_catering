package com.example.catering.thali;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThaliRepository extends JpaRepository<Thali, Long> {
    List<Thali> findAllByAvailableTrueOrderByNameAsc();
    List<Thali> findAllByOrderByNameAsc();
}
