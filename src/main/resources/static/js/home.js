/* ==========================================================================
   ARQUIVO JAVASCRIPT UNIFICADO E BLINDADO CONTRA ACENTOS - GUARDIÃS DAS ÁGUAS
   ========================================================================== */

document.addEventListener("DOMContentLoaded", () => {
  
  // 1. CONTROLE DINÂMICO E SEGURO DO CABEÇALHO (SCROLL)
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

      if (currentScrollY > 150) {
        if (currentScrollY > lastScrollY) {
          header.style.transform = "translateY(-100%)";
        } else {
          header.style.transform = "translateY(0)";
        }
      } else {
        header.style.transform = "translateY(0)";
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

  // 2. SISTEMA DE FILTRAGEM DE NOTÍCIAS ULTRA-ESTÁVEL (MÁXIMA UX)
  const searchInput = document.querySelector(".search-box-wrapper input");
  const filterTags = document.querySelectorAll(".filter-tag");
  const newsCards = document.querySelectorAll(".card-noticia");

  if (newsCards.length > 0) {
    
    // Função auxiliar para remover acentos e espaços extras das strings
    const normalizeString = (str) => {
      return str
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "") // Remove acentos cirurgicamente
        .trim();
    };

    const filterNews = () => {
      const searchText = searchInput ? normalizeString(searchInput.value) : "";
      const activeTag = document.querySelector(".filter-tag.active");
      const activeCategory = activeTag ? normalizeString(activeTag.textContent) : "todas";

         newsCards.forEach(card => {
        const title = normalizeString(card.querySelector("h2").textContent);
        const description = normalizeString(card.querySelector("p").textContent);
        const cardCategory = normalizeString(card.getAttribute("data-category") || "");

        const matchesSearch = title.includes(searchText) || description.includes(searchText);
        
        // MÁGICA DA FILTRAGEM MULTIPLA: Se o texto do botão estiver contido na lista do card, ele passa!
        const matchesCategory = (activeCategory === "todas") || (cardCategory.includes(activeCategory));

        if (matchesSearch && matchesCategory) {
          card.style.display = "flex"; 
          card.style.opacity = "1";
          card.style.transform = "scale(1)";
        } else {
          card.style.display = "none";
          card.style.opacity = "0";
          card.style.transform = "scale(0.9)";
        }

      });
    };

    // Ouvinte para a Barra de Busca
    if (searchInput) {
      searchInput.addEventListener("input", filterNews);
    }

    // Ouvinte para as Tags de Categoria
    if (filterTags.length > 0) {
      filterTags.forEach(tag => {
        tag.addEventListener("click", () => {
          filterTags.forEach(t => {
            t.classList.remove("active");
            t.setAttribute("aria-selected", "false");
          });
          tag.classList.add("active");
          tag.setAttribute("aria-selected", "true");

          filterNews(); // Executa o filtro cruzado imune a acentos
        });
      });
    }
  }
});
