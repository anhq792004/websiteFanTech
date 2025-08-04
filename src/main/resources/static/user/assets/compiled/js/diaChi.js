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


