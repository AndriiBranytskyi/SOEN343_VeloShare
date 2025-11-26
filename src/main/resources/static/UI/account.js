// account.js

document.addEventListener('DOMContentLoaded', () => {
    const backBtn = document.getElementById('backHomeBtn');
    if (backBtn) {
        backBtn.addEventListener('click', () => {
            window.location.href = 'index.html';
        });
    }

    // Wait for Firebase auth to be ready (simple version)
    firebase.auth().onAuthStateChanged(user => {
        if (!user) {
            // not logged in -> send back to login
            window.location.href = 'LoginPage.html';
            return;
        }

        loadAccountInfo(user.uid);
    });
});

function loadAccountInfo(userId) {
    fetch(`/api/account/${userId}`)
        .then(resp => {
            if (!resp.ok) {
                throw new Error('Failed to load account info');
            }
            return resp.json();
        })
        .then(data => {
            // loyalty
            const loyaltyBadge = document.getElementById('loyaltyBadge');
            if (loyaltyBadge) {
                loyaltyBadge.textContent = data.loyaltyTier || 'Entry';
            }

            // flex dollars
            const flexElem = document.getElementById('flexBalance');
            if (flexElem) {
                const value = typeof data.flexDollars === 'number'
                    ? data.flexDollars
                    : parseFloat(data.flexDollars || '0');
                flexElem.textContent = value.toFixed(2);
            }

            // roles (stub but future-proof)
            const roles = data.roles || [];
            const activeRole = data.activeRole || (roles[0] || null);
            setupRoleSection(roles, activeRole);
        })
        .catch(err => {
            console.error(err);
            alert('Could not load account information.');
        });
}

function setupRoleSection(roles, activeRole) {
    const section = document.getElementById('roleSection');
    const select = document.getElementById('roleSelect');
    if (!section || !select) return;

    if (!roles || roles.length === 0) {
        section.style.display = 'none';
        return;
    }

    // show section if we have at least one role
    section.style.display = 'block';
    select.innerHTML = '';

    roles.forEach(role => {
        const opt = document.createElement('option');
        opt.value = role;
        opt.textContent = role;
        if (role === activeRole) {
            opt.selected = true;
        }
        select.appendChild(opt);
    });

    // For now, changing the role just updates local state.
    // Later you can POST to /api/account/role or similar.
    select.addEventListener('change', () => {
        const newRole = select.value;
        console.log('Role changed to:', newRole);
        // TODO: call backend when the role-switching logic is implemented
    });
}
