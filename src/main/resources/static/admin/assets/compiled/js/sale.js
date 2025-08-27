$('#taoHoaDon').on('submit', function (e) {
    e.preventDefault(); // Ngăn form thực hiện submit mặc định

    $.ajax({
        url: '/sale/tao-hoa-don',
        type: 'POST',
        success: function (response) {
            Swal.fire({
                toast: true,
                icon: 'success',
                title: response, // Thông báo thành công từ server
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
                title: xhr.responseText, // Thông báo lỗi trả về từ controller
                position: 'top-end',
                showConfirmButton: false,
                timer: 2000,
                timerProgressBar: true
            }).then(() => {
                location.reload();
            });
        }
    });
});

$(document).ready(function () {
    $(".huyHoaDon").click(function () {
        const hoaDonId = $(this).data("id"); // Lấy ID từ data-id trên thẻ a

        Swal.fire({
            title: "Hủy hóa đơn?",
            text: "Bạn có chắc chắn muốn hủy hóa đơn này?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Xác nhận"
        }).then((result) => {
            if (result.isConfirmed) {
                const ghiChu = result.value;

                $.ajax({
                    url: "/sale/huy",
                    type: "POST",
                    data: {
                        id: hoaDonId,
                        ghiChu: ghiChu
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
                            window.location.href = "/sale/index";
                        });
                    },
                    error: function (xhr) {
                        Swal.fire("Lỗi!", xhr.responseText, "error");
                    }
                });
            }
        });
    });
});

// Thêm khách hàng
$(document).ready(function () {
    // Thêm khách hàng
    $(".btn-add-khachHang").click(function () {
        const hoaDonId = $(this).data("id-hd");
        const sanPhamId = $(this).data("id-sp");
        const ten = $(this).data("ten");
        const sdt = $(this).data("sdt");

        // Clear selected discount voucher
        selectedPhieuGiamGia = null; // Reset global variable
        clearDiscountOnUI(); // Reset UI elements
        removeDiscountFromOrder(hoaDonId); // Call backend to remove voucher

        // Proceed with adding customer
        $.ajax({
            url: '/sale/addKH',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                idHD: hoaDonId,
                idSP: sanPhamId,
                ten: ten,
                sdt: sdt
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
                    // Update current customer ID and reload voucher list
                    currentCustomerId = sanPhamId; // Assuming id-sp is the customer ID
                    loadPhieuGiamGia(); // Reload voucher list for the new customer
                    location.reload(); // Reload page to reflect changes
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
                }).then(() => {
                    location.reload();
                });
            }
        });
    });

    // Function to clear discount-related UI elements
    function clearDiscountOnUI() {
        $('#tenPhieuGiamGia').text('Chưa chọn'); // Reset voucher name
        $('#giaTriGiamGia').text('0 đ'); // Reset discount value
        $('#giaTriGiamGia').attr('data-value', '0');
        const tongTien = parseFloat($('#tienThanhToan').attr('data-value')) || 0;
        $('#tongTienSauGiam').text(formatNumberToVND(tongTien)); // Reset total after discount
        $('#tongTienSauGiam').attr('data-value', tongTien);

        // Recalculate change (tienThua) if customer has entered payment amount
        const tienKhachTra = parseFloat($('#tienKhachTra').val()) || 0;
        const tienThua = tienKhachTra - tongTien;
        $('#tienThua').text(formatNumberToVND(tienThua));
        if (tienThua < 0) {
            $('#tienThua').addClass("text-danger");
            $('#btnThanhToan').removeClass('btn-success').addClass('btn-outline-warning');
        } else {
            $('#tienThua').removeClass("text-danger");
            $('#btnThanhToan').removeClass('btn-outline-warning').addClass('btn-success');
        }
    }

    // Function to remove discount from order (call backend)
    function removeDiscountFromOrder(hoaDonId) {
        $.ajax({
            url: '/sale/remove-discount',
            type: 'POST',
            data: {idHD: hoaDonId},
            success: function (response) {
                console.log('Discount removed successfully:', response);
            },
            error: function (xhr) {
                console.error('Error removing discount:', xhr.responseText);
                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: xhr.responseText || 'Không thể xóa phiếu giảm giá',
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 2000
                });
            }
        });
    }
});
//xoa san pham khoi gio hang
$(".btn-xoa-sanPham").click(function () {
    const sanPhamId = $(this).data("id-sp");
    const hoaDonId = $(this).data("id-hd");

    $.ajax({
        url: '/sale/xoa',
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
            }).then(() => location.reload());
        }
    });
});
//update trang thai
$(document).ready(function () {
    $(".toggle-trang-thai").change(function () {
        const hoaDonId = $(this).attr("data-id-hd"); // hoặc .data("id-hd")
        const loaiHoaDon = $(this).is(":checked") ? false : true;

        $.ajax({
            url: '/sale/update-trang-thai',
            type: 'POST',
            data: {
                id: hoaDonId,
                loaiHoaDon: loaiHoaDon
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
                });
            },
            error: function (xhr) {
                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: xhr.responseText,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 2000,
                    timerProgressBar: true
                }).then(() => location.reload());
            }
        });
    });
});

//tang so luong
$(".quantity-btn").click(function () {
    const idSP = $(this).closest("form").data("idsp");  // lấy từ data-idsp
    const idHD = $(this).closest("form").data("idhd");  // lấy từ data-idhd

    $.ajax({
        url: '/sale/tangSoLuong',
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
            }).then(() => location.reload());
        }
    });
});
// giam so luong
$(".quantity-btn-1").click(function () {
    const idSP = $(this).closest("form").data("idsp");  // lấy từ data-idsp
    const idHD = $(this).closest("form").data("idhd");  // lấy từ data-idhd

    $.ajax({
        url: '/sale/giamSoLuong',
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
            }).then(() => location.reload());
        }
    });
});
// update so lượng
$(document).ready(function () {
    $('.update-so-luong-form').on('submit', function (e) {
        e.preventDefault();

        const form = $(this);
        const idsp = form.data('idsp');
        const idhd = form.data('idhd');
        const soLuong = form.find('.quantity-input').val();
        const gia = form.data('gia');

        $.ajax({
            url: `/sale/updateSoLuong`,
            type: 'POST',
            data: JSON.stringify({
                idSP: idsp,
                idHD: idhd,
                soLuong: parseInt(soLuong),
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
                let errorMessage = "Có lỗi xảy ra";

                // Thử parse JSON response nếu có
                try {
                    const response = JSON.parse(xhr.responseText);
                    errorMessage = response.message || response.error || xhr.responseText;
                } catch (e) {
                    // Nếu không parse được JSON, dùng responseText trực tiếp
                    errorMessage = xhr.responseText || "Có lỗi xảy ra khi cập nhật số lượng";
                }

                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: errorMessage,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 4000,
                    timerProgressBar: true
                }).then(() => {
                    // Reload trang để đưa số lượng về trạng thái ban đầu
                    location.reload();
                });
            }
        });
    });

    // Thêm event listener cho input số lượng khi nhấn Enter
    $(document).on('keypress', '.quantity-input', function (e) {
        if (e.which === 13) { // Enter key
            e.preventDefault();
            $(this).closest('form').submit();
        }
    });

    // Thêm event listener cho input số lượng khi blur (mất focus)
    $(document).on('blur', '.quantity-input', function () {
        const currentValue = parseInt($(this).val());
        const form = $(this).closest('form');

        // Kiểm tra giá trị hợp lệ
        if (isNaN(currentValue) || currentValue < 1) {
            $(this).val(1);
            return;
        }

        // Tự động submit form khi người dùng thay đổi giá trị
        form.submit();
    });
});

