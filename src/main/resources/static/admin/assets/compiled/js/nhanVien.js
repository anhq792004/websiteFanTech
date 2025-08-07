$(document).ready(function () {
    // Hàm kiểm tra định dạng số điện thoại (10 chữ số)
    function isValidPhoneNumber(phone) {
        const phoneRegex = /^[0-9]{10}$/;
        return phoneRegex.test(phone);
    }

    // Hàm kiểm tra định dạng CCCD (12 chữ số)
    function isValidCCCD(cccd) {
        const cccdRegex = /^[0-9]{12}$/;
        return cccdRegex.test(cccd);
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

    $('#addNhanVienForm').on('submit', function (event) {
        event.preventDefault();

        // Lấy giá trị các trường
        const ten = $('#ten').val().trim();
        const email = $('#email').val().trim();
        const sdt = $('#sdt').val().trim();
        const cccd = $('#cccd').val().trim();
        const ngaySinh = $('#ngaySinh').val().trim();
        const tinhThanhPho = $('#city').val().trim();
        const quanHuyen = $('#district').val().trim();
        const xaPhuong = $('#ward').val().trim();
        const soNhaNgoDuong = $('#soNhaNgoDuong').val().trim();
        const gioiTinh = $('#gioiTinh').val().trim();
        const chucVu = $('#chucVu').val().trim();
        const hinhAnhFile = $('#hinhAnh')[0].files[0];

        // Kiểm tra các trường bắt buộc
        if (!ten) {
            showToast('error', 'Vui lòng nhập tên nhân viên');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!email) {
            showToast('error', 'Vui lòng nhập email');
            $('#email').addClass('is-invalid');
            $('#email-error').text('Vui lòng nhập email').show();
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!sdt) {
            showToast('error', 'Vui lòng nhập số điện thoại');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!isValidPhoneNumber(sdt)) {
            showToast('error', 'Số điện thoại phải có 10 chữ số');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!cccd) {
            showToast('error', 'Vui lòng nhập số CCCD');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!isValidCCCD(cccd)) {
            showToast('error', 'Số CCCD phải có 12 chữ số');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!ngaySinh) {
            showToast('error', 'Vui lòng nhập ngày sinh');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!isValidDate(ngaySinh)) {
            showToast('error', 'Ngày sinh không hợp lệ hoặc trong tương lai');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!tinhThanhPho) {
            showToast('error', 'Vui lòng chọn Tỉnh/Thành phố');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!quanHuyen) {
            showToast('error', 'Vui lòng chọn Quận/Huyện');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!xaPhuong) {
            showToast('error', 'Vui lòng chọn Xã/Phường');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!soNhaNgoDuong) {
            showToast('error', 'Vui lòng nhập địa chỉ cụ thể');
            $('#btnNV').prop('disabled', false);
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
        if (!chucVu) {
            showToast('error', 'Vui lòng chọn chức vụ');
            $('#submitBtn').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }

        // Vô hiệu hóa nút và hiển thị spinner
        $('#btnNV').prop('disabled', true);
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
                $('#btnNV').prop('disabled', false);
                $('#btnSpinner').addClass('d-none');
                $('#btnIcon').removeClass('d-none');
                Swal.close();
                return;
            }

            // Tạo FormData để gửi cả text và file
            const formData = new FormData();
            formData.append('ten', ten);
            formData.append('canCuocCongDan', cccd);
            formData.append('email', email);
            formData.append('soDienThoai', sdt);
            formData.append('ngaySinh', ngaySinh);
            formData.append('gioiTinh', gioiTinh);
            formData.append('tinhThanhPho', tinhThanhPho);
            formData.append('quanHuyen', quanHuyen);
            formData.append('xaPhuong', xaPhuong);
            formData.append('soNhaNgoDuong', soNhaNgoDuong);
            formData.append('chucVu', chucVu);

            // Thêm file ảnh nếu có
            const hinhAnhFile = $('#hinhAnh')[0].files[0];
            if (hinhAnhFile) {
                formData.append('hinhAnh', hinhAnhFile);
            }

            $.ajax({
                url: '/admin/nhan-vien/them',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (response) {
                    Swal.fire({
                        toast: true,
                        icon: 'success',
                        title: response.message || 'Thêm nhân viên thành công',
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 1000,
                        timerProgressBar: true
                    }).then(() => {
                        window.location.href = '/admin/nhan-vien/index';
                    });
                },
                error: function (xhr) {
                    Swal.close();
                    showToast('error', xhr.responseText || 'Lỗi khi thêm nhân viên');
                    $('#btnNV').prop('disabled', false);
                    $('#btnSpinner').addClass('d-none');
                    $('#btnIcon').removeClass('d-none');
                }
            });
        });
    });
});

