package com.example.datn.service.NhanVienService;

import com.example.datn.dto.request.AddNhanVienRequest;
import com.example.datn.dto.request.UpdateNhanVienRequest;
import com.example.datn.entity.NhanVien.NhanVien;
import com.example.datn.repository.NhanVienRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
public interface NhanVienService {

    Page<NhanVien> findAll(String keyword, Boolean trangThai, Pageable pageable);

    NhanVien findNhanVienById(Long id);

    void updateNhanVien(UpdateNhanVienRequest request);
    
    void updateNhanVien(UpdateNhanVienRequest request, MultipartFile hinhAnh);

    boolean thayDoiTrangThaiNhanVien(Long id);

    void addNhanVien(AddNhanVienRequest nhanVien);
    
    void addNhanVien(AddNhanVienRequest nhanVien, MultipartFile hinhAnh);

    String generateCode();

    ResponseEntity<?> changeStatus(Long id);
}
