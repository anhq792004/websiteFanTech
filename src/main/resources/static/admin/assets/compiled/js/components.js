if (typeof jQuery === "undefined") {
    console.error("jQuery is not loaded. Please include jQuery before running this script.")
} else {
    document.addEventListener("DOMContentLoaded", function () {
            const citis = document.getElementById("city");
            const districts = document.getElementById("district");
            const wards = document.getElementById("ward");

            axios.get("https://raw.githubusercontent.com/kenzouno1/DiaGioiHanhChinhVN/master/data.json")
                .then(function (response) {
                    const data = response.data;
                    renderCity(data);
                })
                .catch(function (error) {
                    console.error("Lỗi tải dữ liệu:", error);
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
    );
}

// Định dạng số thành tiền VNĐ chỉ cho những phần tử có class giaVND
document.addEventListener('DOMContentLoaded', function() {
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
