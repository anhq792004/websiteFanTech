$(document).ready(function() {
    // Load dữ liệu cho các select
    loadSelectOptions();

    // Xử lý khi click nút thêm mới
    $('#addProductSpctBtn').click(function() {
        $('#addProductSpctForm')[0].reset();
        $('#addProductSpctModal').modal('show');
    });

    // Xử lý khi submit form thêm mới
    $('#saveProductSpctBtn').click(function() {
        const formData = new FormData($('#addProductSpctForm')[0]);
        
        $.ajax({
            url: '/admin/api/san-pham-chi-tiet/add',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                $('#addProductSpctModal').modal('hide');
                showToast('success', 'Thêm sản phẩm chi tiết thành công');
                setTimeout(function() {
                    location.reload();
                }, 1000);
            },
            error: function(xhr, status, error) {
                showToast('error', 'Có lỗi xảy ra khi thêm sản phẩm chi tiết');
            }
        });
    });

    // Xử lý khi click nút sửa
    $('.edit-variant-btn').click(function() {
        const id = $(this).data('id');
        
        $.ajax({
            url: '/admin/api/san-pham-chi-tiet/' + id,
            type: 'GET',
            success: function(data) {
                $('#editSpctId').val(data.id);
                $('#editMauSac').val(data.mauSac?.id);
                $('#editCongSuat').val(data.congSuat?.id);
                $('#editHang').val(data.hang?.id);
                $('#editNutBam').val(data.nutBam?.id);
                $('#editSoLuong').val(data.soLuong);
                $('#editGia').val(data.gia);
                $('#editCanNang').val(data.canNang);
                $('#editTrangThai').val(data.trangThai.toString());
                $('#editMoTa').val(data.moTa);

                if (data.hinhAnh) {
                    $('#editPreviewImage img').attr('src', data.hinhAnh.hinhAnh).show();
                } else {
                    $('#editPreviewImage img').hide();
                }

                $('#editProductSpctModal').modal('show');
            },
            error: function(xhr, status, error) {
                showToast('error', 'Có lỗi xảy ra khi tải thông tin sản phẩm chi tiết');
            }
        });
    });

    // Xử lý khi submit form sửa
    $('#saveEditSpct').click(function() {
        const formData = new FormData($('#editProductSpctForm')[0]);
        const id = $('#editSpctId').val();
        
        $.ajax({
            url: '/admin/api/san-pham-chi-tiet/update/' + id,
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                $('#editProductSpctModal').modal('hide');
                showToast('success', 'Cập nhật sản phẩm chi tiết thành công');
                setTimeout(function() {
                    location.reload();
                }, 1000);
            },
            error: function(xhr, status, error) {
                showToast('error', 'Có lỗi xảy ra khi cập nhật sản phẩm chi tiết');
            }
        });
    });

    // Xử lý khi click nút thay đổi trạng thái
    $('.toggle-status-variant-btn').click(function() {
        const id = $(this).data('id');
        const currentStatus = $(this).data('status');
        const actionText = currentStatus ? 'tắt hoạt động' : 'bật hoạt động';
        
        if (confirm(`Bạn có chắc chắn muốn ${actionText} sản phẩm chi tiết này không?`)) {
            $.ajax({
                url: `/api/san-pham-chi-tiet/${id}/toggle-status`,
                type: 'PUT',
                success: function(response) {
                    showToast('success', `Đã ${actionText} sản phẩm chi tiết thành công`);
                    setTimeout(function() {
                        location.reload();
                    }, 1000);
                },
                error: function(xhr, status, error) {
                    showToast('error', 'Có lỗi xảy ra khi thay đổi trạng thái sản phẩm chi tiết');
                }
            });
        }
    });

    // Xử lý preview hình ảnh khi chọn file
    $('#editHinhAnh').change(function() {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $('#editPreviewImage img').attr('src', e.target.result).show();
            }
            reader.readAsDataURL(file);
        }
    });
});

// Hàm hiển thị thông báo
function showToast(type, message) {
    Toastify({
        text: message,
        duration: 3000,
        gravity: "top",
        position: 'right',
        backgroundColor: type === 'success' ? '#4caf50' : '#f44336'
    }).showToast();
}

// Hàm load dữ liệu cho các select
function loadSelectOptions() {
    // Load màu sắc
    $.ajax({
        url: '/admin/api/mau-sac',
        type: 'GET',
        success: function(data) {
            let options = '<option value="">-- Chọn màu sắc --</option>';
            data.forEach(function(item) {
                options += `<option value="${item.id}">${item.ten}</option>`;
            });
            $('#mauSac, #editMauSac').html(options);
        }
    });

    // Load công suất
    $.ajax({
        url: '/admin/api/cong-suat',
        type: 'GET',
        success: function(data) {
            let options = '<option value="">-- Chọn công suất --</option>';
            data.forEach(function(item) {
                options += `<option value="${item.id}">${item.ten}</option>`;
            });
            $('#congSuat, #editCongSuat').html(options);
        }
    });

    // Load hãng
    $.ajax({
        url: '/admin/api/hang',
        type: 'GET',
        success: function(data) {
            let options = '<option value="">-- Chọn hãng --</option>';
            data.forEach(function(item) {
                options += `<option value="${item.id}">${item.ten}</option>`;
            });
            $('#hang, #editHang').html(options);
        }
    });

    // Load nút bấm
    $.ajax({
        url: '/admin/api/nut-bam',
        type: 'GET',
        success: function(data) {
            let options = '<option value="">-- Chọn nút bấm --</option>';
            data.forEach(function(item) {
                options += `<option value="${item.id}">${item.ten}</option>`;
            });
            $('#nutBam, #editNutBam').html(options);
        }
    });
} 