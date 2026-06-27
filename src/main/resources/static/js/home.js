/* ==========================================================================
   SISTEMA DE INTERAÇÃO GLOBAL - PORTAL DE NOTÍCIAS (PRODUÇÃO)
   ========================================================================= */

document.addEventListener("DOMContentLoaded", async () => {
  
  // 1. CONTROLE DO CABEÇALHO (SCROLL PERFORMANCE COM THROTTLE)
  const header = document.querySelector(".site-header");
  if (header) {
    let lastScrollY = window.scrollY;
    let ticking = false;

    const updateHeaderState = () => {
      const currentScrollY = window.scrollY;
      if (currentScrollY > 15) {
        header.classList.add("header-scrolled");
      } else {
        header.classList.remove("header-scrolled");
      }
      lastScrollY = currentScrollY;
      ticking = false;
    };

    window.addEventListener("scroll", () => {
      if (!ticking) {
        window.requestAnimationFrame(updateHeaderState);
        ticking = true;
      }
    }, { passive: true });
  }

  // 2. CONSUMO DA API DO BANCO DE DADOS (POSTS DINÂMICOS DO ADMIN)
  const grid = document.getElementById("noticias-dinamicas");
  const loadingNews = document.getElementById("loading-news");

  try {
    // Chamada AJAX para buscar os posts reais salvos no banco
    const response = await fetch('/api/posts');
    if (!response.ok) throw new Error("Erro na requisição da API");
    
    const posts = await response.json();

    // Injeta cada post vindo do banco de dados no início do grid
    posts.forEach(post => {
      const article = document.createElement("article");
      article.className = "card-noticia";
      
      // Vincula dinamicamente os posts criados às categorias padrão
      article.setAttribute("data-category", "saneamento recursos hidricos");

      article.innerHTML = `
        <div class="card-thumb">
          <img src="/img/logo.png" alt="Capa da notícia">
        </div>
        <div class="card-body">
          <h2>${post.title}</h2>
          <p>${post.content}</p>
          <span class="card-date">${new Date(post.createdAt || new Date()).toLocaleDateString('pt-BR')}</span>
          <a href="/post?id=${post.id}" class="btn-ler-mais">Ler mais</a>
        </div>
      `;
      
      // Insere no topo (início) da grade de notícias
      if (grid) {
        grid.insertBefore(article, grid.firstChild);
      }
    });

  } catch (error) {
    console.log("Modo de desenvolvimento: Usando apenas os cards estáticos locais.", error);
  } finally {
    // Esconde o spinner de carregamento após a tentativa de fetch
    if (loadingNews) {
      loadingNews.style.display = "none";
    }
  }

  // 3. SISTEMA DE FILTRAGEM AVANÇADO (BUSCA + TAGS COMBINADAS)
  const searchInput = document.getElementById("search-input");
  const filterTags = document.querySelectorAll(".filter-tag");

  // Helper indispensável para ignorar acentos e letras maiúsculas/minúsculas
  const normalizeString = (str) => {
    return str
      .toLowerCase()
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .trim();
  };

  const filterNews = () => {
    const searchText = searchInput ? normalizeString(searchInput.value) : "";
    const activeTag = document.querySelector(".filter-tag.active");
    const activeCategory = activeTag ? normalizeString(activeTag.getAttribute("data-filter") || "all") : "all";

    // CRÍTICO: Captura os cards ATUALIZADOS na tela toda vez que o usuário interage
    // Isso garante que os posts recém-chegados da API também entrem no filtro!
    const currentCards = document.querySelectorAll(".card-noticia");

    currentCards.forEach(card => {
      const title = normalizeString(card.querySelector("h2").textContent);
      const description = normalizeString(card.querySelector("p").textContent);
      const cardCategory = normalizeString(card.getAttribute("data-category") || "");

      const matchesSearch = title.includes(searchText) || description.includes(searchText);
      const matchesCategory = (activeCategory === "all") || (cardCategory.includes(activeCategory));

      if (matchesSearch && matchesCategory) {
        // Força o card a voltar para o fluxo simétrico do Flexbox
        card.style.setProperty('display', 'flex', 'important');
        card.style.setProperty('position', 'relative', 'important');
        card.style.setProperty('float', 'none', 'important');
        card.style.opacity = "1";
        card.style.transform = "scale(1)";
      } else {
        // Oculta e move de forma absoluta para não quebrar o layout vizinho
        card.style.setProperty('display', 'none', 'important');
        card.style.setProperty('position', 'absolute', 'important');
        card.style.opacity = "0";
        card.style.transform = "scale(0.95)";
      }
    });
  };

  // Ouvinte de digitação na barra de pesquisa
  if (searchInput) {
    searchInput.addEventListener("input", filterNews);
  }

  // Ouvinte de cliques nas tags de categoria
  if (filterTags.length > 0) {
    filterTags.forEach(tag => {
      tag.addEventListener("click", (e) => {
        e.preventDefault();
        
        // Reseta estados visuais e de acessibilidade de todas as tags
        filterTags.forEach(t => {
          t.classList.remove("active");
          t.setAttribute("aria-selected", "false");
        });
        
        // Ativa a tag atual clicada
        tag.classList.add("active");
        tag.setAttribute("aria-selected", "true");
        
        // Executa o filtro refinado
        filterNews();
      });
    });
  }
});