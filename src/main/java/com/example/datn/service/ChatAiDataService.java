package com.example.datn.service;

import com.example.datn.entity.SanPham.SanPham;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.repository.SanPhamRepo.SanPhamChiTietRepository;
import com.example.datn.repository.SanPhamRepo.SanPhamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatAiDataService {

    private final SanPhamRepo sanPhamRepo;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    /**
     * Tạo ngữ cảnh (context) từ dữ liệu sản phẩm trong database
     * để AI có thể trả lời câu hỏi của khách hàng
     */
    public String getProductContext() {
        try {
            List<SanPhamChiTiet> allProducts = sanPhamChiTietRepository.findAll();
            
            if (allProducts.isEmpty()) {
                return "Hiện tại cửa hàng chưa có sản phẩm nào.";
            }

            StringBuilder context = new StringBuilder();
            context.append("THÔNG TIN SẢN PHẨM CỬA HÀNG:\n\n");

            for (SanPhamChiTiet spct : allProducts) {
                if (spct.getSanPham() != null && spct.getTrangThai() != null && spct.getTrangThai()) {
                    SanPham sp = spct.getSanPham();
                    
                    context.append("- Tên: ").append(sp.getTen() != null ? sp.getTen() : "Chưa có tên");
                    
                    if (spct.getMauSac() != null) {
                        context.append(", Màu: ").append(spct.getMauSac().getTen());
                    }
                    
                    if (spct.getCongSuat() != null) {
                        context.append(", Công suất: ").append(spct.getCongSuat().getTen());
                    }
                    
                    if (spct.getHang() != null) {
                        context.append(", Hãng: ").append(spct.getHang().getTen());
                    }
                    
                    if (spct.getGia() != null) {
                        context.append(", Giá: ").append(String.format("%,.0f", spct.getGia().doubleValue())).append(" VNĐ");
                    }
                    
                    if (spct.getSoLuong() != null) {
                        context.append(", Số lượng tồn: ").append(spct.getSoLuong());
                        if (spct.getSoLuong() > 0) {
                            context.append(" (Còn hàng)");
                        } else {
                            context.append(" (Hết hàng)");
                        }
                    }
                    
                    if (sp.getKieuQuat() != null) {
                        context.append(", Loại: ").append(sp.getKieuQuat().getTen());
                    }
                    
                    if (sp.getMoTa() != null && !sp.getMoTa().trim().isEmpty()) {
                        context.append(", Mô tả: ").append(sp.getMoTa().trim());
                    }
                    
                    context.append("\n");
                }
            }
            
            context.append("\nLưu ý: Chỉ trả lời dựa trên thông tin sản phẩm được cung cấp ở trên. ");
            context.append("Nếu khách hỏi về sản phẩm không có trong danh sách, hãy lịch sự thông báo rằng cửa hàng hiện không có sản phẩm đó.");
            
            return context.toString();
            
        } catch (Exception e) {
            return "Xin lỗi, hiện tại không thể truy cập thông tin sản phẩm. Vui lòng thử lại sau.";
        }
    }

    /**
     * Tạo prompt đầy đủ với ngữ cảnh sản phẩm cho AI
     */
    public String createContextualPrompt(String userQuestion) {
        String productContext = getProductContext();
        
        return String.format(
            "%s\n\n" +
            "Bạn là nhân viên tư vấn bán hàng của cửa hàng quạt điện. " +
            "Hãy trả lời câu hỏi của khách hàng một cách thân thiện, chính xác và hữu ích. " +
            "Sử dụng thông tin sản phẩm ở trên để đưa ra lời khuyên phù hợp.\n\n" +
            "Câu hỏi của khách hàng: %s\n\n" +
            "Trả lời:",
            productContext,
            userQuestion
        );
    }
}
