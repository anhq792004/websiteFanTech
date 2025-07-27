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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                // Sử dụng FileUploadService để lưu file
                String imagePath = fileUploadService.saveFile(imageFile);
                // Kiểm tra xem sản phẩm đã có hình ảnh chưa
                boolean hasImage = false;
                HinhAnh existingImage = null;

                if (existingSanPham.getSanPhamChiTiet() != null && !existingSanPham.getSanPhamChiTiet().isEmpty()) {
                    for (SanPhamChiTiet spct : existingSanPham.getSanPhamChiTiet()) {
                        if (spct.getHinhAnh() != null) {
                            hasImage = true;
                            existingImage = spct.getHinhAnh();
                            
                            // Xóa hình ảnh cũ và cập nhật hình ảnh mới
                            String oldImagePath = existingImage.getHinhAnh();
                            if (oldImagePath != null && !oldImagePath.isEmpty()) {
                                fileUploadService.deleteFile(oldImagePath);
                            }
                            
                            existingImage.setHinhAnh(imagePath);
                            hinhAnhRepo.save(existingImage);
                            break;
                        }
                    }
                }

                // Nếu không có hình ảnh, tạo mới
                if (!hasImage) {
                    // Tạo đối tượng HinhAnh mới
                    HinhAnh hinhAnh = new HinhAnh();
                    hinhAnh.setHinhAnh(imagePath);
                    HinhAnh savedHinhAnh = hinhAnhRepo.save(hinhAnh);

                    // Tạo SanPhamChiTiet mới với ảnh
                    SanPhamChiTiet spct = new SanPhamChiTiet();
                    spct.setSanPham(sanPham);
                    spct.setHinhAnh(savedHinhAnh);
                    spct.setTrangThai(true);

                    // Khởi tạo danh sách nếu chưa có
                    if (sanPham.getSanPhamChiTiet() == null) {
                        sanPham.setSanPhamChiTiet(new ArrayList<>());
                    }

                    sanPhamChiTietRepo.save(spct);
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
}
