package com.example.datn.controller.webController;
import com.example.datn.entity.SanPham.SanPham;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.repository.KhachHangRepo.KhachHangRepo;
import com.example.datn.repository.SanPhamRepo.SanPhamRepo;
import com.example.datn.repository.TaiKhoanRepo;
import com.example.datn.service.SanPhamSerivce.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/fanTech")
public class giaoDienController {
    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private SanPhamRepo sanPhamRepo;

    @GetMapping("/index")
    public String index(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        // Phân trang với 8 sản phẩm mỗi trang
        int size = 8;
        Page<SanPham> sanPhamPage = sanPhamService.findAllActiveProductsPaginated(page, size);

        // Thêm dữ liệu phân trang vào model
        model.addAttribute("sanPhamList", sanPhamPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamPage.getTotalPages());
        model.addAttribute("totalElements", sanPhamPage.getTotalElements());
        model.addAttribute("hasNext", sanPhamPage.hasNext());
        model.addAttribute("hasPrevious", sanPhamPage.hasPrevious());

        return "user/index";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long id, Model model) {
        try {
            Optional<SanPham> sanPhamOpt = sanPhamRepo.findById(id);
            if (sanPhamOpt.isPresent()) {
                SanPham sanPham = sanPhamOpt.get();
                model.addAttribute("sanPham", sanPham);
                if (sanPham.getSanPhamChiTiet() != null && !sanPham.getSanPhamChiTiet().isEmpty()) {
                    SanPhamChiTiet chiTietDauTien = sanPham.getSanPhamChiTiet().get(0);
                    System.out.println("Sản phẩm: " + sanPham.getTen() + ", Ảnh: " +
                            (chiTietDauTien.getHinhAnh() != null ? chiTietDauTien.getHinhAnh().getHinhAnh() : "Không có ảnh"));
                    model.addAttribute("chiTietDauTien", chiTietDauTien);
                }
                return "user/detail";
            } else {
                return "redirect:/fanTech/index";
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi load chi tiết sản phẩm: " + e.getMessage());
            return "redirect:/fanTech/index";
        }
    }

}
