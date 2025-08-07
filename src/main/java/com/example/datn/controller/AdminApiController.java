package com.example.datn.controller;

import com.example.datn.entity.ThuocTinh.*;
import com.example.datn.service.ThuocTinhService.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api")
@CrossOrigin(origins = "*")
public class AdminApiController {

    private final MauSacService mauSacService;
    private final CongSuatService congSuatService;
    private final HangService hangService;
    private final KieuQuatService kieuQuatService;
    private final NutBamService nutBamService;
    
    // Endpoint kiểm tra hoạt động
    @GetMapping("/check")
    public ResponseEntity<Map<String, String>> check() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "API đang hoạt động");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mau-sac")
    public ResponseEntity<List<MauSac>> getAllMauSac() {
        return ResponseEntity.ok(mauSacService.findAllMauSac());
    }

    @GetMapping("/cong-suat")
    public ResponseEntity<List<CongSuat>> getAllCongSuat() {
        return ResponseEntity.ok(congSuatService.findAllCongSuat());
    }

    @GetMapping("/hang")
    public ResponseEntity<List<Hang>> getAllHang() {
        return ResponseEntity.ok(hangService.findAllHang());
    }
    
    @GetMapping("/kieu-quat")
    public ResponseEntity<List<KieuQuat>> getAllKieuQuat() {
        return ResponseEntity.ok(kieuQuatService.findAllKieuQuat());
    }
    
    @GetMapping("/nut-bam")
    public ResponseEntity<List<NutBam>> getAllNutBam() {
        return ResponseEntity.ok(nutBamService.findAll());
    }
} 