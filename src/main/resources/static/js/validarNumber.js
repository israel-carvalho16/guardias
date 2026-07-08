document.addEventListener("DOMContentLoaded", function () {
    const phoneInput = document.getElementById("volun-phone");
    const form = document.querySelector("form");

    if (phoneInput) {
        // 1. MÁSCARA EM TEMPO REAL: Formata o número enquanto o usuário digita
        phoneInput.addEventListener("input", function (e) {
            let value = e.target.value.replace(/\D/g, ""); // Remove tudo o que não for número
            
            // Limita o máximo de caracteres para 11 (DDD + 9 dígitos)
            if (value.length > 11) {
                value = value.slice(0, 11);
            }

            // Aplica a formatação (XX) XXXXX-XXXX ou (XX) XXXX-XXXX
            if (value.length > 6) {
                value = `(${value.slice(0, 2)}) ${value.slice(2, 7)}-${value.slice(7)}`;
            } else if (value.length > 2) {
                value = `(${value.slice(0, 2)}) ${value.slice(2)}`;
            } else if (value.length > 0) {
                value = `(${value}`;
            }

            e.target.value = value;
        });

        // 2. VALIDAÇÃO ANTES DO ENVIO: Garante que o telefone tenha todos os dígitos necessários
        form.addEventListener("submit", function (e) {
            // Remove parênteses, espaços e hifens para contar apenas os números reais
            const cleanValue = phoneInput.value.replace(/\D/g, "");

            // Um telefone válido no Brasil precisa ter 10 (fixo) ou 11 (celular) dígitos
            if (cleanValue.length < 10 || cleanValue.length > 11) {
                e.preventDefault(); // Bloqueia o envio do formulário para o Spring Boot
                
                // Cria ou exibe um alerta amigável na tela
                alert("Por favor, introduza um número de telefone válido com DDD (ex: (11) 99999-9999).");
                phoneInput.focus();
                phoneInput.style.borderColor = "#ff4d4f"; // Deixa a borda vermelha
            }
        });
    }
});