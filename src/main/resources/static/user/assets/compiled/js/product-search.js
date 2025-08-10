// Product Search and Filter JavaScript
class ProductSearch {
    constructor() {
        this.currentPage = 0;
        this.pageSize = 12;
        this.filters = {
            query: '',
            kieuQuatId: null,
            congSuatId: null,
            hangId: null,
            mauSacId: null,
            nutBamId: null,
            minPrice: null,
            maxPrice: null
        };

        this.init();
    }

    init() {
        this.bindEvents();
        this.loadFilterOptions();
        this.loadProducts();
    }

    bindEvents() {
        // Search input
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.filters.query = e.target.value;
                this.debounceSearch();
            });
        }

        // Filter selects
        const filterSelects = document.querySelectorAll('.filter-select');
        filterSelects.forEach(select => {
            select.addEventListener('change', (e) => {
                const filterType = e.target.dataset.filter;
                const value = e.target.value;

                if (value && value !== '') {
                    this.filters[filterType] = parseInt(value);
                } else {
                    this.filters[filterType] = null;
                }

                this.currentPage = 0;
                this.loadProducts();
            });
        });

        // Price range slider
        const priceSlider = document.getElementById('priceSlider');
        if (priceSlider) {
            priceSlider.addEventListener('input', (e) => {
                this.filters.maxPrice = parseInt(e.target.value);
                this.updatePriceDisplay();
                this.debounceSearch();
            });
        }

        // Clear filters button (only in sidebar)
        const clearFiltersBtn = document.querySelector('#sidebar_fixed #clearFilters');
        if (clearFiltersBtn) {
            clearFiltersBtn.addEventListener('click', () => {
                this.clearFilters();
            });
        }

        // Pagination
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('page-link')) {
                e.preventDefault();
                const page = parseInt(e.target.dataset.page);
                this.currentPage = page;
                this.loadProducts();
            }
        });
    }

    debounceSearch() {
        clearTimeout(this.searchTimeout);
        this.searchTimeout = setTimeout(() => {
            this.currentPage = 0;
            this.loadProducts();
        }, 500);
    }

    async loadFilterOptions() {
        try {
            const response = await fetch('/api/products/filters');
            const data = await response.json();

            if (response.ok) {
                this.populateFilterOptions(data);
            } else {
                console.error('Error loading filter options:', data.error);
            }
        } catch (error) {
            console.error('Error loading filter options:', error);
        }
    }

    populateFilterOptions(data) {
        // Populate Kiểu quạt
        this.populateSelect('kieuQuatSelect', data.kieuQuat, 'kieuQuatId');

        // Populate Công suất
        this.populateSelect('congSuatSelect', data.congSuat, 'congSuatId');

        // Populate Hãng
        this.populateSelect('hangSelect', data.hang, 'hangId');

        // Populate Màu sắc
        this.populateSelect('mauSacSelect', data.mauSac, 'mauSacId');

        // Populate Nút bấm
        this.populateSelect('nutBamSelect', data.nutBam, 'nutBamId');

        // Set price range
        if (data.priceRange) {
            this.setupPriceRange(data.priceRange);
        }
    }

    populateSelect(selectId, options, filterType) {
        const select = document.getElementById(selectId);
        if (!select) return;

        // Clear existing options except the first one
        while (select.children.length > 1) {
            select.removeChild(select.lastChild);
        }

        // Add new options
        options.forEach(option => {
            const optionElement = document.createElement('option');
            optionElement.value = option.id;
            optionElement.textContent = option.ten;
            select.appendChild(optionElement);
        });
    }

    setupPriceRange(priceRange) {
        const priceSlider = document.getElementById('priceSlider');
        const priceDisplay = document.getElementById('priceDisplay');

        if (priceSlider && priceDisplay) {
            priceSlider.min = priceRange.min || 0;
            // Cố định max = 5.000.000 VND
            priceSlider.max = 5000000;
            priceSlider.value = 5000000;

            this.updatePriceDisplay();
        }
    }

    updatePriceDisplay() {
        const priceSlider = document.getElementById('priceSlider');
        const priceDisplay = document.getElementById('priceDisplay');

        if (priceSlider && priceDisplay) {
            const value = parseInt(priceSlider.value);
            priceDisplay.textContent = this.formatCurrency(value);
        }
    }

    async loadProducts() {
        try {
            const params = new URLSearchParams();

            // Add filters
            Object.keys(this.filters).forEach(key => {
                if (this.filters[key] !== null && this.filters[key] !== '') {
                    params.append(key, this.filters[key]);
                }
            });

            // Add pagination
            params.append('page', this.currentPage);
            params.append('size', this.pageSize);

            const response = await fetch(`/api/products/search?${params.toString()}`);
            const data = await response.json();

            if (response.ok) {
                this.renderProducts(data);
            } else {
                console.error('Error loading products:', data.error);
            }
        } catch (error) {
            console.error('Error loading products:', error);
        }
    }

    renderProducts(data) {
        const productsContainer = document.getElementById('productsContainer');
        if (!productsContainer) return;

        // Clear existing products
        productsContainer.innerHTML = '';

        // Sắp xếp sản phẩm theo giá từ thấp đến cao
        const sortedProducts = data.products.sort((a, b) => {
            // Lấy giá nhỏ nhất của mỗi sản phẩm
            const getMinPrice = (product) => {
                if (!product.sanPhamChiTiet || product.sanPhamChiTiet.length === 0) return 0;
                return Math.min(...product.sanPhamChiTiet.map(spct => spct.gia || 0));
            };

            const priceA = getMinPrice(a);
            const priceB = getMinPrice(b);

            return priceA - priceB; // Sắp xếp tăng dần (giá thấp lên đầu)
        });

        // Render products đã sắp xếp
        sortedProducts.forEach(product => {
            const productCard = this.createProductCard(product);
            productsContainer.appendChild(productCard);
        });

        // Render pagination
        this.renderPagination(data);

        // Update product count
        this.updateProductCount(data.totalProducts);
    }

    createProductCard(product) {
        const card = document.createElement('div');
        card.className = 'col-xl-4 col-lg-4 col-md-6 col-sm-6 mb-2';

        // Lọc SanPhamChiTiet có giá nhỏ nhất
        const cheapestVariant = product.sanPhamChiTiet && product.sanPhamChiTiet.length > 0
            ? product.sanPhamChiTiet.reduce((min, current) =>
                (current.gia && current.gia < min.gia) ? current : min, product.sanPhamChiTiet[0])
            : null;

        const imageUrl = cheapestVariant && cheapestVariant.hinhAnh
            ? cheapestVariant.hinhAnh.hinhAnh
            : '/user/assets/images/default_product.jpg';

        const price = cheapestVariant ? cheapestVariant.gia : 0;
        const status = product.trangThai ? 'Có sẵn' : 'Hết hàng';
        const statusClass = product.trangThai ? 'bg-success' : 'bg-secondary';

        // Format description
        const description = product.moTa || 'Chưa có mô tả';
        const shortDescription = description.length > 60 ? description.substring(0, 60) + '...' : description;

        // Format product code
        const productCode = product.ma || 'Null';
        const shortCode = productCode.length > 5 ? productCode.substring(0, 5) + '...' : productCode;

        // Format product type
        const productType = product.kieuQuat ? product.kieuQuat.ten : 'Null';
        const shortType = productType.length > 10 ? productType.substring(0, 10) + '...' : productType;

        // Dữ liệu cho nút thêm vào giỏ hàng
        const detailId = cheapestVariant ? cheapestVariant.id : '';
        const quantity = cheapestVariant ? cheapestVariant.soLuong : 0;
        const color = cheapestVariant && cheapestVariant.mauSac ? cheapestVariant.mauSac.ten : 'Không có';
        const power = cheapestVariant && cheapestVariant.congSuat ? cheapestVariant.congSuat.ten : 'Không có';

        card.innerHTML = `
        <div class="card shadow-sm">
            <!-- Hình ảnh sản phẩm -->
            <div class="position-relative">
                <img src="${imageUrl}" 
                     class="card-img-top" 
                     style="height: 210px; object-fit: contain;" 
                     alt="${product.ten}">
                
                <!-- Badge trạng thái -->
                <span class="badge ${statusClass} position-absolute top-0 start-0 m-2">${status}</span>
            </div>

            <!-- Thông tin sản phẩm -->
            <div class="p-3 d-flex flex-column flex-grow-1">
                <!-- Tên sản phẩm -->
                <h5 class="card-title">${product.ten}</h5>

                <!-- Mô tả ngắn -->
                <div>
                    <p class="text-muted small line-clamp-2 mb-0">${shortDescription}</p>
                </div>

                <!-- Thông tin chi tiết -->
                <div class="d-flex justify-content-between">
                    <!-- Mã sản phẩm -->
                    <small class="text-muted d-block">
                        <strong>Mã SP:</strong> ${shortCode}
                    </small>

                    <!-- Loại quạt -->
                    <small class="text-muted d-block">
                        <strong>Loại:</strong> ${shortType}
                    </small>
                </div>

                <!-- Giá sản phẩm -->
                <div class="mt-2">
                    <h4 class="text-dark-emphasis mb-1 text-sm fw-bold">
                        <span>${this.formatCurrency(price)}</span>
                        <small>đ</small>
                    </h4>
                </div>

                <!-- Nút hành động -->
                <div class="d-flex justify-content-center gap-2 mt-2">
                    <a href="/fanTech/detail?id=${product.id}"
                       class="btn btn-outline-dark btn-sm flex-grow-1 text-nowrap text-center"
                       style="font-size: 0.85rem; padding: 0.5rem 0;">
                        Xem chi tiết
                    </a>
                    <a href="#"
                       class="btn btn-dark btn-sm text-nowrap text-center"
                       style="font-size: 0.85rem; padding: 0.5rem 0.6rem; width: 38px;"
                       data-detail-id="${detailId}"
                       data-quantity="${quantity}"
                       data-price="${price}"
                       data-color="${color}"
                       data-power="${power}"
                       data-product-name="${product.ten}"
                       data-image-url="${imageUrl}"
                       ${product.trangThai && quantity > 0 ? '' : 'disabled'}
                       onclick="addToCart(this)">
                        <i class="fas fa-cart-plus"></i>
                    </a>
                </div>
            </div>
        </div>
    `;

        return card;
    }

    renderPagination(data) {
        const paginationContainer = document.getElementById('paginationContainer');
        if (!paginationContainer) return;

        paginationContainer.innerHTML = '';

        if (data.totalPages <= 1) return;

        const pagination = document.createElement('nav');
        pagination.innerHTML = `
            <ul class="pagination justify-content-center">
                ${this.createPaginationItems(data.currentPage, data.totalPages)}
            </ul>
        `;

        paginationContainer.appendChild(pagination);
    }

    createPaginationItems(currentPage, totalPages) {
        let items = '';

        // Previous button
        items += `
            <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">Trước</a>
            </li>
        `;

        // Page numbers
        for (let i = 0; i < totalPages; i++) {
            items += `
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
                </li>
            `;
        }

        // Next button
        items += `
            <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">Sau</a>
            </li>
        `;

        return items;
    }

    updateProductCount(totalProducts) {
        const countElement = document.getElementById('productCount');
        if (countElement) {
            countElement.innerHTML = `<i class="fas fa-box me-2"></i>${totalProducts} sản phẩm`;
        }
    }

    clearFilters() {
        // Reset all filter values
        this.filters = {
            query: '',
            kieuQuatId: null,
            congSuatId: null,
            hangId: null,
            mauSacId: null,
            nutBamId: null,
            minPrice: null,
            maxPrice: null
        };

        // Reset form elements
        const searchInput = document.getElementById('searchInput');
        if (searchInput) searchInput.value = '';

        const filterSelects = document.querySelectorAll('.filter-select');
        filterSelects.forEach(select => {
            select.selectedIndex = 0;
        });

        const priceSlider = document.getElementById('priceSlider');
        if (priceSlider) {
            priceSlider.value = priceSlider.max;
            this.updatePriceDisplay();
        }

        // Reload products
        this.currentPage = 0;
        this.loadProducts();
    }

    formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN').format(amount);
    }
}