$(document).ready(function () {
    $('#btnThanhToan').click(function () {
        const idHD = $(this).data('id'); // lấy giá trị id hóa đơn từ nút

        // Lấy giá trị tổng tiền sau giảm giá
        const tongTienSauGiam = parseFloat($('#tongTienSauGiam').attr('data-value') || $('#tienThanhToan').attr('data-value')) || 0;

        // Lấy phương thức thanh toán được chọn
        const phuongThucThanhToan = $('input[name="phuongThucThanhToan"]:checked').val();

        // Nếu là thanh toán tiền mặt, kiểm tra số tiền khách trả
        if (phuongThucThanhToan === 'TIEN_MAT') {
            // Lấy số tiền khách trả (đã format có thể chứa dấu chấm)
            const tienKhachTra = (function(){
                const raw = $('#tienKhachTra').val() || '0';
                return Number((raw+'').replace(/[^0-9-]/g, '')) || 0;
            })();

            // Kiểm tra xem khách đã trả đủ tiền chưa
            if (tienKhachTra < tongTienSauGiam) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Chưa đủ tiền',
                    text: 'Số tiền khách trả chưa đủ để thanh toán!',
                    confirmButtonText: 'Đồng ý'
                });
                return;
            }
        }

        // Hỏi người dùng có muốn in hóa đơn không
        Swal.fire({
            title: 'Xác nhận thanh toán',
            text: 'Bạn có muốn in hóa đơn sau khi thanh toán không?',
            icon: 'question',
            showCancelButton: true,
            showDenyButton: true,
            confirmButtonText: 'Có, in hóa đơn',
            denyButtonText: 'Không in hóa đơn',
            cancelButtonText: 'Hủy',
            confirmButtonColor: '#28a745',
            denyButtonColor: '#6c757d',
            cancelButtonColor: '#dc3545'
        }).then((result) => {
            if (result.isConfirmed || result.isDenied) {
                // Tiếp tục thanh toán với thông tin về việc in hóa đơn
                const shouldPrint = result.isConfirmed;
                processPayment(idHD, phuongThucThanhToan, shouldPrint);
            }
        });
    });

    // Hàm xử lý thanh toán
    function processPayment(idHD, phuongThucThanhToan, shouldPrint) {

        $.ajax({
            url: '/sale/thanh-toan',
            type: 'POST',
            data: {
                idHD: idHD,
                phuongThucThanhToan: phuongThucThanhToan,
                shouldPrint: shouldPrint
            },
            success: function (response) {
                // Kiểm tra nếu phản hồi là QR code Momo
                if (response.startsWith('MOMO_QR_CODE:')) {
                    const qrCodeUrl = response.substring('MOMO_QR_CODE:'.length);
                    showMomoQRCode(qrCodeUrl, idHD);
                }
                // Kiểm tra nếu phản hồi là chuyển hướng đến trang thanh toán Momo
                else if (response.startsWith('MOMO_REDIRECT:')) {
                    const payUrl = response.substring('MOMO_REDIRECT:'.length);
                    // Hiển thị thông báo
                    Swal.fire({
                        title: 'Chuyển hướng đến Momo',
                        text: 'Bạn sẽ được chuyển đến trang thanh toán Momo...',
                        icon: 'info',
                        showCancelButton: true,
                        confirmButtonText: 'Đồng ý',
                        cancelButtonText: 'Hủy'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            // Lưu ID hóa đơn vào sessionStorage để kiểm tra khi quay lại
                            sessionStorage.setItem('pending_momo_order', idHD);

                            // Lưu URL hiện tại để quay lại sau khi thanh toán
                            sessionStorage.setItem('return_url', window.location.href);

                            // Chuyển hướng đến trang thanh toán Momo trong cùng tab thay vì mở tab mới
                            window.location.href = payUrl;
                        } else {
                            // Hủy thanh toán
                            cancelMomoPayment(idHD);
                        }
                    });
                } else {
                    // Xử lý thành công bình thường
                    let message = response;
                    let shouldPrintInvoice = false;

                    // Kiểm tra xem có yêu cầu in hóa đơn không
                    if (response.includes('PRINT_INVOICE:')) {
                        const parts = response.split('PRINT_INVOICE:');
                        message = parts[0].trim();
                        shouldPrintInvoice = true;
                    }

                    Swal.fire({
                        toast: true,
                        icon: 'success',
                        title: message,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 1000,
                        timerProgressBar: true
                    }).then(() => {
                        // Nếu người dùng chọn in hóa đơn, mở tab in hóa đơn
                        if (shouldPrintInvoice) {
                            window.open(`/hoa-don/print/${idHD}`, '_blank');
                        }
                        window.location.href = '/sale/index';
                    });
                }
            },
            error: function (xhr) {
                if (xhr.responseText === "Hóa đơn đã được xử lý bởi người khác!") {
                    // Chuyển hướng về trang '/sale/index'
                    Swal.fire({
                        toast: true,
                        icon: 'error',
                        title: xhr.responseText,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 1000,
                        timerProgressBar: true
                    }).then(() => {
                        // Reload trang để đưa về trạng thái ban đầu
                        window.location.href = '/sale/index';
                    });
                } else {
                    // Hiển thị thông báo lỗi và reload trang
                    Swal.fire({
                        toast: true,
                        icon: 'error',
                        title: xhr.responseText,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 1000,
                        timerProgressBar: true
                    }).then(() => {
                        // Reload trang để đưa về trạng thái ban đầu
                        location.reload();
                    });
                }
            }
        });
    }
});

// Thêm hàm giám sát quá trình thanh toán Momo
function monitorMomoPayment(hoaDonId) {
    Swal.fire({
        title: 'Đang chờ thanh toán',
        html: `
            <p>Bạn đã được chuyển đến trang thanh toán Momo.</p>
            <p>Vui lòng hoàn tất thanh toán trên ứng dụng Momo hoặc trình duyệt.</p>
            <div class="mt-3">
                <div class="d-flex justify-content-center">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
            </div>
        `,
        showCancelButton: true,
        showConfirmButton: false,
        cancelButtonText: 'Hủy',
        allowOutsideClick: false,
        didOpen: () => {
            // Bắt đầu kiểm tra trạng thái thanh toán
            startPaymentStatusCheck(hoaDonId);
        }
    }).then((result) => {
        if (result.dismiss === Swal.DismissReason.cancel) {
            // Người dùng bấm nút hủy
            cancelMomoPayment(hoaDonId);
        }
    });
}

