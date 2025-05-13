$(document).ready(function () {
    // Load tất cả các thuộc tính sản phẩm khi trang được tải
    loadProductAttributes();

    // Xử lý khi nhấp vào nút Thêm biến thể
    $("#addProductVariantBtn").click(function () {
        // Reset form
        $("#addVariantForm")[0].reset();
        // Hiển thị modal
        $("#addVariantModal").modal("show");
    });

    // Lưu biến thể mới
    $("#saveVariantBtn").click(function () {
        // Tạo FormData object để xử lý file upload
        const formData = new FormData($("#addVariantForm")[0]);

        $.ajax({
            url: "/admin/san-pham-chi-tiet/them",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                $("#addVariantModal").modal("hide");
                showToast('success', 'Thêm biến thể sản phẩm thành công');
                setTimeout(function () {
                    location.reload();
                }, 1000);
            },
            error: function (error) {
                console.error("Lỗi khi thêm biến thể sản phẩm:", error);
                showToast('error', 'Lỗi khi thêm biến thể sản phẩm');
            }
        });
    });


    // Xử lý khi nhấp vào nút Chỉnh sửa biến thể
    $(document).on("click", ".edit-variant-btn", function () {
        const variantId = $(this).data("id");
        console.log("Variant ID:", variantId);

        // Tải thông tin biến thể
        $.ajax({
            url: `/admin/san-pham-chi-tiet/get/${variantId}`,
            type: "GET",
            success: function (data) {
                console.log("Data received:", data);
                // Điền thông tin vào form
                $("#editVariantId").val(data.id);
                $("#editSoLuong").val(data.soLuong);
                $("#editGia").val(data.gia);
                $("#editMoTa").val(data.moTa);

                // Điền thông tin vào các select
                $("#editMauSac").val(data.mauSac ? data.mauSac.id : '');
                $("#editChatLieuCanh").val(data.chatLieuCanh ? data.chatLieuCanh.id : '');
                $("#editChatLieuKhung").val(data.chatLieuKhung ? data.chatLieuKhung.id : '');
                $("#editDuongKinhCanh").val(data.duongKinhCanh ? data.duongKinhCanh.id : '');
                $("#editCongSuat").val(data.congSuat ? data.congSuat.id : '');
                $("#editHang").val(data.hang ? data.hang.id : '');
                $("#editCheDoGio").val(data.cheDoGio ? data.cheDoGio.id : '');

                // Hiển thị modal
                $("#editVariantModal").modal("show");
            },
            error: function (error) {
                console.error("Lỗi khi tải thông tin biến thể:", error);
                showToast('error', 'Lỗi khi tải thông tin biến thể');
            }
        });
    });

    // Cập nhật biến thể
    $("#updateVariantBtn").click(function () {
        // Tạo FormData object để xử lý file upload
        const formData = new FormData($("#editVariantForm")[0]);

        $.ajax({
            url: "/admin/san-pham-chi-tiet/sua",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                $("#editVariantModal").modal("hide");
                showToast('success', 'Cập nhật biến thể sản phẩm thành công');
                setTimeout(function () {
                    location.reload();
                }, 1000);
            },
            error: function (error) {
                console.error("Lỗi khi cập nhật biến thể sản phẩm:", error);
                showToast('error', 'Lỗi khi cập nhật biến thể sản phẩm');
            }
        });
    });

    // Xử lý khi nhấp vào nút Xem chi tiết biến thể
    $(document).on("click", ".variant-detail-btn", function () {
        const variantId = $(this).data("id");

        // Tải thông tin biến thể
        $.ajax({
            url: `/admin/san-pham-chi-tiet/get/${variantId}`,
            type: "GET",
            success: function (data) {
                // Điền thông tin vào modal
                $("#detailMauSac").text(data.mauSac ? data.mauSac.ten : 'N/A');
                $("#detailChatLieuCanh").text(data.chatLieuCanh ? data.chatLieuCanh.ten : 'N/A');
                $("#detailChatLieuKhung").text(data.chatLieuKhung ? data.chatLieuKhung.ten : 'N/A');
                $("#detailDuongKinhCanh").text(data.duongKinhCanh ? data.duongKinhCanh.ten : 'N/A');
                $("#detailCongSuat").text(data.congSuat ? data.congSuat.ten : 'N/A');
                $("#detailHang").text(data.hang ? data.hang.ten : 'N/A');
                $("#detailCheDoGio").text(data.cheDoGio ? data.cheDoGio.ten : 'N/A');
                $("#detailSoLuong").text(data.soLuong);
                $("#detailGia").text(formatCurrency(data.gia) + ' VNĐ');
                $("#detailMoTa").text(data.moTa ? data.moTa : 'Không có mô tả');

                // Hiển thị hình ảnh
                if (data.hinhAnh && data.hinhAnh.duongDan) {
                    $("#variantImage").attr("src", data.hinhAnh.duongDan);
                    $("#variantImageContainer").show();
                } else {
                    $("#variantImage").attr("src", "/admin/assets/images/no-image.png");
                    $("#variantImageContainer").show();
                }

                // Hiển thị modal
                $("#variantDetailModal").modal("show");
            },
            error: function (error) {
                console.error("Lỗi khi tải thông tin biến thể", error);
                showToast('error', 'Lỗi khi tải thông tin biến thể');
            }
        });
    });

    // Xử lý khi nhấp vào nút Xóa biến thể
    $(document).on("click", ".delete-variant-btn", function () {
        const variantId = $(this).data("id");

        if (confirm("Bạn có chắc chắn muốn xóa biến thể này không?")) {
            $.ajax({
                url: `/admin/san-pham-chi-tiet/xoa/${variantId}`,
                type: "POST",
                success: function (response) {
                    showToast('success', 'Xóa biến thể sản phẩm thành công');
                    setTimeout(function () {
                        location.reload();
                    }, 1000);
                },
                error: function (error) {
                    console.error("Lỗi khi xóa biến thể sản phẩm:", error);
                    showToast('error', 'Lỗi khi xóa biến thể sản phẩm');
                }
            });
        }
    });

    // Hàm định dạng số tiền thành định dạng tiền tệ
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN').format(amount);
    }

    // Hiển thị thông báo toast
    function showToast(type, message) {
        Toastify({
            text: message,
            duration: 3000,
            close: true,
            gravity: "top",
            position: "right",
            backgroundColor: type === 'success' ? "#4caf50" : "#f44336",
        }).showToast();
    }

    // Load tất cả các thuộc tính sản phẩm
    function loadProductAttributes() {
        // Load màu sắc
        $.ajax({
            url: "/admin/api/mau-sac",
            type: "GET",
            success: function (data) {
                populateSelectOptions("#mauSac, #editMauSac", data, "-- Chọn màu sắc --");
            },
            error: function (error) {
                console.error("Lỗi khi tải danh sách màu sắc:", error);
            }
        });

        // Load chất liệu cánh
        $.ajax({
            url: "/admin/api/chat-lieu-canh",
            type: "GET",
            success: function (data) {
                populateSelectOptions("#chatLieuCanh, #editChatLieuCanh", data, "-- Chọn chất liệu cánh --");
            },
            error: function (error) {
                console.error("Lỗi khi tải danh sách chất liệu cánh:", error);
            }
        });

        // Load chất liệu khung
        $.ajax({
            url: "/admin/api/chat-lieu-khung",
            type: "GET",
            success: function (data) {
                populateSelectOptions("#chatLieuKhung, #editChatLieuKhung", data, "-- Chọn chất liệu khung --");
            },
            error: function (error) {
                console.error("Lỗi khi tải danh sách chất liệu khung:", error);
            }
        });

        // Load đường kính cánh
        $.ajax({
            url: "/admin/api/duong-kinh-canh",
            type: "GET",
            success: function (data) {
                populateSelectOptions("#duongKinhCanh, #editDuongKinhCanh", data, "-- Chọn đường kính cánh --");
            },
            error: function (error) {
                console.error("Lỗi khi tải danh sách đường kính cánh:", error);
            }
        });

        // Load công suất
        $.ajax({
            url: "/admin/api/cong-suat",
            type: "GET",
            success: function (data) {
                populateSelectOptions("#congSuat, #editCongSuat", data, "-- Chọn công suất --");
            },
            error: function (error) {
                console.error("Lỗi khi tải danh sách công suất:", error);
            }
        });

        // Load hãng
        $.ajax({
            url: "/admin/api/hang",
            type: "GET",
            success: function (data) {
                populateSelectOptions("#hang, #editHang", data, "-- Chọn hãng --");
            },
            error: function (error) {
                console.error("Lỗi khi tải danh sách hãng:", error);
            }
        });

        // Load chế độ gió
        $.ajax({
            url: "/admin/api/che-do-gio",
            type: "GET",
            success: function (data) {
                populateSelectOptions("#cheDoGio, #editCheDoGio", data, "-- Chọn chế độ gió --");
            },
            error: function (error) {
                console.error("Lỗi khi tải danh sách chế độ gió:", error);
            }
        });
    }

    // Hàm điền dữ liệu vào các select
    function populateSelectOptions(selector, data, defaultLabel) {
        let options = `<option value="">${defaultLabel}</option>`;
        data.forEach(function (item) {
            options += `<option value="${item.id}">${item.ten}</option>`;
        });
        $(selector).html(options);
    }
});