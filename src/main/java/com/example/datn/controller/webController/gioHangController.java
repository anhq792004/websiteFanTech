package com.example.datn.controller.webController;

import com.example.datn.dto.gioHangDTO;
import com.example.datn.service.gioHangService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class gioHangController {
    @Autowired
    private gioHangService gioHangService;

    // Hiển thị trang giỏ hàng
    @GetMapping
    public String showCart(HttpSession session, Model model) {
        List<gioHangDTO> cartItems = gioHangService.getCart(session);
        BigDecimal totalAmount = gioHangService.getTotalAmount(session);
        int itemCount = gioHangService.getCartItemCount(session);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("itemCount", itemCount);
        model.addAttribute("isEmpty", gioHangService.isEmpty(session));

        return "user/gioHang";
    }

    // Thêm sản phẩm vào giỏ hàng (AJAX)
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestParam Long sanPhamChiTietId,
            @RequestParam Integer soLuong,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        boolean success = gioHangService.addToCart(session, sanPhamChiTietId, soLuong);

        if (success) {
            Map<String, Object> cartInfo = gioHangService.getCartInfo(session);
            response.put("success", true);
            response.put("message", "Đã thêm sản phẩm vào giỏ hàng!");
            response.put("cartCount", cartInfo.get("itemCount"));
            response.put("totalAmount", cartInfo.get("totalAmount"));
            response.put("totalWeight", cartInfo.get("totalWeight"));
        } else {
            response.put("success", false);
            response.put("message", "Không thể thêm sản phẩm vào giỏ hàng!");
        }

        return ResponseEntity.ok(response);
    }

    // Cập nhật số lượng sản phẩm (AJAX)
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @RequestParam Long sanPhamChiTietId,
            @RequestParam Integer soLuong,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        boolean success = gioHangService.updateQuantity(session, sanPhamChiTietId, soLuong);

        if (success) {
            Map<String, Object> cartInfo = gioHangService.getCartInfo(session);
            response.put("success", true);
            response.put("message", "Đã cập nhật số lượng!");
            response.put("cartCount", cartInfo.get("itemCount"));
            response.put("totalAmount", cartInfo.get("totalAmount"));
            response.put("totalWeight", cartInfo.get("totalWeight"));
        } else {
            response.put("success", false);
            response.put("message", "Không thể cập nhật số lượng!");
        }

        return ResponseEntity.ok(response);
    }

    // Xóa sản phẩm khỏi giỏ hàng (AJAX)
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @RequestParam Long sanPhamChiTietId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        boolean success = gioHangService.removeFromCart(session, sanPhamChiTietId);

        if (success) {
            Map<String, Object> cartInfo = gioHangService.getCartInfo(session);
            response.put("success", true);
            response.put("message", "Đã xóa sản phẩm khỏi giỏ hàng!");
            response.put("cartCount", cartInfo.get("itemCount"));
            response.put("totalAmount", cartInfo.get("totalAmount"));
            response.put("totalWeight", cartInfo.get("totalWeight"));
            response.put("isEmpty", cartInfo.get("isEmpty"));
        } else {
            response.put("success", false);
            response.put("message", "Không thể xóa sản phẩm!");
        }

        return ResponseEntity.ok(response);
    }

    // Xóa toàn bộ giỏ hàng (AJAX)
    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            gioHangService.clearCart(session);
            response.put("success", true);
            response.put("message", "Đã xóa toàn bộ giỏ hàng!");
            response.put("cartCount", 0);
            response.put("totalAmount", BigDecimal.ZERO);
            response.put("isEmpty", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể xóa giỏ hàng!");
        }

        return ResponseEntity.ok(response);
    }

    // Lấy thông tin giỏ hàng (AJAX)
    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartInfo(HttpSession session) {
        // Sử dụng method getCartInfo() có sẵn để đảm bảo đồng bộ với checkout
        Map<String, Object> cartInfo = gioHangService.getCartInfo(session);
        return ResponseEntity.ok(cartInfo);
    }
}
