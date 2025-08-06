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
                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: xhr.responseText,
                    position: 'top-end',
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
            Swal.fire({
                toast: true,
                icon: 'error',
                title: xhr.responseText,
                position: 'top-end',
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

