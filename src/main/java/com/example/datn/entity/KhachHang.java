package com.example.datn.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "khach_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tai_khoan")
    TaiKhoan taiKhoan;

    @Column(name = "ma")
    String ma;

    @Column(name = "ten")
    String ten;

    @Column(name = "gioi_tinh")
    String gioiTinh;

    @Column(name = "so_dien_thoai")
    String soDienThoai;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_sinh")
    @Temporal(TemporalType.TIMESTAMP)
    Date ngaySinh;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_tao")
    @Temporal(TemporalType.TIMESTAMP)
    Date ngayTao;

    @Column(name = "trang_thai")
    Boolean trangThai;

    @Column(name = "hinh_anh")
    String anh;
    
    @Column(name = "dia_chi_mac_dinh_id")
    Long diaChiMacDinhId;
}
