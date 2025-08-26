package com.example.datn.entity.SanPham;

import com.example.datn.entity.ThuocTinh.KieuQuat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "san_pham")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "ma", unique = true)
    String ma;

    @Column(name = "ten")
    @Size(max = 150, message = "Tên sản phẩm tối đa 150 ký tự")
    String ten;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    String moTa;

    @Column(name = "ngay_tao")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime ngayTao;

    @Column(name = "trang_thai")
    Boolean trangThai;

    @ManyToOne
    @JoinColumn(name = "id_kieu_quat")
    KieuQuat kieuQuat;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<SanPhamChiTiet> sanPhamChiTiet;
}
