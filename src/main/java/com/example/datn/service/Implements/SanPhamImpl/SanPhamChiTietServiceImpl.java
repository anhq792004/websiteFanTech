package com.example.datn.service.Implements.SanPhamImpl;

import com.example.datn.dto.request.AddMultipleVariantsRequest;
import com.example.datn.dto.request.AddMultipleVariantsRequestV2;
import com.example.datn.entity.HinhAnh;
import com.example.datn.entity.SanPham.SanPham;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.entity.ThuocTinh.CongSuat;
import com.example.datn.entity.ThuocTinh.Hang;
import com.example.datn.entity.ThuocTinh.MauSac;
import com.example.datn.entity.ThuocTinh.NutBam;
import com.example.datn.repository.HinhAnhRepo;
import com.example.datn.repository.SanPhamRepo.SanPhamChiTietRepository;
import com.example.datn.repository.SanPhamRepo.SanPhamRepo;
import com.example.datn.repository.ThuocTinhRepo.CongSuatRepository;
import com.example.datn.repository.ThuocTinhRepo.HangRepository;
import com.example.datn.repository.ThuocTinhRepo.MauSacRepository;
import com.example.datn.repository.ThuocTinhRepo.NutBamRepository;
import com.example.datn.service.FileUploadService;
import com.example.datn.service.SanPhamSerivce.SanPhamChiTietService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SanPhamChiTietServiceImpl implements SanPhamChiTietService {

    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final SanPhamRepo sanPhamRepo;
    private final MauSacRepository mauSacRepository;
    private final CongSuatRepository congSuatRepository;
    private final HangRepository hangRepository;
    private final NutBamRepository nutBamRepository;
    private final HinhAnhRepo hinhAnhRepo;
    private final FileUploadService fileUploadService;

    @Override
    public SanPhamChiTiet findById(Long id) {
        return sanPhamChiTietRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public List<SanPhamChiTiet> addMultipleVariants(AddMultipleVariantsRequest request) {
        log.info("=== BẮT ĐẦU THÊM NHIỀU BIẾN THỂ ===");
        log.info("Sản phẩm ID: {}", request.getSanPhamId());
        log.info("Màu sắc IDs: {}", request.getMauSacIds());
        log.info("Công suất IDs: {}", request.getCongSuatIds());

        // Kiểm tra sản phẩm tồn tại
        SanPham sanPham = sanPhamRepo.findById(request.getSanPhamId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Lấy thông tin hãng và nút bấm
        Hang hang = hangRepository.findById(request.getHangId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hãng"));
        
        NutBam nutBam = nutBamRepository.findById(request.getNutBamId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nút bấm"));

        List<SanPhamChiTiet> createdVariants = new ArrayList<>();

        // Tạo biến thể cho mỗi kết hợp màu sắc và công suất
        for (Long mauSacId : request.getMauSacIds()) {
            MauSac mauSac = mauSacRepository.findById(mauSacId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + mauSacId));

            for (Long congSuatId : request.getCongSuatIds()) {
                CongSuat congSuat = congSuatRepository.findById(congSuatId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy công suất với ID: " + congSuatId));

                // Kiểm tra biến thể đã tồn tại chưa (kiểm tra tất cả thuộc tính)
                boolean exists = sanPhamChiTietRepository.existsBySanPhamIdAndMauSacIdAndCongSuatIdAndHangIdAndNutBamId(
                        request.getSanPhamId(), mauSacId, congSuatId, request.getHangId(), request.getNutBamId());

                if (exists) {
                    log.warn("Không thể tạo biến thể vì đã tồn tại biến thể với cùng thuộc tính: SP={}, Màu={}, Công suất={}, Hãng={}, Nút bấm={}", 
                            request.getSanPhamId(), mauSac.getTen(), congSuat.getTen(), hang.getTen(), nutBam.getTen());
                    throw new RuntimeException("Biến thể với các thuộc tính này đã tồn tại: " + mauSac.getTen() + " - " + congSuat.getTen() + " - " + hang.getTen() + " - " + nutBam.getTen());
                }

                // Tạo biến thể mới
                SanPhamChiTiet variant = new SanPhamChiTiet();
                variant.setSanPham(sanPham);
                variant.setMauSac(mauSac);
                variant.setCongSuat(congSuat);
                variant.setHang(hang);
                variant.setNutBam(nutBam);
                variant.setSoLuong(request.getSoLuong());
                variant.setGia(request.getGia());
                variant.setCanNang(request.getCanNang());
                variant.setMoTa(request.getMoTa());
                variant.setTrangThai(request.getTrangThai());

                SanPhamChiTiet savedVariant = sanPhamChiTietRepository.save(variant);
                createdVariants.add(savedVariant);

                log.info("Đã tạo biến thể: {} - {} - {}", 
                        sanPham.getTen(), mauSac.getTen(), congSuat.getTen());
            }
        }

        log.info("=== HOÀN THÀNH THÊM {} BIẾN THỂ ===", createdVariants.size());
        return createdVariants;
    }

    @Override
    @Transactional
    public void update(Long id, Long mauSacId, Long congSuatId, Long hangId, Long nutBamId,
                      Integer soLuong, Double gia, Double canNang, Boolean trangThai,
                      String moTa, MultipartFile hinhAnh) {
        log.info("=== BẮT ĐẦU CẬP NHẬT SPCT ===");
        log.info("ID: {}", id);
        log.info("MauSacId: {}", mauSacId);
        log.info("CongSuatId: {}", congSuatId);
        log.info("HangId: {}", hangId);
        log.info("NutBamId: {}", nutBamId);
        log.info("SoLuong: {}", soLuong);
        log.info("Gia: {}", gia);
        log.info("TrangThai: {}", trangThai);
        log.info("HinhAnh file: {}", hinhAnh != null ? hinhAnh.getOriginalFilename() : "null");
        
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết"));
        log.info("Tìm thấy SPCT: {}", spct.getId());

        if (mauSacId != null) {
            MauSac mauSac = mauSacRepository.findById(mauSacId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc"));
            spct.setMauSac(mauSac);
            log.info("Set màu sắc: {}", mauSac.getTen());
        }

        if (congSuatId != null) {
            CongSuat congSuat = congSuatRepository.findById(congSuatId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy công suất"));
            spct.setCongSuat(congSuat);
            log.info("Set công suất: {}", congSuat.getTen());
        }

        if (hangId != null) {
            Hang hang = hangRepository.findById(hangId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hãng"));
            spct.setHang(hang);
            log.info("Set hãng: {}", hang.getTen());
        }

        if (nutBamId != null) {
            NutBam nutBam = nutBamRepository.findById(nutBamId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nút bấm"));
            spct.setNutBam(nutBam);
            log.info("Set nút bấm: {}", nutBam.getTen());
        }

        if (soLuong != null) {
            spct.setSoLuong(soLuong);
            log.info("Set số lượng: {}", soLuong);
        }
        if (gia != null) {
            spct.setGia(BigDecimal.valueOf(gia));
            log.info("Set giá: {}", gia);
        }
        if (canNang != null) {
            spct.setCanNang(canNang.floatValue());
            log.info("Set cân nặng: {}", canNang);
        }
        if (trangThai != null) {
            spct.setTrangThai(trangThai);
            log.info("Set trạng thái: {}", trangThai);
        }
        if (moTa != null) {
            spct.setMoTa(moTa);
            log.info("Set mô tả: {}", moTa.length() > 50 ? moTa.substring(0, 50) + "..." : moTa);
        }

        // Xử lý hình ảnh
        if (hinhAnh != null && !hinhAnh.isEmpty()) {
            log.info("=== XỬ LÝ HÌNH ẢNH ===");
            log.info("File name: {}", hinhAnh.getOriginalFilename());

            try {
                // Sử dụng FileUploadService để lưu file
                String imagePath = fileUploadService.saveFile(hinhAnh);
                log.info("Đã lưu file tại: {}", imagePath);

                // Cập nhật hoặc tạo mới HinhAnh
                HinhAnh hinhAnhEntity = spct.getHinhAnh();
                if (hinhAnhEntity == null) {
                    hinhAnhEntity = new HinhAnh();
                    log.info("Tạo mới HinhAnh entity");
                } else {
                    log.info("Cập nhật HinhAnh entity có ID: {}", hinhAnhEntity.getId());
                    // Xóa hình ảnh cũ
                    String oldImagePath = hinhAnhEntity.getHinhAnh();
                    if (oldImagePath != null && !oldImagePath.isEmpty()) {
                        fileUploadService.deleteFile(oldImagePath);
                        log.info("Đã xóa hình ảnh cũ: {}", oldImagePath);
                    }
                }
                hinhAnhEntity.setHinhAnh(imagePath);
                // Lưu HinhAnh trước
                hinhAnhEntity = hinhAnhRepo.save(hinhAnhEntity);
                log.info("Đã lưu HinhAnh với ID: {}", hinhAnhEntity.getId());
                spct.setHinhAnh(hinhAnhEntity);
            } catch (IOException e) {
                log.error("Lỗi khi lưu file: {}", hinhAnh.getOriginalFilename(), e);
                throw new RuntimeException("Không thể lưu file: " + hinhAnh.getOriginalFilename(), e);
            }
        } else {
            log.info("Không có hình ảnh mới để cập nhật");
        }

        try {
            SanPhamChiTiet saved = sanPhamChiTietRepository.save(spct);
            log.info("=== CẬP NHẬT THÀNH CÔNG ===");
            log.info("SPCT đã lưu với ID: {}", saved.getId());
        } catch (Exception e) {
            log.error("=== LỖI KHI LƯU SPCT ===", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public SanPhamChiTiet add(Long sanPhamId, Long mauSacId, Long congSuatId, Long hangId, Long nutBamId,
                             Integer soLuong, Double gia, Double canNang, Boolean trangThai,
                             String moTa, MultipartFile hinhAnh) {
        log.info("Bắt đầu thêm sản phẩm chi tiết mới cho sản phẩm ID: {}", sanPhamId);
        
        // Kiểm tra biến thể đã tồn tại chưa (kiểm tra tất cả thuộc tính)
        log.info("=== KIỂM TRA DUPLICATE (TẤT CẢ THUỘC TÍNH) ===");
        log.info("SanPham ID: {}, MauSac ID: {}, CongSuat ID: {}, Hang ID: {}, NutBam ID: {}", 
                sanPhamId, mauSacId, congSuatId, hangId, nutBamId);
        
        boolean exists = sanPhamChiTietRepository.existsBySanPhamIdAndMauSacIdAndCongSuatIdAndHangIdAndNutBamId(
                sanPhamId, mauSacId, congSuatId, hangId, nutBamId);
        
        if (exists) {
            log.warn("=== BIẾN THỂ ĐÃ TỒN TẠI (CÙNG TẤT CẢ THUỘC TÍNH) ===");
            throw new RuntimeException("Biến thể với các thuộc tính này đã tồn tại! Vui lòng thay đổi ít nhất một thuộc tính.");
        }
        
        log.info("=== BIẾN THỂ CHƯA TỒN TẠI, TIẾP TỤC TẠO MỚI ===");
        SanPhamChiTiet spct = new SanPhamChiTiet();
        
        // Set sản phẩm
        com.example.datn.entity.SanPham.SanPham sanPham = new com.example.datn.entity.SanPham.SanPham();
        sanPham.setId(sanPhamId);
        spct.setSanPham(sanPham);

        // Set thuộc tính
        MauSac mauSac = mauSacRepository.findById(mauSacId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc"));
        spct.setMauSac(mauSac);

        CongSuat congSuat = congSuatRepository.findById(congSuatId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công suất"));
        spct.setCongSuat(congSuat);

        Hang hang = hangRepository.findById(hangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hãng"));
        spct.setHang(hang);

        NutBam nutBam = nutBamRepository.findById(nutBamId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nút bấm"));
        spct.setNutBam(nutBam);

        // Set thông tin cơ bản
        spct.setSoLuong(soLuong);
        spct.setGia(BigDecimal.valueOf(gia));
        spct.setCanNang(canNang != null ? canNang.floatValue() : null);
        spct.setTrangThai(trangThai);
        spct.setMoTa(moTa);

        // Xử lý hình ảnh
        if (hinhAnh != null && !hinhAnh.isEmpty()) {
            try {
                // Sử dụng FileUploadService để lưu file
                String imagePath = fileUploadService.saveFile(hinhAnh);
                log.info("Đã lưu file tại: {}", imagePath);

                // Tạo HinhAnh entity
                HinhAnh hinhAnhEntity = new HinhAnh();
                hinhAnhEntity.setHinhAnh(imagePath);
                // Lưu HinhAnh trước
                hinhAnhEntity = hinhAnhRepo.save(hinhAnhEntity);
                spct.setHinhAnh(hinhAnhEntity);
            } catch (IOException e) {
                throw new RuntimeException("Không thể lưu file: " + hinhAnh.getOriginalFilename(), e);
            }
        }

        SanPhamChiTiet result = sanPhamChiTietRepository.save(spct);
        log.info("Thêm sản phẩm chi tiết thành công với ID: {}", result.getId());
        return result;
    }

    @Override
    @Transactional
    public void updateInline(Long id, Integer soLuong, Double gia) {
        log.info("=== BẮT ĐẦU INLINE UPDATE ===");
        log.info("ID: {}, SoLuong: {}, Gia: {}", id, soLuong, gia);
        
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết"));
        log.info("Tìm thấy SPCT: {}", spct.getId());

        // Chỉ cập nhật field được gửi
        if (soLuong != null) {
            spct.setSoLuong(soLuong);
            log.info("Cập nhật số lượng: {}", soLuong);
        }
        
        if (gia != null) {
            spct.setGia(BigDecimal.valueOf(gia));
            log.info("Cập nhật giá: {}", gia);
        }

        try {
            SanPhamChiTiet saved = sanPhamChiTietRepository.save(spct);
            log.info("=== INLINE UPDATE THÀNH CÔNG ===");
            log.info("SPCT đã lưu với ID: {}", saved.getId());
        } catch (Exception e) {
            log.error("=== LỖI KHI LƯU INLINE UPDATE ===", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public List<SanPhamChiTiet> addMultipleVariantsV2(AddMultipleVariantsRequestV2 request) {
        log.info("=== BẮT ĐẦU THÊM NHIỀU BIẾN THỂ V2 ===");
        log.info("Sản phẩm ID: {}", request.getSanPhamId());
        log.info("Số lượng biến thể: {}", request.getVariants().size());

        // Kiểm tra sản phẩm tồn tại
        SanPham sanPham = sanPhamRepo.findById(request.getSanPhamId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        List<SanPhamChiTiet> createdVariants = new ArrayList<>();

        // Tạo từng biến thể từ danh sách chi tiết
        for (AddMultipleVariantsRequestV2.VariantDetail variant : request.getVariants()) {
            log.info("Đang xử lý biến thể: MauSac={}, CongSuat={}", 
                    variant.getMauSacId(), variant.getCongSuatId());

            // Lấy các entity cần thiết
            MauSac mauSac = mauSacRepository.findById(variant.getMauSacId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + variant.getMauSacId()));

            CongSuat congSuat = congSuatRepository.findById(variant.getCongSuatId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy công suất với ID: " + variant.getCongSuatId()));

            Hang hang = hangRepository.findById(variant.getHangId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hãng với ID: " + variant.getHangId()));

            NutBam nutBam = nutBamRepository.findById(variant.getNutBamId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nút bấm với ID: " + variant.getNutBamId()));

            // Kiểm tra biến thể đã tồn tại chưa (kiểm tra tất cả thuộc tính)
            boolean exists = sanPhamChiTietRepository.existsBySanPhamIdAndMauSacIdAndCongSuatIdAndHangIdAndNutBamId(
                    request.getSanPhamId(), variant.getMauSacId(), variant.getCongSuatId(), variant.getHangId(), variant.getNutBamId());

            if (exists) {
                log.warn("Biến thể đã tồn tại với cùng tất cả thuộc tính: SP={}, Màu={}, Công suất={}, Hãng={}, Nút bấm={}", 
                        request.getSanPhamId(), mauSac.getTen(), congSuat.getTen(), hang.getTen(), nutBam.getTen());
                continue;
            }

            // Tạo biến thể mới
            SanPhamChiTiet newVariant = new SanPhamChiTiet();
            newVariant.setSanPham(sanPham);
            newVariant.setMauSac(mauSac);
            newVariant.setCongSuat(congSuat);
            newVariant.setHang(hang);
            newVariant.setNutBam(nutBam);
            newVariant.setSoLuong(variant.getSoLuong());
            newVariant.setGia(variant.getGia() != null ? BigDecimal.valueOf(variant.getGia()) : BigDecimal.ZERO);
            newVariant.setCanNang(variant.getCanNang() != null ? variant.getCanNang().floatValue() : null);
            newVariant.setMoTa(variant.getMoTa());
            newVariant.setTrangThai(variant.getTrangThai());

            SanPhamChiTiet savedVariant = sanPhamChiTietRepository.save(newVariant);
            createdVariants.add(savedVariant);

            log.info("Đã tạo biến thể: {} - {} - {}", 
                    sanPham.getTen(), mauSac.getTen(), congSuat.getTen());
        }

        log.info("=== HOÀN THÀNH THÊM {} BIẾN THỂ V2 ===", createdVariants.size());
        return createdVariants;
    }

    @Override
    public void delete(Long id) {
        sanPhamChiTietRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void toggleStatus(Long id) {
        log.info("=== BẮT ĐẦU THAY ĐỔI TRẠNG THÁI SPCT ===");
        log.info("ID: {}", id);
        
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết với ID: " + id));
        
        Boolean currentStatus = spct.getTrangThai();
        Boolean newStatus = currentStatus == null ? false : !currentStatus;
        
        log.info("Trạng thái hiện tại: {}, Trạng thái mới: {}", currentStatus, newStatus);
        
        spct.setTrangThai(newStatus);
        
        try {
            SanPhamChiTiet saved = sanPhamChiTietRepository.save(spct);
            log.info("=== THAY ĐỔI TRẠNG THÁI THÀNH CÔNG ===");
            log.info("SPCT ID: {}, Trạng thái mới: {}", saved.getId(), saved.getTrangThai());
        } catch (Exception e) {
            log.error("=== LỖI KHI THAY ĐỔI TRẠNG THÁI ===", e);
            throw new RuntimeException("Không thể thay đổi trạng thái: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean checkDuplicate(Long sanPhamId, Long mauSacId, Long congSuatId, Long hangId, Long nutBamId) {
        log.info("=== KIỂM TRA DUPLICATE CHO PREVIEW ===");
        log.info("SanPham ID: {}, MauSac ID: {}, CongSuat ID: {}, Hang ID: {}, NutBam ID: {}", 
                sanPhamId, mauSacId, congSuatId, hangId, nutBamId);
        
        boolean exists = sanPhamChiTietRepository.existsBySanPhamIdAndMauSacIdAndCongSuatIdAndHangIdAndNutBamId(
                sanPhamId, mauSacId, congSuatId, hangId, nutBamId);
        
        log.info("Kết quả check duplicate: {}", exists);
        return exists;
    }
} 