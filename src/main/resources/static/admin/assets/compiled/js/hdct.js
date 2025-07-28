let hoaDonId
let btn
$(document).ready(function () {
    $(".btn-xac-nhan").click(function () {
        btn = $(this);
        hoaDonId = $(this).closest("form").data("id"); // Lấy ID hóa đơn
//         btn.prop("disabled", true);
        Swal.fire({
            title: "Xác nhận hóa đơn",
            input: "textarea",
            inputPlaceholder: "Nhập để xác nhận đơn hàng",
            inputAttributes: {
                "aria-label": "Nhập để xác nhận đơn hàng"
            },
            icon: "success",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Xác nhận",
            didOpen: () => {
                const confirmBtn = Swal.getConfirmButton();
                const input = Swal.getInput();

                confirmBtn.disabled = true; // ban đầu disable

                input.addEventListener('input', () => {
                    confirmBtn.disabled = !input.value.trim(); // có nhập thì enable
                });
            }
        }).then((result) => {
            if (result.isConfirmed) {
                const ghiChu = result.value;

                $.ajax({
                    url: "/hoa-don/xac-nhan",
                    type: "POST",
                    data: {
                        id: hoaDonId,
                        ghiChu: ghiChu
                    }, // Gửi ID của hóa đơn
                    success: function (response) {
                        Swal.fire({
                            toast: true,
                            icon: 'success',
                            title: response, // Thông báo thành công từ server
                            position: 'top-end',
                            showConfirmButton: false,
                            timer: 1000,
                            timerProgressBar: true
                        }).then(() => {
                            location.reload(); // Reload lại trang sau khi nhấn OK
                        });
                    },
                    error: function () {
                        Swal.fire("Lỗi!", "Lỗi khi xác nhận hóa đơn", "error");
                        btn.prop("disabled", false);
                    }
                });
            }
        });
    });
});


//
$(document).ready(function () {
    $(".btn-giao-hang").click(function () {
        btn = $(this);
        hoaDonId = $(this).closest("form").data("id"); // Lấy ID hóa đơn
        // Vô hiệu hóa nút
        btn.prop("disabled", true);

        $.ajax({
            url: "/hoa-don/giao-hang",
            type: "POST",
            data: {id: hoaDonId}, // Gửi ID của hóa đơn
            success: function (response) {
                Swal.fire({
                    toast: true,
                    icon: 'success',
                    title: response, // Thông báo từ server
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true
                }).then(() => {
                    location.reload(); // Reload lại trang sau khi thành công
                });
            },
            error: function () {
                Swal.fire("Lỗi!", "Lỗi khi giao hóa đơn", "error");
                btn.prop("disabled", false);
            }
        });
    });
});

$(document).ready(function () {
    $(".btn-hoan-thanh").click(function () {
        btn = $(this);
        hoaDonId = $(this).closest("form").data("id"); // Lấy ID hóa đơn
        // Vô hiệu hóa nút
        btn.prop("disabled", true);

        $.ajax({
            url: "/hoa-don/hoan-thanh",
            type: "POST",
            data: {id: hoaDonId}, // Gửi ID của hóa đơn
            success: function (response) {
                Swal.fire({
                    toast: true,
                    icon: 'success',
                    title: response, // Thông báo từ server
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true
                }).then(() => {
                    location.reload(); // Reload lại trang sau khi thành công
                });
            },
            error: function () {
                Swal.fire("Lỗi!", "Lỗi khi hoàn thành hóa đơn", "error");
                btn.prop("disabled", false);

            }
        });
    });
});

$(document).ready(function () {
    $(".btn-huy").click(function () {
        hoaDonId = $(this).closest("form").data("id"); // Lấy ID hóa đơn

        Swal.fire({
            title: "Hủy hóa đơn?",
            input: "textarea",
            inputPlaceholder: "Vui lòng nhập lí do hủy hóa đơn",
            inputAttributes: {
                "aria-label": "Vui lòng nhập lí do hủy hóa đơn"
            },
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Xác nhận",
            didOpen: () => {
                const confirmBtn = Swal.getConfirmButton();
                const input = Swal.getInput();

                confirmBtn.disabled = true; // ban đầu disable

                input.addEventListener('input', () => {
                    confirmBtn.disabled = !input.value.trim(); // có nhập thì enable
                });
            }
        }).then((result) => {
            if (result.isConfirmed) {
                const ghiChu = result.value;

                $.ajax({
                    url: "/hoa-don/huy",
                    type: "POST",
                    data: {
                        id: hoaDonId,
                        ghiChu: ghiChu
                    }, // Gửi ID của hóa đơn
                    success: function (response) {
                        Swal.fire({
                            toast: true,
                            icon: 'success',
                            title: response, // Thông báo thành công từ server
                            position: 'top-end',
                            showConfirmButton: false,
                            timer: 1000,
                            timerProgressBar: true
                        }).then(() => {
                            location.reload(); // Reload lại trang sau khi nhấn OK
                        });
                    },
                    error: function () {
                        Swal.fire("Lỗi!", "Lỗi khi xác nhận hóa đơn", "error");
                    }
                });
            }
        });
    });
});

