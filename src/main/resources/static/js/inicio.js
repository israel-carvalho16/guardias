

document.addEventListener("DOMContentLoaded", () => {
  initNavbarScrollAndParallax();
  initDropdownHandler();
  initScrollReveal();
  initMagneticButtons();
  initAnimatedCounters();
  initTypewriterEffect();
  initCustomCursor();
  init3DCardTilt();
  initWaterParticles();
  initWaterRippleOnClick(); // Ativa as ondas de água reais ao clicar!
});

/**
 * 1. MULTI-EFEITO: Menu, Parallax no Vídeo e Fade do Texto
 */
function initNavbarScrollAndParallax() {
  const header = document.querySelector(".site-header");
  const video = document.querySelector(".hero .video-bg");
  const heroContent = document.querySelector(".hero-content");
  
  if (!header) return;
  let ticking = false;

  const handleScrollEffects = () => {
    const scrollY = window.scrollY;
    if (scrollY > 15) header.classList.add("header-scrolled");
    else header.classList.remove("header-scrolled");

    if (video && scrollY < 600) video.style.transform = `translateY(${scrollY * 0.4}px)`;
    if (heroContent && scrollY < 500) {
      heroContent.style.opacity = Math.max(1 - scrollY / 400, 0);
      heroContent.style.transform = `translateY(${scrollY * 0.15}px)`;
    }
    ticking = false;
  };

  window.addEventListener("scroll", () => {
    if (!ticking) {
      window.requestAnimationFrame(handleScrollEffects);
      ticking = true;
    }
  }, { passive: true });
}

/**
 * 2. Fechamento do Menu Dropdown
 */
function initDropdownHandler() {
  const dropdownDetails = document.querySelector(".dropdown-item details");
  if (!dropdownDetails) return;
  document.addEventListener("click", (e) => {
    if (dropdownDetails.hasAttribute("open") && !dropdownDetails.contains(e.target)) {
      dropdownDetails.removeAttribute("open");
    }
  });
}

/**
 * 3. Revelação Suave ao Rolar (Scroll Reveal)
 */
function initScrollReveal() {
  const cards = document.querySelectorAll(".card-topic, #toma section, .team-card-item, .san-item");
  const observer = new IntersectionObserver((entries, obs) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.style.opacity = "1";
        entry.target.style.setProperty('transform', 'translateY(0)', 'important');
        obs.unobserve(entry.target);
      }
    });
  }, { threshold: 0.05 });

  cards.forEach(card => {
    card.style.opacity = "0";
    card.style.transform = "translateY(25px)";
    card.style.transition = "opacity 0.6s ease, transform 0.6s ease";
    observer.observe(card);
  });
}

/**
 * 4. EFEITO MAGNÉTICO NOS BOTÕES DO HEADER
 */
function initMagneticButtons() {
  const buttons = document.querySelectorAll(".btn-auth, .btn-submit-volun, .btn-ler-mais");
  buttons.forEach(btn => {
    btn.addEventListener("mousemove", (e) => {
      const position = btn.getBoundingClientRect();
      const x = e.clientX - position.left - position.width / 2;
      const y = e.clientY - position.top - position.height / 2;
      btn.style.transform = `translate(${x * 0.35}px, ${y * 0.35}px)`;
    });
    btn.addEventListener("mouseout", () => {
      btn.style.transform = "translate(0px, 0px)";
    });
  });
}
/**
 * 5. CONTADORES NUMÉRICOS ANIMADOS (ODOMETER)
 */
function initAnimatedCounters() {
  const counters = document.querySelectorAll(".counter-number");
  const animate = (counter) => {
    const target = +counter.getAttribute("data-target");
    const duration = 2000;
    const startTime = performance.now();

    const updateNumber = (currentTime) => {
      const elapsedTime = currentTime - startTime;
      const progress = Math.min(elapsedTime / duration, 1);
      const easeProgress = progress * (2 - progress);
      counter.textContent = Math.floor(easeProgress * target);
      if (progress < 1) window.requestAnimationFrame(updateNumber);
      else counter.textContent = target;
    };
    window.requestAnimationFrame(updateNumber);
  };

  const observer = new IntersectionObserver((entries, obs) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) { animate(entry.target); obs.unobserve(entry.target); }
    });
  }, { threshold: 0.3 });
  counters.forEach(counter => observer.observe(counter));
}

/**
 * 6. EFEITO MÁQUINA DE ESCREVER (TYPEWRITER)
 */
function initTypewriterEffect() {
  const element = document.getElementById("text-typewriter");
  if (!element) return;

  const words = ["Sustentabilidade", "Protagonismo Feminino", "Educação Ambiental", "Preservação"];
  let wordIndex = 0;
  let charIndex = 0;
  let isDeleting = false;

  const type = () => {
    const currentWord = words[wordIndex];
    if (isDeleting) {
      element.textContent = currentWord.substring(0, charIndex - 1);
      charIndex--;
    } else {
      element.textContent = currentWord.substring(0, charIndex + 1);
      charIndex++;
    }

    let typeSpeed = isDeleting ? 50 : 120;

    if (!isDeleting && charIndex === currentWord.length) {
      typeSpeed = 1500;
      isDeleting = true;
    } else if (isDeleting && charIndex === 0) {
      isDeleting = false;
      wordIndex = (wordIndex + 1) % words.length;
      typeSpeed = 400;
    }
    setTimeout(type, typeSpeed);
  };
  type();
}

