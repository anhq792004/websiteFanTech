    function toggleUserDropdown(event) {
    event.preventDefault();
    const dropdown = document.getElementById('userDropdownMenu');
    dropdown.classList.toggle('show');
}

    // Close dropdown when clicking outside
    document.addEventListener('click', function (event) {
    const dropdown = document.getElementById('userDropdownMenu');
    const button = document.querySelector('.user-info-btn');

    if (dropdown && !dropdown.contains(event.target) && !button.contains(event.target)) {
    dropdown.classList.remove('show');
}
});

    // Close dropdown when pressing Escape
    document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') {
    const dropdown = document.getElementById('userDropdownMenu');
    if (dropdown) {
    dropdown.classList.remove('show');
}
}
});