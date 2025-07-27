package com.example.datn.controller.SanPhamController;

import com.example.datn.entity.SanPham.SanPham;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.entity.ThuocTinh.KieuQuat;
import com.example.datn.service.ThuocTinhService.KieuQuatService;
import com.example.datn.service.ThuocTinhService.MauSacService;
import com.example.datn.service.ThuocTinhService.CongSuatService;
import com.example.datn.service.ThuocTinhService.HangService;
import com.example.datn.service.ThuocTinhService.NutBamService;
import com.example.datn.service.SanPhamSerivce.SanPhamService;
import com.example.datn.service.SanPhamSerivce.SanPhamChiTietService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/san-pham")
public class SanPhamController {
    private final SanPhamService sanPhamService;
    private final KieuQuatService kieuQuatService;
    private final SanPhamChiTietService sanPhamChiTietService;
    private final MauSacService mauSacService;
    private final CongSuatService congSuatService;
    private final HangService hangService;
    private final NutBamService nutBamService;


    @GetMapping("/list")
    public String getAllSanPham(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "kieuQuat", required = false) Long kieuQuatId,
            @RequestParam(value = "trangThai", required = false) Boolean trangThai,
            Model model
    ) {
        Page<SanPham> sanPhamPage = sanPhamService.findAllSanPham(page, size, search, kieuQuatId, trangThai);
        System.out.println("Số lượng sản phẩm: " + sanPhamPage.getTotalElements());
        System.out.println("Tổng số trang: " + sanPhamPage.getContent());
        model.addAttribute("sanPhamPage", sanPhamPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", sanPhamPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("kieuQuat", kieuQuatId);
        model.addAttribute("trangThai", trangThai);
        if (!sanPhamPage.getContent().isEmpty()) {
            System.out.println("Sản phẩm đầu tiên: " + sanPhamPage.getContent().get(0));
        }
        return "admin/san_pham/index";
    }

    @GetMapping("/detail")
    public String getSanPhamDetail(@RequestParam(value = "id") Long id, Model model) {
        Optional<SanPham> sanPhamOptional = sanPhamService.findSanPhamById(id);
        if (sanPhamOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
        }
        model.addAttribute("sanPham", sanPhamOptional.get());
        return "admin/san_pham/detail";
    }
    
    @GetMapping("/add")
    public String showAddSanPhamForm(Model model) {
        model.addAttribute("sanPham", new SanPham());
        model.addAttribute("kieuQuat", kieuQuatService.findAllKieuQuat());
        return "admin/san_pham/add";
    }
    
    @PostMapping("/add")
    public String addSanPham(@ModelAttribute SanPham sanPham) {
        
        // Set ngày tạo cho sản phẩm -> Lấy ngày hiện tại
        sanPham.setNgayTao(LocalDateTime.now());
        
        // Chỉ lưu sản phẩm, không tạo biến thể
        // Hình ảnh sẽ được thêm khi tạo biến thể sản phẩm chi tiết
            sanPhamService.saveSanPham(sanPham);
        
        return "redirect:/admin/san-pham/list";
    }
    
    @GetMapping("/edit")
    public String showEditSanPhamForm(@RequestParam(value = "id") Long id, Model model) {
        Optional<SanPham> sanPhamOptional = sanPhamService.findSanPhamById(id);
        if (sanPhamOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
        }
        model.addAttribute("sanPham", sanPhamOptional.get());
        model.addAttribute("kieuQuat", kieuQuatService.findAllKieuQuat());
        return "admin/san_pham/edit";
    }
    
    @PostMapping("/update")
    public String updateSanPham(
            @ModelAttribute SanPham sanPham,
            @RequestParam(value = "hinhAnh", required = false) MultipartFile hinhAnh) {
        
        // Lưu hình ảnh nếu có
        if (hinhAnh != null && !hinhAnh.isEmpty()) {
            // Xử lý lưu hình ảnh trong service
            sanPhamService.updateSanPhamWithImage(sanPham, hinhAnh);
        } else {
            sanPhamService.updateSanPham(sanPham);
        }
        
        return "redirect:/admin/san-pham/list";
    }

    @PostMapping("/them")
    @ResponseBody
    public ResponseEntity<String> addSanPhamAPI(@RequestBody SanPham sanPham) {
        // Set ngày tạo cho sản phẩm -> Lấy ngày hiện tại
        sanPham.setNgayTao(LocalDateTime.now());
        sanPhamService.saveSanPham(sanPham);
        return ResponseEntity.ok("Thêm sản phẩm thành công");
    }

    @PostMapping("/sua")
    @ResponseBody
    public ResponseEntity<String> updateSanPhamAPI(@RequestBody SanPham sanPham) {
        sanPhamService.updateSanPham(sanPham);
        return ResponseEntity.ok("Sửa sản phẩm thành công");
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public ResponseEntity<SanPham> getSanPhamById(@PathVariable("id") Long id) {
        Optional<SanPham> sanPhamOptional = sanPhamService.findSanPhamById(id);
        return sanPhamOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/thay-doi-trang-thai/{id}")
    @ResponseBody
    public ResponseEntity<String> thayDoiTrangThaiSanPham(@PathVariable("id") Long id) {
        boolean result = sanPhamService.thayDoiTrangThaiSanPham(id);
        if (result) {
            return ResponseEntity.ok("Thay đổi trạng thái sản phẩm thành công");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy sản phẩm");
        }
    }

    @GetMapping("/api/kieu-quat")
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<KieuQuat>> getKieuQuat() {
        List<KieuQuat> kieuQuatList = kieuQuatService.findAllKieuQuat();
        return ResponseEntity.ok(kieuQuatList);
    }

    // Thêm biến thể sản phẩm (thay thế cách cũ bằng tính năng thêm nhiều biến thể)
    @GetMapping("/chi-tiet/add")
    public String showAddVariantForm(@RequestParam(value = "productId") Long productId, Model model) {
        Optional<SanPham> sanPhamOptional = sanPhamService.findSanPhamById(productId);
        if (sanPhamOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
        }
        
        model.addAttribute("sanPham", sanPhamOptional.get());
        model.addAttribute("mauSacList", mauSacService.findAllMauSac());
        model.addAttribute("congSuatList", congSuatService.findAllCongSuat());
        model.addAttribute("hangList", hangService.findAllHang());
        model.addAttribute("nutBamList", nutBamService.findAll());
        
        return "admin/san_pham/add-multiple-variants";
    }

    // Endpoint cũ để tương thích - redirect về endpoint mới
    @GetMapping("/chi-tiet/add-multiple")
    public String showAddMultipleVariantsForm(@RequestParam(value = "productId") Long productId, Model model) {
        return showAddVariantForm(productId, model);
    }

    // Thêm nhiều biến thể cùng lúc - hỗ trợ cả 2 format
    @PostMapping("/chi-tiet/add-multiple")
    @ResponseBody
    public ResponseEntity<?> addMultipleVariants(@RequestBody String requestBody) {
        try {
            // Parse JSON để xác định format
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(requestBody);
            
            // Kiểm tra format: nếu có field "variants" thì là format mới
            if (jsonNode.has("variants")) {
                // Format mới với danh sách variants chi tiết
                com.example.datn.dto.request.AddMultipleVariantsRequestV2 requestV2 = 
                    objectMapper.readValue(requestBody, com.example.datn.dto.request.AddMultipleVariantsRequestV2.class);
                
                return handleMultipleVariantsV2(requestV2);
            } else {
                // Format cũ với mauSacIds + congSuatIds arrays  
                com.example.datn.dto.request.AddMultipleVariantsRequest request = 
                    objectMapper.readValue(requestBody, com.example.datn.dto.request.AddMultipleVariantsRequest.class);
                
                return handleMultipleVariantsV1(request);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi khi parse request: " + e.getMessage()
            ));
        }
    }
    
    // Xử lý format cũ (legacy)
    private ResponseEntity<?> handleMultipleVariantsV1(com.example.datn.dto.request.AddMultipleVariantsRequest request) {
        try {
            // Validate dữ liệu đầu vào
            if (request.getSanPhamId() == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "ID sản phẩm không được để trống"));
            }
            
            if (request.getMauSacIds() == null || request.getMauSacIds().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Phải chọn ít nhất một màu sắc"));
            }
            
            if (request.getCongSuatIds() == null || request.getCongSuatIds().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Phải chọn ít nhất một công suất"));
            }
            
            if (request.getHangId() == null || request.getNutBamId() == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Hãng và nút bấm không được để trống"));
            }
            
            // Thêm các biến thể
            var createdVariants = sanPhamChiTietService.addMultipleVariants(request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã tạo " + createdVariants.size() + " biến thể sản phẩm",
                "data", createdVariants
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi khi tạo biến thể: " + e.getMessage()
            ));
        }
    }
    
    // Xử lý format mới (với preview)
    private ResponseEntity<?> handleMultipleVariantsV2(com.example.datn.dto.request.AddMultipleVariantsRequestV2 request) {
        try {
            // Validate dữ liệu đầu vào
            if (request.getSanPhamId() == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "ID sản phẩm không được để trống"));
            }
            
            if (request.getVariants() == null || request.getVariants().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Danh sách biến thể không được để trống"));
            }
            
            // Validate từng biến thể
            for (com.example.datn.dto.request.AddMultipleVariantsRequestV2.VariantDetail variant : request.getVariants()) {
                if (variant.getMauSacId() == null || variant.getCongSuatId() == null || 
                    variant.getHangId() == null || variant.getNutBamId() == null) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, 
                        "message", "Thiếu thông tin bắt buộc trong biến thể"));
                }
            }
            
            // Thêm các biến thể từ danh sách chi tiết
            List<com.example.datn.entity.SanPham.SanPhamChiTiet> createdVariants = sanPhamChiTietService.addMultipleVariantsV2(request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã lưu thành công " + createdVariants.size() + " biến thể sản phẩm",
                "data", createdVariants
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi khi lưu biến thể: " + e.getMessage()
            ));
        }
    }
    
    // Thêm nhiều biến thể với hình ảnh
    @PostMapping("/chi-tiet/add-multiple-with-images")
    @ResponseBody
    public ResponseEntity<?> addMultipleVariantsWithImages(
            @RequestParam("sanPhamId") Long sanPhamId,
            @RequestParam Map<String, String> params,
            @RequestParam Map<String, MultipartFile> files) {
        
        try {
            System.out.println("=== NHẬN REQUEST VỚI HÌNH ẢNH ===");
            System.out.println("sanPhamId: " + sanPhamId);
            System.out.println("Params keys: " + params.keySet());
            System.out.println("Files keys: " + files.keySet());
            
            // Parse variants từ params
            java.util.List<com.example.datn.dto.request.AddMultipleVariantsRequestV2.VariantDetail> variants = 
                new java.util.ArrayList<>();
            
            // Đếm số variants
            int variantCount = 0;
            for (String key : params.keySet()) {
                if (key.startsWith("variants[") && key.endsWith("].mauSacId")) {
                    variantCount++;
                }
            }
            
            System.out.println("Số lượng variants: " + variantCount);
            
            // Parse từng variant
            for (int i = 0; i < variantCount; i++) {
                com.example.datn.dto.request.AddMultipleVariantsRequestV2.VariantDetail variant = 
                    new com.example.datn.dto.request.AddMultipleVariantsRequestV2.VariantDetail();
                
                variant.setMauSacId(Long.parseLong(params.get("variants[" + i + "].mauSacId")));
                variant.setCongSuatId(Long.parseLong(params.get("variants[" + i + "].congSuatId")));
                variant.setHangId(Long.parseLong(params.get("variants[" + i + "].hangId")));
                variant.setNutBamId(Long.parseLong(params.get("variants[" + i + "].nutBamId")));
                variant.setSoLuong(Integer.parseInt(params.get("variants[" + i + "].soLuong")));
                variant.setGia(Integer.parseInt(params.get("variants[" + i + "].gia")));
                variant.setCanNang(Double.parseDouble(params.get("variants[" + i + "].canNang")));
                variant.setMoTa(params.get("variants[" + i + "].moTa"));
                variant.setTrangThai(Boolean.parseBoolean(params.get("variants[" + i + "].trangThai")));
                
                variants.add(variant);
                
                System.out.println("Parsed variant " + i + ": " + variant.getMauSacId() + ", " + variant.getCongSuatId());
            }
            
            // Tạo request V2
            com.example.datn.dto.request.AddMultipleVariantsRequestV2 request = 
                new com.example.datn.dto.request.AddMultipleVariantsRequestV2();
            request.setSanPhamId(sanPhamId);
            request.setVariants(variants);
            
            // Lưu variants trước (không có ảnh)
            java.util.List<com.example.datn.entity.SanPham.SanPhamChiTiet> createdVariants = 
                sanPhamChiTietService.addMultipleVariantsV2(request);
            
            System.out.println("Đã tạo " + createdVariants.size() + " variants");
            
            // Sau đó cập nhật ảnh cho từng variant có ảnh
            int imageUpdated = 0;
            for (int i = 0; i < createdVariants.size(); i++) {
                String imageKey = "variantImages[" + i + "]";
                if (files.containsKey(imageKey)) {
                    MultipartFile imageFile = files.get(imageKey);
                    if (imageFile != null && !imageFile.isEmpty()) {
                        System.out.println("Cập nhật ảnh cho variant " + i + ": " + imageFile.getOriginalFilename());
                        
                        // Update variant với ảnh
                        com.example.datn.entity.SanPham.SanPhamChiTiet variant = createdVariants.get(i);
                        sanPhamChiTietService.update(
                            variant.getId(),
                            null, null, null, null, // Không đổi thuộc tính
                            null, null, null, null, null, // Không đổi thông tin khác  
                            imageFile // Chỉ cập nhật ảnh
                        );
                        imageUpdated++;
                    }
                }
            }
            
            System.out.println("Đã cập nhật ảnh cho " + imageUpdated + " variants");
            
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "Đã lưu thành công " + createdVariants.size() + " biến thể sản phẩm" + 
                          (imageUpdated > 0 ? " (có " + imageUpdated + " ảnh)" : ""),
                "data", createdVariants
            ));
            
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu variants với ảnh: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", "Lỗi khi lưu biến thể: " + e.getMessage()
            ));
        }
    }

    // Sửa biến thể sản phẩm
    @GetMapping("/chi-tiet/edit")
    public String showEditVariantForm(@RequestParam(value = "id") Long id, Model model) {
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietService.findById(id);
        if (sanPhamChiTiet == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể sản phẩm");
        }
        
        model.addAttribute("sanPham", sanPhamChiTiet.getSanPham());
        model.addAttribute("sanPhamChiTiet", sanPhamChiTiet);
        model.addAttribute("mauSacList", mauSacService.findAllMauSac());
        model.addAttribute("congSuatList", congSuatService.findAllCongSuat());
        model.addAttribute("hangList", hangService.findAllHang());
        model.addAttribute("nutBamList", nutBamService.findAll());
        
        return "admin/san_pham/edit-variant";
    }

    // Debug endpoint - kiểm tra dữ liệu
    @GetMapping("/debug/data")
    @ResponseBody
    public ResponseEntity<?> debugData() {
        return ResponseEntity.ok(Map.of(
            "mauSacCount", mauSacService.findAllMauSac().size(),
            "congSuatCount", congSuatService.findAllCongSuat().size(),
            "hangCount", hangService.findAllHang().size(),
            "nutBamCount", nutBamService.findAll().size(),
            "mauSacList", mauSacService.findAllMauSac(),
            "congSuatList", congSuatService.findAllCongSuat()
        ));
    }
}