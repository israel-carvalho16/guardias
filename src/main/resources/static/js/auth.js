const auth = {
    getToken: () => localStorage.getItem('token'),
    getUser: () => JSON.parse(localStorage.getItem('user') || 'null'),
    isLoggedIn: () => !!localStorage.getItem('token'),
    isAdmin: () => {
        const user = auth.getUser();
        return user && user.role === 'ROLE_ADMIN';
    },

    save: (data) => {
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify({
            name: data.name,
            email: data.email,
            role: data.role
        }));
    },

    logout: () => {
        localStorage.clear();
        window.location.href = '/login.html';
    },

    requireLogin: () => {
        if (!auth.isLoggedIn()) {
            window.location.href = '/login.html';
        }
    },

    requireAdmin: () => {
        if (!auth.isLoggedIn() || !auth.isAdmin()) {
            window.location.href = '/index.html';
        }
    }
};