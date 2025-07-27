package com.example.datn.controller.SanPham;

import com.example.datn.entity.SanPham.SanPham;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.service.SanPhamSerivce.SanPhamChiTietService;
import com.example.datn.service.SanPhamSerivce.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/san-pham-chi-tiet")
public class SanPhamChiTietController {
    
    private final SanPhamChiTietService sanPhamChiTietService;
    private final SanPhamService sanPhamService;

    @GetMapping("/{id}")
    public ResponseEntity<SanPhamChiTiet> getById(@PathVariable Long id) {
        SanPhamChiTiet spct = sanPhamChiTietService.findById(id);
        if (spct != null) {
            return ResponseEntity.ok(spct);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                  @RequestParam(required = false) Long mauSacId,
                                  @RequestParam(required = false) Long congSuatId,
                                  @RequestParam(required = false) Long hangId,
                                  @RequestParam(required = false) Long nutBamId,
                                  @RequestParam(required = false) Integer soLuong,
                                  @RequestParam(required = false) Double gia,
                                  @RequestParam(required = false) Double canNang,
                                  @RequestParam(required = false) Boolean trangThai,
                                  @RequestParam(required = false) String moTa,
                                  @RequestParam(required = false) MultipartFile hinhAnh) {
        System.out.println("=== Controller UPDATE CALLED ===");
        System.out.println("ID: " + id);
        System.out.println("MauSacId: " + mauSacId);
        System.out.println("CongSuatId: " + congSuatId);
        System.out.println("HangId: " + hangId);
        System.out.println("NutBamId: " + nutBamId);
        System.out.println("SoLuong: " + soLuong);
        System.out.println("Gia: " + gia);
        System.out.println("TrangThai: " + trangThai);
        System.out.println("Request type: " + (soLuong != null || gia != null ? "INLINE_EDIT" : "FULL_EDIT"));
        
        try {
            // Nếu chỉ cập nhật số lượng hoặc giá (inline edit)
            if ((soLuong != null || gia != null) && mauSacId == null && congSuatId == null && hangId == null && nutBamId == null) {
                System.out.println("=== INLINE EDIT MODE ===");
                sanPhamChiTietService.updateInline(id, soLuong, gia);
            } else {
                System.out.println("=== FULL EDIT MODE ===");
                sanPhamChiTietService.update(id, mauSacId, congSuatId, hangId, nutBamId, 
                        soLuong, gia, canNang, trangThai, moTa, hinhAnh);
            }
            System.out.println("=== UPDATE SUCCESS ===");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("=== UPDATE ERROR: " + e.getMessage() + " ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestParam Long sanPhamId,
                               @RequestParam Long mauSacId,
                               @RequestParam Long congSuatId,
                               @RequestParam Long hangId,
                               @RequestParam Long nutBamId,
                               @RequestParam Integer soLuong,
                               @RequestParam Double gia,
                               @RequestParam(required = false) Double canNang,
                               @RequestParam(defaultValue = "true") Boolean trangThai,
                               @RequestParam(required = false) String moTa,
                               @RequestParam(required = false) MultipartFile hinhAnh) {
        try {
            SanPhamChiTiet result = sanPhamChiTietService.add(sanPhamId, mauSacId, congSuatId, 
                    hangId, nutBamId, soLuong, gia, canNang, trangThai, moTa, hinhAnh);
            return ResponseEntity.ok("Thêm biến thể thành công với ID: " + result.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            sanPhamChiTietService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Thay đổi trạng thái sản phẩm chi tiết (tắt/bật) thay vì xóa
     */
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id) {
        try {
            sanPhamChiTietService.toggleStatus(id);
            return ResponseEntity.ok("Thay đổi trạng thái thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Kiểm tra biến thể duplicate (check tất cả thuộc tính)
     */
    @GetMapping("/check-duplicate")
    public ResponseEntity<?> checkDuplicate(@RequestParam String sanPhamId,
                                          @RequestParam String mauSacId,
                                          @RequestParam String congSuatId, 
                                          @RequestParam String hangId, 
                                          @RequestParam String nutBamId) {
        try {
            // Convert string to Long
            Long sanPhamIdLong = Long.parseLong(sanPhamId);
            Long mauSacIdLong = Long.parseLong(mauSacId);
            Long congSuatIdLong = Long.parseLong(congSuatId);
            Long hangIdLong = Long.parseLong(hangId);
            Long nutBamIdLong = Long.parseLong(nutBamId);
            
            boolean exists = sanPhamChiTietService.checkDuplicate(sanPhamIdLong, mauSacIdLong, congSuatIdLong, hangIdLong, nutBamIdLong);
            return ResponseEntity.ok(java.util.Map.of("exists", exists));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Tham số không hợp lệ: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/test")
    public ResponseEntity<?> testUpdate(@PathVariable Long id,
                                       @RequestParam(required = false) Integer soLuong,
                                       @RequestParam(required = false) Double gia) {
        System.out.println("=== TEST UPDATE ENDPOINT ===");
        System.out.println("ID: " + id);
        System.out.println("SoLuong: " + soLuong);  
        System.out.println("Gia: " + gia);
        
        try {
            sanPhamChiTietService.updateInline(id, soLuong, gia);
            return ResponseEntity.ok("Test update thành công!");
        } catch (Exception e) {
            System.out.println("Test error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Test error: " + e.getMessage());
        }
    }
} 