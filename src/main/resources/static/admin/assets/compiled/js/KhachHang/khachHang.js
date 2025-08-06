$(document).ready(function () {
    $('#addKHForm').on('submit', function (event) {
        event.preventDefault();

        $('#submitBtn').prop('disabled', true);
        $('#btnIcon').addClass('d-none');
        $('#btnSpinner').removeClass('d-none');

        // Hiện loading SweetAlert2
        Swal.fire({
            title: 'Đang xử lý...',
            html: 'Vui lòng chờ trong giây lát',
            allowOutsideClick: false,
            allowEscapeKey: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        // Tạo FormData để gửi cả text và file
        const formData = new FormData();
        formData.append('ten', $('#name').val());
        formData.append('email', $('#email').val());
        formData.append('soDienThoai', $('#soDienThoai').val());
        formData.append('ngaySinh', $('#ngaySinh').val());
        formData.append('gioiTinh', $('#gioiTinh').val());
        formData.append('tinhThanhPho', $('#city').val());
        formData.append('quanHuyen', $('#district').val());
        formData.append('xaPhuong', $('#ward').val());
        formData.append('soNhaNgoDuong', $('#diaChiCuThe').val());
        
        // Thêm file ảnh nếu có
        const hinhAnhFile = $('#hinhAnh')[0].files[0];
        if (hinhAnhFile) {
            formData.append('hinhAnh', hinhAnhFile);
        }

        $.ajax({
            url: '/khach-hang/add',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                Swal.fire({
                    toast: true,
                    icon: 'success',
                    title: response,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 500,
                    timerProgressBar: true
                }).then(() => {
                    window.location.href = '/khach-hang/index';
                });
            },
            error: function (xhr) {
                Swal.close(); // Tắt loading
                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: xhr.responseText,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true,
                });

                $('#submitBtn').prop('disabled', false);
                $('#btnSpinner').addClass('d-none');
                $('#btnIcon').removeClass('d-none');
            }
        });
    });
    
    // Preview ảnh khi chọn file
    $('#hinhAnh').on('change', function() {
        const file = this.files[0];
        if (file) {
            // Kiểm tra loại file
            if (!file.type.startsWith('image/')) {
                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: 'Vui lòng chọn file hình ảnh hợp lệ',
                    position: 'top-end',
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
                    toast: true,
                    icon: 'error',
                    title: 'Kích thước file không được vượt quá 5MB',
                    position: 'top-end',
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
    
    // Click vào ảnh avatar khách hàng để chọn ảnh
    $('#customerAvatar').on('click', function() {
        // Kiểm tra xem có phải ảnh mặc định không
        const currentSrc = $(this).attr('src');
        const isDefaultAvatar = currentSrc.includes('avatar.jpg');
        
        if (isDefaultAvatar) {
            // Hiển thị thông báo đặc biệt cho lần đầu thêm ảnh
            Swal.fire({
                title: 'Thêm ảnh khách hàng',
                text: 'Hãy chọn một ảnh để tạo ảnh đại diện cho khách hàng!',
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
                title: 'Thay đổi ảnh khách hàng',
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
                        toast: true,
                        icon: 'error',
                        title: 'Vui lòng chọn file hình ảnh hợp lệ',
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 3000,
                        timerProgressBar: true
                    });
                    return;
                }
                
                // Kiểm tra kích thước file (max 10MB)
                if (file.size > 10 * 1024 * 1024) {
                    Swal.fire({
                        toast: true,
                        icon: 'error',
                        title: 'Kích thước file không được vượt quá 10MB',
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 3000,
                        timerProgressBar: true
                    });
                    return;
                }
                
                // Hiển thị preview
                const reader = new FileReader();
                reader.onload = function(e) {
                    // Thay thế ảnh trong ô chính
                    $('#customerAvatar').attr('src', e.target.result);
                    $('#imagePreview').hide(); // Ẩn preview riêng
                    
                    // Hiển thị thông báo
                    Swal.fire({
                        toast: true,
                        icon: 'success',
                        title: 'Đã chọn ảnh thành công!',
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 2000,
                        timerProgressBar: true
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
});




$('.changeStatusKhachHang').on('click', function () {
    const id = $(this).data('id');

    $.ajax({
        url: '/khach-hang/change-status',
        type: 'POST',
        data: {id: id},
        success: function (response) {
            if (response === "Cập nhật trạng thái thành công.") {
                Swal.fire({
                    toast: true,
                    icon: 'success',
                    title: response,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true
                }).then(() => {
                    location.reload();
                });
            } else {
                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: response,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
            }
        },
        error: function (xhr) {
            Swal.fire({
                toast: true,
                icon: 'error',
                title: "Có lỗi xảy ra khi cập nhật trạng thái khách hàng",
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
        }
    });
});
function checkEmailExists(email) {
    if (email && email.length > 0) {
        $.ajax({
            url: '/khach-hang/check-email', // Endpoint kiểm tra email
            type: 'POST',
            data: { email: email },
            success: function (response) {
                if (response.exists) {
                    $('#email').addClass('is-invalid');
                    $('#submitBtn').prop('disabled', true); // Vô hiệu hóa nút submit
                    Swal.fire({
                        toast: true,
                        icon: 'error',
                        title: 'Email này đã được sử dụng',
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 3000,
                        timerProgressBar: true
                    });
                } else {
                    $('#email').removeClass('is-invalid');
                    $('#email-error').hide();
                    $('#submitBtn').prop('disabled', false); // Kích hoạt lại nút submit
                }
            },
            error: function (xhr) {
                // Hiển thị lỗi AJAX bằng SweetAlert2
                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: 'Lỗi khi kiểm tra email',
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
            }
        });
    }
}

// Kiểm tra email khi người dùng rời khỏi input
$('#email').on('blur', function () {
    checkEmailExists($(this).val());
});


