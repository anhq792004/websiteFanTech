package com.example.datn.repository.HoaDonRepo;

import com.example.datn.entity.HoaDon.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuHoaDonRepo extends JpaRepository<LichSuHoaDon,Long> {
    @Query("select l from LichSuHoaDon l where l.hoaDon.id=:idHoaDon")
    List<LichSuHoaDon> findLichSuHoaDonByIdHoaDon(@Param("idHoaDon") long idHoaDon);
}