// Kiểm tra trạng thái thanh toán Momo
function checkMomoPaymentStatus(hoaDonId) {
    // Gọi API để kiểm tra trạng thái thanh toán
    $.ajax({
        url: '/sale/check-momo-payment-status',
        type: 'GET',
        data: {idHD: hoaDonId},
        success: function (response) {
            console.log("Kiểm tra trạng thái thanh toán:", response);

            if (response.success) {
                // Nếu thanh toán thành công và chưa hiển thị thông báo
                if (!window.momoPaymentCompleted) {
                    window.momoPaymentCompleted = true; // Đánh dấu đã hoàn tất

                    // Xóa ID hóa đơn khỏi sessionStorage ngay lập tức
                    sessionStorage.removeItem('pending_momo_order');

                    Swal.close(); // Đóng dialog hiện tại
                    Swal.fire({
                        toast: true,
                        icon: 'success',
                        title: response,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 2000,
                        timerProgressBar: true
                    }).then(() => {
                        window.location.href = '/sale/index';
                    });
                }

                // Dừng kiểm tra trạng thái
                clearInterval(window.paymentCheckInterval);
            } else if (response.status === 3) {
                // Nếu giao dịch đã bị hủy
                Swal.close(); // Đóng dialog hiện tại
                Swal.fire({
                    toast: true,
                    icon: 'info',
                    title: 'Thanh toán đã bị hủy',
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true
                });

                // Xóa ID hóa đơn khỏi sessionStorage
                sessionStorage.removeItem('pending_momo_order');
                // Dừng kiểm tra trạng thái
                clearInterval(window.paymentCheckInterval);
            } else if (response.status === 2) {
                // Nếu giao dịch bị từ chối/lỗi
                clearInterval(window.paymentCheckInterval);
                Swal.close(); // Đóng dialog hiện tại

                // Hiển thị thông báo lỗi và hỏi người dùng có muốn thử lại không
                Swal.fire({
                    title: 'Thanh toán bị từ chối',
                    text: 'Giao dịch bị từ chối. Bạn có thể áp dụng mã giảm giá trước khi thử lại.',
                    icon: 'warning',
                    showDenyButton: true,
                    showCancelButton: true,
                    confirmButtonText: 'Thử lại thanh toán',
                    denyButtonText: 'Áp dụng mã giảm giá',
                    cancelButtonText: 'Hủy thanh toán'
                }).then((result) => {
                    if (result.isConfirmed) {
                        // Thử lại thanh toán
                        retryMomoPayment(hoaDonId);
                    } else if (result.isDenied) {
                        // Mở modal phiếu giảm giá
                        openDiscountModal(hoaDonId);
                    } else {
                        // Hủy thanh toán
                        cancelMomoPayment(hoaDonId);
                    }
                });
            }
        },
        error: function (xhr) {
            console.error('Lỗi khi kiểm tra trạng thái thanh toán:', xhr);
        }
    });
}

// Bắt đầu kiểm tra trạng thái thanh toán định kỳ
function startPaymentStatusCheck(hoaDonId) {
    // Reset flag thanh toán hoàn tất
    window.momoPaymentCompleted = false;
    
    // Xóa sessionStorage cũ để tránh báo thành công lần nữa
    sessionStorage.removeItem('pending_momo_order');
    
    // Dừng interval cũ nếu có
    if (window.paymentCheckInterval) {
        clearInterval(window.paymentCheckInterval);
    }

    // Kiểm tra ngay lập tức
    checkMomoPaymentStatus(hoaDonId);

    // Thiết lập kiểm tra định kỳ mỗi 3 giây
    window.paymentCheckInterval = setInterval(function () {
        checkMomoPaymentStatus(hoaDonId);
    }, 3000);
}

// Hàm hiển thị QR code thanh toán Momo
function showMomoQRCode(qrCodeUrl, hoaDonId) {
    Swal.fire({
        title: 'Thanh toán qua Momo',
        html: `
            <div class="text-center">
                <p>Vui lòng quét mã QR bằng ứng dụng Momo để hoàn tất thanh toán</p>
                <div class="mb-3">
                    <img src="https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(qrCodeUrl)}" 
                         alt="Momo QR Code" class="img-fluid"/>
                </div>
                <div class="bg-light p-2 mb-3 rounded">
                    <p class="mb-1"><strong>Hướng dẫn:</strong></p>
                    <p class="mb-1">1. Mở ứng dụng Momo trên điện thoại</p>
                    <p class="mb-1">2. Chọn "Quét mã QR"</p>
                    <p class="mb-1">3. Quét mã QR trên màn hình</p>
                    <p class="mb-1">4. Xác nhận thanh toán trên ứng dụng</p>
                </div>
                <div class="small text-muted">Mã đơn hàng: ${qrCodeUrl.split('id=')[1].split('&')[0]}</div>
            </div>
        `,
        showCancelButton: true,
        showConfirmButton: false,
        cancelButtonText: 'Hủy',
        allowOutsideClick: false,
        didOpen: () => {
            // Bắt đầu kiểm tra trạng thái thanh toán
            startPaymentStatusCheck(hoaDonId);
        }
    }).then((result) => {
        if (result.dismiss === Swal.DismissReason.cancel) {
            // Người dùng bấm nút hủy
            cancelMomoPayment(hoaDonId);
        }
        // Dừng kiểm tra trạng thái khi dialog đóng
        clearInterval(window.paymentCheckInterval);
    });
}

// Xác nhận thanh toán Momo đã hoàn tất
function confirmMomoPayment(hoaDonId) {
    $.ajax({
        url: '/sale/confirm-momo-payment',
        type: 'POST',
        data: {idHD: hoaDonId},
        success: function (response) {
            let message = response;
            let shouldPrintInvoice = false;

            // Kiểm tra xem có yêu cầu in hóa đơn không
            if (response.includes('PRINT_INVOICE:')) {
                const parts = response.split('PRINT_INVOICE:');
                message = parts[0].trim();
                shouldPrintInvoice = true;
            }

            Swal.fire({
                toast: true,
                icon: 'success',
                title: message,
                position: 'top-end',
                showConfirmButton: false,
                timer: 2000,
                timerProgressBar: true
            }).then(() => {
                // Nếu người dùng chọn in hóa đơn, mở tab in hóa đơn
                if (shouldPrintInvoice) {
                    window.open(`/hoa-don/print/${hoaDonId}`, '_blank');
                }
                window.location.href = '/sale/index';
            });
        },
        error: function (xhr) {
            Swal.fire({
                icon: 'error',
                title: 'Lỗi xác nhận thanh toán',
                text: xhr.responseText || 'Không thể xác nhận thanh toán. Vui lòng thử lại.',
                confirmButtonText: 'Đồng ý'
            });
        }
    });
}

// Hủy thanh toán Momo
function cancelMomoPayment(hoaDonId) {
    $.ajax({
        url: '/sale/cancel-momo-payment',
        type: 'POST',
        data: {idHD: hoaDonId},
        success: function (response) {
            Swal.fire({
                toast: true,
                icon: 'info',
                title: 'Đã hủy thanh toán Momo',
                position: 'top-end',
                showConfirmButton: false,
                timer: 1000,
                timerProgressBar: true
            });
        },
        error: function (xhr) {
            console.error('Lỗi khi hủy thanh toán:', xhr);
        }
    });
}

// Hàm thử lại thanh toán Momo
function retryMomoPayment(hoaDonId) {
    $.ajax({
        url: '/sale/thanh-toan',
        type: 'POST',
        data: {
            idHD: hoaDonId,
            phuongThucThanhToan: 'MOMO'
        },
        success: function (response) {
            // Kiểm tra nếu phản hồi là QR code Momo
            if (response.startsWith('MOMO_QR_CODE:')) {
                const qrCodeUrl = response.substring('MOMO_QR_CODE:'.length);
                showMomoQRCode(qrCodeUrl, hoaDonId);
            }
            // Kiểm tra nếu phản hồi là chuyển hướng đến trang thanh toán Momo
            else if (response.startsWith('MOMO_REDIRECT:')) {
                const payUrl = response.substring('MOMO_REDIRECT:'.length);
                // Hiển thị thông báo
                Swal.fire({
                    title: 'Chuyển hướng đến Momo',
                    text: 'Bạn sẽ được chuyển đến trang thanh toán Momo...',
                    icon: 'info',
                    showCancelButton: true,
                    confirmButtonText: 'Đồng ý',
                    cancelButtonText: 'Hủy'
                }).then((result) => {
                    if (result.isConfirmed) {
                        // Lưu ID hóa đơn vào sessionStorage để kiểm tra khi quay lại
                        sessionStorage.setItem('pending_momo_order', hoaDonId);

                        // Mở cửa sổ thanh toán Momo
                        window.open(payUrl, '_blank');

                        // Hiển thị dialog kiểm tra thanh toán
                        monitorMomoPayment(hoaDonId);
                    } else {
                        // Hủy thanh toán
                        cancelMomoPayment(hoaDonId);
                    }
                });
            } else {
                // Xử lý thành công bình thường
                Swal.fire({
                    toast: true,
                    icon: 'success',
                    title: response,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true
                }).then(() => {
                    window.location.href = '/sale/index';
                });
            }
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
            }).then(() => {
                // Reload trang để đưa về trạng thái ban đầu
                location.reload();
            });
        }
    });
}

