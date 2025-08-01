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

    const data = {
        idKH: $('#idKH').val(),
        ten: $('#name').val(),
        gioiTinh: $('#gioiTinh').val(),
        soDienThoai: $('#soDienThoai').val(),
        ngaySinh: $('#ngaySinh').val(),
    };

    $.ajax({
        url: '/khach-hang/update',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
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
            $('#formUpdateKH button[type="submit"]').prop('disabled', false);

        }
    });
});

