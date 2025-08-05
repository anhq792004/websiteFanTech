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

        const formData = {
            ten: $('#name').val(),
            email: $('#email').val(),
            soDienThoai: $('#soDienThoai').val(),
            ngaySinh: $('#ngaySinh').val(),
            gioiTinh: $('#gioiTinh').val(),
            tinhThanhPho: $('#city').val(),
            quanHuyen: $('#district').val(),
            xaPhuong: $('#ward').val(),
            soNhaNgoDuong: $('#diaChiCuThe').val()
        };

        $.ajax({
            url: '/khach-hang/add',
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


