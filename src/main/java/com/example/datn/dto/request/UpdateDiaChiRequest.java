package com.example.datn.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateDiaChiRequest {
     Long id;                 // ID của địa chỉ cần cập nhật
     Long khachHangId;        // ID của khách hàng (để redirect về sau khi cập nhật)
     String tinh;             // ID Tỉnh/Thành phố
     String huyen;            // ID Quận/Huyện
     String xa;               // ID Xã/Phường
     String soNhaNgoDuong;
}
