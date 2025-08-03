// Định dạng số thành tiền VNĐ chỉ cho những phần tử có class giaVND
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.giaVND').forEach(element => {
        const originalValue = parseFloat(element.innerText || '0');
        if (!isNaN(originalValue)) {
            element.innerText = originalValue.toLocaleString('vi-VN', {
                style: 'currency',
                currency: 'VND',
                currencyDisplay: 'symbol'
            });
        }
    });

    // Debug để kiểm tra dữ liệu
    console.log("Số lượng phiếu giảm giá:", document.querySelectorAll('tbody tr').length);
});