//tính tiền thừa
document.addEventListener("DOMContentLoaded", function () {
    const inputTienKhachTra = document.getElementById('tienKhachTra');
    const pTienThanhToan = document.getElementById('tienThanhToan');
    const pTienThua = document.getElementById('tienThua');
    const btnThanhToan = document.getElementById('btnThanhToan');

    // Định dạng tiền tệ cho tất cả các giá trị tiền hàng khi trang tải
    formatCurrency();

    // Nếu input đã có giá trị số, format sẵn khi load
    if (inputTienKhachTra && inputTienKhachTra.value) {
        inputTienKhachTra.value = (Number((inputTienKhachTra.value+'').replace(/[^0-9]/g, '')) || 0).toLocaleString('vi-VN');
    }

    if (!pTienThanhToan) {
        console.error("Không tìm thấy phần tử 'tienThanhToan'");
        return;
    }

    // Mặc định nút thanh toán có màu xám
    if (btnThanhToan) {
        btnThanhToan.classList.remove('btn-success');
        btnThanhToan.classList.add('btn-outline-warning');
    }

    // Tiện ích: chuẩn hóa chuỗi số VND -> number (loại bỏ . , và ký tự khác)
    function parseVNDToNumber(str) {
        if (typeof str !== 'string') return Number(str) || 0;
        // Loại bỏ ký tự không phải số
        const digits = str.replace(/[^0-9-]/g, '');
        return Number(digits) || 0;
    }

    // Định dạng input theo VND realtime khi gõ
    function formatInputAsVND(el) {
        const caretPos = el.selectionStart;
        const raw = el.value;
        const numeric = raw.replace(/[^0-9]/g, '');
        if (numeric.length === 0) {
            el.value = '';
            return;
        }
        const number = Number(numeric);
        el.value = number.toLocaleString('vi-VN');
        // Cố gắng giữ vị trí con trỏ gần cuối
        try { el.setSelectionRange(el.value.length, el.value.length); } catch (e) {}
    }

    inputTienKhachTra.addEventListener('input', function () {
        // Format realtime
        formatInputAsVND(inputTienKhachTra);

        const tienKhachTra = parseVNDToNumber(inputTienKhachTra.value);
        const tongTienSauGiam = parseFloat($('#tongTienSauGiam').attr('data-value') || $('#tienThanhToan').attr('data-value')) || 0;
        const tienThua = tienKhachTra - tongTienSauGiam;

        // Format tiền thừa (có thể âm)
        const tienThuaFormatted = formatNumberToVND(tienThua);
        pTienThua.innerText = tienThuaFormatted;

        // Đổi màu nếu âm
        if (tienThua < 0) {
            pTienThua.classList.add("text-danger");

            // Hiển thị thông báo nhỏ nếu tiền không đủ
            if (!document.getElementById('warningMessage')) {
                const warningMsg = document.createElement('small');
                warningMsg.id = 'warningMessage';
                warningMsg.className = 'text-danger d-block mt-1';
                warningMsg.innerText = 'Số tiền khách trả chưa đủ';
                pTienThua.parentNode.appendChild(warningMsg);
            }

            // Nút thanh toán có màu xám
            if (btnThanhToan) {
                btnThanhToan.classList.remove('btn-success');
                btnThanhToan.classList.add('btn-outline-warning');
            }
        } else {
            pTienThua.classList.remove("text-danger");

            // Xóa thông báo cảnh báo nếu có
            const warningMsg = document.getElementById('warningMessage');
            if (warningMsg) {
                warningMsg.remove();
            }

            // Nút thanh toán có màu xanh nếu đủ tiền
            if (btnThanhToan) {
                btnThanhToan.classList.remove('btn-outline-warning');
                btnThanhToan.classList.add('btn-success');
            }
        }
    });
});

// Hàm định dạng số thành VND
function formatNumberToVND(number) {
    return number.toLocaleString('vi-VN') + ' đ';
}

// Hàm định dạng tất cả các giá trị tiền tệ trên trang
function formatCurrency() {
    console.log("Formatting currency values...");

    // Định dạng tiền hàng
    const tienThanhToan = document.getElementById('tienThanhToan');
    if (tienThanhToan) {
        const value = parseFloat(tienThanhToan.getAttribute('data-value')) || 0;
        tienThanhToan.textContent = formatNumberToVND(value);
        console.log("Tiền thanh toán:", value, formatNumberToVND(value));
    }

    // Định dạng tổng tiền sau giảm
    const tongTienSauGiam = document.getElementById('tongTienSauGiam');
    if (tongTienSauGiam) {
        const tongTien = parseFloat(document.getElementById('tienThanhToan')?.getAttribute('data-value')) || 0;
        const giamGia = parseFloat(document.getElementById('giaTriGiamGia')?.getAttribute('data-value')) || 0;
        const value = tongTien - giamGia;
        tongTienSauGiam.textContent = formatNumberToVND(value);
        tongTienSauGiam.setAttribute('data-value', value);
        console.log("Tổng tiền sau giảm:", value, formatNumberToVND(value));
    }
}

// Thêm code xử lý cho nút thêm sản phẩm
$(document).ready(function () {
    console.log("Document ready - checking btn-add-sanPham elements");
    const addButtons = $('.btn-add-sanPham');
    console.log("Found " + addButtons.length + " add product buttons");

    // Xóa tất cả event handlers cũ để tránh trùng lặp
    $('.btn-add-sanPham').off('click');

    // Thêm event listener mới
    $('.btn-add-sanPham').on('click', function (e) {
        e.preventDefault();
        e.stopPropagation();

        console.log("Add product button clicked");
        const hoaDonId = $(this).data("id-hd");
        const sanPhamId = $(this).data("id-sp");
        const gia = $(this).data("gia");

        // Vô hiệu hóa nút để tránh nhấn nhiều lần
        const button = $(this);
        button.prop('disabled', true);

        $.ajax({
            url: '/sale/addSP',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                idHD: hoaDonId,
                idSP: sanPhamId,
                gia: gia,
                soLuong: 1
            }),
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
                console.error("Error: ", xhr);
                // Kích hoạt lại nút trong trường hợp lỗi
                button.prop('disabled', false);

                Swal.fire({
                    toast: true,
                    icon: 'error',
                    title: xhr.responseText,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true
                }).then(() => {
                    // Reload trang để đưa về trạng thái ban đầu
                    location.reload();
                });
            }
        });
    });

    // Chạy format currency khi trang tải xong
    formatCurrency();

    // Kiểm tra và disable nút thanh toán khi load trang
    checkAndTogglePaymentButton();
});


// Hàm kiểm tra và toggle nút thanh toán
function checkAndTogglePaymentButton() {
    const productList = document.getElementById('product-list');
    const paymentButton = document.getElementById('btnThanhToan');

    if (!paymentButton) {
        return; // Không có nút thanh toán (chưa tạo hóa đơn)
    }

    // Kiểm tra xem có sản phẩm nào trong giỏ hàng không
    let hasProducts = false;

    if (productList) {
        // Đếm số dòng sản phẩm (div có class product-details)
        const productItems = productList.querySelectorAll('.product-details');
        hasProducts = productItems.length > 0;
    }

    // Enable/disable nút thanh toán
    if (hasProducts) {
        paymentButton.disabled = false;
        paymentButton.classList.remove('disabled');
        paymentButton.title = '';
    } else {
        paymentButton.disabled = true;
        paymentButton.classList.add('disabled');
        paymentButton.title = 'Vui lòng thêm sản phẩm trước khi thanh toán';
    }
}