/**
 * 7. CURSOR CUSTOMIZADO FLUIDO (HIGH UI MOUSE)
 */
function initCustomCursor() {
  const cursor = document.createElement("div");
  cursor.style.cssText = `
    position: fixed; width: 12px; height: 12px; background: rgba(91, 40, 158, 0.4);
    border-radius: 50%; pointer-events: none; z-index: 9999999;
    transform: translate(-50%, -50%); transition: width 0.2s, height 0.2s, background 0.2s;
    left: -100px; top: -100px; backdrop-filter: blur(1px);
  `;
  document.body.appendChild(cursor);

  document.addEventListener("mousemove", (e) => {
    cursor.style.left = e.clientX + "px";
    cursor.style.top = e.clientY + "px";
  });

  const interactives = document.querySelectorAll("a, button, details, summary");
  interactives.forEach(el => {
    el.addEventListener("mouseenter", () => {
      cursor.style.width = "40px";
      cursor.style.height = "40px";
      cursor.style.background = "rgba(91, 40, 158, 0.15)";
      cursor.style.border = "1px solid #5b289e";
    });
    el.addEventListener("mouseleave", () => {
      cursor.style.width = "12px";
      cursor.style.height = "12px";
      cursor.style.background = "rgba(91, 40, 158, 0.4)";
      cursor.style.border = "none";
    });
  });
}

/**
 * 8. EFEITO DE INCLINAÇÃO 3D (CARD TILT)
 */
function init3DCardTilt() {
  const targets = document.querySelectorAll(".card-topic, .team-card-item");
  targets.forEach(card => {
    const parent = card.parentElement;
    if (parent) parent.style.perspective = "1000px";
    card.style.transformStyle = "preserve-3d";
    card.style.transition = "transform 0.15s ease-out, box-shadow 0.15s ease-out";

    card.addEventListener("mousemove", (e) => {
      const rect = card.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;
      const rotateX = ((rect.height / 2) - y) / 10;
      const rotateY = (x - (rect.width / 2)) / 10;
      card.style.transform = `rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale3d(1.02, 1.02, 1.02)`;
      card.style.boxShadow = "0 20px 40px rgba(91, 40, 158, 0.12)";
    });

    card.addEventListener("mouseleave", () => {
      card.style.transform = "rotateX(0deg) rotateY(0deg) scale3d(1, 1, 1)";
      card.style.boxShadow = "0 4px 6px -1px rgba(91, 40, 158, 0.05)";
    });
  });
}

/**
 * 9. PARTÍCULAS DE ÁGUA LÍQUIDAS INTERATIVAS
 */
function initWaterParticles() {
  let lastMove = 0;
  document.addEventListener("mousemove", (e) => {
    const now = performance.now();
    if (now - lastMove < 35) return;
    lastMove = now;

    const particle = document.createElement("div");
    const size = Math.random() * 10 + 6;
    const colors = ["rgba(145, 164, 206, 0.6)", "rgba(145, 176, 212, 0.6)", "rgba(146, 190, 219, 0.6)"];
    const randomColor = colors[Math.floor(Math.random() * colors.length)];

    particle.style.cssText = `
      position: fixed; width: ${size}px; height: ${size}px; background: ${randomColor};
      border-radius: 50%; pointer-events: none; z-index: 999999;
      left: ${e.clientX}px; top: ${e.clientY}px; transform: translate(-50%, -50%);
      box-shadow: 0 2px 6px rgba(255,255,255,0.4); backdrop-filter: blur(0.5px);
      transition: transform 1.2s cubic-bezier(0.1, 0.8, 0.3, 1), opacity 1.2s ease-out;
    `;
    document.body.appendChild(particle);

    requestAnimationFrame(() => {
      const moveX = (Math.random() - 0.5) * 80;
      const moveY = -(Math.random() * 60 + 40);
      particle.style.transform = `translate(calc(-50% + ${moveX}px), calc(-50% + ${moveY}px)) scale(0)`;
      particle.style.opacity = "0";
    });
    setTimeout(() => { particle.remove(); }, 1200);
  });
}

/**
 * 10. ONDULAÇÃO DE ÁGUA REALISTA AO CLIQUE (WATER RIPPLE CLICK)
 */
function initWaterRippleOnClick() {
  document.addEventListener("click", (e) => {
    if (e.target.closest("a, button, input, summary")) return;

    const ripple = document.createElement("div");
    ripple.style.cssText = `
      position: fixed;
      left: ${e.clientX}px;
      top: ${e.clientY}px;
      width: 2px;
      height: 2px;
      border: 2px solid rgba(91, 40, 158, 0.4);
      background: radial-gradient(circle, rgba(146,190,219,0.2) 0%, rgba(255,255,255,0) 70%);
      border-radius: 50%;
      pointer-events: none;
      z-index: 999998;
      transform: translate(-50%, -50%) scale(1);
      opacity: 1;
      transition: transform 0.8s cubic-bezier(0.1, 0.4, 0.2, 1), opacity 0.8s ease-out, border-width 0.8s ease-out;
    `;

    document.body.appendChild(ripple);

    requestAnimationFrame(() => {
      ripple.style.transform = "translate(-50%, -50%) scale(150)";
      ripple.style.opacity = "0";
      ripple.style.borderWidth = "0.5px";
    });

    setTimeout(() => {
      ripple.remove();
    }, 800);
  });
}