$('.changeStatusNhanVien').on('click', function () {
    const id = $(this).data('id');

    $.ajax({
        url: '/admin/nhan-vien/change-status',
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
                    timer: 500,
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
                    timer: 1000,
                    timerProgressBar: true
                });
            }
        },
        error: function (xhr) {
            Swal.fire({
                toast: true,
                icon: 'error',
                title: "Có lỗi xảy ra khi cập nhật trạng thái nhân viên",
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
        }
    });
});

$(document).ready(function () {
    $('#formUpdateNV').submit(function (e) {
        e.preventDefault();

        // Tạo FormData để gửi cả text và file
        const formData = new FormData();
        formData.append('id', $('#id').val());
        formData.append('ten', $('#name').val());
        formData.append('soDienThoai', $('#soDienThoai').val());
        formData.append('canCuocCongDan', $('#canCuocCongDan').val());
        formData.append('ngaySinh', $('#ngaySinh').val());
        formData.append('gioiTinh', $('#gioiTinh').val());
        formData.append('tinhThanhPho', $('#city').val());
        formData.append('quanHuyen', $('#district').val());
        formData.append('xaPhuong', $('#ward').val());
        formData.append('soNhaNgoDuong', $('#soNhaNgoDuong').val());
        formData.append('chucVu', $('#chucVu').val());

        // Thêm file ảnh nếu có
        const hinhAnhFile = $('#hinhAnh')[0].files[0];
        if (hinhAnhFile) {
            formData.append('hinhAnh', hinhAnhFile);
        }

        $.ajax({
            url: '/admin/nhan-vien/update',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                Swal.fire({
                    toast: true,
                    icon: 'success',
                    title: response.message || 'Thông tin nhân viên đã được thay đổi',
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true
                }).then(() => {
                    window.location.href = '/admin/nhan-vien/index';
                });
            },
            error: function (xhr) {
                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: xhr.responseText || 'Lỗi khi thay đổi thông tin nhân viên',
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true
                });
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
                // Thay thế ảnh trong ô chính
                $('#employeeAvatar').attr('src', e.target.result);
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
        } else {
            $('#imagePreview').hide();
        }
    });

    // Click vào ảnh avatar nhân viên để chọn ảnh
    $('#employeeAvatar').on('click', function() {
        // Kiểm tra xem có phải ảnh mặc định không
        const currentSrc = $(this).attr('src');
        const isDefaultAvatar = currentSrc.includes('avatar.jpg');

        if (isDefaultAvatar) {
            // Hiển thị thông báo đặc biệt cho lần đầu thêm ảnh
            Swal.fire({
                title: 'Thêm ảnh nhân viên',
                text: 'Hãy chọn một ảnh để tạo ảnh đại diện cho nhân viên!',
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
                title: 'Thay đổi ảnh nhân viên',
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
                    return;
                }

                // Hiển thị preview
                const reader = new FileReader();
                reader.onload = function(e) {
                    // Thay thế ảnh trong ô chính
                    $('#employeeAvatar').attr('src', e.target.result);
                    $('#imagePreview').hide(); // Ẩn preview riêng

                    // Hiển thị thông báo
                    Swal.fire({
                        toast: true,
                        icon: 'success',
                        title: 'Đã chọn ảnh thành công! Vui lòng nhấn "Lưu" để cập nhật.',
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

function checkEmailExists(email, callback) {
    if (email && email.length > 0) {
        $.ajax({
            url: '/admin/nhan-vien/check-email',
            type: 'POST',
            data: { email: email },
            success: function (response) {
                if (response.exists) {
                    $('#email').addClass('is-invalid');
                    $('#email-error').text('Email này đã được sử dụng').show();
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

// Gọi kiểm tra email khi người dùng nhập xong
$('#email').on('blur', function() {
    checkEmailExists($(this).val());
});