// Khởi tạo biến cho phiếu giảm giá
let selectedPhieuGiamGia = null;
let currentCustomerId = null; // Biến lưu ID khách hàng hiện tại

// Tải danh sách phiếu giảm giá khi mở modal
$(document).on('show.bs.modal', '#modalPhieuGiamGia', function () {
    // Lấy thông tin khách hàng hiện tại từ form
    getCurrentCustomerInfo();
    loadPhieuGiamGia();
});

// Xử lý khi modal bị đóng để đảm bảo không có vấn đề với backdrop
$(document).on('hidden.bs.modal', '#modalPhieuGiamGia', function () {
    // Đảm bảo backdrop được xóa
    $('.modal-backdrop').remove();
    $('body').removeClass('modal-open').css('overflow', '');
    $('body').css('padding-right', '');
});

// Hàm lấy thông tin khách hàng hiện tại
function getCurrentCustomerInfo() {
    const hoaDon = window.hoaDon || {};
    const tenKhachHang = $('#name').val() || $('#tenHienThi').text() || 'Khách Lẻ';

    // Cập nhật hiển thị thông tin khách hàng trong modal
    if (tenKhachHang && tenKhachHang !== 'Khách Lẻ') {
        $('#customerInfo').show();
        $('#currentCustomerName').text(tenKhachHang);

        // Lấy ID khách hàng từ hóa đơn hoặc từ form hidden nếu có
        if (hoaDon.khachHang && hoaDon.khachHang.id) {
            currentCustomerId = hoaDon.khachHang.id;
        } else {
            // Tìm ID khách hàng từ danh sách khách hàng đã load trước đó
            const khachHangList = window.khachHangList || [];
            const foundCustomer = khachHangList.find(kh => kh.ten === tenKhachHang);
            currentCustomerId = foundCustomer ? foundCustomer.id : null;
        }
    } else {
        $('#customerInfo').hide();
        $('#currentCustomerName').text('Khách Lẻ');
        currentCustomerId = null;
    }

    console.log('Current customer ID:', currentCustomerId);
}

// Hàm tải danh sách phiếu giảm giá từ API
function loadPhieuGiamGia() {
    // Lấy tổng tiền hóa đơn từ UI
    const tongTienDonHang = parseFloat($('#tongTienSauGiam').attr('data-value') || $('#tienThanhToan').attr('data-value')) || 0;
    const requestData = currentCustomerId ? {
        idKH: currentCustomerId,
        tongTienDonHang: tongTienDonHang
    } : {tongTienDonHang: tongTienDonHang};
    console.log('Requesting PGG with data:', requestData);

    $.ajax({
        url: '/sale/api/phieu-giam-gia/active',
        type: 'GET',
        data: requestData,
        success: function (data) {
            console.log('Received PGG data:', data);
            renderPhieuGiamGia(data);
        },
        error: function (xhr) {
            console.error('Error loading PGG:', xhr.responseText);
            Swal.fire({
                toast: true,
                icon: 'error',
                title: 'Không thể tải danh sách phiếu giảm giá',
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000
            });
        }
    });
}

// Hàm hiển thị danh sách phiếu giảm giá
function renderPhieuGiamGia(data) {
    const tbody = $('#phieuGiamGiaList');
    tbody.empty();

    if (data && data.length > 0) {
        data.forEach(function (item, index) {
            const row = $('<tr>');
            row.append($('<td>').text(index + 1));
            row.append($('<td>').text(item.ma || ''));
            row.append($('<td>').text(item.ten || ''));

            const giaTriGiam = item.loaiGiamGia ? item.giaTriGiam + '%' : formatNumberToVND(item.giaTriGiam);
            row.append($('<td>').text(giaTriGiam));

            const loaiGiam = item.loaiGiamGia ? 'Phần trăm' : 'Tiền mặt';
            row.append($('<td>').text(loaiGiam));

            // Thêm cột giá trị đơn hàng tối thiểu
            const giaTriDonHangToiThieu = item.giaTriDonHangToiThieu ?
                formatNumberToVND(item.giaTriDonHangToiThieu) : 'Không có';
            row.append($('<td>').text(giaTriDonHangToiThieu));

            // Thêm cột giá trị giảm tối đa
            const giaTriGiamToiDa = item.giaTriGiamToiDa ?
                formatNumberToVND(item.giaTriGiamToiDa) : 'Không có';
            row.append($('<td>').text(giaTriGiamToiDa));

            // Ngày kết thúc
            const ngayKetThuc = item.ngayKetThuc ?
                new Date(item.ngayKetThuc).toLocaleDateString('vi-VN') : 'N/A';
            row.append($('<td>').text(ngayKetThuc));

            const loaiPhieuCell = $('<td>');
            const loaiPhieuText = item.isPersonal ? 'Cá nhân' : 'Công khai';
            loaiPhieuCell.html(`<span class="badge ${item.isPersonal ? 'bg-primary' : 'bg-success'}">${loaiPhieuText}</span>`);
            row.append(loaiPhieuCell);

            // Tạo nút áp dụng và kiểm tra điều kiện hợp lệ
            const btnApDung = $('<button>')
                .addClass('btn btn-outline-success btn-sm')
                .text('Áp dụng')
                .attr('data-id', item.id)
                .attr('data-loai', item.loaiGiamGia)
                .attr('data-giatri', item.giaTriGiam)
                .attr('data-giatri-giam-toi-da', item.giaTriGiamToiDa || '')
                .attr('data-ten', item.ten)
                .attr('data-is-personal', item.isPersonal || false)
                .attr('data-pggkh-id', item.pggkhId || '');

            // Vô hiệu hóa nút nếu không đủ điều kiện
            if (!item.isEligible) {
                btnApDung
                    .prop('disabled', true)
                    .addClass('btn-outline-secondary')
                    .removeClass('btn-outline-success')
                    .attr('title', 'Đơn hàng chưa đạt giá trị tối thiểu để áp dụng phiếu này');
            }

            row.append($('<td>').append(btnApDung));
            tbody.append(row);
        });
    } else {
        const noDataMessage = currentCustomerId ?
            'Không có phiếu giảm giá nào cho khách hàng này' :
            'Không có phiếu giảm giá nào';
        tbody.append(`<tr><td colspan="10" class="text-center">${noDataMessage}</td></tr>`);
    }
}

// Sự kiện khi click vào nút áp dụng phiếu giảm giá
$(document).on('click', '#phieuGiamGiaList button', function () {
    const idPGG = $(this).data('id');
    const loaiGiamGia = $(this).data('loai'); // true: phần trăm, false: tiền mặt
    const giaTriGiam = $(this).data('giatri');
    const tenPGG = $(this).data('ten');
    const isPersonal = $(this).data('is-personal');
    const pggkhId = $(this).data('pggkh-id');
    const idHD = $('#btnThanhToan').data('id');

    // Lưu thông tin phiếu giảm giá đã chọn
    selectedPhieuGiamGia = {
        id: idPGG,
        loaiGiamGia: loaiGiamGia,
        giaTriGiam: giaTriGiam,
        ten: tenPGG,
        isPersonal: isPersonal,
        pggkhId: pggkhId
    };

    // Áp dụng giảm giá vào hóa đơn
    applyDiscount(idHD, idPGG, isPersonal, pggkhId);

    // Đóng modal đúng cách
    const modalElement = document.getElementById('modalPhieuGiamGia');
    const modal = bootstrap.Modal.getInstance(modalElement);
    if (modal) {
        modal.hide();
    }

    // Xóa backdrop nếu còn
    $('.modal-backdrop').remove();
    $('body').removeClass('modal-open').css('overflow', '');
    $('body').css('padding-right', '');
});

