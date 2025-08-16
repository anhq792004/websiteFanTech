package com.example.datn.controller.webController;

import com.example.datn.entity.SanPham.SanPham;
import com.example.datn.entity.ThuocTinh.*;
import com.example.datn.service.SanPhamSerivce.SanPhamService;
import com.example.datn.service.ThuocTinhService.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {

    private final SanPhamService sanPhamService;
    private final KieuQuatService kieuQuatService;
    private final CongSuatService congSuatService;
    private final HangService hangService;
    private final MauSacService mauSacService;
    private final NutBamService nutBamService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long kieuQuatId,
            @RequestParam(required = false) Long congSuatId,
            @RequestParam(required = false) Long hangId,
            @RequestParam(required = false) Long mauSacId,
            @RequestParam(required = false) Long nutBamId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        try {
            // Lấy danh sách sản phẩm đã filter
            List<SanPham> products = sanPhamService.searchAndFilterProducts(
                    query, kieuQuatId, congSuatId, hangId, mauSacId, nutBamId, 
                    minPrice, maxPrice, page, size);

            // Lấy tổng số sản phẩm
            long totalProducts = sanPhamService.countFilteredProducts(
                    query, kieuQuatId, congSuatId, hangId, mauSacId, nutBamId, 
                    minPrice, maxPrice);

            // Lấy các filter options
            Map<String, Object> filterOptions = getFilterOptionsData();

            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("totalProducts", totalProducts);
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("totalPages", (int) Math.ceil((double) totalProducts / size));
            response.put("filterOptions", filterOptions);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Có lỗi xảy ra khi tìm kiếm sản phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/filters")
    public ResponseEntity<Map<String, Object>> getFilterOptions() {
        try {
            Map<String, Object> filterOptions = getFilterOptionsData();
            return ResponseEntity.ok(filterOptions);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Có lỗi xảy ra khi lấy filter options: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    private Map<String, Object> getFilterOptionsData() {
        Map<String, Object> filterOptions = new HashMap<>();
        
        // Lấy danh sách kiểu quạt
        List<KieuQuat> kieuQuatList = kieuQuatService.findAllKieuQuat();
        filterOptions.put("kieuQuat", kieuQuatList);
        
        // Lấy danh sách công suất
        List<CongSuat> congSuatList = congSuatService.findAllCongSuat();
        filterOptions.put("congSuat", congSuatList);
        
        // Lấy danh sách hãng
        List<Hang> hangList = hangService.findAllHang();
        filterOptions.put("hang", hangList);
        
        // Lấy danh sách màu sắc
        List<MauSac> mauSacList = mauSacService.findAllMauSac();
        filterOptions.put("mauSac", mauSacList);
        
        // Lấy danh sách nút bấm
        List<NutBam> nutBamList = nutBamService.findAll();
        filterOptions.put("nutBam", nutBamList);
        
        // Lấy khoảng giá min/max
        Map<String, Double> priceRange = sanPhamService.getPriceRange();
        filterOptions.put("priceRange", priceRange);
        
        return filterOptions;
    }
} 