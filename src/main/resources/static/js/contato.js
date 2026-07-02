document.addEventListener("DOMContentLoaded", function () {
  // Ajustado para os seletores reais do seu HTML
  const radios = document.querySelectorAll(".rating-pastilles-wrapper input[type='radio']");
  const labels = document.querySelectorAll(".rating-pastilles-wrapper label");

  radios.forEach(function (radio) {
    radio.addEventListener("change", function () {
      // Limpa as classes de todas as labels antes de aplicar as novas
      labels.forEach(function (label) {
        label.classList.remove("red", "green");
      });

      const valorSelecionado = parseInt(this.value);

      // Proteção caso o value não seja um número válido
      if (isNaN(valorSelecionado)) return;

      // Percorre as labels para pintar de vermelho ou verde
      labels.forEach(function (label) {
        const numero = parseInt(label.textContent.trim());

        // Se o número da label for menor ou igual ao que o usuário clicou
        if (!isNaN(numero) && numero <= valorSelecionado) {
          if (valorSelecionado < 5) {
            label.classList.add("red");
          } else {
            label.classList.add("green");
          }
        }
      });
    });
  });
});