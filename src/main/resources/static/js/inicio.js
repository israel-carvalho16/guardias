/* ==========================================================================
   COMPORTAMENTOS INTERATIVOS - GUARDIÃS DAS ÁGUAS
   ========================================================================== */

   document.addEventListener("DOMContentLoaded", () => {
    initNavbarScroll();
    initDropdownHandler();
  });
  
  /**
   * 1. Efeito Sticky no Cabeçalho ao rolar a página
   */
  function initNavbarScroll() {
    const header = document.querySelector(".site-header");
    if (!header) return;
  
    window.addEventListener("scroll", () => {
      if (window.scrollY > 20) {
        header.classList.add("header-scrolled");
      } else {
        header.classList.remove("header-scrolled");
      }
    });
  }
  
  /**
   * 2. Gerenciador do Menu Dropdown (Nossos Núcleos)
   * Fecha o menu se o usuário clicar em qualquer outro lugar da tela
   */
  function initDropdownHandler() {
    const dropdownDetails = document.querySelector(".dropdown-item details");
    
    if (!dropdownDetails) return;
  
    document.addEventListener("click", (event) => {
      // Se o menu estiver aberto e o clique ocorrer fora do elemento <details>
      if (dropdownDetails.hasAttribute("open") && !dropdownDetails.contains(event.target)) {
        dropdownDetails.removeAttribute("open");
      }
    });
  }
  