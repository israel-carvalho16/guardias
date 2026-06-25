const API_URL = 'http://localhost:8080/api';

const api = {
    async request(method, endpoint, body = null) {
        const token = localStorage.getItem('token');
        const headers = { 'Content-Type': 'application/json' };

        if (token) headers['Authorization'] = `Bearer ${token}`;

        const config = { method, headers };
        if (body) config.body = JSON.stringify(body);

        const response = await fetch(`${API_URL}${endpoint}`, config);

        if (response.status === 401) {
            localStorage.clear();
            window.location.href = '/login.html';
            return;
        }

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Erro na requisição');
        }

        if (response.status === 204) return null;
        return await response.json();
    },

    // Auth
    login: (data) => api.request('POST', '/auth/login', data),
    register: (data) => api.request('POST', '/auth/register', data),

    // Posts
    getPosts: () => api.request('GET', '/posts'),
    getPost: (id) => api.request('GET', `/posts/${id}`),
    createPost: (data) => api.request('POST', '/posts', data),
    updatePost: (id, data) => api.request('PUT', `/posts/${id}`, data),
    deletePost: (id) => api.request('DELETE', `/posts/${id}`),

    // Comments
    getComments: (postId) => api.request('GET', `/comments/post/${postId}`),
    createComment: (postId, data) => api.request('POST', `/comments/post/${postId}`, data),
    deleteComment: (commentId) => api.request('DELETE', `/comments/${commentId}`)
};