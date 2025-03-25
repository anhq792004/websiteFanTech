// Xử lý thêm mới nút bấm
$('#addNutBam').on('submit', function (e) {
    e.preventDefault(); // Ngăn form thực hiện submit mặc định

    const name = $('#name').val();

    // Kiểm tra tên có trống không
    if (!name || name.trim() === '') {
        Swal.fire({
            toast: true,
            icon: 'error',
            title: 'Tên nút bấm không được để trống',
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true
        });
        return;
    }

    $.ajax({
        url: '/nut-bam/add',
        type: 'POST',
        data: {name: name},
        success: function (response) {
            if (response === "Nút bấm thêm mới thành công.") {
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
                title: "Có lỗi xảy ra khi thêm mới nút bấm",
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
        }
    });
});

// Xử lý hiển thị thông tin khi click vào nút chỉnh sửa
$('.hienThiThongTinModal').on('click', function () {
    const id = $(this).data('id');
    $.ajax({
        url: '/nut-bam/find-by-id',
        type: 'GET',
        data: {id: id},
        success: function (response) {
            $('#modalChinhSua #id').val(response.id);
            $('#modalChinhSua #updateName').val(response.ten);
        },
        error: function (xhr) {
            Swal.fire({
                toast: true,
                icon: 'error',
                title: "Có lỗi xảy ra khi lấy thông tin nút bấm",
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
        }
    });
});

// Xử lý cập nhật nút bấm
$('#updateNutBam').on('submit', function (e) {
    e.preventDefault(); // Ngăn form thực hiện submit mặc định

    const id = $('#modalChinhSua #id').val();
    const name = $('#modalChinhSua #updateName').val();

    // Kiểm tra tên có trống không
    if (!name || name.trim() === '') {
        Swal.fire({
            toast: true,
            icon: 'error',
            title: 'Tên nút bấm không được để trống',
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true
        });
        return;
    }

    // Gửi request AJAX
    $.ajax({
        url: '/nut-bam/update',
        type: 'POST',
        data: {id: id, name: name},
        success: function (response) {
            if (response === "Cập nhật nút bấm thành công.") {
                $('#modalChinhSua').modal('hide');
                Swal.fire({
                    toast: true,
                    icon: 'success',
                    title: response,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true
                }).then(() => {
                    window.location.reload();
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
                title: "Có lỗi xảy ra khi cập nhật nút bấm",
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
        }
    });
});

// Xử lý thay đổi trạng thái nút bấm
$('.changeStatusNutBam').on('click', function () {
    const id = $(this).data('id');

    $.ajax({
        url: '/nut-bam/change-status',
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
                title: "Có lỗi xảy ra khi cập nhật trạng thái nút bấm",
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
        }
    });
});

// Xử lý tìm kiếm
$('#searchInput, #trangThai').on('change', function(e) {
    e.preventDefault(); // Ngăn chặn sự kiện mặc định

    const name = $('#searchInput').val();
    const trangThai = $('#trangThai').val();

    // Submit form tìm kiếm
    window.location.href = `/nut-bam/index?name=${name}&status=${trangThai}`;
});