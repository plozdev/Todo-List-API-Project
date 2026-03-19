// Get DOM elements
const loginForm = document.getElementById('loginForm');
const notification = document.getElementById('notification');
const notificationMessage = document.getElementById('notificationMessage');
const notificationClose = document.getElementById('notificationClose');

// ==================== LOGIN FORM HANDLER ====================
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    // Disable button during submission
    const submitBtn = loginForm.querySelector('.btn-submit');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const originalText = submitBtn.textContent;
    emailInput.disabled = true;
    passwordInput.disabled = true;
    submitBtn.disabled = true;
    submitBtn.textContent = 'Logging in...';

    try {
        const response = await fetch('/api/v1/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            const data = await response.json();
            console.log("✅ Login successful", data);
            
            // Redirect to homepage after 1.5 seconds
            setTimeout(() => {
                window.location.href = '/';
            }, 500);
  
            loginForm.reset();
        } else {
            const errorData = await response.json();
            console.error("❌ Login failed:", errorData);
            console.error("Error message:", errorData.message);
            showNotification(errorData.message || 'Login failed!', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('An error occurred, please try again...', 'error');
    } finally {
        // Re-enable button
        emailInput.disabled = false;
        passwordInput.disabled = false;
        submitBtn.disabled = false;
        submitBtn.textContent = originalText;
    }
});


// ==================== PASSWORD VISIBILITY TOGGLE ====================
function togglePasswordVisibility(fieldId) {
    const input = document.getElementById(fieldId);
    const toggleBtn = input.parentElement.querySelector('.password-toggle-btn');
    const showIcon = toggleBtn.querySelector('.eye-icon.show');
    const hideIcon = toggleBtn.querySelector('.eye-icon.hide');

    if (input.type === 'password') {
        input.type = 'text';
        showIcon.classList.add('hidden');
        hideIcon.classList.remove('hidden');
        console.log(`👁️ Password field "${fieldId}" is now visible`);
    } else {
        input.type = 'password';
        showIcon.classList.remove('hidden');
        hideIcon.classList.add('hidden');
        console.log(`👁️ Password field "${fieldId}" is now hidden`);
    }
}

// ==================== NOTIFICATION FUNCTION ====================
function showNotification(message, type) {
    
    notificationMessage.textContent = message;
    notification.classList.remove('hidden');
    notification.classList.remove('notification-success', 'notification-error', 'notification-warning');
    notification.classList.add(`notification-${type}`);
}

notificationClose.addEventListener('click', () => {
    notification.classList.add('hidden');
});
