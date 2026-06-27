/* ==========================================================================
   NÚCLEO DE COMUNICAÇÃO HTTP (API AJAX REST)
   ========================================================================= */

const API_URL = '/api';

const api = {
    /**
     * Gerenciador padrão de requisições HTTP Fetch
     */
        /**
     * Gerenciador padrão de requisições HTTP Fetch (Atualizado para suportar Headers customizados)
     */
    async request(method, endpoint, body = null, extraHeaders = {}) { // <--- Injetado extraHeaders aqui
        const token = localStorage.getItem('token');
        
        // Junta os cabeçalhos padrão com os novos anti-cache
        const headers = { 
            'Content-Type': 'application/json',
            ...extraHeaders 
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = { method, headers };
        if (body) {
            config.body = JSON.stringify(body);
        }

        const response = await fetch(`${API_URL}${endpoint}`, config);

        if (response.status === 401) {
            localStorage.clear();
            window.location.href = '/login'; 
            return null;
        }

        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || 'Erro interno na requisição');
        }

        if (response.status === 204) return null;
        
        return await response.json();
    },


    /* MÓDULO DE AUTENTICAÇÃO */
    // Após o login/registro, o backend retorna um JSON. 
    // É obrigatório salvar esse token no localStorage para o 'request' funcionar.
    login: async (data) => {
        const response = await api.request('POST', '/auth/login', data);
        if (response && response.token) {
            localStorage.setItem('token', response.token);
        }
        return response;
    },

    register: async (data) => {
        const response = await api.request('POST', '/auth/register', data);
        // Se o registro também retorna o token, salve-o aqui:
        if (response && response.token) {
            localStorage.setItem('token', response.token);
        }
        return response;
    },

    /* MÓDULO DE POSTS E COMENTÁRIOS (MANTIDOS IGUAIS) */
       /* MÓDULO DE POSTS E COMENTÁRIOS ATUALIZADO (ANTI-CACHE) */
    // Adicionado cabeçalho Pragma e Cache-Control para forçar o navegador a buscar direto do banco Neon
    getPosts: () => api.request('GET', '/posts', null, { 'Cache-Control': 'no-cache', 'Pragma': 'no-cache' }),
    getPost: (id) => api.request('GET', `/posts/${id}`, null, { 'Cache-Control': 'no-cache', 'Pragma': 'no-cache' }),

    createPost: (data) => api.request('POST', '/posts', data),
    updatePost: (id, data) => api.request('PUT', `/posts/${id}`, data),
    deletePost: (id) => api.request('DELETE', `/posts/${id}`),
    getComments: (postId) => api.request('GET', `/comments/post/${postId}`),
    createComment: (postId, data) => api.request('POST', `/comments/post/${postId}`, data),
    deleteComment: (commentId) => api.request('DELETE', `/comments/${commentId}`)
};