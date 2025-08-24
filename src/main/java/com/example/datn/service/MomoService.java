package com.example.datn.service;

import com.example.datn.entity.HoaDon.HoaDon;
import com.example.datn.entity.MomoTransaction;

import java.util.Map;

public interface MomoService {
    /**
     * Tạo giao dịch Momo mới
     * @param hoaDon Hóa đơn cần thanh toán
     * @return Thông tin giao dịch Momo
     */
    MomoTransaction createTransaction(HoaDon hoaDon);
    MomoTransaction createTransaction(Map<String, Object> tempOrder, String tempOrderId);
    /**
     * Tạo giao dịch Momo mới với context cụ thể
     * @param hoaDon Hóa đơn cần thanh toán
     * @param context Context: "admin" hoặc "user"
     * @return Thông tin giao dịch Momo
     */
    MomoTransaction createTransaction(HoaDon hoaDon, String context);
    
    /**
     * Xác nhận giao dịch đã hoàn tất
     * @param hoaDonId ID của hóa đơn
     * @return true nếu xác nhận thành công, false nếu không
     */
    boolean confirmTransaction(Long hoaDonId);
    
    /**
     * Hủy giao dịch
     * @param hoaDonId ID của hóa đơn
     * @return true nếu hủy thành công, false nếu không
     */
    boolean cancelTransaction(Long hoaDonId);
    
    /**
     * Lấy thông tin giao dịch theo mã hóa đơn
     * @param hoaDonId ID của hóa đơn
     * @return Thông tin giao dịch Momo
     */
    MomoTransaction getTransactionByHoaDonId(Long hoaDonId);
    
    /**
     * Lấy thông tin giao dịch theo mã đơn hàng
     * @param orderId Mã đơn hàng từ Momo
     * @return Thông tin giao dịch Momo
     */
    MomoTransaction getTransactionByOrderId(String orderId);
} 