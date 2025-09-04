// Xử lý thêm mới hãng
$(document).ready(function() {
    $('#addHang').on('submit', function (e) {
        e.preventDefault(); // Ngăn form thực hiện submit mặc định

        const name = $('#name').val();

        // Kiểm tra tên có trống không
        if (!name || name.trim() === '') {
            Swal.fire({
                toast: true,
                icon: 'error',
                title: 'Tên hãng không được để trống',
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
            return;
        }

        $.ajax({
            url: '/hang/add',
            type: 'POST',
            data: {name: name},
            success: function (response) {
                if (response === "Hãng thêm mới thành công.") {
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
                    title: "Có lỗi xảy ra khi thêm mới hãng",
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
        const ten = $(this).closest('tr').find('td:eq(1)').text();
        
        $('#id').val(id);
        $('#updateName').val(ten);
    });

    // Xử lý cập nhật hãng khi nhấn nút Lưu
    $('#btnSaveUpdate').on('click', function () {
        const id = $('#id').val();
        const name = $('#updateName').val();

        // Kiểm tra tên có trống không
        if (!name || name.trim() === '') {
            Swal.fire({
                toast: true,
                icon: 'error',
                title: 'Tên hãng không được để trống',
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
            return;
        }

        // Gửi request AJAX
        $.ajax({
            url: '/hang/update',
            type: 'POST',
            data: {id: id, name: name},
            success: function (response) {
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
                    location.reload();
                });
            },
            error: function (xhr) {
                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: "Có lỗi xảy ra khi cập nhật hãng",
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
            }
        });
    });

    // Xử lý form submit để ngăn chặn hành vi mặc định
    $('#updateHang').on('submit', function (e) {
        e.preventDefault(); // Ngăn form thực hiện submit mặc định
        e.stopPropagation(); // Dừng sự kiện lan truyền lên các phần tử cha
        return false; // Ngăn chặn form submit mặc định
    });

    // Xử lý thay đổi trạng thái hãng
    $('.changeStatusHang').on('click', function () {
        const id = $(this).data('id');

        $.ajax({
            url: '/hang/change-status',
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
                    title: "Có lỗi xảy ra khi cập nhật trạng thái hãng",
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
        const status = $('#trangThai').val();

        // Submit form tìm kiếm
        window.location.href = `/hang/index?name=${name}&status=${status}`;
    });
});