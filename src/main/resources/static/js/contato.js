document.addEventListener("DOMContentLoaded", function () {
  const radios = document.querySelectorAll(".rating input");
  const labels = document.querySelectorAll(".rating label");

  radios.forEach(function (radio) {
    radio.addEventListener("change", function () {
      labels.forEach(function (label) {
        label.classList.remove("red");
        label.classList.remove("green");
      });

      const valorSelecionado = parseInt(this.value);

      labels.forEach(function (label) {
        const numero = parseInt(label.textContent);

        if (numero <= valorSelecionado) {
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