const API_URL = ''; 

const api = {
    /**
     * Gerenciador padrão de requisições HTTP Fetch (Atualizado para suportar FormData e Headers customizados)
     */
    async request(method, endpoint, body = null, extraHeaders = {}) {
        const token = localStorage.getItem('token');
        
        // Cria os cabeçalhos iniciais
        const headers = { ...extraHeaders };

        // CORREÇÃO CRÍTICA DO CONTENT-TYPE:
        // Só injeta application/json se o corpo NÃO for um objeto FormData (arquivo)
        if (body && !(body instanceof FormData)) {
            headers['Content-Type'] = 'application/json';
        }

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = { method, headers };
        
        // CORREÇÃO DO BODY:
        // Se for FormData, passa o corpo direto sem dar JSON.stringify
        if (body) {
            config.body = body instanceof FormData ? body : JSON.stringify(body);
        }

        const finalUrl = `${API_URL}${endpoint}`;

        const response = await fetch(finalUrl, config);

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
    login: async (data) => {
        const response = await api.request('POST', '/api/auth/login', data);
        if (response && response.token) {
            localStorage.setItem('token', response.token);
        }
        return response;
    },

    register: async (data) => {
        const response = await api.request('POST', '/api/auth/register', data);
        if (response && response.token) {
            localStorage.setItem('token', response.token);
        }
        return response;
    },

    /* MÓDULO DE POSTS E COMENTÁRIOS ATUALIZADO */
    getPosts: () => api.request('GET', '/posts', null, { 'Cache-Control': 'no-cache', 'Pragma': 'no-cache' }),
    getPost: (id) => api.request('GET', `/posts/${id}`, null, { 'Cache-Control': 'no-cache', 'Pragma': 'no-cache' }),

    // Ambos os métodos agora estão prontos para receber o FormData
    createPost: (formData) => api.request('POST', '/posts', formData),
    
    // ALTERADO AQUI: Mudado para 'POST' para bater com o @PostMapping("/{id}") do seu PostController
    updatePost: (id, formData) => api.request('POST', `/posts/${id}`, formData), 
    
    deletePost: (id) => api.request('DELETE', `/posts/${id}`),
    getComments: (postId) => api.request('GET', `/comments/post/${postId}`),
    createComment: (postId, data) => api.request('POST', `/comments/post/${postId}`, data),
    deleteComment: (commentId) => api.request('DELETE', `/comments/${commentId}`)
};