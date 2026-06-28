/* ==========================================================================
   MÓDULO UNIFICADO - PORTAL DE NOTÍCIAS (GUARDIÃS DAS ÁGUAS)
   ========================================================================= */

// Variável global para armazenar a lista vinda da API
let todasAsNoticias = []; 

document.addEventListener('DOMContentLoaded', async () => {
    
    // 1. CONTROLE VISUAL DO CABEÇALHO (Efeito de Scroll)
    const header = document.querySelector(".site-header");
    if (header) {
        let ticking = false;
        const updateHeaderState = () => {
            if (window.scrollY > 15) {
                header.classList.add("header-scrolled");
            } else {
                header.classList.remove("header-scrolled");
            }
            ticking = false;
        };
        window.addEventListener("scroll", () => {
            if (!ticking) {
                window.requestAnimationFrame(updateHeaderState);
                ticking = true;
            }
        }, { passive: true });
    }

    // 2. INICIALIZAÇÃO DA CARGA DE DADOS DO BANCO NEON
    const loadingDiv = document.getElementById('loading-news');
    const gridDiv = document.getElementById('noticias-dinamicas');

    try {
        // Busca os posts usando a abstração do api.js
        const posts = await api.getPosts();
        todasAsNoticias = (posts && Array.isArray(posts)) ? posts : [];

        if (loadingDiv) loadingDiv.style.display = 'none';
        if (gridDiv) gridDiv.style.display = 'grid';

        renderizarNoticias(todasAsNoticias);
        configurarFiltros();
        configurarBusca();

    } catch (error) {
        console.error("Erro na carga de notícias:", error);
        if (loadingDiv) {
            loadingDiv.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Não foi possível sincronizar as notícias no momento.';
        }
    }
});

// 3. RENDERIZAÇÃO DOS CARDS
function renderizarNoticias(lista) {
    const gridDiv = document.getElementById('noticias-dinamicas');
    if (!gridDiv) return;
    
    // TRAVA CRÍTICA ANTI-DUPLICAÇÃO: Esvazia o container antes de desenhar os elementos
    gridDiv.innerHTML = "";
    
    if (lista.length === 0) {
        gridDiv.innerHTML = '<p style="grid-column: 1/-1; text-align: center; font-weight: bold; color: #666; padding: 40px 0;">Nenhuma notícia corresponde aos termos selecionados.</p>';
        return;
    }

    gridDiv.innerHTML = lista.map(post => {
        const categoriaTexto = post.category || post.categoria || 'Geral';
        const tituloTexto = post.title || post.titulo || 'Sem Título';
        const conteudoTexto = post.content || post.conteudo || 'Sem conteúdo disponível...';
        
        // CORREÇÃO DA IMAGEM: Garante o caminho relativo correto para a pasta física de uploads
        let nomeImagem = post.imageUrl || post.imagemUrl || "";
        let linkImagem = "/img/primeira.png"; 

        if (nomeImagem) {
            linkImagem = nomeImagem.startsWith("/uploads/") ? nomeImagem : `/uploads/${nomeImagem}`;
        }
        
        // CORREÇÃO DA DATA: Trata strings ISO ou timestamps
        const dataBruta = post.createdAt || post.dataCriacao || new Date();
        const dataFormatada = new Date(dataBruta).toLocaleDateString('pt-BR');
        
        // Remove tags HTML (<p>, <strong>) geradas pelo editor de texto rico no resumo
        const conteudoLimpo = conteudoTexto.replace(/<[^>]*>/g, '');
        const resumoCard = conteudoLimpo.length > 120 ? conteudoLimpo.substring(0, 120) + "..." : conteudoLimpo;

        return `
          <article class="card-noticia" data-category="${categoriaTexto.toLowerCase().trim()}">
            <div class="card-thumb">
              <img src="${linkImagem}" alt="Imagem ilustrativa da notícia: ${tituloTexto}">
              <span class="badge-categoria" style="position: absolute; top: 10px; left: 10px; background: #5c2d91; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; z-index: 10;">${categoriaTexto}</span>
            </div>
            <div class="card-body">
              <h2>${tituloTexto}</h2>
              <p>${resumoCard}</p>
              <div class="card-footer-info">
                <span class="card-date"><i class="far fa-calendar-alt"></i> ${dataFormatada}</span>
              </div>
              <a href="/noticiaAberta?id=${post.id}" class="btn-ler-mais">Ler mais</a>
            </div>
          </article>
        `;
    }).join('');
}

// 4. SISTEMA DE FILTRO POR CLIQUE (TAGS)
function configurarFiltros() {
    const tags = document.querySelectorAll('.filter-tag');
    tags.forEach(tag => {
        tag.addEventListener('click', (e) => {
            tags.forEach(t => t.classList.remove('active'));
            e.target.classList.add('active');

            const filtro = e.target.getAttribute('data-filter').toLowerCase().trim();
            
            if (filtro === 'all' || filtro === 'todas') {
                renderizarNoticias(todasAsNoticias);
            } else {
                const filtradas = todasAsNoticias.filter(post => {
                    const cat = post.category || post.categoria || '';
                    return cat.toLowerCase().trim() === filtro;
                });
                renderizarNoticias(filtradas);
            }
        });
    });
}

// Helper para ignorar acentos e maiúsculas na busca em tempo real
const normalizeStr = (str) => {
    return str.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim();
};

// 5. SISTEMA DE FILTRO POR DIGITAÇÃO (BARRA DE PESQUISA)
function configurarBusca() {
    const inputBusca = document.getElementById('search-input');
    if (!inputBusca) return;

    inputBusca.addEventListener('input', (e) => {
        const termo = normalizeStr(e.target.value);
        
        const resultadoBusca = todasAsNoticias.filter(post => {
            const tit = normalizeStr(post.title || post.titulo || '');
            const con = normalizeStr(post.content || post.conteudo || '');
            const cat = normalizeStr(post.category || post.categoria || '');
            
            return tit.includes(termo) || 
                   con.includes(termo) ||
                   cat.includes(termo);
        });
        
        renderizarNoticias(resultadoBusca);
    });
}
