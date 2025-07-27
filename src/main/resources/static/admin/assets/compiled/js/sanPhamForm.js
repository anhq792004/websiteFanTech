$(document).ready(function() {
    // Khởi tạo các biến global
    
    // Hàm hiển thị thông báo
    function showToast(icon, message) {
        Swal.fire({
            toast: true,
            icon: icon,
            title: message,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true
        });
    }

    // Hàm load danh sách kiểu quạt
    function loadFanTypes() {
        return $.ajax({
            url: "/admin/san-pham/api/kieu-quat",
            type: "GET",
            dataType: "json"
        });
    }
    
    // Load các thuộc tính sản phẩm
    function loadProductAttributes() {
        console.log("Bắt đầu tải thuộc tính sản phẩm");
        
        // Load kiểu quạt
        $.ajax({
            url: "/admin/san-pham/api/kieu-quat",
            type: "GET",
            dataType: "json",
            success: function(data) {
                console.log("Dữ liệu kiểu quạt:", data);
                addOptionToSelect("#fanType", data, "-- Chọn kiểu quạt --");
                addOptionToSelect("#editFanType", data, "-- Chọn kiểu quạt --");
            },
            error: function(error) {
                console.error("Lỗi khi tải danh sách kiểu quạt:", error);
            }
        });
        
        // Load hãng
        $.ajax({
            url: "/admin/api/hang",
            type: "GET",
            dataType: "json",
            success: function(data) {
                console.log("Dữ liệu hãng:", data);
                addOptionToSelect("#productBrand", data, "-- Chọn hãng --");
            },
            error: function(error) {
                console.error("Lỗi khi tải danh sách hãng:", error);
            }
        });

        // Load công suất
        $.ajax({
            url: "/admin/api/cong-suat",
            type: "GET",
            dataType: "json",
            success: function(data) {
                console.log("Dữ liệu công suất:", data);
                addOptionToSelect("#productPower", data, "-- Chọn công suất --");
            },
            error: function(error) {
                console.error("Lỗi khi tải danh sách công suất:", error);
            }
        });

        // Load các thuộc tính khác nếu cần
        loadMoreAttributes();
    }
    
    // Load thêm các thuộc tính khác
    function loadMoreAttributes() {
        // Load chế độ gió
        $.ajax({
            url: "/admin/api/che-do-gio",
            type: "GET",
            dataType: "json",
            success: function(data) {
                addOptionToSelect("#productWindMode", data, "-- Chọn chế độ gió --");
            },
            error: function(error) {
                console.error("Lỗi khi tải danh sách chế độ gió:", error);
            }
        });

        // Load màu sắc
        $.ajax({
            url: "/admin/api/mau-sac",
            type: "GET",
            dataType: "json",
            success: function(data) {
                addOptionToSelect("#productColor", data, "-- Chọn màu sắc --");
            },
            error: function(error) {
                console.error("Lỗi khi tải danh sách màu sắc:", error);
            }
        });

        // Load chất liệu cánh
        $.ajax({
            url: "/admin/api/chat-lieu-canh",
            type: "GET", 
            dataType: "json",
            success: function(data) {
                addOptionToSelect("#productBladeMaterial", data, "-- Chọn chất liệu cánh --");
            },
            error: function(error) {
                console.error("Lỗi khi tải danh sách chất liệu cánh:", error);
            }
        });

        // Load chất liệu khung
        $.ajax({
            url: "/admin/api/chat-lieu-khung",
            type: "GET",
            dataType: "json",
            success: function(data) {
                addOptionToSelect("#productFrameMaterial", data, "-- Chọn chất liệu khung --");
            },
            error: function(error) {
                console.error("Lỗi khi tải danh sách chất liệu khung:", error);
            }
        });

        // Load đường kính cánh
        $.ajax({
            url: "/admin/api/duong-kinh-canh",
            type: "GET",
            dataType: "json",
            success: function(data) {
                addOptionToSelect("#productBladeDiameter", data, "-- Chọn đường kính cánh --");
            },
            error: function(error) {
                console.error("Lỗi khi tải danh sách đường kính cánh:", error);
            }
        });
    }

    // Thêm option vào select
    function addOptionToSelect(selector, data, defaultLabel) {
        console.log("Đang thêm option vào selector:", selector);
        const selectElement = $(selector);
        if (selectElement.length === 0) {
            console.error("Không tìm thấy element với selector:", selector);
            return;
        }
        
        let options = `<option value="">${defaultLabel}</option>`;
        data.forEach(function(item) {
            options += `<option value="${item.id}">${item.ten}</option>`;
        });
        selectElement.html(options);
    }

    // Hàm validate form sản phẩm
    function validateProductForm(formId) {
        const form = document.getElementById(formId);
        if (!form) return false;
        
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return false;
        }
        
        const productCodeField = form.querySelector('[name="ma"]');
        if (productCodeField && !productCodeField.readOnly) {
            const productCode = productCodeField.value;
            if (productCode && !productCode.match(/^[A-Za-z0-9]+$/)) {
            showToast("error", "Mã sản phẩm chỉ được chứa chữ cái và số");
            return false;
            }
        }
        
        // Kiểm tra kiểu quạt
        const fanType = form.querySelector('[name="kieuQuat.id"]').value;
        if (!fanType) {
            showToast("error", "Vui lòng chọn kiểu quạt");
            return false;
        }
        
        return true;
    }

    // Load các thuộc tính khi trang được tải
    loadProductAttributes();
    
    // Thiết lập sự kiện cho form thêm mới
    if (document.getElementById('addProductForm')) {
        document.getElementById('addProductForm').addEventListener('submit', function(event) {
            if (!validateProductForm('addProductForm')) {
                event.preventDefault();
                event.stopPropagation();
            }
        });
    }
    
    // Thiết lập sự kiện cho modal form thêm mới
    if (document.getElementById('addProductModalForm')) {
        document.getElementById('addProductModalForm').addEventListener('submit', function(event) {
            if (!validateProductForm('addProductModalForm')) {
                event.preventDefault();
                event.stopPropagation();
            }
        });
    }
    
    // Thiết lập sự kiện cho form chỉnh sửa
    if (document.getElementById('editProductForm')) {
        document.getElementById('editProductForm').addEventListener('submit', function(event) {
            if (!validateProductForm('editProductForm')) {
                event.preventDefault();
                event.stopPropagation();
            }
        });
    }
}); 