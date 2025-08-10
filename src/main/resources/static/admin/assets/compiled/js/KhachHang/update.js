$(document).ready(function () {
    // Hàm kiểm tra định dạng số điện thoại (10-12 chữ số)
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

    // Validation cho form thêm địa chỉ
    $('#addDCForm').on('submit', function (event) {
        event.preventDefault();

        // Lấy giá trị các trường
        const idKH = $('input[name="idKH"]').val().trim();
        const tinh = $('#city2').val().trim();
        const huyen = $('#district2').val().trim();
        const xa = $('#ward2').val().trim();
        const soNhaNgoDuong = $('#diaChiCuThe').val().trim();

        // Kiểm tra các trường bắt buộc
        if (!idKH) {
            showToast('error', 'ID khách hàng không được để trống');
            $('#addDCForm button[type="submit"]').prop('disabled', false);
            return;
        }
        if (!tinh) {
            showToast('error', 'Vui lòng chọn Tỉnh/Thành phố');
            $('#addDCForm button[type="submit"]').prop('disabled', false);
            return;
        }
        if (tinh.length > 100) {
            showToast('error', 'Tỉnh/Thành phố không được dài quá 100 ký tự');
            $('#addDCForm button[type="submit"]').prop('disabled', false);
            return;
        }
        if (!huyen) {
            showToast('error', 'Vui lòng chọn Quận/Huyện');
            $('#addDCForm button[type="submit"]').prop('disabled', false);
            return;
        }
        if (huyen.length > 100) {
            showToast('error', 'Quận/Huyện không được dài quá 100 ký tự');
            $('#addDCForm button[type="submit"]').prop('disabled', false);
            return;
        }
        if (!xa) {
            showToast('error', 'Vui lòng chọn Xã/Phường');
            $('#addDCForm button[type="submit"]').prop('disabled', false);
            return;
        }
        if (xa.length > 100) {
            showToast('error', 'Xã/Phường không được dài quá 100 ký tự');
            $('#addDCForm button[type="submit"]').prop('disabled', false);
            return;
        }
        if (!soNhaNgoDuong) {
            showToast('error', 'Vui lòng nhập địa chỉ cụ thể');
            $('#addDCForm button[type="submit"]').prop('disabled', false);
            return;
        }
        if (soNhaNgoDuong.length > 100) {
            showToast('error', 'Địa chỉ cụ thể không được dài quá 100 ký tự');
            $('#addDCForm button[type="submit"]').prop('disabled', false);
            return;
        }

        // Vô hiệu hóa nút và hiển thị loading
        $('#addDCForm button[type="submit"]').prop('disabled', true);
        Swal.fire({
            title: 'Đang xử lý...',
            html: 'Vui lòng chờ trong giây lát',
            allowOutsideClick: false,
            allowEscapeKey: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        // Thu thập dữ liệu từ form
        const formData = {
            idKH: idKH,
            tinh: tinh,
            huyen: huyen,
            xa: xa,
            soNhaNgoDuong: soNhaNgoDuong
        };

        // Gửi Ajax request
        $.ajax({
            url: '/khach-hang/add-dia-chi',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
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
                    location.reload();
                });
            },
            error: function (xhr) {
                Swal.close();
                showToast('error', xhr.responseText || 'Lỗi khi thêm địa chỉ');
                $('#addDCForm button[type="submit"]').prop('disabled', false);
            }
        });
    });

    // Validation cho form cập nhật khách hàng
    $('#formUpdateKH').on('submit', function (event) {
        event.preventDefault();

        // Lấy giá trị các trường
        const idKH = $('#idKH').val().trim();
        const ten = $('#name').val().trim();
        const soDienThoai = $('#soDienThoai').val().trim();
        const ngaySinh = $('#ngaySinh').val().trim();
        const gioiTinh = $('#gioiTinh').val().trim();
        const hinhAnhFile = $('#hinhAnh')[0].files[0];

        // Kiểm tra các trường bắt buộc
        if (!idKH) {
            showToast('error', 'ID khách hàng không được để trống');
            $('#formUpdateKH button[type="submit"]').prop('disabled', false);
            return;
        }
        if (!ten) {
            showToast('error', 'Vui lòng nhập tên khách hàng');
            $('#formUpdateKH button[type="submit"]').prop('disabled', false);
            return;
        }
        if (ten.length > 100) {
            showToast('error', 'Tên không được dài quá 100 ký tự');
            $('#formUpdateKH button[type="submit"]').prop('disabled', false);
            return;
        }
        if (!soDienThoai) {
            showToast('error', 'Vui lòng nhập số điện thoại');
            $('#formUpdateKH button[type="submit"]').prop('disabled', false);
            return;
        }
        if (!isValidPhoneNumber(soDienThoai)) {
            showToast('error', 'Số điện thoại phải có 10-12 chữ số');
            $('#formUpdateKH button[type="submit"]').prop('disabled', false);
            return;
        }
        if (!gioiTinh) {
            showToast('error', 'Vui lòng chọn giới tính');
            $('#formUpdateKH button[type="submit"]').prop('disabled', false);
            return;
        }
        if (ngaySinh && !isValidDate(ngaySinh)) {
            showToast('error', 'Ngày sinh không hợp lệ hoặc trong tương lai');
            $('#formUpdateKH button[type="submit"]').prop('disabled', false);
            return;
        }
        // Kiểm tra file ảnh nếu có
        if (hinhAnhFile) {
            if (!hinhAnhFile.type.startsWith('image/')) {
                showToast('error', 'Vui lòng chọn file hình ảnh hợp lệ');
                $('#formUpdateKH button[type="submit"]').prop('disabled', false);
                return;
            }
            if (hinhAnhFile.size > 5 * 1024 * 1024) {
                showToast('error', 'Kích thước file không được vượt quá 5MB');
                $('#formUpdateKH button[type="submit"]').prop('disabled', false);
                return;
            }
        }

        // Vô hiệu hóa nút và hiển thị loading
        $('#formUpdateKH button[type="submit"]').prop('disabled', true);
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
        formData.append('idKH', idKH);
        formData.append('ten', ten);
        formData.append('soDienThoai', soDienThoai);
        formData.append('ngaySinh', ngaySinh);
        formData.append('gioiTinh', gioiTinh);

        // Thêm file ảnh nếu có
        if (hinhAnhFile) {
            formData.append('hinhAnh', hinhAnhFile);
        }

        $.ajax({
            url: '/khach-hang/update',
            method: 'POST',
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
                    timer: 1500,
                    timerProgressBar: true
                }).then(() => {
                    window.location.href = '/khach-hang/index';
                });
            },
            error: function (xhr) {
                Swal.close();
                showToast('error', xhr.responseText || 'Lỗi khi cập nhật khách hàng');
                $('#formUpdateKH button[type="submit"]').prop('disabled', false);
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
                $('#imagePreview').hide();
                return;
            }

            // Kiểm tra kích thước file (max 5MB)
            if (file.size > 5 * 1024 * 1024) {
                showToast('error', 'Kích thước file không được vượt quá 5MB');
                this.value = '';
                $('#imagePreview').hide();
                return;
            }

            // Hiển thị preview
            const reader = new FileReader();
            reader.onload = function (e) {
                $('#previewImg').attr('src', e.target.result);
                $('#imagePreview').show();
            };
            reader.readAsDataURL(file);
        } else {
            $('#imagePreview').hide();
        }
    });
});