package com.example.datn.controller;

import com.example.datn.entity.PhieuGiamGia;
import com.example.datn.entity.PhieuGiamGiaKhachHang;
import com.example.datn.repository.KhachHangRepo.KhachHangRepo;
import com.example.datn.repository.PhieuGiamGiaKhachHangRepo;
import com.example.datn.repository.PhieuGiamGiaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/phieu-giam-gia")
public class PhieuGiamGiaController {

    @Autowired
    private PhieuGiamGiaRepo phieuGiamGiaRepo;

    @Autowired
    private PhieuGiamGiaKhachHangRepo phieuGiamGiaKhachHangRepo;

    @Autowired
    private KhachHangRepo khachHangRepo;

    @GetMapping("/index")
    public String hienThiDanhSach(Model model,
                                  @RequestParam(value = "search", required = false) String search,
                                  @RequestParam(value = "trangThai", required = false) Boolean trangThai,
                                  @RequestParam(value = "ngayBatDau", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
                                  @RequestParam(value = "ngayKetThuc", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKetThuc) {

        List<PhieuGiamGia> dsPhieuGiamGia;
        Date currentDate = new Date();

        // Tìm kiếm và lọc dữ liệu
        if ((search != null && !search.trim().isEmpty()) ||
                trangThai != null ||
                ngayBatDau != null ||
                ngayKetThuc != null) {

            dsPhieuGiamGia = phieuGiamGiaRepo.findWithFilters(
                    search != null ? search.trim() : null,
                    trangThai,
                    ngayBatDau,
                    ngayKetThuc
            );
        } else {
            dsPhieuGiamGia = phieuGiamGiaRepo.findAll();
        }

        // Cập nhật trạng thái cho các phiếu hết hạn
        for (PhieuGiamGia pgg : dsPhieuGiamGia) {
            if (pgg.getNgayKetThuc() != null && pgg.getNgayKetThuc().before(currentDate) && pgg.isTrangThai()) {
                pgg.setTrangThai(false);
                phieuGiamGiaRepo.save(pgg);
            }
        }

        model.addAttribute("dsPhieuGiamGia", dsPhieuGiamGia);

        // Thêm các giá trị tìm kiếm vào model để giữ trong form
        model.addAttribute("search", search);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("ngayBatDau", ngayBatDau);
        model.addAttribute("ngayKetThuc", ngayKetThuc);

        return "admin/phieu_giam_gia/index";
    }

    @GetMapping("/create")
    public String hienThiFormTao(Model model) {
        PhieuGiamGia phieuGiamGia = new PhieuGiamGia();
        phieuGiamGia.setMa(generateNextCode());
        phieuGiamGia.setTrangThai(true);
        model.addAttribute("phieuGiamGia", phieuGiamGia);
        model.addAttribute("dsKhachHang", khachHangRepo.findByTrangThai(true));
        return "admin/phieu_giam_gia/create";
    }

    @PostMapping("/save")
    public String themMoi(@ModelAttribute PhieuGiamGia phieuGiamGia,
                          @RequestParam(value = "selectedKhachHang", required = false) List<Long> selectedKhachHang,
                          RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra và tạo mã nếu cần
            if (phieuGiamGia.getMa() == null || phieuGiamGia.getMa().trim().isEmpty()) {
                phieuGiamGia.setMa(generateNextCode());
            }

            phieuGiamGia.setNgayTao(new Date());

            // Validate dữ liệu
            if (!validatePhieuGiamGia(phieuGiamGia, redirectAttributes)) {
                return "redirect:/admin/phieu-giam-gia/create";
            }

            // Lưu phiếu giảm giá
            PhieuGiamGia savedPhieuGiamGia = phieuGiamGiaRepo.save(phieuGiamGia);

            // Nếu là phiếu cá nhân, lưu vào bảng trung gian
            if (!phieuGiamGia.getLoaiPhieu() && selectedKhachHang != null && !selectedKhachHang.isEmpty()) {
                for (Long khachHangId : selectedKhachHang) {
                    PhieuGiamGiaKhachHang pggKh = new PhieuGiamGiaKhachHang();
                    pggKh.setPhieuGiamGia(savedPhieuGiamGia);
                    pggKh.setKhachHang(khachHangRepo.findById(khachHangId).orElse(null));
                    pggKh.setDaSuDung(false);
                    pggKh.setNgayTao(new Date());
                    pggKh.setTrangThai(true);
                    pggKh.setNguoiTao("Admin"); // Có thể lấy từ session

                    phieuGiamGiaKhachHangRepo.save(pggKh);
                }
                redirectAttributes.addFlashAttribute("success",
                        "Tạo phiếu giảm giá cá nhân thành công cho " + selectedKhachHang.size() + " khách hàng!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Tạo phiếu giảm giá thành công!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/phieu-giam-gia/index";
    }

    @GetMapping("/edit/{id}")
    public String sua(@PathVariable Long id, Model model) {
        Optional<PhieuGiamGia> pgg = phieuGiamGiaRepo.findById(id);
        if (pgg.isPresent()) {
            model.addAttribute("phieuGiamGia", pgg.get());
            model.addAttribute("dsKhachHang", khachHangRepo.findByTrangThai(true));

            // Nếu là phiếu cá nhân, lấy danh sách khách hàng đã được cấp
            if (!pgg.get().getLoaiPhieu()) {
                List<PhieuGiamGiaKhachHang> dsKhachHangDaCoPhieu =
                        phieuGiamGiaKhachHangRepo.findByPhieuGiamGiaId(id);
                model.addAttribute("dsKhachHangDaCoPhieu", dsKhachHangDaCoPhieu);
            }

            return "admin/phieu_giam_gia/update";
        } else {
            return "redirect:/admin/phieu-giam-gia/index";
        }
    }

    @PostMapping("/update")
    public String capNhat(@ModelAttribute PhieuGiamGia phieuGiamGia,
                          @RequestParam(value = "selectedKhachHang", required = false) List<Long> selectedKhachHang,
                          RedirectAttributes redirectAttributes) {
        try {
            Optional<PhieuGiamGia> pggCu = phieuGiamGiaRepo.findById(phieuGiamGia.getId());
            if (pggCu.isPresent()) {
                phieuGiamGia.setNgayTao(pggCu.get().getNgayTao());

                if (phieuGiamGia.getMa() == null || phieuGiamGia.getMa().trim().isEmpty()) {
                    phieuGiamGia.setMa(pggCu.get().getMa());
                }

                // Validate dữ liệu
                if (!validatePhieuGiamGia(phieuGiamGia, redirectAttributes)) {
                    return "redirect:/admin/phieu-giam-gia/edit/" + phieuGiamGia.getId();
                }

                // Lưu phiếu giảm giá
                PhieuGiamGia savedPhieuGiamGia = phieuGiamGiaRepo.save(phieuGiamGia);

                // Xử lý phiếu cá nhân
                if (!phieuGiamGia.getLoaiPhieu()) {
                    // Xóa các quan hệ cũ (chỉ những chưa sử dụng)
                    List<PhieuGiamGiaKhachHang> oldRelations =
                            phieuGiamGiaKhachHangRepo.findByPhieuGiamGiaId(phieuGiamGia.getId());

                    for (PhieuGiamGiaKhachHang relation : oldRelations) {
                        if (!relation.getDaSuDung()) {
                            phieuGiamGiaKhachHangRepo.delete(relation);
                        }
                    }

                    // Thêm các quan hệ mới
                    if (selectedKhachHang != null && !selectedKhachHang.isEmpty()) {
                        for (Long khachHangId : selectedKhachHang) {
                            // Kiểm tra xem khách hàng đã có phiếu này chưa
                            if (!phieuGiamGiaKhachHangRepo.existsByPhieuGiamGiaIdAndKhachHangId(
                                    phieuGiamGia.getId(), khachHangId)) {

                                PhieuGiamGiaKhachHang pggKh = new PhieuGiamGiaKhachHang();
                                pggKh.setPhieuGiamGia(savedPhieuGiamGia);
                                pggKh.setKhachHang(khachHangRepo.findById(khachHangId).orElse(null));
                                pggKh.setDaSuDung(false);
                                pggKh.setNgayTao(new Date());
                                pggKh.setTrangThai(true);
                                pggKh.setNguoiTao("Admin");

                                phieuGiamGiaKhachHangRepo.save(pggKh);
                            }
                        }
                    }
                }

                redirectAttributes.addFlashAttribute("success", "Cập nhật phiếu giảm giá thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/phieu-giam-gia/index";
    }

    @GetMapping("/delete/{id}")
    public String xoa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (phieuGiamGiaRepo.existsById(id)) {
                // Kiểm tra xem phiếu đã được sử dụng chưa
                Long usedCount = phieuGiamGiaKhachHangRepo.countUsedByPhieuGiamGiaId(id);
                if (usedCount > 0) {
                    redirectAttributes.addFlashAttribute("error",
                            "Không thể xóa phiếu giảm giá đã được sử dụng!");
                    return "redirect:/admin/phieu-giam-gia/index";
                }

                // Xóa các quan hệ trong bảng trung gian trước
                List<PhieuGiamGiaKhachHang> relations =
                        phieuGiamGiaKhachHangRepo.findByPhieuGiamGiaId(id);
                phieuGiamGiaKhachHangRepo.deleteAll(relations);

                // Xóa phiếu giảm giá
                phieuGiamGiaRepo.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Xóa phiếu giảm giá thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/phieu-giam-gia/index";
    }

    /**
     * Phương thức validate dữ liệu phiếu giảm giá
     */
    private boolean validatePhieuGiamGia(PhieuGiamGia phieuGiamGia, RedirectAttributes redirectAttributes) {
        Date currentDate = new Date();

        // Kiểm tra ngày bắt đầu và kết thúc
        if (phieuGiamGia.getNgayBatDau() != null && phieuGiamGia.getNgayKetThuc() != null) {
            if (phieuGiamGia.getNgayBatDau().after(phieuGiamGia.getNgayKetThuc())) {
                redirectAttributes.addFlashAttribute("error",
                        "Ngày bắt đầu không thể sau ngày kết thúc!");
                return false;
            }
            // Nếu ngày bắt đầu trong tương lai, đặt trạng thái thành false
            if (phieuGiamGia.getNgayBatDau().after(currentDate)) {
                phieuGiamGia.setTrangThai(false);
                System.out.println("Set trangThai to false for voucher " + phieuGiamGia.getMa() + " due to future start date");
            }
        }

        // Kiểm tra giá trị giảm nếu là phần trăm
        if (phieuGiamGia.getLoaiGiamGia() && phieuGiamGia.getGiaTriGiam() != null) {
            if (phieuGiamGia.getGiaTriGiam().doubleValue() > 100 ||
                    phieuGiamGia.getGiaTriGiam().doubleValue() <= 0) {
                redirectAttributes.addFlashAttribute("error",
                        "Giá trị giảm theo phần trăm phải từ 1 đến 100!");
                return false;
            }
        }

        return true;
    }

    /**
     * Phương thức tạo mã tự tăng cho phiếu giảm giá
     */
    private String generateNextCode() {
        List<PhieuGiamGia> phieuGiamGias = phieuGiamGiaRepo.findAll(Sort.by(Sort.Direction.DESC, "ma"));
        int nextNumber = 1;

        if (!phieuGiamGias.isEmpty()) {
            String maxCode = phieuGiamGias.get(0).getMa();
            try {
                if (maxCode != null && maxCode.length() > 3 && maxCode.startsWith("PGG")) {
                    String numberPart = maxCode.substring(3);
                    nextNumber = Integer.parseInt(numberPart) + 1;
                }
            } catch (NumberFormatException e) {
                nextNumber = 1;
            }
        }

        return String.format("PGG%03d", nextNumber);
    }
}
