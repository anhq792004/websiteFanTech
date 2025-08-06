$(document).ready(function () {
    $('#addDCForm').on('submit', function (event) {
        event.preventDefault(); // Ngăn form submit mặc định

        $('#addDCForm button[type="submit"]').prop('disabled', true);

        // Thu thập dữ liệu từ form
        const formData = {
            idKH: $('input[name="idKH"]').val(),
            tinh: $('#city2').val(),
            huyen: $('#district2').val(),
            xa: $('#ward2').val(),
            soNhaNgoDuong: $('#diaChiCuThe').val()
        };

        // Gửi Ajax request
        $.ajax({
            url: '/profile/add-dia-chi',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                Swal.fire({
                    toast: false,
                    icon: 'success',
                    position: 'center',
                    title: response,
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true
                }).then(() => {
                    location.reload();
                });
            },
            error: function (xhr) {
                Swal.fire({
                    toast: false,
                    icon: 'error',
                    position: 'center',
                    title: xhr.responseText,
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true
                });
                $('#addDCForm button[type="submit"]').prop('disabled', false);
            }
        });
    });
});

$('#formUpdateKH').submit(function (e) {
    e.preventDefault();
    $('#formUpdateKH button[type="submit"]').prop('disabled', true);

    // Tạo FormData để gửi cả text và file
    const formData = new FormData();
    formData.append('idKH', $('#idKH').val());
    formData.append('ten', $('#name').val());
    formData.append('gioiTinh', $('#gioiTinh').val());
    formData.append('soDienThoai', $('#soDienThoai').val());
    formData.append('ngaySinh', $('#ngaySinh').val());
    
    // Thêm file ảnh nếu có
    const hinhAnhFile = $('#hinhAnh')[0].files[0];
    if (hinhAnhFile) {
        formData.append('hinhAnh', hinhAnhFile);
    }

    $.ajax({
        url: '/profile/update',
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            Swal.fire({
                toast: false,
                icon: 'success',
                title: response,
                position: 'center',
                showConfirmButton: false,
                timer: 1500,
                timerProgressBar: true
            }).then(() => {
                location.reload();
            });
        },
        error: function (xhr) {
            Swal.fire({
                toast: false,
                icon: 'error',
                title: xhr.responseText,
                position: 'center',
                showConfirmButton: false,
                timer: 1000,
                timerProgressBar: true
            });
            $('#formUpdateKH button[type="submit"]').prop('disabled', false);
        }
    });
});

// Preview ảnh khi chọn file trong form update
$('#hinhAnh').on('change', function() {
    const file = this.files[0];
    if (file) {
        // Kiểm tra loại file
        if (!file.type.startsWith('image/')) {
            Swal.fire({
                toast: false,
                icon: 'error',
                title: 'Vui lòng chọn file hình ảnh hợp lệ',
                position: 'center',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
            this.value = '';
            $('#imagePreview').hide();
            return;
        }
        
        // Kiểm tra kích thước file (max 5MB)
        if (file.size > 5 * 1024 * 1024) {
            Swal.fire({
                toast: false,
                icon: 'error',
                title: 'Kích thước file không được vượt quá 5MB',
                position: 'center',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
            this.value = '';
            $('#imagePreview').hide();
            return;
        }
        
        // Hiển thị preview
        const reader = new FileReader();
        reader.onload = function(e) {
            $('#previewImg').attr('src', e.target.result);
            $('#imagePreview').show();
        };
        reader.readAsDataURL(file);
    } else {
        $('#imagePreview').hide();
    }
});

// Click vào ảnh avatar trong sidebar để chọn ảnh
$('#sidebarAvatar').on('click', function() {
    // Kiểm tra xem có phải ảnh mặc định không
    const currentSrc = $(this).attr('src');
    const isDefaultAvatar = currentSrc.includes('avatar.jpg');
    
    if (isDefaultAvatar) {
        // Hiển thị thông báo đặc biệt cho lần đầu thêm ảnh
        Swal.fire({
            title: 'Thêm ảnh đại diện',
            text: 'Bạn chưa có ảnh đại diện. Hãy chọn một ảnh để tạo ảnh đại diện cho tài khoản!',
            icon: 'info',
            showCancelButton: true,
            confirmButtonText: 'Chọn ảnh',
            cancelButtonText: 'Hủy'
        }).then((result) => {
            if (result.isConfirmed) {
                openFileSelector();
            }
        });
    } else {
        // Thông báo thay đổi ảnh
        Swal.fire({
            title: 'Thay đổi ảnh đại diện',
            text: 'Bạn muốn thay đổi ảnh đại diện hiện tại?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Thay đổi',
            cancelButtonText: 'Hủy'
        }).then((result) => {
            if (result.isConfirmed) {
                openFileSelector();
            }
        });
    }
});

// Hàm mở file selector
function openFileSelector() {
    // Tạo input file ẩn
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = 'image/*';
    fileInput.style.display = 'none';
    
    fileInput.onchange = function(e) {
        const file = e.target.files[0];
        if (file) {
            // Kiểm tra loại file
            if (!file.type.startsWith('image/')) {
                Swal.fire({
                    toast: false,
                    icon: 'error',
                    title: 'Vui lòng chọn file hình ảnh hợp lệ',
                    position: 'center',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
                return;
            }
            
            // Kiểm tra kích thước file (max 10MB)
            if (file.size > 10 * 1024 * 1024) {
                Swal.fire({
                    toast: false,
                    icon: 'error',
                    title: 'Kích thước file không được vượt quá 10MB',
                    position: 'center',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
                return;
            }
            
            // Hiển thị preview trong form
            const reader = new FileReader();
            reader.onload = function(e) {
                $('#previewImg').attr('src', e.target.result);
                $('#imagePreview').show();
                
                // Tự động scroll đến form
                $('html, body').animate({
                    scrollTop: $('#formUpdateKH').offset().top - 100
                }, 500);
                
                // Hiển thị thông báo
                Swal.fire({
                    toast: false,
                    icon: 'success',
                    title: 'Đã chọn ảnh thành công! Vui lòng nhấn "Lưu" để cập nhật.',
                    position: 'center',
                    showConfirmButton: true,
                    confirmButtonText: 'OK'
                });
            };
            reader.readAsDataURL(file);
            
            // Gán file vào input file trong form
            $('#hinhAnh')[0].files = e.target.files;
        }
        
        // Xóa input file ẩn
        document.body.removeChild(fileInput);
    };
    
    // Thêm input file vào body và trigger click
    document.body.appendChild(fileInput);
    fileInput.click();
}


