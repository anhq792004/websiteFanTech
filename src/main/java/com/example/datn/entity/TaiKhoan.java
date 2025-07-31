package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "tai_khoan")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaiKhoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "id_chuc_vu")
    ChucVu chucVu;

    @Column(name = "ma")
    String ma;

    @Column(name = "email")
    String email;

    @Column(name = "mat_khau")
    String matKhau;

    @Column(name = "reset_token")
    String resetToken;

    @Column(name = "ngay_tao")
    Date ngayTao;

    @Column(name = "trang_thai")
    Boolean trangThai;

    private String verificationCode;
}
