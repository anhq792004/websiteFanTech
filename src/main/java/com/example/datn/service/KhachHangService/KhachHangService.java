package com.example.datn.service.KhachHangService;

import com.example.datn.dto.request.AddKhachHangRequest;
import com.example.datn.dto.request.UpdateInforKhachHangRequest;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


public interface KhachHangService {
    Page<KhachHang> findAll(String keyword, Boolean trang_thai, Pageable pageable);

    KhachHang findById(Long id);

    KhachHang addKH(AddKhachHangRequest request);
    
    KhachHang addKH(AddKhachHangRequest request, MultipartFile hinhAnh);

    void updateInforKhachHang(UpdateInforKhachHangRequest request);
    
    void updateInforKhachHang(UpdateInforKhachHangRequest request, MultipartFile hinhAnh);

    String generateCode();

    /**
     * Tìm khách hàng theo tài khoản
     */
    KhachHang findByTaiKhoan(TaiKhoan taiKhoan);

    /**
     * Lưu thông tin khách hàng
     */
    KhachHang save(KhachHang khachHang);

    /**
     * Đếm số lượng khách hàng
     */
    long count();

    ResponseEntity<?> changeStatus(Long id);


}
