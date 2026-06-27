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

    // CORREÇÃO CRÍTICA: Redirecionamentos limpos sem usar a extensão física .html
    logout: () => {
        localStorage.clear();
        window.location.href = '/login';
    },

    requireLogin: () => {
        if (!auth.isLoggedIn()) {
            window.location.href = '/login';
        }
    },

    requireAdmin: () => {
        if (!auth.isLoggedIn() || !auth.isAdmin()) {
            window.location.href = '/';
        }
    }
};