// Áp dụng phiếu giảm giá vào hóa đơn
function applyDiscount(idHD, idPGG, isPersonal = false, pggkhId = null) {
    const requestData = {
        idHD: idHD,
        idPGG: idPGG,
        isPersonal: isPersonal
    };

    if (isPersonal && pggkhId) {
        requestData.pggkhId = pggkhId;
    }

    $.ajax({
        url: '/sale/apply-discount',
        type: 'POST',
        data: requestData,
        success: function (response) {
            Swal.fire({
                toast: true,
                icon: 'success',
                title: 'Áp dụng phiếu giảm giá thành công',
                position: 'top-end',
                showConfirmButton: false,
                timer: 1500
            });

            // Lấy lại dữ liệu hóa đơn từ server để cập nhật UI
            $.ajax({
                url: '/sale/api/get-hoa-don',
                type: 'GET',
                data: {idHD: idHD},
                success: function (hoaDonData) {
                    if (hoaDonData) {
                        const tongTienSauGiamGia = hoaDonData.tongTienSauGiamGia || hoaDonData.tongTien || 0;
                        $('#tongTienSauGiam').text(formatNumberToVND(tongTienSauGiamGia));
                        $('#tongTienSauGiam').attr('data-value', tongTienSauGiamGia);

                        const giaTriGiamGia = hoaDonData.giaTriGiamGia || 0;
                        $('#giaTriGiamGia').text(formatNumberToVND(giaTriGiamGia));
                        $('#giaTriGiamGia').attr('data-value', giaTriGiamGia);

                        $('#tenPhieuGiamGia').text(hoaDonData.phieuGiamGia?.ten || 'Chưa chọn');
                    }
                },
                error: function (xhr) {
                    console.error('Lỗi khi lấy dữ liệu hóa đơn:', xhr);
                }
            });

            // Reload trang sau khi cập nhật UI
            setTimeout(() => {
                location.reload();
            }, 1500);
        },
        error: function (xhr) {
            console.error("Lỗi khi áp dụng phiếu giảm giá:", xhr);
            const modalElement = document.getElementById('modalPhieuGiamGia');
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) modal.hide();
            $('.modal-backdrop').remove();
            $('body').removeClass('modal-open').css('overflow', '').css('padding-right', '');

            Swal.fire({
                toast: true,
                icon: 'error',
                title: xhr.responseText || 'Lỗi khi áp dụng phiếu giảm giá',
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000
            });
            selectedPhieuGiamGia = null;
        }
    });
}

// Cập nhật hiển thị giảm giá và tổng tiền
function updateDiscountDisplay() {
    if (!selectedPhieuGiamGia) return;

    const tongTien = parseFloat($('#tienThanhToan').attr('data-value')) || 0;
    let giaTriGiamGia = 0;

    // Tính giá trị giảm
    if (selectedPhieuGiamGia.loaiGiamGia) {
        // Loại phần trăm
        giaTriGiamGia = (tongTien * selectedPhieuGiamGia.giaTriGiam) / 100;

        // Kiểm tra giá trị giảm tối đa nếu có
        if (selectedPhieuGiamGia.giaTriGiamToiDa &&
            giaTriGiamGia > selectedPhieuGiamGia.giaTriGiamToiDa) {
            giaTriGiamGia = selectedPhieuGiamGia.giaTriGiamToiDa;
        }
    } else {
        // Loại tiền mặt
        giaTriGiamGia = parseFloat(selectedPhieuGiamGia.giaTriGiam);
    }

    // Đảm bảo giá trị giảm không lớn hơn tổng tiền
    giaTriGiamGia = Math.min(giaTriGiamGia, tongTien);

    // Cập nhật UI hiển thị giá trị giảm
    $('#giaTriGiamGia').text(formatNumberToVND(giaTriGiamGia));
    $('#giaTriGiamGia').attr('data-value', giaTriGiamGia);

    // Tính tổng tiền sau giảm và cập nhật hiển thị
    const tongTienSauGiam = tongTien - giaTriGiamGia;
    $('#tongTienSauGiam').text(formatNumberToVND(tongTienSauGiam));
    $('#tongTienSauGiam').attr('data-value', tongTienSauGiam);

    // Cập nhật lại tiền thừa nếu người dùng đã nhập tiền khách trả
    const tienKhachTra = parseFloat($('#tienKhachTra').val()) || 0;
    if (tienKhachTra > 0) {
        const tienThua = tienKhachTra - tongTienSauGiam;
        $('#tienThua').text(formatNumberToVND(tienThua));

        // Cập nhật màu sắc cho tiền thừa
        if (tienThua < 0) {
            $('#tienThua').addClass("text-danger");
            // Cập nhật trạng thái nút thanh toán
            $('#btnThanhToan').removeClass('btn-success').addClass('btn-outline-warning');
        } else {
            $('#tienThua').removeClass("text-danger");
            $('#btnThanhToan').removeClass('btn-outline-warning').addClass('btn-success');
        }
    }

    console.log("Cập nhật UI - Tổng tiền:", tongTien, "Giảm giá:", giaTriGiamGia, "Tổng sau giảm:", tongTienSauGiam);
}

// Sự kiện khi khách hàng được chọn - cập nhật danh sách phiếu giảm giá
$(document).on('click', '#phieuGiamGiaList button', function () {
    const idPGG = $(this).data('id');
    const loaiGiamGia = $(this).data('loai'); // true: phần trăm, false: tiền mặt
    const giaTriGiam = $(this).data('giatri');
    const tenPGG = $(this).data('ten');
    const isPersonal = $(this).data('is-personal');
    const pggkhId = $(this).data('pggkh-id');
    const idHD = $('#btnThanhToan').data('id');

    // Lấy thêm thông tin giá trị giảm tối đa từ data attribute
    const giaTriGiamToiDa = $(this).data('giatri-giam-toi-da') || null;

    // Lưu thông tin phiếu giảm giá đã chọn
    selectedPhieuGiamGia = {
        id: idPGG,
        loaiGiamGia: loaiGiamGia,
        giaTriGiam: giaTriGiam,
        giaTriGiamToiDa: giaTriGiamToiDa, // Thêm thông tin này
        ten: tenPGG,
        isPersonal: isPersonal,
        pggkhId: pggkhId
    };

    // Áp dụng giảm giá vào hóa đơn
    applyDiscount(idHD, idPGG, isPersonal, pggkhId);

    // Đóng modal đúng cách
    const modalElement = document.getElementById('modalPhieuGiamGia');
    const modal = bootstrap.Modal.getInstance(modalElement);
    if (modal) {
        modal.hide();
    }

    // Xóa backdrop nếu còn
    $('.modal-backdrop').remove();
    $('body').removeClass('modal-open').css('overflow', '');
    $('body').css('padding-right', '');
});

// Hàm tìm kiếm phiếu giảm giá
$(document).on('input', '#searchPGG', function () {
    const searchTerm = $(this).val().toLowerCase();

    $('#phieuGiamGiaList tr').each(function () {
        const row = $(this);
        const ma = row.find('td:eq(1)').text().toLowerCase();
        const ten = row.find('td:eq(2)').text().toLowerCase();

        if (ma.includes(searchTerm) || ten.includes(searchTerm)) {
            row.show();
        } else {
            row.hide();
        }
    });
});

