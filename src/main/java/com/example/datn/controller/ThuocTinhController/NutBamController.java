package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.ThuocTinh.NutBam;
import com.example.datn.service.ThuocTinhService.NutBamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NutBamController {
    private final NutBamService nutBamService;
    @GetMapping("/nut-bam/index")
    public String index(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NutBam> list = nutBamService.search(name, status, pageable);
        model.addAttribute("list", list);
        model.addAttribute("name", name);
        model.addAttribute("status", status);
        return "admin/thuoc_tinh/nut_bam";
    }



    @GetMapping("/nut-bam/list")
    public String list(Model model) {
        List<NutBam> nutBams = nutBamService.findAll();
        model.addAttribute("nutBams", nutBams);
        return "admin/thuoc_tinh/nut_bam/list";
    }

    @GetMapping("/api/nut-bam/list")
    @ResponseBody
    public ResponseEntity<List<NutBam>> getAll() {
        List<NutBam> nutBams = nutBamService.findAll();
        return ResponseEntity.ok(nutBams);
    }

    @GetMapping("/nut-bam/search")
    public String search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "trangThai", required = false) Boolean trangThai,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NutBam> nutBams = nutBamService.search(query, trangThai, pageable);
        model.addAttribute("nutBams", nutBams);
        return "admin/thuoc_tinh/nut_bam/list";
    }

    @PostMapping("/nut-bam/them")
    public String them(@ModelAttribute NutBam nutBam) {
        nutBamService.save(nutBam);
        return "redirect:/nut-bam/index";
    }

    @PostMapping("/nut-bam/sua")
    public String sua(@ModelAttribute NutBam nutBam) {
        nutBamService.save(nutBam);
        return "redirect:/nut-bam/index";
    }

    @PostMapping("/nut-bam/xoa/{id}")
    public String xoa(@PathVariable("id") Long id) {
        nutBamService.delete(id);
        return "redirect:/nut-bam/index";
    }

    @PostMapping("/nut-bam/thay-doi-trang-thai/{id}")
    public String thayDoiTrangThai(@PathVariable("id") Long id) {
        nutBamService.thayDoiTrangThai(id);
        return "redirect:/nut-bam/index";
    }
    
    @PostMapping("/nut-bam/add")
    @ResponseBody
    public ResponseEntity<?> add(@RequestParam("name") String name) {
        return nutBamService.add(name);
    }
    
    @PostMapping("/nut-bam/update")
    @ResponseBody
    public ResponseEntity<?> update(@RequestParam("id") Integer id, @RequestParam("name") String name) {
        return nutBamService.update(id, name);
    }
    
    @PostMapping("/nut-bam/change-status")
    @ResponseBody
    public ResponseEntity<?> changeStatus(@RequestParam("id") Integer id) {
        return nutBamService.changeStatus(id);
    }
}