$(".btn-add-sanPham").click(function () {
    const hoaDonId = $(this).data("id-hd");
    const sanPhamId = $(this).data("id-sp");
    const gia = $(this).data("gia");
    const soLuong = 1;

    $.ajax({
        url: '/hoa-don/addSP',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            idHD: hoaDonId,
            idSP: sanPhamId,
            gia: gia,
            soLuong: soLuong
        }),
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
                // Reload trang để cập nhật đầy đủ
                location.reload();

                // Hoặc có thể cập nhật động (nâng cao hơn)
                // updateTongTienDisplay();
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
        }
    });
});


$(".btn-xoa-sanPham").click(function () {
    const sanPhamId = $(this).data("id-sp");
    const hoaDonId = $(this).data("id-hd");

    $.ajax({
        url: '/hoa-don/xoa',
        type: 'POST',
        data: {
            idSP: sanPhamId,
            idHD: hoaDonId
        },
        success: function (response) {
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
                title: xhr.responseText,
                position: 'top-end',
                showConfirmButton: false,
                timer: 1500,
                timerProgressBar: true
            });
        }
    });
});

$(".quantity-btn").click(function () {
    const idSP = $(this).closest("form").data("idsp");
    const idHD = $(this).closest("form").data("idhd");

    $.ajax({
        url: '/hoa-don/tangSoLuong',
        type: 'POST',
        data: {
            idSP: idSP,
            idHD: idHD,
        },
        success: function (response) {
            Swal.fire({
                toast: true,
                icon: 'success',
                title: response,
                position: 'top-end',
                showConfirmButton: false,
                timer: 1000
            }).then(() => {
                location.reload(); // Reload để cập nhật tổng tiền
            });
        },
        error: function (xhr) {
            Swal.fire({
                toast: true,
                icon: 'error',
                title: xhr.responseText,
                position: 'top-end',
                showConfirmButton: false,
                timer: 1000
            });
        }
    });
});

$(".quantity-btn-1").click(function () {
    const idSP = $(this).closest("form").data("idsp");
    const idHD = $(this).closest("form").data("idhd");

    $.ajax({
        url: '/hoa-don/giamSoLuong',
        type: 'POST',
        data: {
            idSP: idSP,
            idHD: idHD,
        },
        success: function (response) {
            Swal.fire({
                toast: true,
                icon: 'success',
                title: response,
                position: 'top-end',
                showConfirmButton: false,
                timer: 1000
            }).then(() => {
                location.reload(); // Reload để cập nhật tổng tiền
            });
        },
        error: function (xhr) {
            Swal.fire({
                toast: true,
                icon: 'error',
                title: xhr.responseText,
                position: 'top-end',
                showConfirmButton: false,
                timer: 1000
            });
        }
    });
});
// update infor khach hang
$(document).ready(function () {
    $(".btn-update-infor").click(function () {
        let formData = {
            idHD: $("#idHD").val(),
            tenNguoiNhan: $("#tenNguoiNhan").val(),
            sdtNguoiNhan: $("#sdtNguoiNhan").val(),
            soNhaNgoDuong: $("#soNhaNgoDuong").val(),
            xa: $("#ward").val(),
            huyen: $("#district").val(),
            tinh: $("#city").val()
        };

        $.ajax({
            url: "/hoa-don/updateInfor",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function (response) {
                Swal.fire({
                    toast: true,
                    icon: 'success',
                    title: response,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 500
                }).then(() => location.reload());
            },
            error: function (xhr) {
                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: xhr.responseText,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1000
                });
            }
        });
    });
});

function updateTongTienDisplay() {
    // Tính toán lại tổng tiền từ các sản phẩm hiện có
    let tongTien = 0;
    $('.product-total').each(function() {
        let thanhTien = parseFloat($(this).text().replace(/[^\d]/g, '')) || 0;
        tongTien += thanhTien;
    });

    // Cập nhật hiển thị tổng tiền
    $('.order-total').text(formatCurrency(tongTien));

    // Tính toán tổng tiền sau giảm giá (cần gọi API để lấy thông tin phiếu giảm giá)
    updateTongTienSauGiamGia(tongTien);
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function updateTongTienSauGiamGia(tongTien) {
    // Gọi API để tính toán tổng tiền sau giảm giá
    const hoaDonId = $('form[data-id]').first().data('id');

    $.ajax({
        url: '/hoa-don/tinh-tong-tien-sau-giam',
        type: 'POST',
        data: {
            idHD: hoaDonId,
            tongTien: tongTien
        },
        success: function(response) {
            // Cập nhật hiển thị tổng tiền sau giảm giá
            $('.order-total:last').text(formatCurrency(response.tongTienSauGiamGia));
        },
        error: function() {
            console.log('Lỗi khi tính toán tổng tiền sau giảm giá');
        }
    });
}

$(document).ready(function () {
    $('.update-so-luong-form').on('submit', function (e) {
        e.preventDefault();

        const form = $(this);
        const idsp = form.data('idsp');
        const idhd = form.data('idhd');
        const soLuong = form.find('.quantity-input').val();
        const gia = form.data('gia');

        $.ajax({
            url: `/hoa-don/updateSoLuong`,
            type: 'POST',
            data: JSON.stringify({
                idSP: idsp,
                idHD: idhd,
                soLuong: soLuong,
                gia: gia
            }),
            contentType: 'application/json; charset=utf-8',
            success: function (response) {
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
                    title: xhr.responseText,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
            }
        });
    });
});



