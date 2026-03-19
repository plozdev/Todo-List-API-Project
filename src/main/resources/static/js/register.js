// Get DOM elements
const registerForm = document.getElementById('registerForm');
const passwordInput = document.getElementById('password');
const confirmPasswordInput = document.getElementById('confirmPassword');
const notification = document.getElementById('notification');
const notificationMessage = document.getElementById('notificationMessage');
const notificationClose = document.getElementById('notificationClose');

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
    // type: 'success', 'error', 'warning'
    
    notificationMessage.textContent = message;
    notification.classList.remove('hidden');
    notification.classList.remove('notification-success', 'notification-error', 'notification-warning');
    notification.classList.add(`notification-${type}`);
}

// Close notification button
notificationClose.addEventListener('click', () => {
    notification.classList.add('hidden');
});

// ==================== PASSWORD MATCH CHECK ====================
function checkPasswordMatch() {
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;
    
    // If both fields have values
    if (password && confirmPassword) {
        if (password === confirmPassword) {
            // Match
            confirmPasswordInput.classList.remove('no-match');
            confirmPasswordInput.classList.add('match');
            return true;
        } else {
            // No match
            confirmPasswordInput.classList.remove('match');
            confirmPasswordInput.classList.add('no-match');
            return false;
        }
    } else {
        // Clear styles if fields are empty
        confirmPasswordInput.classList.remove('match', 'no-match');
        return false;
    }
}

// ==================== EVENT LISTENERS ====================
// Check password match on input (realtime)
passwordInput.addEventListener('input', checkPasswordMatch);
confirmPasswordInput.addEventListener('input', checkPasswordMatch);

// Form submit handler
registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const fullName = document.getElementById('fullName').value;
    const email = document.getElementById('email').value;
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;

    // Validate: password match
    if (password !== confirmPassword) {
        showNotification('Passwords do not match!', 'error');
        return;
    }

    // Validate: password length
    if (password.length < 6) {
        showNotification('Password must be at least 6 characters!', 'error');
        return;
    }

    // Disable button during submission
    const submitBtn = registerForm.querySelector('.btn-submit');
    const originalText = submitBtn.textContent;
    submitBtn.disabled = true;
    submitBtn.textContent = 'Registering...';

    try {
        const response = await fetch('/api/v1/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                fullName, 
                email, 
                password 
            })
        });

        if (response.ok) {
            const data = await response.json();
            console.log("✅ Registration successful", data);
            showNotification('Registration successful! Redirecting to login...', 'success');
            
            registerForm.reset();
            confirmPasswordInput.classList.remove('match', 'no-match');
            
            setTimeout(() => {
                window.location.href = '/login';
            }, 2000);
        } else {
            const errorData = await response.json();
            console.error("❌ Registration failed:", errorData);
            showNotification(errorData.message || 'Registration failed!', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('An error occurred, please try again...', 'error');
    } finally {
        // Re-enable button
        submitBtn.disabled = false;
        submitBtn.textContent = originalText;
    }
});

