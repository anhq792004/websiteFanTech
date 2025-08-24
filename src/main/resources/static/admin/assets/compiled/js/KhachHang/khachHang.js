$(document).ready(function () {
    // Hàm kiểm tra định dạng số điện thoại (10 chữ số)
    function isValidPhoneNumber(phone) {
        const phoneRegex = /^[0-9]{10,12}$/;
        return phoneRegex.test(phone);
    }

    // Hàm kiểm tra định dạng ngày sinh (dd/mm/yyyy) và không trong tương lai
    function isValidDate(dateStr) {
        const dateRegex = /^(\d{2})\/(\d{2})\/(\d{4})$/;
        if (!dateRegex.test(dateStr)) return false;

        const [day, month, year] = dateStr.split('/').map(Number);
        const date = new Date(year, month - 1, day);
        const today = new Date();
        today.setHours(0, 0, 0, 0); // Đặt giờ về 0 để so sánh ngày
        return (
            date.getDate() === day &&
            date.getMonth() === month - 1 &&
            date.getFullYear() === year &&
            date <= today
        );
    }

    // Hàm hiển thị thông báo SweetAlert2 toast
    function showToast(icon, title) {
        Swal.fire({
            toast: true,
            icon: icon,
            title: title,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true
        });
    }

    $('#addKHForm').on('submit', function (event) {
        event.preventDefault();

        // Lấy giá trị các trường
        const ten = $('#name').val().trim();
        const email = $('#email').val().trim();
        const soDienThoai = $('#soDienThoai').val().trim();
        const ngaySinh = $('#ngaySinh').val().trim();
        const gioiTinh = $('#gioiTinh').val().trim();
        const tinhThanhPho = $('#city').val().trim();
        const quanHuyen = $('#district').val().trim();
        const xaPhuong = $('#ward').val().trim();
        const diaChiCuThe = $('#diaChiCuThe').val().trim();
        const hinhAnhFile = $('#hinhAnh')[0].files[0];

        // Kiểm tra các trường bắt buộc
        if (!ten) {
            showToast('error', 'Vui lòng nhập tên khách hàng');
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!ten.length > 100) {
            showToast('error', 'Tên không được dài quá 100 ký tư');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!email) {
            showToast('error', 'Vui lòng nhập email');
            $('#email').addClass('is-invalid');
            $('#email-error').text('Vui lòng nhập email').show();
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!email.length > 100) {
            showToast('error', 'email không dài quá 100 ký tự');
            $('#email').addClass('is-invalid');
            $('#email-error').text('email không dài quá 100 ký tự').show();
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!soDienThoai) {
            showToast('error', 'Vui lòng nhập số điện thoại');
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!isValidPhoneNumber(soDienThoai)) {
            showToast('error', 'Số điện thoại phải có 10 chữ số');
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!gioiTinh) {
            showToast('error', 'Vui lòng chọn giới tính');
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (ngaySinh && !isValidDate(ngaySinh)) {
            showToast('error', 'Ngày sinh không hợp lệ hoặc trong tương lai');
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!tinhThanhPho) {
            showToast('error', 'Vui lòng chọn Tỉnh/Thành phố');
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!quanHuyen) {
            showToast('error', 'Vui lòng chọn Quận/Huyện');
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!xaPhuong) {
            showToast('error', 'Vui lòng chọn Xã/Phường');
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!diaChiCuThe) {
            showToast('error', 'Vui lòng nhập địa chỉ cụ thể');
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (diaChiCuThe.length > 100) {
            showToast('error', 'Địa chỉ cụ thể không được dài quá 100 ký tự');
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }

        // Vô hiệu hóa nút và hiển thị spinner
        $('#submitBtn').prop('disabled', true);
        $('#btnIcon').addClass('d-none');
        $('#btnSpinner').removeClass('d-none');

        // Hiển thị loading SweetAlert2
        Swal.fire({
            title: 'Đang xử lý...',
            html: 'Vui lòng chờ trong giây lát',
            allowOutsideClick: false,
            allowEscapeKey: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        // Kiểm tra email trùng lặp
        checkEmailExists(email, function (emailValid) {
            if (!emailValid) {
                $('#submitBtn').prop('disabled', false);
                $('#btnSpinner').addClass('d-none');
                $('#btnIcon').removeClass('d-none');
                Swal.close();
                return;
            }

            // Tạo FormData để gửi cả text và file
            const formData = new FormData();
            formData.append('ten', ten);
            formData.append('email', email);
            formData.append('soDienThoai', soDienThoai);
            formData.append('ngaySinh', ngaySinh);
            formData.append('gioiTinh', gioiTinh);
            formData.append('tinhThanhPho', tinhThanhPho);
            formData.append('quanHuyen', quanHuyen);
            formData.append('xaPhuong', xaPhuong);
            formData.append('soNhaNgoDuong', diaChiCuThe);

            // Thêm file ảnh nếu có
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
                    Swal.close();
                    showToast('error', xhr.responseText || 'Lỗi khi thêm khách hàng');
                    $('#submitBtn').prop('disabled', false);
                    $('#btnSpinner').addClass('d-none');
                    $('#btnIcon').removeClass('d-none');
                }
            });
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

function checkEmailExists(email, callback) {
    if (email && email.length > 0) {
        $.ajax({
            url: '/khach-hang/check-email',
            type: 'POST',
            data: { email: email },
            success: function (response) {
                if (response.exists) {
                    $('#email').addClass('is-invalid');
                    $('#submitBtn').prop('disabled', true);
                    Swal.fire({
                        toast: true,
                        icon: 'error',
                        title: 'Email này đã được sử dụng',
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 3000,
                        timerProgressBar: true
                    });
                    callback(false);
                } else {
                    $('#email').removeClass('is-invalid');
                    $('#email-error').hide();
                    $('#submitBtn').prop('disabled', false);
                    callback(true);
                }
            },
            error: function (xhr) {
                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: 'Lỗi khi kiểm tra email',
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
                callback(false);
            }
        });
    } else {
        callback(false);
    }
}

// Kiểm tra email khi người dùng rời khỏi input
$('#email').on('blur', function () {
    checkEmailExists($(this).val());
});