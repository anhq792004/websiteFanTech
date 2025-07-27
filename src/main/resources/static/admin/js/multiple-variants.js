/**
 * Script xử lý thêm biến thể sản phẩm
 */

class VariantsManager {
    constructor() {
        this.selectedMauSacIds = new Set();
        this.selectedCongSuatIds = new Set();
        this.sanPhamId = document.getElementById('sanPhamId').value;
        
        // Storage cho tên màu sắc và công suất
        this.mauSacData = new Map(); // id -> {id, ten}
        this.congSuatData = new Map(); // id -> {id, ten}
        
        // Danh sách biến thể preview trước khi lưu
        this.variantsList = [];
        
        // Storage cho file ảnh của từng biến thể
        this.variantImages = new Map(); // index -> File object
        
        // Map tên màu thành mã màu hex
        this.colorMap = {
            'đỏ': '#dc3545',
            'xanh': '#0d6efd',
            'đen': '#212529',
            'trắng': '#f8f9fa',
            'vàng': '#ffc107',
            'xanh lá': '#198754',
            'tím': '#6f42c1',
            'hồng': '#d63384',
            'cam': '#fd7e14',
            'xám': '#6c757d',
            'nâu': '#8b4513',
            'xanh dương': '#0dcaf0',
            'xanh lá cây': '#20c997',
            'blue': '#0d6efd',
            'red': '#dc3545',
            'black': '#212529',
            'white': '#f8f9fa',
            'yellow': '#ffc107',
            'green': '#198754',
            'purple': '#6f42c1',
            'pink': '#d63384',
            'orange': '#fd7e14',
            'gray': '#6c757d',
            'brown': '#8b4513'
        };
        
        // Debug log
        console.log('VariantsManager initialized');
        console.log('sanPhamId:', this.sanPhamId);
        
        this.init();
    }

    init() {
        console.log('Initializing event listeners...');
        this.loadOptionsData();
        this.bindEvents();
        this.setupModalEvents();
        this.updatePreview();
    }
    
    // Setup event listeners cho modal
    setupModalEvents() {
        // Event khi modal mở để reset trạng thái
        document.getElementById('mauSacModal').addEventListener('show.bs.modal', () => {
            this.resetModalButtons('mau-sac-option', 'btn-outline-primary', 'btn-primary');
        });
        
        document.getElementById('congSuatModal').addEventListener('show.bs.modal', () => {
            this.resetModalButtons('cong-suat-option', 'btn-outline-info', 'btn-info');
        });
        
        // Event khi modal đóng để đảm bảo làm sạch
        document.getElementById('mauSacModal').addEventListener('hidden.bs.modal', () => {
            this.cleanupModal();
        });
        
        document.getElementById('congSuatModal').addEventListener('hidden.bs.modal', () => {
            this.cleanupModal();
        });
    }
    
    // Reset trạng thái các nút trong modal
    resetModalButtons(className, defaultClass, selectedClass) {
        const buttons = document.querySelectorAll(`.${className}`);
        buttons.forEach(button => {
            button.classList.remove(selectedClass);
            button.classList.add(defaultClass);
            button.disabled = false;
            // Xóa dấu tick nếu có
            button.innerHTML = button.textContent.replace('✓ ', '');
        });
    }
    
    // Làm sạch sau khi modal đóng
    cleanupModal() {
        // Xóa backdrop nếu còn
        const backdrops = document.querySelectorAll('.modal-backdrop');
        backdrops.forEach(backdrop => backdrop.remove());
        
        // Xóa class modal-open khỏi body
        document.body.classList.remove('modal-open');
        
        // Reset style của body
        document.body.style.overflow = '';
        document.body.style.paddingRight = '';
    }
    
    // Load data cho dropdown options
    loadOptionsData() {
        // Load màu sắc data từ modal buttons
        const mauSacButtons = document.querySelectorAll('.mau-sac-option');
        mauSacButtons.forEach(button => {
            const id = button.getAttribute('data-mau-sac-id');
            const ten = button.textContent.trim();
            if (id) {
                this.mauSacData.set(id, {
                    id: id,
                    ten: ten
                });
            }
        });
        
        // Load công suất data từ modal buttons
        const congSuatButtons = document.querySelectorAll('.cong-suat-option');
        congSuatButtons.forEach(button => {
            const id = button.getAttribute('data-cong-suat-id');
            const ten = button.textContent.trim();
            if (id) {
                this.congSuatData.set(id, {
                    id: id,
                    ten: ten
                });
            }
        });
        
        console.log('Loaded mauSac data:', this.mauSacData);
        console.log('Loaded congSuat data:', this.congSuatData);
    }

