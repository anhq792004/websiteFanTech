package com.example.datn.entity.NhanVien;

import com.example.datn.entity.ChucVu;
import com.example.datn.entity.DiaChi;
import com.example.datn.entity.TaiKhoan;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "nhan_vien")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "id_chuc_vu")
    ChucVu chucVu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tai_khoan")
    TaiKhoan taiKhoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dia_chi")
    DiaChi diaChi;

    @Column(name = "ma")
    String ma;

    @Column(name = "ten")
    String ten;

    @Column(name = "gioi_tinh")
    String gioiTinh;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_sinh")
    Date ngaySinh;

    @Column(name = "so_dien_thoai")
    String soDienThoai;

    @Column(name = "can_cuoc_cong_dan")
    String canCuocCongDan;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_tao")
    LocalDateTime ngayTao;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_sua")
    LocalDateTime ngaySua;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "hinh_anh")
    String anh;
}
