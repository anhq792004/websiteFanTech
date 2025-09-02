$(document).ready(function () {
    // Hàm kiểm tra định dạng số điện thoại (10-12 chữ số)
    function isValidPhoneNumber(phone) {
        const phoneRegex = /^[0-9]{10,12}$/;
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

    // Hàm tải danh sách tỉnh/thành phố
    function loadCities() {
        const tinh = $('#tinh').val();
        $.ajax({
            url: 'https://provinces.open-api.vn/api/p/', // API tỉnh/thành phố
            method: 'GET',
            success: function (data) {
                $('#city').empty().append('<option value="">Chọn tỉnh thành</option>');
                data.forEach(city => {
                    $('#city').append(`<option value="${city.code}">${city.name}</option>`);
                });
                if (tinh) {
                    $('#city').val(tinh); // Điền giá trị mặc định
                    loadDistricts(tinh); // Tải quận/huyện
                }
            },
            error: function () {
                showToast('error', 'Không thể tải danh sách tỉnh/thành phố');
            }
        });
    }

    // Hàm tải danh sách quận/huyện
    function loadDistricts(cityCode) {
        const huyen = $('#huyen').val();
        $.ajax({
            url: `https://provinces.open-api.vn/api/p/${cityCode}?depth=2`, // API quận/huyện
            method: 'GET',
            success: function (data) {
                $('#district').empty().append('<option value="">Chọn quận huyện</option>');
                data.districts.forEach(district => {
                    $('#district').append(`<option value="${district.code}">${district.name}</option>`);
                });
                if (huyen) {
                    $('#district').val(huyen); // Điền giá trị mặc định
                    loadWards(huyen); // Tải xã/phường
                }
            },
            error: function () {
                showToast('error', 'Không thể tải danh sách quận/huyện');
            }
        });
    }

    // Hàm tải danh sách xã/phường
    function loadWards(districtCode) {
        const xa = $('#xa').val();
        $.ajax({
            url: `https://provinces.open-api.vn/api/d/${districtCode}?depth=2`, // API xã/phường
            method: 'GET',
            success: function (data) {
                $('#ward').empty().append('<option value="">Chọn phường xã</option>');
                data.wards.forEach(ward => {
                    $('#ward').append(`<option value="${ward.code}">${ward.name}</option>`);
                });
                if (xa) {
                    $('#ward').val(xa); // Điền giá trị mặc định
                }
            },
            error: function () {
                showToast('error', 'Không thể tải danh sách xã/phường');
            }
        });
    }

    // Gọi hàm tải tỉnh/thành phố khi trang được tải
    loadCities();

    // Xử lý sự kiện thay đổi tỉnh/thành phố
    $('#city').change(function () {
        const cityCode = $(this).val();
        if (cityCode) {
            loadDistricts(cityCode);
        } else {
            $('#district').empty().append('<option value="">Chọn quận huyện</option>');
            $('#ward').empty().append('<option value="">Chọn phường xã</option>');
        }
    });

    // Xử lý sự kiện thay đổi quận/huyện
    $('#district').change(function () {
        const districtCode = $(this).val();
        if (districtCode) {
            loadWards(districtCode);
        } else {
            $('#ward').empty().append('<option value="">Chọn phường xã</option>');
        }
    });

    // Xử lý submit form thêm nhân viên
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
        if (ten.length > 100) {
            showToast('error', 'Tên không được dài quá 100 ký tự');
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
        if (email.length > 100) {
            showToast('error', 'Email không được dài quá 100 ký tự');
            $('#email').addClass('is-invalid');
            $('#email-error').text('Email không được dài quá 100 ký tự').show();
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
            showToast('error', 'Số điện thoại phải có từ 10 đến 12 chữ số');
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
        if (!soNhaNgoDuong) {
            showToast('error', 'Vui lòng nhập địa chỉ cụ thể');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (soNhaNgoDuong.length > 100) {
            showToast('error', 'Địa chỉ cụ thể không được dài quá 100 ký tự');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!gioiTinh) {
            showToast('error', 'Vui lòng chọn giới tính');
            $('#btnNV').prop('disabled', false);
            $('#btnSpinner').addClass('d-none');
            $('#btnIcon').removeClass('d-none');
            return;
        }
        if (!chucVu) {
            showToast('error', 'Vui lòng chọn chức vụ');
            $('#btnNV').prop('disabled', false);
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

    // Xử lý submit form cập nhật nhân viên - FIXED VERSION
    $('#formUpdateNV').on('submit', function (event) {
        event.preventDefault();

        // Lấy giá trị các trường
        const id = $('#id').val().trim();
        const ten = $('#name').val().trim();
        const sdt = $('#soDienThoai').val().trim();
        const cccd = $('#canCuocCongDan').val().trim();
        const ngaySinh = $('#ngaySinh').val().trim();
        const tinhThanhPho = $('#city').val().trim();
        const quanHuyen = $('#district').val().trim();
        const xaPhuong = $('#ward').val().trim();
        const soNhaNgoDuong = $('#soNhaNgoDuong').val().trim();
        const gioiTinh = $('#gioiTinh').val().trim();
        const chucVu = $('#chucVu').val().trim();
        const hinhAnhFile = $('#hinhAnh')[0].files[0];

        // Debug log để kiểm tra giá trị chức vụ
        console.log('Giá trị chức vụ:', chucVu);
        console.log('Element chức vụ:', $('#chucVu'));

        // Kiểm tra các trường bắt buộc
        if (!ten) {
            showToast('error', 'Vui lòng nhập tên nhân viên');
            return;
        }
        if (ten.length > 100) {
            showToast('error', 'Tên không được dài quá 100 ký tự');
            return;
        }
        if (!sdt) {
            showToast('error', 'Vui lòng nhập số điện thoại');
            return;
        }
        if (!isValidPhoneNumber(sdt)) {
            showToast('error', 'Số điện thoại phải có từ 10 đến 12 chữ số');
            return;
        }
        if (!cccd) {
            showToast('error', 'Vui lòng nhập số CCCD');
            return;
        }
        if (!isValidCCCD(cccd)) {
            showToast('error', 'Số CCCD phải có 12 chữ số');
            return;
        }
        if (!ngaySinh) {
            showToast('error', 'Vui lòng nhập ngày sinh');
            return;
        }
        if (!isValidDate(ngaySinh)) {
            showToast('error', 'Ngày sinh không hợp lệ hoặc trong tương lai');
            return;
        }
        if (!soNhaNgoDuong) {
            showToast('error', 'Vui lòng nhập địa chỉ cụ thể');
            return;
        }
        if (soNhaNgoDuong.length > 100) {
            showToast('error', 'Địa chỉ cụ thể không được dài quá 100 ký tự');
            return;
        }
        if (!gioiTinh) {
            showToast('error', 'Vui lòng chọn giới tính');
            return;
        }
        if (!chucVu || chucVu === "") {
            showToast('error', 'Vui lòng chọn chức vụ');
            console.log('Chức vụ rỗng hoặc không hợp lệ:', chucVu);
            return;
        }
        if (hinhAnhFile) {
            if (!hinhAnhFile.type.startsWith('image/')) {
                showToast('error', 'Vui lòng chọn file hình ảnh hợp lệ');
                return;
            }
            if (hinhAnhFile.size > 5 * 1024 * 1024) {
                showToast('error', 'Kích thước file không được vượt quá 5MB');
                return;
            }
        }

        // Vô hiệu hóa nút và hiển thị spinner
        const $submitBtn = $('button[type="submit"]');
        $submitBtn.prop('disabled', true);
        $submitBtn.find('.bi-pencil-square').addClass('d-none');
        $submitBtn.append('<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>');

        // Hiển thị loading
        Swal.fire({
            title: 'Đang cập nhật...',
            html: 'Vui lòng chờ trong giây lát',
            allowOutsideClick: false,
            allowEscapeKey: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        // Tạo FormData
        const formData = new FormData();
        formData.append('id', id);
        formData.append('ten', ten);
        formData.append('soDienThoai', sdt);
        formData.append('canCuocCongDan', cccd);
        formData.append('ngaySinh', ngaySinh);
        formData.append('gioiTinh', gioiTinh);
        formData.append('tinhThanhPho', tinhThanhPho);
        formData.append('quanHuyen', quanHuyen);
        formData.append('xaPhuong', xaPhuong);
        formData.append('soNhaNgoDuong', soNhaNgoDuong);
        formData.append('chucVu', chucVu); // Đảm bảo field này được gửi

        if (hinhAnhFile) {
            formData.append('hinhAnh', hinhAnhFile);
        }

        // Debug log để kiểm tra FormData
        console.log('FormData contents:');
        for (let pair of formData.entries()) {
            console.log(pair[0] + ': ' + pair[1]);
        }

        // Gửi AJAX request
        $.ajax({
            url: '/admin/nhan-vien/update',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            beforeSend: function(xhr, settings) {
                console.log('Sending data to:', settings.url);
                console.log('Request data:', formData);
            },
            success: function (response) {
                console.log('Update response:', response);
                Swal.close();
                Swal.fire({
                    toast: true,
                    icon: 'success',
                    title: response || 'Cập nhật thông tin nhân viên thành công',
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1500,
                    timerProgressBar: true
                }).then(() => {
                    window.location.href = '/admin/nhan-vien/index';
                });
            },
            error: function (xhr, status, error) {
                console.error('Update error:', xhr.responseText);
                console.error('Status:', status);
                console.error('Error:', error);

                Swal.close();
                showToast('error', xhr.responseText || 'Lỗi khi cập nhật thông tin nhân viên');

                // Khôi phục lại nút submit
                $submitBtn.prop('disabled', false);
                $submitBtn.find('.spinner-border').remove();
                $submitBtn.find('.bi-pencil-square').removeClass('d-none');
            }
        });
    });

    // Preview ảnh khi chọn file trong form update
    $('#hinhAnh').on('change', function () {
        const file = this.files[0];
        if (file) {
            // Kiểm tra loại file
            if (!file.type.startsWith('image/')) {
                showToast('error', 'Vui lòng chọn file hình ảnh hợp lệ');
                this.value = '';
                return;
            }

            // Kiểm tra kích thước file (max 5MB)
            if (file.size > 5 * 1024 * 1024) {
                showToast('error', 'Kích thước file không được vượt quá 5MB');
                this.value = '';
                return;
            }

            // Hiển thị preview
            const reader = new FileReader();
            reader.onload = function (e) {
                $('#employeeAvatar').attr('src', e.target.result);
                showToast('success', 'Đã chọn ảnh thành công!');
            };
            reader.readAsDataURL(file);
        }
    });

    // Click vào ảnh avatar nhân viên để chọn ảnh
    $('#employeeAvatar').on('click', function () {
        const currentSrc = $(this).attr('src');
        const isDefaultAvatar = currentSrc.includes('avatar.jpg');

        Swal.fire({
            title: isDefaultAvatar ? 'Thêm ảnh nhân viên' : 'Thay đổi ảnh nhân viên',
            text: isDefaultAvatar
                ? 'Hãy chọn một ảnh để tạo ảnh đại diện cho nhân viên!'
                : 'Bạn muốn thay đổi ảnh đại diện hiện tại?',
            icon: isDefaultAvatar ? 'info' : 'question',
            showCancelButton: true,
            confirmButtonText: 'Chọn ảnh',
            cancelButtonText: 'Hủy'
        }).then((result) => {
            if (result.isConfirmed) {
                openFileSelector();
            }
        });
    });

    // Hàm mở file selector
    function openFileSelector() {
        const fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.accept = 'image/*';
        fileInput.style.display = 'none';

        fileInput.onchange = function (e) {
            const file = e.target.files[0];
            if (file) {
                if (!file.type.startsWith('image/')) {
                    showToast('error', 'Vui lòng chọn file hình ảnh hợp lệ');
                    return;
                }
                if (file.size > 5 * 1024 * 1024) {
                    showToast('error', 'Kích thước file không được vượt quá 5MB');
                    return;
                }

                const reader = new FileReader();
                reader.onload = function (e) {
                    $('#employeeAvatar').attr('src', e.target.result);
                    showToast('success', 'Đã chọn ảnh thành công! Vui lòng nhấn "Lưu" để cập nhật.');
                };
                reader.readAsDataURL(file);

                $('#hinhAnh')[0].files = e.target.files;
            }
            document.body.removeChild(fileInput);
        };

        document.body.appendChild(fileInput);
        fileInput.click();
    }

    // Xử lý thay đổi trạng thái nhân viên
    $('.changeStatusNhanVien').on('click', function () {
        const id = $(this).data('id');

        $.ajax({
            url: '/admin/nhan-vien/change-status',
            type: 'POST',
            data: { id: id },
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
                        title: response, // Hiển thị thông báo lỗi từ server
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
                    title: xhr.responseText || "Có lỗi xảy ra khi cập nhật trạng thái nhân viên",
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
            }
        });
    });

    // Thêm event listener cho dropdown chức vụ để debug
    $('#chucVu').on('change', function() {
        console.log('Chức vụ đã thay đổi:', $(this).val());
    });
});

// Hàm kiểm tra email tồn tại
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
                    showToast('error', 'Email này đã được sử dụng');
                    callback(false);
                } else {
                    $('#email').removeClass('is-invalid');
                    $('#email-error').hide();
                    callback(true);
                }
            },
            error: function (xhr) {
                showToast('error', 'Lỗi khi kiểm tra email');
                callback(false);
            }
        });
    } else {
        callback(false);
    }
}

// Gọi kiểm tra email khi người dùng nhập xong
$('#email').on('blur', function () {
    checkEmailExists($(this).val());
});