/* ==========================================================================
   ARQUIVO JAVASCRIPT CORRIGIDO - FORÇA O REARRANJO DOS CARDS NO GRID
   ========================================================================== */

document.addEventListener("DOMContentLoaded", () => {
  
  // 1. CONTROLE DO CABEÇALHO (SCROLL)
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

  // 2. SISTEMA DE FILTRAGEM COM RESOLUÇÃO DE POSICIONAMENTO E FLUXO
  const searchInput = document.getElementById("search-input");
  const filterTags = document.querySelectorAll(".filter-tag");
  const newsCards = document.querySelectorAll(".card-noticia");

  const loadingNews = document.getElementById("loading-news");
  if (loadingNews) loadingNews.style.display = "none";

  if (newsCards.length > 0) {
    
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

      newsCards.forEach(card => {
        const title = normalizeString(card.querySelector("h2").textContent);
        const description = normalizeString(card.querySelector("p").textContent);
        const cardCategory = normalizeString(card.getAttribute("data-category") || "");

        const matchesSearch = title.includes(searchText) || description.includes(searchText);
        const matchesCategory = (activeCategory === "all") || (cardCategory.includes(activeCategory));

        if (matchesSearch && matchesCategory) {
          // FORÇA O CARD A ENTRAR NO FLUXO RELATIVO NATURAL DO GRID, FAZENDO ELE SE MOVER
          card.style.setProperty('display', 'flex', 'important');
          card.style.setProperty('position', 'relative', 'important');
          card.style.setProperty('float', 'none', 'important');
          card.style.opacity = "1";
          card.style.transform = "scale(1)";
        } else {
          // REMOVE COMPLETAMENTE O ESPAÇO E REALOCA EM ABSOLUTO FORA DA TELA PARA NÃO TRAVAR NINGUÉM
          card.style.setProperty('display', 'none', 'important');
          card.style.setProperty('position', 'absolute', 'important');
          card.style.opacity = "0";
          card.style.transform = "scale(0.95)";
        }
      });
    };

    if (searchInput) searchInput.addEventListener("input", filterNews);

    if (filterTags.length > 0) {
      filterTags.forEach(tag => {
        tag.addEventListener("click", (e) => {
          e.preventDefault();
          filterTags.forEach(t => t.classList.remove("active"));
          tag.classList.add("active");
          filterNews();
        });
      });
    }
  }
});
