package com.example.datn.service;

import com.example.datn.dto.gioHangDTO;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.repository.SanPhamRepo.SanPhamChiTietRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class gioHangService {
    @Autowired
    private SanPhamChiTietRepo sanPhamChiTietRepo;

    private static final String CART_SESSION_KEY = "cart";

    // Lấy giỏ hàng từ session
    @SuppressWarnings("unchecked")
    public List<gioHangDTO> getCart(HttpSession session) {
        List<gioHangDTO> cart = (List<gioHangDTO>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    // Thêm sản phẩm vào giỏ hàng
    public boolean addToCart(HttpSession session, Long sanPhamChiTietId, Integer soLuong) {
        try {
            Optional<SanPhamChiTiet> sanPhamChiTietOpt = sanPhamChiTietRepo.findById(sanPhamChiTietId);
            if (!sanPhamChiTietOpt.isPresent()) {
                return false;
            }

            SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietOpt.get();

            // Kiểm tra số lượng tồn kho
            if (sanPhamChiTiet.getSoLuong() < soLuong) {
                return false;
            }

            List<gioHangDTO> cart = getCart(session);

            // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
            Optional<gioHangDTO> existingItem = cart.stream()
                    .filter(item -> item.getSanPhamChiTietId().equals(sanPhamChiTietId))
                    .findFirst();

            if (existingItem.isPresent()) {
                // Nếu đã có, tăng số lượng
                gioHangDTO item = existingItem.get();
                int newQuantity = item.getSoLuong() + soLuong;
                if (newQuantity <= sanPhamChiTiet.getSoLuong()) {
                    item.setSoLuong(newQuantity);
                } else {
                    return false; // Vượt quá số lượng tồn kho
                }
            } else {
                // Nếu chưa có, thêm mới
                gioHangDTO newItem = new gioHangDTO();
                newItem.setSanPhamChiTietId(sanPhamChiTietId);
                newItem.setTenSanPham(sanPhamChiTiet.getSanPham().getTen());
                newItem.setMauSac(sanPhamChiTiet.getMauSac() != null ? sanPhamChiTiet.getMauSac().getTen() : "");
                newItem.setCongSuat(sanPhamChiTiet.getCongSuat() != null ? sanPhamChiTiet.getCongSuat().getTen() : "");
                newItem.setHang(sanPhamChiTiet.getHang() != null ? sanPhamChiTiet.getHang().getTen() : "");
                newItem.setGia(sanPhamChiTiet.getGia());
                newItem.setSoLuong(soLuong);
                newItem.setSoLuongTon(sanPhamChiTiet.getSoLuong());
                // Sửa dòng này để gán hình ảnh
                newItem.setHinhAnh(sanPhamChiTiet.getHinhAnh() != null ? sanPhamChiTiet.getHinhAnh().getHinhAnh() : "/images/default-product.jpg");
                newItem.setCanNang(sanPhamChiTiet.getCanNang());

                cart.add(newItem);
            }

            session.setAttribute(CART_SESSION_KEY, cart);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Các phương thức khác giữ nguyên
    public boolean updateQuantity(HttpSession session, Long sanPhamChiTietId, Integer soLuong) {
        try {
            List<gioHangDTO> cart = getCart(session);
            Optional<gioHangDTO> itemOpt = cart.stream()
                    .filter(item -> item.getSanPhamChiTietId().equals(sanPhamChiTietId))
                    .findFirst();

            if (itemOpt.isPresent()) {
                gioHangDTO item = itemOpt.get();
                if (soLuong <= 0) {
                    cart.remove(item);
                } else if (soLuong <= item.getSoLuongTon()) {
                    item.setSoLuong(soLuong);
                } else {
                    return false; // Vượt quá số lượng tồn kho
                }
                session.setAttribute(CART_SESSION_KEY, cart);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removeFromCart(HttpSession session, Long sanPhamChiTietId) {
        try {
            List<gioHangDTO> cart = getCart(session);
            cart.removeIf(item -> item.getSanPhamChiTietId().equals(sanPhamChiTietId));
            session.setAttribute(CART_SESSION_KEY, cart);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    public BigDecimal getTotalAmount(HttpSession session) {
        List<gioHangDTO> cart = getCart(session);
        return cart.stream()
                .map(gioHangDTO::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCartItemCount(HttpSession session) {
        List<gioHangDTO> cart = getCart(session);
        return cart.stream()
                .mapToInt(gioHangDTO::getSoLuong)
                .sum();
    }

    public boolean isEmpty(HttpSession session) {
        List<gioHangDTO> cart = getCart(session);
        return cart.isEmpty();
    }

    public Map<String, Object> getCartInfo(HttpSession session) {
        List<gioHangDTO> cart = getCart(session);
        Map<String, Object> cartInfo = new HashMap<>();
        cartInfo.put("items", cart);
        cartInfo.put("isEmpty", cart.isEmpty());
        cartInfo.put("totalAmount", getTotalAmount(session));
        cartInfo.put("itemCount", getCartItemCount(session));
        cartInfo.put("totalWeight", getTotalWeight(session)); // Thêm dòng này
        return cartInfo;
    }

    public float getTotalWeight(HttpSession session) {
        List<gioHangDTO> cart = getCart(session);
        float totalWeight = 0f;
        for (gioHangDTO item : cart) {
            if (item.getCanNang() != null && item.getSoLuong() != null) {
                totalWeight += item.getCanNang() * item.getSoLuong();
            }
        }
        return totalWeight;
    }
}