    bindEvents() {
        // Click vào nút màu sắc trong modal
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('mau-sac-option')) {
                e.preventDefault();
                e.stopPropagation();
                
                const mauSacId = e.target.getAttribute('data-mau-sac-id');
                
                // Kiểm tra xem đã chọn chưa
                if (this.selectedMauSacIds.has(mauSacId)) {
                    this.showError('Màu sắc này đã được chọn');
                    return;
                }
                
                // Thêm màu sắc
                this.addMauSacById(mauSacId);
                
                // Đổi màu nút để hiển thị đã chọn
                e.target.classList.remove('btn-outline-primary');
                e.target.classList.add('btn-primary');
                e.target.innerHTML = '✓ ' + e.target.textContent.replace('✓ ', '');
                e.target.disabled = true;
                
                // Đóng modal ngay lập tức
                this.closeModal('mauSacModal');
            }
        });

        // Click vào nút công suất trong modal
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('cong-suat-option')) {
                e.preventDefault();
                e.stopPropagation();
                
                const congSuatId = e.target.getAttribute('data-cong-suat-id');
                
                // Kiểm tra xem đã chọn chưa
                if (this.selectedCongSuatIds.has(congSuatId)) {
                    this.showError('Công suất này đã được chọn');
                    return;
                }
                
                // Thêm công suất
                this.addCongSuatById(congSuatId);
                
                // Đổi màu nút để hiển thị đã chọn
                e.target.classList.remove('btn-outline-info');
                e.target.classList.add('btn-info');
                e.target.innerHTML = '✓ ' + e.target.textContent.replace('✓ ', '');
                e.target.disabled = true;
                
                // Đóng modal ngay lập tức
                this.closeModal('congSuatModal');
            }
        });

        // Generate variants list button
        const generateBtn = document.getElementById('generateVariantsList');
        if (generateBtn) {
            generateBtn.addEventListener('click', () => this.generateVariantsList());
        }
        
        // Save to database button  
        const saveBtn = document.getElementById('saveToDatabase');
        if (saveBtn) {
            saveBtn.addEventListener('click', () => this.saveToDatabase());
        }
        
        // Edit all toggle button
        const editToggleBtn = document.getElementById('editAllToggle');
        if (editToggleBtn) {
            editToggleBtn.addEventListener('click', () => this.toggleEditMode());
        }
    }
    
    // Đóng modal đúng cách  
    closeModal(modalId) {
        try {
        const modalElement = document.getElementById(modalId);
            if (!modalElement) {
                console.warn(`Modal element with id "${modalId}" not found`);
                return;
            }
            
        const modal = bootstrap.Modal.getInstance(modalElement);
        
        if (modal) {
            modal.hide();
        } else {
            // Nếu không có instance, tạo mới và đóng
            const newModal = new bootstrap.Modal(modalElement);
            newModal.hide();
            }
        } catch (error) {
            console.error('Error closing modal:', error);
            // Fallback: đóng modal bằng cách trigger click vào backdrop
            const modalElement = document.getElementById(modalId);
            if (modalElement) {
                modalElement.classList.remove('show');
                modalElement.style.display = 'none';
                this.cleanupModal();
            }
        }
    }
    
    // Lấy mã màu hex từ tên màu
    getColorFromName(tenMau) {
        if (!tenMau) return '#6c757d'; // Default gray
        
        const tenMauLower = tenMau.toLowerCase().trim();
        
        // Tìm exact match trước
        if (this.colorMap[tenMauLower]) {
            return this.colorMap[tenMauLower];
        }
        
        // Tìm partial match
        for (const [key, value] of Object.entries(this.colorMap)) {
            if (tenMauLower.includes(key) || key.includes(tenMauLower)) {
                return value;
            }
        }
        
        // Default color nếu không tìm thấy
        return '#6c757d';
    }
    
    // Tính toán màu chữ tương phản (đen hoặc trắng) dựa trên màu nền
    getContrastColor(hexColor) {
        // Chuyển hex thành RGB
        const r = parseInt(hexColor.substr(1, 2), 16);
        const g = parseInt(hexColor.substr(3, 2), 16);
        const b = parseInt(hexColor.substr(5, 2), 16);
        
        // Tính độ sáng (luminance)
        const luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
        
        // Trả về màu tương phản
        return luminance > 0.5 ? '#000000' : '#ffffff';
    }
    
    // Thêm màu sắc theo ID
    addMauSacById(mauSacId) {
        if (!mauSacId) return;
        
        // Thêm vào danh sách đã chọn
        this.selectedMauSacIds.add(mauSacId);
        
        // Cập nhật UI
        this.renderSelectedMauSac();
        this.updatePreview();
        
        // Show mini success toast
        const mauSac = this.mauSacData.get(mauSacId);
        this.showMiniToast(`✓ Đã thêm ${mauSac?.ten || 'màu sắc'}`);
        
        console.log('Added mauSac:', mauSacId);
    }
    
    // Thêm công suất theo ID
    addCongSuatById(congSuatId) {
        if (!congSuatId) return;
        
        // Thêm vào danh sách đã chọn
        this.selectedCongSuatIds.add(congSuatId);
        
        // Cập nhật UI
        this.renderSelectedCongSuat();
        this.updatePreview();
        
        // Show mini success toast
        const congSuat = this.congSuatData.get(congSuatId);
        this.showMiniToast(`✓ Đã thêm ${congSuat?.ten || 'kích cỡ'}`);
        
        console.log('Added congSuat:', congSuatId);
    }

    
    // Render danh sách màu sắc đã chọn
    renderSelectedMauSac() {
        const container = document.getElementById('selectedMauSacContainer');
        if (!container) return;
        
        let html = '';
        this.selectedMauSacIds.forEach(id => {
            const mauSac = this.mauSacData.get(id);
            if (mauSac) {
                const colorCode = this.getColorFromName(mauSac.ten);
                
                html += `
                    <div class="d-inline-flex align-items-center justify-content-center me-2 mb-2 position-relative" 
                         style="background-color: ${colorCode}; border: 2px solid #dee2e6; border-radius: 50%; width: 35px; height: 35px;" 
                         title="${mauSac.ten}">
                        <button type="button" class="btn btn-sm position-absolute top-0 start-100 translate-middle rounded-circle" 
                                onclick="variantsManager.removeMauSac('${id}')" 
                                style="background-color: #dc3545; border: 2px solid white; width: 20px; height: 20px; padding: 0; font-size: 11px; color: white; display: flex; align-items: center; justify-content: center;"
                                aria-label="Xóa">×</button>
                    </div>
                `;
            }
        });
        
        container.innerHTML = html;
    }
    
    // Render danh sách công suất đã chọn
    renderSelectedCongSuat() {
        const container = document.getElementById('selectedCongSuatContainer');
        if (!container) return;
        
        let html = '';
        this.selectedCongSuatIds.forEach(id => {
            const congSuat = this.congSuatData.get(id);
            if (congSuat) {
                html += `
                    <div class="d-inline-flex align-items-center me-2 mb-2 position-relative" 
                         style="background-color: #0dcaf0; border: 2px solid #dee2e6; border-radius: 20px; padding: 5px 25px 5px 15px; min-width: 60px; height: 35px;" 
                         title="${congSuat.ten}">
                        <span style="color: #000000; font-size: 0.85em; font-weight: 500;">${congSuat.ten}</span>
                        <button type="button" class="btn btn-sm position-absolute top-0 start-100 translate-middle rounded-circle" 
                                onclick="variantsManager.removeCongSuat('${id}')" 
                                style="background-color: #dc3545; border: 2px solid white; width: 20px; height: 20px; padding: 0; font-size: 11px; color: white; display: flex; align-items: center; justify-content: center;"
                                aria-label="Xóa">×</button>
                    </div>
                `;
            }
        });
        
        container.innerHTML = html;
    }
    
    // Xóa màu sắc
    removeMauSac(id) {
        this.selectedMauSacIds.delete(id);
        this.renderSelectedMauSac();
        this.updatePreview();
        console.log('Removed mauSac:', id);
    }
    
    // Xóa công suất
    removeCongSuat(id) {
        this.selectedCongSuatIds.delete(id);
        this.renderSelectedCongSuat();
        this.updatePreview();
        console.log('Removed congSuat:', id);
          }

    updatePreview() {
        const previewContainer = document.getElementById('variantsPreview');
        if (!previewContainer) return;

        const totalVariants = this.selectedMauSacIds.size * this.selectedCongSuatIds.size;
        
        let previewHtml = `
            <div class="preview-header">
                <h6 class="mb-0">Xem trước: <span class="text-primary">${totalVariants} biến thể</span></h6>
            </div>
        `;
        
        if (totalVariants > 0) {
            previewHtml += '<div class="preview-list">';
            
            this.selectedMauSacIds.forEach(mauSacId => {
                const mauSac = this.mauSacData.get(mauSacId);
                
                this.selectedCongSuatIds.forEach(congSuatId => {
                    const congSuat = this.congSuatData.get(congSuatId);
                    
                    previewHtml += `
                        <div class="preview-item mb-1">
                            <small class="variant-name">${mauSac?.ten || mauSacId} - ${congSuat?.ten || congSuatId}</small>
                        </div>
                    `;
                });
            });
            
            previewHtml += '</div>';
        } else {
            previewHtml += `
                <div class="text-center text-muted mt-3">
                    <i class="bi bi-eye display-6 mb-2"></i>
                    <p>Chọn màu sắc và công suất để xem trước các biến thể sẽ được tạo</p>
                </div>
            `;
        }
        
        previewContainer.innerHTML = previewHtml;
    }
    
    // Kiểm tra biến thể duplicate trước khi preview
    async checkDuplicateVariant(sanPhamId, mauSacId, congSuatId, hangId, nutBamId) {
        try {
            // Kiểm tra tham số hợp lệ và không phải NaN
            if (!sanPhamId || !mauSacId || !congSuatId || !hangId || !nutBamId ||
                isNaN(sanPhamId) || isNaN(mauSacId) || isNaN(congSuatId) || isNaN(hangId) || isNaN(nutBamId)) {
                console.error('Tham số không hợp lệ:', { sanPhamId, mauSacId, congSuatId, hangId, nutBamId });
                return false;
            }

            const url = `/api/san-pham-chi-tiet/check-duplicate?sanPhamId=${sanPhamId}&mauSacId=${mauSacId}&congSuatId=${congSuatId}&hangId=${hangId}&nutBamId=${nutBamId}`;
            console.log('Checking duplicate với URL:', url);
            
            const response = await fetch(url);
            
            if (!response.ok) {
                console.error('API response error:', response.status, response.statusText);
                return false;
            }
            
            const result = await response.json();
            return result.exists || false;
        } catch (error) {
            console.error('Lỗi khi kiểm tra duplicate:', error);
            return false;
        }
    }

    // Tạo danh sách biến thể preview
    async generateVariantsList() {
        try {
            // Validate
            if (this.selectedMauSacIds.size === 0) {
                this.showError('Vui lòng chọn ít nhất một màu sắc');
                return;
            }

            if (this.selectedCongSuatIds.size === 0) {
                this.showError('Vui lòng chọn ít nhất một công suất');
                return;
            }

            const hangId = document.getElementById('hangId').value;
            const nutBamId = document.getElementById('nutBamId').value;

            if (!hangId || !nutBamId) {
                this.showError('Vui lòng chọn hãng và nút bấm');
                return;
            }

            // Lấy thông tin chung từ form
            const commonData = {
                hangId: parseInt(hangId),
                nutBamId: parseInt(nutBamId),
                soLuong: parseInt(document.getElementById('soLuong').value) || 0,
                gia: parseInt(document.getElementById('gia').value) || 0,
                canNang: parseFloat(document.getElementById('canNang').value) || 0,
                moTa: document.getElementById('moTa').value || '',
                trangThai: document.getElementById('trangThai').checked
            };

            // Tạo danh sách biến thể và kiểm tra duplicate cho từng combination
            this.variantsList = [];
            let index = 1;
            const duplicates = [];
            
            console.log('=== KIỂM TRA DUPLICATE CHO TỪNG BIẾN THỂ ===');
            
            for (const mauSacId of this.selectedMauSacIds) {
                const mauSac = this.mauSacData.get(mauSacId);
                
                for (const congSuatId of this.selectedCongSuatIds) {
                    const congSuat = this.congSuatData.get(congSuatId);
                    
                    // Kiểm tra duplicate cho combination này (đảm bảo các tham số là số)
                    const isDuplicate = await this.checkDuplicateVariant(
                        parseInt(this.sanPhamId), 
                        parseInt(mauSacId), 
                        parseInt(congSuatId), 
                        parseInt(hangId), 
                        parseInt(nutBamId)
                    );
                    
                    if (isDuplicate) {
                        duplicates.push(`${mauSac?.ten || ''} - ${congSuat?.ten || ''}`);
                        console.log(`Duplicate found: ${mauSac?.ten} - ${congSuat?.ten}`);
                    } else {
                    this.variantsList.push({
                        index: index++,
                        mauSacId: parseInt(mauSacId),
                        mauSacTen: mauSac?.ten || '',
                        congSuatId: parseInt(congSuatId),
                        congSuatTen: congSuat?.ten || '',
                        ...commonData
                    });
                    }
                }
            }
            
            // Nếu có duplicate, hiển thị cảnh báo nhưng vẫn tiếp tục với các variant không duplicate
            if (duplicates.length > 0) {
                this.showError(`Các biến thể sau đã tồn tại và sẽ được bỏ qua: ${duplicates.join(', ')}`);
            }
            
            // Nếu không có variant nào có thể tạo
            if (this.variantsList.length === 0) {
                this.showError('Tất cả các biến thể đã tồn tại! Vui lòng chọn các thuộc tính khác.');
                return;
            }

            // Hiển thị bảng và nút lưu
            this.renderVariantsTable();
            document.getElementById('variantsTableContainer').style.display = 'block';
            document.getElementById('saveButtonContainer').style.display = 'block';
            
            this.showSuccess(`Đã tạo danh sách ${this.variantsList.length} biến thể!`);
            
            // Scroll to table
            document.getElementById('variantsTableContainer').scrollIntoView({ behavior: 'smooth' });

        } catch (error) {
            console.error('Error generating variants list:', error);
            this.showError('Có lỗi xảy ra khi tạo danh sách: ' + error.message);
        }
    }
    
    // Render bảng biến thể
    renderVariantsTable() {
        const container = document.getElementById('variantsTablesByColor');
        const countSpan = document.getElementById('variantsCount');
        
        if (!container) return;
        
        countSpan.textContent = this.variantsList.length;
        
        // Nhóm biến thể theo màu sắc
        const variantsByColor = {};
        this.variantsList.forEach((variant, idx) => {
            const colorKey = variant.mauSacTen;
            if (!variantsByColor[colorKey]) {
                variantsByColor[colorKey] = [];
            }
            variantsByColor[colorKey].push({...variant, actualIndex: idx});
        });
        
        // Tạo HTML cho từng màu
        let html = '';
        Object.keys(variantsByColor).forEach(colorName => {
            const variants = variantsByColor[colorName];
            const colorCode = this.getColorFromName(colorName);
            const textColor = this.getContrastColor(colorCode);
            
            html += `
                <div class="color-table-container" data-color="${colorName}">
                    <div class="color-table-header" style="background-color: ${colorCode}; color: ${textColor};">
                        <div class="color-preview-circle" style="background-color: ${colorCode};"></div>
                        <h6>Màu ${colorName} (${variants.length} biến thể)</h6>
                    </div>
                    <div class="color-table-body">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover">
                                <thead class="table-light">
                                    <tr>
                                        <th width="6%">#</th>
                                        <th width="12%">Công suất</th>
                                        <th width="10%">Số lượng</th>
                                        <th width="12%">Giá (VNĐ)</th>
                                        <th width="10%">Cân nặng</th>
                                        <th width="20%">Mô tả</th>
                                        <th width="15%">Hình ảnh</th>
                                        <th width="8%">Trạng thái</th>
                                        <th width="7%">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
            `;
            
            variants.forEach((variant) => {
                html += `
                    <tr data-index="${variant.actualIndex}">
                        <td class="text-center">${variant.index}</td>
                        <td class="text-center">${variant.congSuatTen}</td>
                        <td>
                            <input type="number" class="form-control form-control-sm variant-input" 
                                   data-field="soLuong" value="${variant.soLuong}" min="0" disabled>
                        </td>
                        <td>
                            <input type="number" class="form-control form-control-sm variant-input" 
                                   data-field="gia" value="${variant.gia}" min="0" step="1000" disabled>
                        </td>
                        <td>
                            <input type="number" class="form-control form-control-sm variant-input" 
                                   data-field="canNang" value="${variant.canNang}" min="0" step="0.1" disabled>
                        </td>
                        <td>
                            <textarea class="form-control form-control-sm variant-input" 
                                      data-field="moTa" rows="2" disabled>${variant.moTa}</textarea>
                        </td>
                        <td>
                            <div class="image-upload-container">
                                <input type="file" class="form-control form-control-sm variant-image-input" 
                                       accept="image/*" data-variant-index="${variant.actualIndex}" disabled style="margin-bottom: 5px;">
                                <div class="image-preview-container" id="imagePreview_${variant.actualIndex}" style="min-height: 60px; border: 1px dashed #dee2e6; border-radius: 4px; display: flex; align-items: center; justify-content: center; background-color: #f8f9fa;">
                                    <small class="text-muted">Chưa chọn ảnh</small>
                                </div>
                            </div>
                        </td>
                        <td class="text-center">
                            <div class="form-check">
                                <input type="checkbox" class="form-check-input variant-input" 
                                       data-field="trangThai" ${variant.trangThai ? 'checked' : ''} disabled>
                            </div>
                        </td>
                        <td class="text-center">
                            <button type="button" class="btn btn-sm btn-outline-danger remove-variant-btn" 
                                    data-variant-index="${variant.actualIndex}" title="Xóa biến thể này" disabled>
                                <i class="bi bi-trash"></i>
                            </button>
                        </td>
                    </tr>
                `;
            });
            
            html += `
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            `;
        });
        
        container.innerHTML = html;
        
        // Bind events cho input fields
        this.bindVariantInputEvents();
    }
    
    // Bind events cho các input trong bảng
    bindVariantInputEvents() {
        const inputs = document.querySelectorAll('.variant-input');
        inputs.forEach(input => {
            input.addEventListener('change', (e) => {
                const row = e.target.closest('tr');
                const index = parseInt(row.dataset.index);
                const field = e.target.dataset.field;
                
                if (field === 'trangThai') {
                    this.variantsList[index][field] = e.target.checked;
                } else if (field === 'soLuong' || field === 'gia' || field === 'hangId' || field === 'nutBamId') {
                    this.variantsList[index][field] = parseInt(e.target.value) || 0;
                } else if (field === 'canNang') {
                    this.variantsList[index][field] = parseFloat(e.target.value) || 0;
                } else {
                    this.variantsList[index][field] = e.target.value;
                }
            });
        });
        
        // Bind events cho image input
        const imageInputs = document.querySelectorAll('.variant-image-input');
        imageInputs.forEach(input => {
            input.addEventListener('change', (e) => {
                const index = parseInt(e.target.dataset.variantIndex);
                const file = e.target.files[0];
                
                if (file) {
                    // Validate file
                    if (!file.type.startsWith('image/')) {
                        this.showError('Vui lòng chọn file hình ảnh hợp lệ');
                        e.target.value = '';
                        return;
                    }
                    
                    if (file.size > 10 * 1024 * 1024) { 
                        this.showError('Kích thước file không được vượt quá 10MB');
                        e.target.value = '';
                        return;
                    }
                    
                    // Lưu file vào storage
                    this.variantImages.set(index, file);
                    
                    // Preview ảnh
                    this.previewImage(file, index);
                    
                    console.log(`Đã chọn ảnh cho biến thể ${index}:`, file.name);
                } else {
                    // Xóa ảnh nếu không chọn file
                    this.variantImages.delete(index);
                    this.clearImagePreview(index);
                }
            });
        });
        
        // Bind events cho nút xóa biến thể
        const removeButtons = document.querySelectorAll('.remove-variant-btn');
        removeButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                const index = parseInt(e.target.closest('button').dataset.variantIndex);
                this.removeVariantFromList(index);
            });
        });
    }
    
    // Toggle chế độ chỉnh sửa
    toggleEditMode() {
        const inputs = document.querySelectorAll('.variant-input');
        const imageInputs = document.querySelectorAll('.variant-image-input');
        const removeButtons = document.querySelectorAll('.remove-variant-btn');
        const button = document.getElementById('editAllToggle');
        const isDisabled = inputs[0]?.disabled;
        
        inputs.forEach(input => {
            input.disabled = !isDisabled;
        });
        
        imageInputs.forEach(input => {
            input.disabled = !isDisabled;
        });
        
        removeButtons.forEach(button => {
            button.disabled = !isDisabled;
        });
        
        if (isDisabled) {
            button.innerHTML = '<i class="bi bi-check-lg"></i> Xong';
            button.className = 'btn btn-success btn-sm';
        } else {
            button.innerHTML = '<i class="bi bi-pencil"></i> Chỉnh sửa tất cả';
            button.className = 'btn btn-outline-warning btn-sm';
        }
    }
    
    // Preview ảnh đã chọn
    previewImage(file, variantIndex) {
        const previewContainer = document.getElementById(`imagePreview_${variantIndex}`);
        if (!previewContainer) return;
        
        const reader = new FileReader();
        reader.onload = (e) => {
            previewContainer.innerHTML = `
                <div class="position-relative">
                    <img src="${e.target.result}" 
                         style="max-width: 100%; max-height: 60px; object-fit: cover; border-radius: 4px;" 
                         alt="Preview">
                    <button type="button" class="btn btn-sm btn-danger position-absolute top-0 end-0" 
                            style="width: 20px; height: 20px; padding: 0; font-size: 10px; border-radius: 50%;"
                            onclick="variantsManager.clearImagePreview(${variantIndex}, true)" 
                            title="Xóa ảnh">×</button>
                </div>
                <small class="text-muted d-block mt-1">${file.name}</small>
            `;
        };
        reader.readAsDataURL(file);
    }
    
    // Xóa preview ảnh
    clearImagePreview(variantIndex, clearFile = false) {
        const previewContainer = document.getElementById(`imagePreview_${variantIndex}`);
        if (previewContainer) {
            previewContainer.innerHTML = '<small class="text-muted">Chưa chọn ảnh</small>';
        }
        
        if (clearFile) {
            // Xóa file khỏi storage
            this.variantImages.delete(variantIndex);
            
            // Clear input file
            const imageInput = document.querySelector(`.variant-image-input[data-variant-index="${variantIndex}"]`);
            if (imageInput) {
                imageInput.value = '';
            }
        }
    }
    
    // Xóa biến thể khỏi danh sách
    removeVariantFromList(index) {
        if (confirm('Bạn có chắc muốn xóa biến thể này khỏi danh sách?')) {
            // Xóa khỏi danh sách
            this.variantsList.splice(index, 1);
            
            // Xóa ảnh nếu có
            this.variantImages.delete(index);
            
            // Cập nhật lại index cho các biến thể còn lại
            this.updateVariantIndices();
            
            // Render lại bảng
            this.renderVariantsTable();
            
            this.showMiniToast('Đã xóa biến thể khỏi danh sách');
        }
    }
    
    // Cập nhật lại index sau khi xóa
    updateVariantIndices() {
        // Cập nhật lại index trong variantsList
        this.variantsList.forEach((variant, idx) => {
            variant.index = idx + 1;
        });
        
        // Cập nhật lại variantImages với index mới
        const newImageMap = new Map();
        this.variantImages.forEach((file, oldIndex) => {
            // Tìm vị trí mới của biến thể này trong danh sách
            if (oldIndex < this.variantsList.length) {
                newImageMap.set(oldIndex, file);
            }
        });
        this.variantImages = newImageMap;
    }

    // Lưu danh sách biến thể vào database
    async saveToDatabase() {
        try {
            // Validate
            if (this.variantsList.length === 0) {
                this.showError('Không có biến thể nào để lưu. Vui lòng tạo danh sách trước!');
                return;
            }

            // Xác nhận trước khi lưu
            const confirmResult = await Swal.fire({
                title: 'Xác nhận lưu',
                text: `Bạn có chắc chắn muốn lưu ${this.variantsList.length} biến thể này không?`,
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#28a745',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Lưu',
                cancelButtonText: 'Hủy'
            });
            
            if (!confirmResult.isConfirmed) {
                return;
            }

            // Hiển thị loading
            const saveBtn = document.getElementById('saveToDatabase');
            const originalText = saveBtn.innerHTML;
            saveBtn.innerHTML = '<i class="bi bi-arrow-clockwise spin"></i> Đang lưu...';
            saveBtn.disabled = true;
            
            // Show loading overlay
            this.showLoadingOverlay('Đang lưu biến thể và upload hình ảnh...');

            // Tạo FormData để gửi file + data
            const formData = new FormData();
            
            // Thêm thông tin cơ bản
            formData.append('sanPhamId', this.sanPhamId);
            
            // Thêm từng biến thể và file ảnh tương ứng
            this.variantsList.forEach((variant, index) => {
                formData.append(`variants[${index}].mauSacId`, variant.mauSacId);
                formData.append(`variants[${index}].congSuatId`, variant.congSuatId);
                formData.append(`variants[${index}].hangId`, variant.hangId);
                formData.append(`variants[${index}].nutBamId`, variant.nutBamId);
                formData.append(`variants[${index}].soLuong`, variant.soLuong);
                formData.append(`variants[${index}].gia`, variant.gia);
                formData.append(`variants[${index}].canNang`, variant.canNang);
                formData.append(`variants[${index}].moTa`, variant.moTa || '');
                formData.append(`variants[${index}].trangThai`, variant.trangThai);
                
                // Thêm file ảnh nếu có
                const imageFile = this.variantImages.get(index);
                if (imageFile) {
                    formData.append(`variantImages[${index}]`, imageFile);
                    console.log(`Thêm ảnh cho biến thể ${index}:`, imageFile.name);
                }
            });

            console.log('Saving data with images:', {
                variantsCount: this.variantsList.length,
                imagesCount: this.variantImages.size
            });

            // Gửi request với FormData
            const response = await fetch('/admin/san-pham/chi-tiet/add-multiple-with-images', {
                method: 'POST',
                body: formData // Không set Content-Type, để browser tự set với boundary
            });

            const result = await response.json();

            if (response.ok && result.success) {
                this.showSuccess(result.message || `Đã lưu thành công ${this.variantsList.length} biến thể!`);
                
                // Redirect về trang detail sau 1.5s
                setTimeout(() => {
                    window.location.href = `/admin/san-pham/detail?id=${this.sanPhamId}`;
                }, 1500);
            } else {
                this.showError(result.message || 'Có lỗi xảy ra khi lưu biến thể');
            }

        } catch (error) {
            console.error('Error:', error);
            this.showError('Có lỗi xảy ra: ' + error.message);
        } finally {
            // Reset button
            const saveBtn = document.getElementById('saveToDatabase');
            saveBtn.innerHTML = '<i class="bi bi-database-check"></i> Lưu vào Database';
            saveBtn.disabled = false;
            
            // Hide loading overlay
            this.hideLoadingOverlay();
        }
    }

    buildRequestData() {
        return {
            sanPhamId: document.getElementById('sanPhamId').value,
            mauSacIds: Array.from(this.selectedMauSacIds).map(id => parseInt(id)),
            congSuatIds: Array.from(this.selectedCongSuatIds).map(id => parseInt(id)),
            hangId: parseInt(document.getElementById('hangId').value),
            nutBamId: parseInt(document.getElementById('nutBamId').value),
            soLuong: parseInt(document.getElementById('soLuong').value) || 0,
            gia: parseInt(document.getElementById('gia').value) || 0,
            canNang: parseFloat(document.getElementById('canNang').value) || 0,
            moTa: document.getElementById('moTa').value || '',
            trangThai: document.getElementById('trangThai').checked
        };
    }

    showSuccess(message) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                icon: 'success',
                title: 'Thành công!',
                text: message,
                timer: 3000,
                showConfirmButton: false,
                position: 'top-end',
                toast: true
            });
        } else if (typeof Toastify !== 'undefined') {
            Toastify({
                text: message,
                duration: 3000,
                gravity: "top",
                position: "right",
                backgroundColor: "#28a745",
            }).showToast();
        } else {
            alert(message);
        }
    }

    showError(message) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                icon: 'error',
                title: 'Lỗi!',
                text: message,
                timer: 3000,
                showConfirmButton: false,
                position: 'top-end',
                toast: true
            });
        } else if (typeof Toastify !== 'undefined') {
            Toastify({
                text: message,
                duration: 3000,
                gravity: "top",
                position: "right",
                backgroundColor: "#dc3545",
            }).showToast();
        } else {
            alert(message);
        }
    }
    
    showMiniToast(message) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                icon: 'success',
                text: message,
                timer: 1500,
                showConfirmButton: false,
                position: 'top-end',
                toast: true,
                customClass: {
                    popup: 'swal2-small'
                }
            });
        } else if (typeof Toastify !== 'undefined') {
            Toastify({
                text: message,
                duration: 1500,
                gravity: "top",
                position: "right",
                backgroundColor: "#28a745",
                style: {
                    fontSize: "14px",
                    padding: "8px 12px"
                }
            }).showToast();
        }
    }
    
    // Show loading overlay
    showLoadingOverlay(message = 'Đang xử lý...') {
        const existing = document.getElementById('loadingOverlay');
        if (existing) return;
        
        const overlay = document.createElement('div');
        overlay.id = 'loadingOverlay';
        overlay.className = 'loading-overlay';
        overlay.innerHTML = `
            <div class="loading-spinner">
                <div class="spinner-border text-primary mb-3" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <div>${message}</div>
            </div>
        `;
        document.body.appendChild(overlay);
    }
    
    // Hide loading overlay
    hideLoadingOverlay() {
        const overlay = document.getElementById('loadingOverlay');
        if (overlay) {
            overlay.remove();
        }
    }
}