// Sự kiện cho nút tìm kiếm phiếu giảm giá
$(document).on('click', '#searchButtonPGG', function () {
    const searchTerm = $('#searchPGG').val();
    if (searchTerm.trim()) {
        // Thực hiện tìm kiếm
        $('#searchPGG').trigger('input');
    } else {
        // Hiển thị tất cả nếu không có từ khóa
        $('#phieuGiamGiaList tr').show();
    }
});

// Hàm mở modal phiếu giảm giá
function openDiscountModal(hoaDonId) {
    // Lưu ID hóa đơn để sử dụng trong modal
    window.currentHoaDonId = hoaDonId;

    // Lấy thông tin khách hàng hiện tại
    getCurrentCustomerInfo();

    // Tải danh sách phiếu giảm giá
    loadPhieuGiamGia();

    // Mở modal phiếu giảm giá
    const modalElement = document.getElementById('modalPhieuGiamGia');
    if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();

        // Thêm event listener cho sự kiện đóng modal
        modalElement.addEventListener('hidden.bs.modal', function () {
            // Khi modal đóng, hiển thị dialog hỏi có muốn thử lại thanh toán không
            Swal.fire({
                title: 'Thử lại thanh toán?',
                text: 'Bạn có muốn thử lại thanh toán với Momo không?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: 'Thử lại',
                cancelButtonText: 'Hủy'
            }).then((result) => {
                if (result.isConfirmed) {
                    // Thử lại thanh toán
                    retryMomoPayment(hoaDonId);
                } else {
                    // Hủy thanh toán
                    cancelMomoPayment(hoaDonId);
                }
            });

            // Xóa event listener sau khi đã xử lý
            modalElement.removeEventListener('hidden.bs.modal', arguments.callee);
        }, {once: true});
    } else {
        console.error("Không tìm thấy modal phiếu giảm giá");
        // Nếu không tìm thấy modal, hiển thị dialog thử lại ngay
        Swal.fire({
            title: 'Thử lại thanh toán?',
            text: 'Không thể mở modal phiếu giảm giá. Bạn có muốn thử lại thanh toán với Momo không?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Thử lại',
            cancelButtonText: 'Hủy'
        }).then((result) => {
            if (result.isConfirmed) {
                // Thử lại thanh toán
                retryMomoPayment(hoaDonId);
            } else {
                // Hủy thanh toán
                cancelMomoPayment(hoaDonId);
            }
        });
    }
}

// Khởi tạo khi trang load
$(document).ready(function () {
    // Định dạng tiền tệ ban đầu
    formatCurrency();

    // Khởi tạo tổng tiền sau giảm
    const tongTienSauGiam = parseFloat($('#tongTienSauGiam').attr('data-value')) || 0;
    $('#tongTienSauGiam').text(formatNumberToVND(tongTienSauGiam));
    $('#tongTienSauGiam').attr('data-value', tongTienSauGiam);

    console.log("Đã khởi tạo định dạng tiền tệ cho trang");

    // Kiểm tra thông báo thanh toán từ flash attribute
    checkPaymentStatus();

    // Lưu danh sách khách hàng vào window object để sử dụng sau
    const khachHangElements = $('.btn-add-khachHang');
    window.khachHangList = [];
    khachHangElements.each(function () {
        const kh = {
            id: $(this).data('id-sp'),
            ten: $(this).data('ten'),
            sdt: $(this).data('sdt')
        };
        window.khachHangList.push(kh);
    });
});

// Chức năng chọn địa chỉ từ danh sách tỉnh thành Việt Nam
document.addEventListener("DOMContentLoaded", function () {
    let citis = document.getElementById("city");
    let districts = document.getElementById("district");
    let wards = document.getElementById("ward");

    // Đảm bảo các phần tử tồn tại trước khi thực hiện
    if (citis && districts && wards) {
        axios.get("https://raw.githubusercontent.com/kenzouno1/DiaGioiHanhChinhVN/master/data.json")
            .then(function (response) {
                let data = response.data;
                renderCity(data);
            })
            .catch(function (error) {
                console.error("Lỗi tải dữ liệu tỉnh thành:", error);
            });

        function renderCity(data) {
            for (const x of data) {
                citis.options[citis.options.length] = new Option(x.Name, x.Id);
            }

            citis.onchange = function () {
                districts.length = 1;
                wards.length = 1;

                let selectedCity = data.find(n => n.Id === citis.value);
                if (selectedCity) {
                    for (const k of selectedCity.Districts) {
                        districts.options[districts.options.length] = new Option(k.Name, k.Id);
                    }
                }
            };

            districts.onchange = function () {
                wards.length = 1;

                let selectedCity = data.find(n => n.Id === citis.value);
                if (selectedCity) {
                    let selectedDistrict = selectedCity.Districts.find(n => n.Id === districts.value);
                    if (selectedDistrict) {
                        for (const w of selectedDistrict.Wards) {
                            wards.options[wards.options.length] = new Option(w.Name, w.Id);
                        }
                    }
                }
            };
        }
    }
});

// Tải lại dữ liệu sản phẩm khi mở modal
// $(document).ready(function () {
//     // Xử lý khi modal sản phẩm được mở
//     $('#modalThemSanPham').on('show.bs.modal', function (e) {
//         // Lấy ID hóa đơn hiện tại
//         const idHD = $('#btnThanhToan').data('id');
//
//         // Sử dụng AJAX để tải dữ liệu sản phẩm mới nhất
//         $.ajax({
//             url: '/sale/api/san-pham',
//             type: 'GET',
//             data: {idHD: idHD},
//             success: function (data) {
//                 // Xóa dữ liệu cũ trong bảng
//                 const tableBody = $('#modalThemSanPham table tbody');
//                 tableBody.empty();
//
//                 // Thêm dữ liệu mới vào bảng
//                 data.forEach(function (sp, index) {
//                     // Kiểm tra dữ liệu trước khi truy cập
//                     const tenSanPham = sp.sanPham && sp.sanPham.ten ? sp.sanPham.ten : '';
//                     const tenMauSac = sp.mauSac && sp.mauSac.ten ? sp.mauSac.ten : '';
//                     const tenKichThuoc = sp.kichThuoc && sp.kichThuoc.ten ? sp.kichThuoc.ten : '';
//                     const soLuong = sp.soLuong || 0;
//                     const gia = sp.gia || 0;
//
//                     let row = `
//                         <tr>
//                             <td>${index + 1}</td>
//                             <td>${tenSanPham}</td>
//                             <td>${formatNumberToVND(gia)}</td>
//                             <td>${tenMauSac}</td>
//                             <td>${tenKichThuoc}</td>
//                             <td>${soLuong}</td>
//                             <td>
//                                 <button
//                                     type="button"
//                                     class="btn btn-outline-success btn-add-sanPham"
//                                     data-id-hd="${idHD}"
//                                     data-id-sp="${sp.id}"
//                                     data-gia="${gia}">
//                                     Thêm
//                                 </button>
//                             </td>
//                         </tr>
//                     `;
//                     tableBody.append(row);
//                 });
//
//                 // Khởi tạo lại sự kiện cho các nút thêm sản phẩm
//                 initAddProductButtons();
//             },
//             error: function (xhr) {
//                 console.error('Lỗi khi tải dữ liệu sản phẩm:', xhr);
//                 Swal.fire({
//                     toast: true,
//                     icon: 'error',
//                     title: 'Không thể tải danh sách sản phẩm',
//                     position: 'top-end',
//                     showConfirmButton: false,
//                     timer: 2000
//                 });
//             }
//         });
//     });
// });

