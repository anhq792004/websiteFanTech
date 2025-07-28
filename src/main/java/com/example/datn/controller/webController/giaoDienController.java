package com.example.datn.controller.webController;
import com.example.datn.entity.SanPham.SanPham;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.repository.SanPhamRepo.SanPhamRepo;
import com.example.datn.service.SanPhamSerivce.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String index(Model model) {
        // Lấy danh sách sản phẩm có trạng thái true (đang hoạt động)
        List<SanPham> sanPhamList = sanPhamService.findAllActiveProducts();

        // Thêm danh sách sản phẩm vào model để sử dụng trong template
        model.addAttribute("sanPhamList", sanPhamList);

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
