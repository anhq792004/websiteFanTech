package com.example.datn.repository;

import com.example.datn.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaChiRepo extends JpaRepository<DiaChi,Long> {
    List<DiaChi> findByKhachHang_Id(Long id);

    Optional<DiaChi> findByIdAndKhachHang_Id(Long idDiaChi, Long idKhachHang);
}
