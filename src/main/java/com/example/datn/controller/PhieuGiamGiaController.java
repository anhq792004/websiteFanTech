package com.example.datn.controller;

import com.example.datn.entity.NhanVien.NhanVien;
import com.example.datn.entity.PhieuGiamGia;
import com.example.datn.entity.PhieuGiamGiaKhachHang;
import com.example.datn.entity.TaiKhoan;
import com.example.datn.repository.KhachHangRepo.KhachHangRepo;
import com.example.datn.repository.NhanVienRepo;
import com.example.datn.repository.PhieuGiamGiaKhachHangRepo;
import com.example.datn.repository.PhieuGiamGiaRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private NhanVienRepo nhanVienRepo;

    @GetMapping("/index")
    public String hienThiDanhSach(Model model,
                                  @RequestParam(value = "search", required = false) String search,
                                  @RequestParam(value = "trangThai", required = false) Boolean trangThai,
                                  @RequestParam(value = "loaiGiamGia", required = false) Boolean loaiGiamGia,
                                  @RequestParam(value = "ngayBatDau", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
                                  @RequestParam(value = "ngayKetThuc", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKetThuc,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size) {

        // Sửa đổi dòng này để sắp xếp theo ngayTao giảm dần
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
        Page<PhieuGiamGia> pagePhieuGiamGia;
        Date currentDate = new Date();

        // Tìm kiếm và lọc dữ liệu
        if ((search != null && !search.trim().isEmpty()) ||
                trangThai != null ||
                loaiGiamGia != null ||
                ngayBatDau != null ||
                ngayKetThuc != null) {
            pagePhieuGiamGia = phieuGiamGiaRepo.findWithFilters(
                    search != null ? search.trim() : null,
                    trangThai,
                    loaiGiamGia,
                    ngayBatDau,
                    ngayKetThuc,
                    pageable
            );
        } else {
            pagePhieuGiamGia = phieuGiamGiaRepo.findAll(pageable);
        }

        // Cập nhật trạng thái cho các phiếu hết hạn
        for (PhieuGiamGia pgg : pagePhieuGiamGia.getContent()) {
            if (pgg.getNgayKetThuc() != null && pgg.getNgayKetThuc().before(currentDate) && pgg.isTrangThai()) {
                pgg.setTrangThai(false);
                phieuGiamGiaRepo.save(pgg);
            }
        }

        model.addAttribute("dsPhieuGiamGia", pagePhieuGiamGia.getContent());
        model.addAttribute("currentPage", pagePhieuGiamGia.getNumber());
        model.addAttribute("totalPages", pagePhieuGiamGia.getTotalPages());
        model.addAttribute("totalItems", pagePhieuGiamGia.getTotalElements());
        model.addAttribute("pageSize", size);

        // Thêm các giá trị tìm kiếm vào model để giữ trong form
        model.addAttribute("search", search);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("loaiGiamGia", loaiGiamGia);
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
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
        try {
            // Kiểm tra đăng nhập
            TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để thực hiện thao tác này!");
                return "redirect:/login";
            }

            // Lấy tên nhân viên từ NhanVien
            String nguoiTao = "Admin"; // Mặc định
            Optional<NhanVien> nhanVienOpt = nhanVienRepo.findByTaiKhoanId(currentUser.getId());
            if (nhanVienOpt.isPresent() && nhanVienOpt.get().getTen() != null) {
                nguoiTao = nhanVienOpt.get().getTen(); // Lấy tên nhân viên
            }

            if (phieuGiamGia.getMa() == null || phieuGiamGia.getMa().trim().isEmpty()) {
                phieuGiamGia.setMa(generateNextCode());
            }

            phieuGiamGia.setNgayTao(new Date());
            phieuGiamGia.setNguoiTao(nguoiTao); // Set nguoiTao

            if (!validatePhieuGiamGia(phieuGiamGia, redirectAttributes)) {
                return "redirect:/admin/phieu-giam-gia/create";
            }

            PhieuGiamGia savedPhieuGiamGia = phieuGiamGiaRepo.save(phieuGiamGia);

            if (!phieuGiamGia.getLoaiPhieu() && selectedKhachHang != null && !selectedKhachHang.isEmpty()) {
                for (Long khachHangId : selectedKhachHang) {
                    PhieuGiamGiaKhachHang pggKh = new PhieuGiamGiaKhachHang();
                    pggKh.setPhieuGiamGia(savedPhieuGiamGia);
                    pggKh.setKhachHang(khachHangRepo.findById(khachHangId).orElse(null));
                    pggKh.setNgayTao(new Date());
                    pggKh.setTrangThai(true);
                    pggKh.setNguoiTao(nguoiTao); // Set nguoiTao cho PhieuGiamGiaKhachHang
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
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
        try {
            // Kiểm tra đăng nhập
            TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để thực hiện thao tác này!");
                return "redirect:/login";
            }

            // Lấy tên nhân viên từ NhanVien
            String nguoiTao = "Admin"; // Mặc định
            Optional<NhanVien> nhanVienOpt = nhanVienRepo.findByTaiKhoanId(currentUser.getId());
            if (nhanVienOpt.isPresent() && nhanVienOpt.get().getTen() != null) {
                nguoiTao = nhanVienOpt.get().getTen(); // Lấy tên nhân viên
            }

            Optional<PhieuGiamGia> pggCu = phieuGiamGiaRepo.findById(phieuGiamGia.getId());
            if (pggCu.isPresent()) {
                phieuGiamGia.setNgayTao(pggCu.get().getNgayTao());
                phieuGiamGia.setNguoiTao(nguoiTao); // Set nguoiTao

                if (phieuGiamGia.getMa() == null || phieuGiamGia.getMa().trim().isEmpty()) {
                    phieuGiamGia.setMa(pggCu.get().getMa());
                }

                if (!validatePhieuGiamGia(phieuGiamGia, redirectAttributes)) {
                    return "redirect:/admin/phieu-giam-gia/edit/" + phieuGiamGia.getId();
                }

                PhieuGiamGia savedPhieuGiamGia = phieuGiamGiaRepo.save(phieuGiamGia);

                if (!phieuGiamGia.getLoaiPhieu()) {
                    List<PhieuGiamGiaKhachHang> oldRelations =
                            phieuGiamGiaKhachHangRepo.findByPhieuGiamGiaId(phieuGiamGia.getId());
                    phieuGiamGiaKhachHangRepo.deleteAll(oldRelations);

                    if (selectedKhachHang != null && !selectedKhachHang.isEmpty()) {
                        for (Long khachHangId : selectedKhachHang) {
                            if (!phieuGiamGiaKhachHangRepo.existsByPhieuGiamGiaIdAndKhachHangId(
                                    phieuGiamGia.getId(), khachHangId)) {
                                PhieuGiamGiaKhachHang pggKh = new PhieuGiamGiaKhachHang();
                                pggKh.setPhieuGiamGia(savedPhieuGiamGia);
                                pggKh.setKhachHang(khachHangRepo.findById(khachHangId).orElse(null));
                                pggKh.setNgayTao(new Date());
                                pggKh.setTrangThai(true);
                                pggKh.setNguoiTao(nguoiTao); // Set nguoiTao cho PhieuGiamGiaKhachHang
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
