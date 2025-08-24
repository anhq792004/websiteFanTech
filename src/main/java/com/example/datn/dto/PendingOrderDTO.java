package com.example.datn.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class PendingOrderDTO {
    private String hoTen;
    private String soDienThoai;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiChiTiet;
    private String phuongThucThanhToan;
    private String ghiChu;
    private Long voucherId;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private List<gioHangDTO> cartItems;
    private String sessionId;

    // Getters và Setters
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getTinhThanh() { return tinhThanh; }
    public void setTinhThanh(String tinhThanh) { this.tinhThanh = tinhThanh; }
    public String getQuanHuyen() { return quanHuyen; }
    public void setQuanHuyen(String quanHuyen) { this.quanHuyen = quanHuyen; }
    public String getPhuongXa() { return phuongXa; }
    public void setPhuongXa(String phuongXa) { this.phuongXa = phuongXa; }
    public String getDiaChiChiTiet() { return diaChiChiTiet; }
    public void setDiaChiChiTiet(String diaChiChiTiet) { this.diaChiChiTiet = diaChiChiTiet; }
    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public Long getVoucherId() { return voucherId; }
    public void setVoucherId(Long voucherId) { this.voucherId = voucherId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    public List<gioHangDTO> getCartItems() { return cartItems; }
    public void setCartItems(List<gioHangDTO> cartItems) { this.cartItems = cartItems; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
