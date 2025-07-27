$(document).ready(function() {
    // Khởi tạo các biến global
    let productData = null;
    
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

    // Hàm điền dữ liệu vào form chỉnh sửa
    function fillEditForm(data) {
        $("#editProductCode").val(data.ma);
        $("#editProductName").val(data.ten);
        if (data.kieuQuat && data.kieuQuat.id) {
            $("#editFanType").val(data.kieuQuat.id);
        }
    }

    // Xử lý khi click nút chỉnh sửa
    $(document).on("click", ".edit-product-btn", function() {
        const productId = $(this).data("id");
        const productRow = $(this).closest("tr");
        
        // Lấy thông tin sản phẩm từ row hiện tại
        productData = {
            id: productId,
            ma: productRow.find("td:eq(1)").text().trim(),
            ten: productRow.find("td:eq(2)").text().trim(),
            kieuQuat: {
                id: productRow.find("td:eq(3)").data("kieu-quat-id")
            }
        };
        
        // Load kiểu quạt và điền form
        loadFanTypes()
            .then(function(fanTypes) {
                let options = '<option value="">-- Chọn kiểu quạt --</option>';
                fanTypes.forEach(function(type) {
                    options += `<option value="${type.id}">${type.ten}</option>`;
                });
                $("#editFanType").html(options);
                
                // Điền dữ liệu vào form
                fillEditForm(productData);
                $("#editProductModal").modal("show");
            })
            .catch(function(error) {
                console.error("Lỗi khi tải danh sách kiểu quạt:", error);
                showToast("error", "Không thể tải danh sách kiểu quạt");
            });
    });

    // Xử lý khi submit form chỉnh sửa
    $("#updateProductBtn").click(function() {
        const kieuQuatId = $("#editFanType").val();
        
        if (!kieuQuatId) {
            showToast("error", "Vui lòng chọn kiểu quạt");
            return;
        }

        const data = {
            id: productData.id,
            ma: $("#editProductCode").val(),
            ten: $("#editProductName").val(),
            kieuQuat: {
                id: kieuQuatId
            }
        };

        $.ajax({
            url: "/admin/san-pham/sua",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function(response) {
                $("#editProductModal").modal("hide");
                showToast("success", "Cập nhật sản phẩm thành công");
                setTimeout(function() {
                    location.reload();
                }, 1500);
            },
            error: function(error) {
                console.error("Lỗi khi cập nhật:", error);
                showToast("error", "Lỗi khi cập nhật sản phẩm");
            }
        });
    });

    // Xử lý đóng modal
    $("#editProductModal").on("hidden.bs.modal", function() {
        $("#editProductForm")[0].reset();
        productData = null;
    });

    // Load các thuộc tính sản phẩm
    function loadProductAttributes() {
        console.log("Bắt đầu tải thuộc tính sản phẩm");
        
        // Kiểm tra ID từ hình ảnh
        console.log("Kiểm tra form:", $("#addProductForm").length);
        console.log("ID kiểu quạt:", $("#fanType").length);
        console.log("ID hãng:", $("#productBrand").length);
        console.log("ID công suất:", $("#productPower").length);
        
        // Load kiểu quạt
        $.ajax({
            url: "/admin/san-pham/api/kieu-quat",
            type: "GET",
            dataType: "json",
            success: function(data) {
                console.log("Dữ liệu kiểu quạt:", data);
                addOptionToSelect("#fanType", data, "-- Chọn kiểu quạt --");
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

        // Load chế độ gió
        $.ajax({
            url: "/admin/api/che-do-gio",
            type: "GET",
            dataType: "json",
            success: function(data) {
                console.log("Dữ liệu chế độ gió:", data);
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
                console.log("Dữ liệu màu sắc:", data);
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
                console.log("Dữ liệu chất liệu cánh:", data);
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
                console.log("Dữ liệu chất liệu khung:", data);
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
                console.log("Dữ liệu đường kính cánh:", data);
                addOptionToSelect("#productBladeDiameter", data, "-- Chọn đường kính cánh --");
            },
            error: function(error) {
                console.error("Lỗi khi tải danh sách đường kính cánh:", error);
            }
        });
    }

    // Helper function để load options vào select
    function populateSelectOptions(selector, data, defaultLabel) {
        let options = `<option value="">${defaultLabel}</option>`;
        data.forEach(function(item) {
            options += `<option value="${item.id}">${item.ten}</option>`;
        });
        $(selector).html(options);
    }

    // Thêm hàm mới để thêm trực tiếp option vào select
    function addOptionToSelect(selector, data, defaultLabel) {
        console.log("Đang thêm option vào selector:", selector);
        const selectElement = $(selector);
        if (selectElement.length === 0) {
            console.error("Không tìm thấy element với selector:", selector);
            return;
        }
        
        // Xóa tất cả option hiện tại
        selectElement.empty();
        
        // Thêm option mặc định
        selectElement.append(`<option value="">${defaultLabel}</option>`);
        
        // Thêm các option từ dữ liệu
        if (Array.isArray(data)) {
            data.forEach(function(item) {
                selectElement.append(`<option value="${item.id}">${item.ten}</option>`);
            });
        } else {
            console.error("Dữ liệu không phải mảng:", data);
        }
    }

    // Load kiểu quạt và các thuộc tính khác khi trang được tải
    loadFanTypes();
    loadProductAttributes();

    
    // Save new product
    $("#saveProductBtn").click(function() {
        // Validate form trước khi submit
        const form = document.getElementById("addProductModalForm");
        if (form) {
            // Kiểm tra các trường bắt buộc
            const tenSanPham = form.querySelector('[name="ten"]').value;
            const kieuQuat = form.querySelector('[name="kieuQuat.id"]').value;
            
            if (!tenSanPham.trim()) {
                showToast('error', 'Vui lòng nhập tên sản phẩm');
                return;
            }
            
            if (!kieuQuat) {
                showToast('error', 'Vui lòng chọn kiểu quạt');
                return;
            }
        }
        
        const formData = getFormData($("#addProductModalForm"));

        // Bỏ mã sản phẩm để backend tự động tạo
        delete formData.ma;

        // Add current date
        formData.ngayTao = new Date().toISOString();

        $.ajax({
            url: "/admin/san-pham/them",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function(response) {
                $("#addProductModal").modal("hide");
                showToast('success', response);
                setTimeout(function() {
                    location.reload();
                }, 1000);
            },
            error: function(error) {
                console.error("Error saving product:", error);
                showToast('error', 'Lỗi khi thêm sản phẩm');
            }
        });
    });

    // Hàm làm sạch JSON string
    function cleanJsonString(str) {
        try {
            // Tìm object JSON đầu tiên có đủ các trường cần thiết
            const regex = /"id"\s*:\s*"?(\d+)"?,\s*"ma"\s*:\s*"([^"]+)",\s*"ten"\s*:\s*"([^"]+)"/;
            const match = str.match(regex);
            
            if (!match) {
                console.log("Không tìm thấy pattern JSON hợp lệ");
                return null;
            }
            
            // Tìm object JSON hoàn chỉnh chứa id, ma, ten đã match
            const startIdx = str.indexOf('{', str.indexOf(match[0]));
            if (startIdx === -1) return null;
            
            let count = 1;
            let endIdx = startIdx + 1;
            
            while (count > 0 && endIdx < str.length) {
                if (str[endIdx] === '{') count++;
                if (str[endIdx] === '}') count--;
                endIdx++;
            }
            
            if (count !== 0) return null;
            
            let jsonStr = str.substring(startIdx, endIdx);
            // Loại bỏ các ký tự không hợp lệ
            jsonStr = jsonStr.replace(/[\x00-\x1F\x7F-\x9F]/g, '');
            
            // Kiểm tra JSON có hợp lệ không
            JSON.parse(jsonStr);
            
            return jsonStr;
        } catch (e) {
            console.error("Lỗi khi làm sạch JSON:", e);
            return null;
        }
    }

    // Preview hình ảnh khi chọn file mới
    $("#editProductImage").change(function() {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $("#editProductImagePreview").attr("src", e.target.result).show();
            };
            reader.readAsDataURL(file);
        }
    });

    // Validate product form
    function validateProductForm() {
        const ma = $("#editProductCode").val();
        const ten = $("#editProductName").val();
        const kieuQuat = $("#editFanType").val();

        if (!ma || ma.trim() === '') {
            showToast('error', 'Vui lòng nhập mã sản phẩm');
            return false;
        }

        if (!ten || ten.trim() === '') {
            showToast('error', 'Vui lòng nhập tên sản phẩm');
            return false;
        }

        if (!kieuQuat) {
            showToast('error', 'Vui lòng chọn kiểu quạt');
            return false;
        }

        return true;
    }

    // Toggle product status (active/inactive)
    $(".toggle-status-btn").click(function() {
        const productId = $(this).data("id");
        const currentStatus = $(this).data("status");
        const newStatus = !currentStatus;
        const statusText = newStatus ? "Đang kinh doanh" : "ngừng kinh doanh";

        Swal.fire({
            title: `${newStatus ? 'Đang kinh Doanh' : 'Ngừng kinh doanh'} sản phẩm?`,
            text: `Bạn có chắc chắn muốn chuyển thành ${statusText} sản phẩm này?`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Hủy"
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/admin/san-pham/thay-doi-trang-thai/${productId}`,
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify({
                        id: productId,
                        trangThai: newStatus
                    }),
                    success: function(response) {
                        showToast('success', `Sản phẩm đã được cập nhật thành ${statusText} thành công`);
                        setTimeout(function() {
                            location.reload();
                        }, 1000);
                    },
                    error: function(error) {
                        console.error("Error toggling product status:", error);
                        showToast('error', `Lỗi khi ${statusText} sản phẩm`);
                    }
                });
            }
        });
    });

    // Helper function to serialize form data to JSON
    function getFormData($form) {
        let unindexed_array = $form.serializeArray();
        let indexed_array = {};

        $.map(unindexed_array, function(n, i) {
            // Handle nested objects like kieuQuat.id
            if (n['name'].includes('.')) {
                const parts = n['name'].split('.');
                if (!indexed_array[parts[0]]) {
                    indexed_array[parts[0]] = {};
                }
                indexed_array[parts[0]][parts[1]] = n['value'];
            } else {
                indexed_array[n['name']] = n['value'];
            }
        });

        // Convert boolean values
        if (indexed_array.trangThai) {
            indexed_array.trangThai = indexed_array.trangThai === "true";
        }

        return indexed_array;
    }

    // Initialize flatpickr for date inputs if needed
    if ($(".flatpickr").length > 0) {
        $(".flatpickr").flatpickr({
            dateFormat: "d-m-Y",
            locale: "vn"
        });
    }

    // Thêm event hander khi modal hiển thị
    $('#addProductModal').on('shown.bs.modal', function() {
        console.log("Modal đã mở, đang tải dữ liệu...");
        loadAllAttributes();
    });
    
    // Hàm tải dữ liệu vào các dropdown khi modal mở
    function loadAllAttributes() {
        // Load kiểu quạt
        console.log("Gọi API kiểu quạt...");
        $.ajax({
            url: "/admin/san-pham/api/kieu-quat",
            type: "GET",
            dataType: "json",
            success: function(data) {
                console.log("Nhận được dữ liệu kiểu quạt:", data);
                // Xóa các option cũ
                $('#fanType').empty();
                // Thêm option mặc định
                $('#fanType').append('<option value="">-- Chọn kiểu quạt --</option>');
                // Thêm các options từ dữ liệu
                data.forEach(function(item) {
                    $('#fanType').append(`<option value="${item.id}">${item.ten}</option>`);
                });
            },
            error: function(xhr, status, error) {
                console.error("Lỗi API kiểu quạt:", xhr.status, xhr.responseText);
            }
        });
        
        // Load hãng
        $.ajax({
            url: "/admin/api/hang",
            type: "GET",
            dataType: "json",
            success: function(data) {
                console.log("Nhận được dữ liệu hãng:", data);
                // Xóa các option cũ
                $('#productBrand').empty();
                // Thêm option mặc định
                $('#productBrand').append('<option value="">-- Chọn hãng --</option>');
                // Thêm các options từ dữ liệu
                data.forEach(function(item) {
                    $('#productBrand').append(`<option value="${item.id}">${item.ten}</option>`);
                });
            },
            error: function(xhr, status, error) {
                console.error("Lỗi API hãng:", xhr.status, xhr.responseText);
            }
        });
        
        // Load công suất
        $.ajax({
            url: "/admin/api/cong-suat",
            type: "GET",
            dataType: "json",
            success: function(data) {
                console.log("Nhận được dữ liệu công suất:", data);
                // Xóa các option cũ
                $('#productPower').empty();
                // Thêm option mặc định
                $('#productPower').append('<option value="">-- Chọn công suất --</option>');
                // Thêm các options từ dữ liệu
                data.forEach(function(item) {
                    $('#productPower').append(`<option value="${item.id}">${item.ten}</option>`);
                });
            },
            error: function(xhr, status, error) {
                console.error("Lỗi API công suất:", xhr.status, xhr.responseText);
            }
        });
        
        // Các thuộc tính khác tương tự
    }

    // Test API button
    $("#testApiBtn").click(function() {
        console.log("Kiểm tra API đang chạy...");
        
        // Test kết nối đến API kiểu quạt
        $.ajax({
            url: "/admin/san-pham/api/kieu-quat",
            type: "GET",
            dataType: "json",
            success: function(data) {
                console.log("API kiểu quạt OK:", data);
                alert("API kiểu quạt hoạt động OK: " + JSON.stringify(data).substring(0, 100) + "...");
            },
            error: function(xhr, status, error) {
                console.error("Lỗi API kiểu quạt:", xhr.status, xhr.responseText);
                alert("Lỗi API kiểu quạt: " + xhr.status + " - " + xhr.responseText);
            }
        });
        
        // Test kết nối đến API hãng
        $.ajax({
            url: "/admin/api/hang",
            type: "GET",
            dataType: "json",
            success: function(data) {
                console.log("API hãng OK:", data);
                alert("API hãng hoạt động OK: " + JSON.stringify(data).substring(0, 100) + "...");
            },
            error: function(xhr, status, error) {
                console.error("Lỗi API hãng:", xhr.status, xhr.responseText);
                alert("Lỗi API hãng: " + xhr.status + " - " + xhr.responseText);
            }
        });
    });
});

