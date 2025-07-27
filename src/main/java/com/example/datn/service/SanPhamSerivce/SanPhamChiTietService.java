package com.example.datn.service.SanPhamSerivce;

import com.example.datn.dto.request.AddMultipleVariantsRequest;
import com.example.datn.dto.request.AddMultipleVariantsRequestV2;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SanPhamChiTietService {
    SanPhamChiTiet findById(Long id);
    
    void update(Long id, Long mauSacId, Long congSuatId, Long hangId, Long nutBamId,
                Integer soLuong, Double gia, Double canNang, Boolean trangThai, 
                String moTa, MultipartFile hinhAnh);
    
    void updateInline(Long id, Integer soLuong, Double gia);
    
    SanPhamChiTiet add(Long sanPhamId, Long mauSacId, Long congSuatId, Long hangId, Long nutBamId,
                       Integer soLuong, Double gia, Double canNang, Boolean trangThai, 
                       String moTa, MultipartFile hinhAnh);
    
    /**
     * Thêm nhiều biến thể sản phẩm cùng lúc dựa trên các màu sắc và công suất
     */
    List<SanPhamChiTiet> addMultipleVariants(AddMultipleVariantsRequest request);
    
    /**
     * Thêm nhiều biến thể sản phẩm từ danh sách chi tiết (format V2)
     */
    List<SanPhamChiTiet> addMultipleVariantsV2(AddMultipleVariantsRequestV2 request);
                
    void delete(Long id);
    
    /**
     * Thay đổi trạng thái sản phẩm chi tiết (tắt/bật) thay vì xóa
     */
    void toggleStatus(Long id);
    
    /**
     * Kiểm tra duplicate biến thể với tất cả thuộc tính
     */
    boolean checkDuplicate(Long sanPhamId, Long mauSacId, Long congSuatId, Long hangId, Long nutBamId);
} 