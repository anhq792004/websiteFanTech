package com.example.datn.service.Implements;

import com.example.datn.entity.HinhAnh;
import com.example.datn.entity.SanPham.SanPham;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.repository.HinhAnhRepo;
import com.example.datn.repository.SanPhamRepo.SanPhamChiTietRepo;
import com.example.datn.repository.SanPhamRepo.SanPhamRepo;
import com.example.datn.service.FileUploadService;
import com.example.datn.service.SanPhamSerivce.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SanPhamServiceImpl implements SanPhamService {
    private final SanPhamRepo sanPhamRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final HinhAnhRepo hinhAnhRepo;
    private final FileUploadService fileUploadService;

    @Override
    public Page<SanPham> findAllSanPham(int page, int size, String search, Long kieuQuatId, Boolean trangThai) {
        Pageable pageable = PageRequest.of(page, size);

        if (search != null && !search.isEmpty()) {
            if (kieuQuatId != null && trangThai != null) {
                return sanPhamRepo.findByTenContainingAndKieuQuatIdAndTrangThai(search, kieuQuatId, trangThai, pageable);
            } else if (kieuQuatId != null) {
                return sanPhamRepo.findByTenContainingAndKieuQuatId(search, kieuQuatId, pageable);
            } else if (trangThai != null) {
                return sanPhamRepo.findByTenContainingAndTrangThai(search, trangThai, pageable);
            } else {
                return sanPhamRepo.findByTenContaining(search, pageable);
            }
        } else {
            if (kieuQuatId != null && trangThai != null) {
                return sanPhamRepo.findByKieuQuatIdAndTrangThai(kieuQuatId, trangThai, pageable);
            } else if (kieuQuatId != null) {
                return sanPhamRepo.findByKieuQuatId(kieuQuatId, pageable);
            } else if (trangThai != null) {
                return sanPhamRepo.findByTrangThai(trangThai, pageable);
            } else {
                return sanPhamRepo.findAll(pageable);
            }
        }
    }

    @Override
    public List<SanPham> findAllActiveProducts() {
        return sanPhamRepo.findByTrangThaiTrue();
    }

    @Override
    public Page<SanPham> findAllActiveProductsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepo.findByTrangThaiTrue(pageable);
    }

    @Override
    public void saveSanPham(SanPham sanPham) {
        if (sanPham.getMa() == null || sanPham.getMa().trim().isEmpty()) {
            sanPham.setMa(generateNextProductCode());
        }
        
        // Đảm bảo ngày tạo được thiết lập
        if (sanPham.getNgayTao() == null) {
            sanPham.setNgayTao(LocalDateTime.now());
        }
        sanPhamRepo.save(sanPham);
    }
    private String generateNextProductCode() {
        List<String> latestCodes = sanPhamRepo.findLatestProductCode();
        
        if (latestCodes.isEmpty()) {
            // Nếu chưa có sản phẩm nào, bắt đầu từ SP001
            return "SP001";
        }
        
        String latestCode = latestCodes.get(0);
        try {
            // Lấy phần số từ mã (bỏ "SP" ở đầu)
            String numberPart = latestCode.substring(2);
            int nextNumber = Integer.parseInt(numberPart) + 1;
            
            // Format lại thành SP + 3 chữ số (001, 002, ...)
            return String.format("SP%03d", nextNumber);
        } catch (Exception e) {
            // Nếu có lỗi trong việc parse, tìm số lượng sản phẩm hiện có và tạo mã mới
            long productCount = sanPhamRepo.count();
            return String.format("SP%03d", productCount + 1);
        }
    }

    @Override
    public void saveSanPhamWithImage(SanPham sanPham, MultipartFile imageFile) {
        // Tự động tạo mã sản phẩm nếu chưa có
        if (sanPham.getMa() == null || sanPham.getMa().trim().isEmpty()) {
            sanPham.setMa(generateNextProductCode());
        }
        
        // Đảm bảo ngày tạo được thiết lập
        if (sanPham.getNgayTao() == null) {
            sanPham.setNgayTao(LocalDateTime.now());
        }

        // Chỉ lưu sản phẩm, không tạo biến thể
        // Hình ảnh sẽ được thêm sau khi tạo biến thể riêng biệt
        sanPhamRepo.save(sanPham);

        // Lưu file hình ảnh vào thư mục để sử dụng sau này
        try {
            String imagePath = fileUploadService.saveFile(imageFile);
            // Log đường dẫn file để sử dụng khi tạo biến thể
            System.out.println("Đã lưu hình ảnh sản phẩm tại: " + imagePath);
            System.out.println("Hình ảnh sẽ được gán khi tạo biến thể sản phẩm chi tiết");
        } catch (IOException e) {
            e.printStackTrace();
            // Không throw exception để không ảnh hưởng đến việc tạo sản phẩm
            System.err.println("Lỗi khi lưu hình ảnh: " + e.getMessage());
        }
    }

    @Override
    public void updateSanPham(SanPham sanPham) {
        // Lấy sản phẩm hiện tại để giữ ngày tạo
        Optional<SanPham> existingSanPham = sanPhamRepo.findById(sanPham.getId());
        if (existingSanPham.isPresent()) {
            // Giữ ngày tạo ban đầu
            sanPham.setNgayTao(existingSanPham.get().getNgayTao());
            // Giữ danh sách chi tiết
            sanPham.setSanPhamChiTiet(existingSanPham.get().getSanPhamChiTiet());
        }
        sanPhamRepo.save(sanPham);
    }

    @Override
    public void updateSanPhamWithImage(SanPham sanPham, MultipartFile imageFile) {
        // Lấy sản phẩm hiện tại
        Optional<SanPham> existingSanPhamOpt = sanPhamRepo.findById(sanPham.getId());
        if (existingSanPhamOpt.isPresent()) {
            SanPham existingSanPham = existingSanPhamOpt.get();
            // Giữ ngày tạo ban đầu
            sanPham.setNgayTao(existingSanPham.getNgayTao());
            // Giữ danh sách chi tiết
            sanPham.setSanPhamChiTiet(existingSanPham.getSanPhamChiTiet());

            // Lưu sản phẩm
            sanPhamRepo.save(sanPham);

            try {
                String imagePath = fileUploadService.saveFile(imageFile);
                
                if (existingSanPham.getSanPhamChiTiet() != null && !existingSanPham.getSanPhamChiTiet().isEmpty()) {
                    SanPhamChiTiet firstVariant = existingSanPham.getSanPhamChiTiet().get(0);
                    
                    if (firstVariant.getHinhAnh() != null) {
                        // Cập nhật hình ảnh hiện có
                        String oldImagePath = firstVariant.getHinhAnh().getHinhAnh();
                        if (oldImagePath != null && !oldImagePath.isEmpty()) {
                            fileUploadService.deleteFile(oldImagePath);
                        }
                        firstVariant.getHinhAnh().setHinhAnh(imagePath);
                        hinhAnhRepo.save(firstVariant.getHinhAnh());
                    } else {
                        HinhAnh hinhAnh = new HinhAnh();
                        hinhAnh.setHinhAnh(imagePath);
                        HinhAnh savedHinhAnh = hinhAnhRepo.save(hinhAnh);
                        firstVariant.setHinhAnh(savedHinhAnh);
                        sanPhamChiTietRepo.save(firstVariant);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi lưu hình ảnh: " + e.getMessage());
            }
        }
    }

    @Override
    public Optional<SanPham> findSanPhamById(Long id) {
        return sanPhamRepo.findById(id);
    }

    @Override
    public boolean thayDoiTrangThaiSanPham(Long id) {
        Optional<SanPham> sanPhamOptional = sanPhamRepo.findById(id);
        if (sanPhamOptional.isPresent()) {
            SanPham sanPham = sanPhamOptional.get();
            sanPham.setTrangThai(!sanPham.getTrangThai());
            sanPhamRepo.save(sanPham);
            return true;
        }
        return false;
    }

    @Override
    public List<SanPham> searchAndFilterProducts(String query, Long kieuQuatId, Long congSuatId, 
                                               Long hangId, Long mauSacId, Long nutBamId, 
                                               Double minPrice, Double maxPrice, int page, int size) {
        // Lấy tất cả sản phẩm đang hoạt động
        List<SanPham> allProducts = sanPhamRepo.findByTrangThaiTrue();
        
        // Filter theo các điều kiện
        return allProducts.stream()
                .filter(sanPham -> {
                    // Filter theo query (tên sản phẩm hoặc mã sản phẩm)
                    if (query != null && !query.trim().isEmpty()) {
                        String searchQuery = query.toLowerCase().trim();
                        String productName = sanPham.getTen() != null ? sanPham.getTen().toLowerCase() : "";
                        String productCode = sanPham.getMa() != null ? sanPham.getMa().toLowerCase() : "";
                        
                        if (!productName.contains(searchQuery) && !productCode.contains(searchQuery)) {
                            return false;
                        }
                    }
                    
                    // Filter theo kiểu quạt
                    if (kieuQuatId != null && (sanPham.getKieuQuat() == null || !sanPham.getKieuQuat().getId().equals(kieuQuatId))) {
                        return false;
                    }
                    
                    // Filter theo giá (chỉ kiểm tra biến thể đầu tiên - hiển thị)
                    if (minPrice != null || maxPrice != null) {
                        if (sanPham.getSanPhamChiTiet() == null || sanPham.getSanPhamChiTiet().isEmpty()) {
                            return false;
                        }
                        
                        // Lấy biến thể đầu tiên (biến thể hiển thị)
                        SanPhamChiTiet firstVariant = sanPham.getSanPhamChiTiet().get(0);
                        if (firstVariant.getGia() == null) {
                            return false;
                        }
                        
                        BigDecimal price = firstVariant.getGia();
                        if (minPrice != null && price.compareTo(BigDecimal.valueOf(minPrice)) < 0) {
                            return false;
                        }
                        if (maxPrice != null && price.compareTo(BigDecimal.valueOf(maxPrice)) > 0) {
                            return false;
                        }
                    }
                    
                    // Filter theo các thuộc tính khác (công suất, hãng, màu sắc, nút bấm)
                    if (congSuatId != null || hangId != null || mauSacId != null || nutBamId != null) {
                        boolean hasValidAttributes = sanPham.getSanPhamChiTiet().stream()
                                .anyMatch(spct -> {
                                    if (congSuatId != null && (spct.getCongSuat() == null || !spct.getCongSuat().getId().equals(congSuatId))) return false;
                                    if (hangId != null && (spct.getHang() == null || !spct.getHang().getId().equals(hangId))) return false;
                                    if (mauSacId != null && (spct.getMauSac() == null || !spct.getMauSac().getId().equals(mauSacId))) return false;
                                    if (nutBamId != null && (spct.getNutBam() == null || !spct.getNutBam().getId().equals(nutBamId))) return false;
                                    return true;
                                });
                        if (!hasValidAttributes) return false;
                    }
                    
                    return true;
                })
                .skip(page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public long countFilteredProducts(String query, Long kieuQuatId, Long congSuatId, 
                                    Long hangId, Long mauSacId, Long nutBamId, 
                                    Double minPrice, Double maxPrice) {
        // Lấy tất cả sản phẩm đang hoạt động
        List<SanPham> allProducts = sanPhamRepo.findByTrangThaiTrue();
        
        // Filter theo các điều kiện (tương tự như searchAndFilterProducts nhưng không có pagination)
        return allProducts.stream()
                .filter(sanPham -> {
                    // Filter theo query (tên sản phẩm hoặc mã sản phẩm)
                    if (query != null && !query.trim().isEmpty()) {
                        String searchQuery = query.toLowerCase().trim();
                        String productName = sanPham.getTen() != null ? sanPham.getTen().toLowerCase() : "";
                        String productCode = sanPham.getMa() != null ? sanPham.getMa().toLowerCase() : "";
                        
                        if (!productName.contains(searchQuery) && !productCode.contains(searchQuery)) {
                            return false;
                        }
                    }
                    
                    // Filter theo kiểu quạt
                    if (kieuQuatId != null && (sanPham.getKieuQuat() == null || !sanPham.getKieuQuat().getId().equals(kieuQuatId))) {
                        return false;
                    }
                    
                    // Filter theo giá (chỉ kiểm tra biến thể đầu tiên - hiển thị)
                    if (minPrice != null || maxPrice != null) {
                        if (sanPham.getSanPhamChiTiet() == null || sanPham.getSanPhamChiTiet().isEmpty()) {
                            return false;
                        }
                        
                        // Lấy biến thể đầu tiên (biến thể hiển thị)
                        SanPhamChiTiet firstVariant = sanPham.getSanPhamChiTiet().get(0);
                        if (firstVariant.getGia() == null) {
                            return false;
                        }
                        
                        BigDecimal price = firstVariant.getGia();
                        if (minPrice != null && price.compareTo(BigDecimal.valueOf(minPrice)) < 0) {
                            return false;
                        }
                        if (maxPrice != null && price.compareTo(BigDecimal.valueOf(maxPrice)) > 0) {
                            return false;
                        }
                    }
                    
                    // Filter theo các thuộc tính khác (công suất, hãng, màu sắc, nút bấm)
                    if (congSuatId != null || hangId != null || mauSacId != null || nutBamId != null) {
                        boolean hasValidAttributes = sanPham.getSanPhamChiTiet().stream()
                                .anyMatch(spct -> {
                                    if (congSuatId != null && (spct.getCongSuat() == null || !spct.getCongSuat().getId().equals(congSuatId))) return false;
                                    if (hangId != null && (spct.getHang() == null || !spct.getHang().getId().equals(hangId))) return false;
                                    if (mauSacId != null && (spct.getMauSac() == null || !spct.getMauSac().getId().equals(mauSacId))) return false;
                                    if (nutBamId != null && (spct.getNutBam() == null || !spct.getNutBam().getId().equals(nutBamId))) return false;
                                    return true;
                                });
                        if (!hasValidAttributes) return false;
                    }
                    
                    return true;
                })
                .count();
    }

    @Override
    public Map<String, Double> getPriceRange() {
        Map<String, Double> priceRange = new HashMap<>();
        
        // Lấy giá min từ sản phẩm chi tiết
        Double minPrice = 0.0;
        
        // Cố định max = 5.000.000 VND theo yêu cầu
        Double maxPrice = 5000000.0;
        
        priceRange.put("min", minPrice != null ? minPrice : 0.0);
        priceRange.put("max", maxPrice);
        
        return priceRange;
    }
}