// Hàm xử lý thêm vào giỏ hàng
function addToCart(button) {
    const detailId = button.dataset.detailId;
    const quantity = 1; // Số lượng mặc định là 1
    const availableQuantity = parseInt(button.dataset.quantity);
    const productName = button.dataset.productName;
    const imageUrl = button.dataset.imageUrl;
    const price = parseInt(button.dataset.price) || 0;
    const color = button.dataset.color || 'Không có';
    const power = button.dataset.power || 'Không có';

    // Kiểm tra nếu không có biến thể
    if (!detailId) {
        showNotification('Vui lòng vào trang chi tiết để chọn biến thể sản phẩm!', 'warning');
        return;
    }

    // Kiểm tra số lượng có sẵn
    if (quantity > availableQuantity) {
        showNotification(`Số lượng vượt quá số lượng có sẵn (${availableQuantity})!`, 'warning');
        return;
    }

    // Tạo đối tượng giỏ hàng
    const cartItem = {
        id: detailId,
        ten: productName,
        mauSac: color,
        congSuat: power,
        gia: price,
        soLuong: quantity,
        hinhAnh: imageUrl
    };

    addToCartServer(cartItem, button);
}

// Hàm gửi yêu cầu thêm vào giỏ hàng
function addToCartServer(cartItem, button) {
    button.disabled = true;
    button.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

    const formData = new FormData();
    formData.append('sanPhamChiTietId', cartItem.id);
    formData.append('soLuong', cartItem.soLuong);

    fetch('/cart/add', {
        method: 'POST',
        body: formData,
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
    })
        .then(response => response.ok ? response.json() : Promise.reject())
        .then(data => {
            if (data.success) {
                showNotification('Đã thêm sản phẩm vào giỏ hàng!', 'success');
                updateCartCount(data.cartCount);
            } else {
                showNotification(data.message || 'Có lỗi xảy ra!', 'error');
            }
        })
        .catch(() => showNotification('Có lỗi xảy ra khi thêm vào giỏ hàng!', 'error'))
        .finally(() => {
            button.disabled = false;
            button.innerHTML = '<i class="fas fa-cart-plus"></i>';
        });
}

// Hàm hiển thị thông báo
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${getBootstrapAlertClass(type)} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    notification.innerHTML = `${getNotificationIcon(type)}${message}<button type="button" class="btn-close" data-bs-dismiss="alert"></button>`;
    document.body.appendChild(notification);
    setTimeout(() => notification.remove(), 5000);
}

// Hàm lấy lớp Bootstrap cho thông báo
function getBootstrapAlertClass(type) {
    return { 'success': 'success', 'error': 'danger', 'warning': 'warning' }[type] || 'info';
}

// Hàm lấy biểu tượng thông báo
function getNotificationIcon(type) {
    return {
        'success': '<i class="fas fa-check-circle me-2"></i>',
        'error': '<i class="fas fa-exclamation-circle me-2"></i>',
        'warning': '<i class="fas fa-exclamation-triangle me-2"></i>'
    }[type] || '<i class="fas fa-info-circle me-2"></i>';
}

// Hàm cập nhật số lượng giỏ hàng
function updateCartCount(count) {
    document.querySelectorAll('.cart-count').forEach(el => {
        el.textContent = count;
        el.style.display = count > 0 ? 'inline' : 'none';
    });
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    new ProductSearch();
});