// Hàm khởi tạo sự kiện cho các nút thêm sản phẩm
// function initAddProductButtons() {
//     // Xóa tất cả event handlers cũ để tránh trùng lặp
//     $('.btn-add-sanPham').off('click');
//
//     // Thêm event listener mới
//     $('.btn-add-sanPham').on('click', function (e) {
//         e.preventDefault();
//         e.stopPropagation();
//
//         console.log("Add product button clicked");
//         const hoaDonId = $(this).data("id-hd");
//         const sanPhamId = $(this).data("id-sp");
//         const gia = $(this).data("gia");
//         // Vô hiệu hóa nút để tránh nhấn nhiều lần
//         const button = $(this);
//         button.prop('disabled', true);
//
//         $.ajax({
//             url: '/sale/addSP',
//             type: 'POST',
//             contentType: 'application/json',
//             data: JSON.stringify({
//                 idHD: hoaDonId,
//                 idSP: sanPhamId,
//                 gia: gia,
//                 soLuong: 1
//             }),
//             success: function (response) {
//                 Swal.fire({
//                     toast: true,
//                     icon: 'success',
//                     title: response,
//                     position: 'top-end',
//                     showConfirmButton: false,
//                     timer: 1000,
//                     timerProgressBar: true
//                 }).then(() => {
//                     location.reload();
//                 });
//             },
//             error: function (xhr) {
//                 console.error("Error: ", xhr);
//                 // Kích hoạt lại nút trong trường hợp lỗi
//                 button.prop('disabled', false);
//
//                 Swal.fire({
//                     toast: true,
//                     icon: 'error',
//                     title: xhr.responseText,
//                     position: 'top-end',
//                     showConfirmButton: false,
//                     timer: 1000,
//                     timerProgressBar: true
//                 });
//             }
//         });
//     });
// }

// Hàm mở modal phiếu giảm giá
function openDiscountModal(hoaDonId) {
    // Lưu ID hóa đơn để sử dụng trong modal
    window.currentHoaDonId = hoaDonId;

    // Tải danh sách phiếu giảm giá
    loadPhieuGiamGia();

    // Mở modal phiếu giảm giá
    const modalElement = document.getElementById('modalPhieuGiamGia');
    if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();

        // Thêm event listener cho sự kiện đóng modal
        modalElement.addEventListener('hidden.bs.modal', function () {
            // Khi modal đóng, hiển thị dialog hỏi có muốn thử lại thanh toán không
            Swal.fire({
                title: 'Thử lại thanh toán?',
                text: 'Bạn có muốn thử lại thanh toán với Momo không?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: 'Thử lại',
                cancelButtonText: 'Hủy'
            }).then((result) => {
                if (result.isConfirmed) {
                    // Thử lại thanh toán
                    retryMomoPayment(hoaDonId);
                } else {
                    // Hủy thanh toán
                    cancelMomoPayment(hoaDonId);
                }
            });

            // Xóa event listener sau khi đã xử lý
            modalElement.removeEventListener('hidden.bs.modal', arguments.callee);
        }, {once: true});
    } else {
        console.error("Không tìm thấy modal phiếu giảm giá");
        // Nếu không tìm thấy modal, hiển thị dialog thử lại ngay
        Swal.fire({
            title: 'Thử lại thanh toán?',
            text: 'Không thể mở modal phiếu giảm giá. Bạn có muốn thử lại thanh toán với Momo không?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Thử lại',
            cancelButtonText: 'Hủy'
        }).then((result) => {
            if (result.isConfirmed) {
                // Thử lại thanh toán
                retryMomoPayment(hoaDonId);
            } else {
                // Hủy thanh toán
                cancelMomoPayment(hoaDonId);
            }
        });
    }
}

$(document).ready(function () {
    // Định dạng tiền tệ ban đầu
    formatCurrency();

    // Khởi tạo tổng tiền sau giảm
    const tongTienSauGiam = parseFloat($('#tongTienSauGiam').attr('data-value')) || 0;
    $('#tongTienSauGiam').text(formatNumberToVND(tongTienSauGiam));
    $('#tongTienSauGiam').attr('data-value', tongTienSauGiam);

    console.log("Đã khởi tạo định dạng tiền tệ cho trang");

    // Kiểm tra thông báo thanh toán từ flash attribute
    checkPaymentStatus();
});

// Kiểm tra thông báo thanh toán từ flash attribute
function checkPaymentStatus() {
    // Kiểm tra nếu có thông báo thanh toán từ server
    const paymentStatus = $('meta[name="payment-status"]').attr('content');
    const paymentMessage = $('meta[name="payment-message"]').attr('content');

    if (paymentStatus && paymentMessage) {
        let icon = 'info';

        if (paymentStatus === 'success') {
            icon = 'success';
        } else if (paymentStatus === 'failed') {
            icon = 'error';
        } else if (paymentStatus === 'error') {
            icon = 'error';
        }

        // Hiển thị thông báo
        Swal.fire({
            toast: true,
            icon: icon,
            title: paymentMessage,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true
        });
    }

    // Xóa sessionStorage cũ nếu có để tránh báo thành công lần nữa
    const oldPendingOrder = sessionStorage.getItem('pending_momo_order');
    if (oldPendingOrder) {
        console.log('Xóa pending Momo order cũ khi load trang:', oldPendingOrder);
        sessionStorage.removeItem('pending_momo_order');
    }
    $(document).ready(function () {
        $('#btnXoaPhieuGiamGia').click(function () {
            const idHD = $(this).data('id');

            Swal.fire({
                title: 'Xóa phiếu giảm giá?',
                text: 'Bạn có chắc chắn muốn xóa phiếu giảm giá này khỏi hóa đơn?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: 'Xóa',
                cancelButtonText: 'Hủy'
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url: '/sale/remove-discount',
                        type: 'POST',
                        data: { idHD: idHD },
                        success: function (response) {
                            Swal.fire({
                                toast: true,
                                icon: 'success',
                                title: response,
                                position: 'top-end',
                                showConfirmButton: false,
                                timer: 1500
                            });

                            // Cập nhật UI sau xóa
                            $('#tenPhieuGiamGia').text('Chưa chọn');
                            $('#giaTriGiamGia').text('0 đ');
                            $('#giaTriGiamGia').attr('data-value', '0');

                            const tongTien = parseFloat($('#tienThanhToan').attr('data-value')) || 0;
                            $('#tongTienSauGiam').text(formatNumberToVND(tongTien));
                            $('#tongTienSauGiam').attr('data-value', tongTien);

                            // Cập nhật tiền thừa
                            const tienKhachTra = parseFloat($('#tienKhachTra').val().replace(/[^0-9]/g, '')) || 0;
                            const tienThua = tienKhachTra - tongTien;
                            $('#tienThua').text(formatNumberToVND(tienThua));
                            if (tienThua < 0) {
                                $('#tienThua').addClass("text-danger");
                                $('#btnThanhToan').removeClass('btn-success').addClass('btn-outline-warning');
                            } else {
                                $('#tienThua').removeClass("text-danger");
                                $('#btnThanhToan').removeClass('btn-outline-warning').addClass('btn-success');
                            }

                            // Disable nút xóa sau khi xóa
                            $('#btnXoaPhieuGiamGia').prop('disabled', true);
                        },
                        error: function (xhr) {
                            Swal.fire({
                                toast: true,
                                icon: 'error',
                                title: xhr.responseText || 'Lỗi khi xóa phiếu giảm giá',
                                position: 'top-end',
                                showConfirmButton: false,
                                timer: 3000
                            });
                        }
                    });
                }
            });
        });
    });
}