// Khởi tạo khi DOM ready
let variantsManager;
document.addEventListener('DOMContentLoaded', function() {
    variantsManager = new VariantsManager();
});

// CSS cho các element được chọn và modal cải thiện
const style = document.createElement('style');
style.textContent = `
    .mau-sac-item, .cong-suat-item {
        cursor: pointer;
        transition: all 0.3s ease;
        border: 2px solid transparent;
        padding: 8px 12px;
        margin: 4px;
        border-radius: 6px;
        display: inline-block;
    }
    
    .mau-sac-item:hover, .cong-suat-item:hover {
        transform: scale(1.05);
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    
    .mau-sac-item.selected, .cong-suat-item.selected {
        border-color: #007bff;
        background-color: rgba(0,123,255,0.1);
    }
    
    .preview-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;
    }
    
    .preview-item {
        padding: 5px 10px;
        background-color: #f8f9fa;
        border-radius: 4px;
        margin: 2px;
        display: inline-block;
    }
    
    .variant-name {
        font-size: 0.9em;
    }
    
    /* Modal improvements */
    .modal.fade {
        transition: opacity 0.15s linear;
    }
    
    .modal.fade .modal-dialog {
        transition: transform 0.15s ease-out;
        transform: translateY(-25px);
    }
    
    .modal.show .modal-dialog {
        transform: translateY(0);
    }
    
    .modal-backdrop {
        transition: opacity 0.15s linear;
    }
    
    /* Button loading state */
    .btn:disabled {
        cursor: not-allowed;
        opacity: 0.65;
    }
    
    /* SweetAlert custom styles */
    .swal2-small {
        font-size: 14px !important;
        padding: 0.5rem !important;
    }
    
    /* Prevent modal body scroll */
    body.modal-open {
        overflow: hidden;
    }
`;
document.head.appendChild(style); 