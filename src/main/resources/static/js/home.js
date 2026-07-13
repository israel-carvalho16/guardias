/* ==========================================================================
   PARTE 1: MÓDULO UNIFICADO - ACESSIBILIDADE DIRETA DA API E DROPDOWN
   ========================================================================= */

let todasAsNoticias = []; 

// GATILHO NATIVO USERWAY: Roda no milissegundo em que o widget injetar as APIs na janela
window.userwayInit = function () {
    console.log("UserWay injetado com sucesso!");
    
    const btnUserWay = document.getElementById("trigger-userway");
    if (btnUserWay) {
        btnUserWay.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            
            const menuDropdown = document.getElementById("access-dropdown-menu");
            if (menuDropdown) menuDropdown.style.display = "none"; // Recolhe a caixinha
            
            // Invoca diretamente o painel de controle nativo do script
            if (window.UserWay && typeof window.UserWay.openWidget === 'function') {
                window.UserWay.openWidget();
            }
        });
    }
};

// Escuta padrão do carregamento do documento
document.addEventListener('DOMContentLoaded', () => {
    const btnToggleAccess = document.getElementById("btn-toggle-access");
    const menuDropdown = document.getElementById("access-dropdown-menu");
    const btnVlibras = document.getElementById("trigger-vlibras");
    const btnMenuMobile = document.getElementById("btn-toggle-menu");
    const btnCloseMenu = document.getElementById("btn-close-menu");
    const overlayMenu = document.getElementById("mobile-menu-overlay");

    // 1. Alterna a exibição do menu suspenso de acessibilidade (CORRIGIDO)
    if (btnToggleAccess && menuDropdown) {
        btnToggleAccess.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            if (menuDropdown.style.display === "flex") {
                menuDropdown.style.display = "none";
            } else {
                menuDropdown.style.display = "flex";
            }
        });

        menuDropdown.addEventListener("click", (e) => {
            e.stopPropagation(); // Protege cliques internos de fecharem a caixinha
        });
    }

    // 2. Fecha a caixinha branca se o usuário clicar no fundo vazio della tela
    document.addEventListener("click", () => {
        if (menuDropdown) menuDropdown.style.display = "none";
    });

    // 3. ATIVAÇÃO DO VLIBRAS SEM SIMULAÇÃO DE CLIQUE CEGO
    if (btnVlibras) {
        btnVlibras.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            if (menuDropdown) menuDropdown.style.display = "none";

            // Se a instância do plugin estiver exposta no escopo global
            if (window.vlibrasWidget && typeof window.vlibrasWidget.toggle === 'function') {
                window.vlibrasWidget.toggle();
            } else {
                // Caça o botão real gerado dinamicamente pelo script do governo
                const activeBtnReal = document.querySelector('.vw-access-button') || 
                                       document.querySelector('[vw-access-button]') ||
                                       document.querySelector('.enabled img');
                if (activeBtnReal) {
                    activeBtnReal.click(); // Dispara o bonequinho nativo
                }
            }
        });
    }

    // 4. CONTROLE DO SEU MENU HAMBÚRGUER ORIGINAL
    if (btnMenuMobile && overlayMenu) {
        btnMenuMobile.addEventListener("click", (e) => {
            e.preventDefault();
            overlayMenu.style.display = "flex"; 
        });
    }

    if (btnCloseMenu && overlayMenu) {
        btnCloseMenu.addEventListener("click", (e) => {
            e.preventDefault();
            overlayMenu.style.display = "none"; 
        });
    }

    if (overlayMenu) {
        overlayMenu.querySelectorAll("a").forEach(link => {
            link.addEventListener("click", () => {
                overlayMenu.style.display = "none"; 
            });
        });
    }
});
/* ==========================================================================
   PARTE 2: CARGA DE DADOS DO BANCO NEON E EFEITO DE SCROLL NO HEADER
   ========================================================================= */
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
/* ==========================================================================
   PARTE 3: RENDERIZAÇÃO DE NOTÍCIAS, FILTROS E BUSCA INTELIGENTE (SEM ACENTO)
   ========================================================================= */

// Helper essencial para busca: remove acentos e deixa tudo em caixa baixa
const normalizeStr = (str) => {
    return str.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim();
};

// 1. RENDERIZAÇÃO DOS CARDS DE NOTÍCIAS COM TEMPLATE LITERALS
function renderizarNoticias(lista) {
    const gridDiv = document.getElementById('noticias-dinamicas');
    if (!gridDiv) return;
    
    gridDiv.innerHTML = "";
    
    if (lista.length === 0) {
        gridDiv.innerHTML = '<p style="grid-column: 1/-1; text-align: center; font-weight: bold; color: #666; padding: 40px 0;">Nenhuma notícia corresponde aos termos selecionados.</p>';
        return;
    }

    gridDiv.innerHTML = lista.map(post => {
        const categoriaTexto = post.category || post.categoria || 'Geral';
        const tituloTexto = post.title || post.titulo || 'Sem Título';
        const conteudoTexto = post.content || post.conteudo || 'Sem conteúdo disponível...';
        
        // ==========================================
        // SCRIPT REPARADO: SUPORTE AO CLOUDINARY E ARCHIVOS LOCAIS
        // ==========================================
        let nomeImagem = post.imageUrl || post.imagemUrl || "";
        let linkImagem = "/img/logo.png"; // Fallback se não houver imagem

        if (nomeImagem) {
            // Se já for uma URL completa da web (Cloudinary), usa direto
            if (nomeImagem.startsWith("http://") || nomeImagem.startsWith("https://")) {
                linkImagem = nomeImagem;
            } else {
                // Suporte para arquivos locais antigos
                linkImagem = nomeImagem.startsWith("/uploads/") ? nomeImagem : `/uploads/${nomeImagem}`;
            }
        }
        // ==========================================
        
        const dataBruta = post.createdAt || post.dataCriacao || new Date();
        const dataFormatada = new Date(dataBruta).toLocaleDateString('pt-BR');
        
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

// 2. SISTEMA DE FILTRO POR CLIQUE (TAGS CATEGORIAS)
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

// 3. SISTEMA DE FILTRO POR DIGITAÇÃO (BUSCA DINÂMICA INTELIGENTE)
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
