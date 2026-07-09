
    document.addEventListener("DOMContentLoaded", function () {
        const cpfInput = document.getElementById("cpf");
        const form = document.getElementById("adminForm");

        // 1. MÁSCARA DE FORMATAÇÃO (000.000.000-00)
        cpfInput.addEventListener("input", function (e) {
            let value = e.target.value.replace(/\D/g, ""); // Remove tudo que não for número
            
            if (value.length > 11) value = value.slice(0, 11); // Garante o limite de 11 dígitos

            // Aplica a formatação por etapas
            if (value.length > 9) {
                value = value.replace(/^(\d{3})(\d{3})(\d{3})(\d{1,2})$/, "$1.$2.$3-$4");
            } else if (value.length > 6) {
                value = value.replace(/^(\d{3})(\d{3})(\d{1,3})$/, "$1.$2.$3");
            } else if (value.length > 3) {
                value = value.replace(/^(\d{3})(\d{1,3})$/, "$1.$2");
            }

            e.target.value = value;
        });

        // 2. FUNÇÃO DE VALIDAÇÃO MATEMÁTICA DO CPF
        function validaCPF(cpf) {
            // Remove pontos e traços
            cpf = cpf.replace(/\D/g, "");

            // Verifica se tem 11 dígitos ou se é uma sequência repetida conhecida
            if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) {
                return false;
            }

            // Validação do primeiro dígito verificador
            let soma = 0;
            let resto;
            for (let i = 1; i <= 9; i++) {
                soma = soma + parseInt(cpf.substring(i - 1, i)) * (11 - i);
            }
            resto = (soma * 10) % 11;
            if (resto === 10 || resto === 11) resto = 0;
            if (resto !== parseInt(cpf.substring(9, 10))) return false;

            // Validação do segundo dígito verificador
            soma = 0;
            for (let i = 1; i <= 10; i++) {
                soma = soma + parseInt(cpf.substring(i - 1, i)) * (12 - i);
            }
            resto = (soma * 10) % 11;
            if (resto === 10 || resto === 11) resto = 0;
            if (resto !== parseInt(cpf.substring(10, 11))) return false;

            return true;
        }

        // 3. INTERCEPTA O ENVIO DO FORMULÁRIO
        form.addEventListener("submit", function (e) {
            const cpfValue = cpfInput.value;

            if (!validaCPF(cpfValue)) {
                e.preventDefault(); // Bloqueia o envio do formulário
                alert("Por favor, insira um CPF válido.");
                cpfInput.focus();
                cpfInput.style.borderColor = "#ff4a4a"; // Destaca o campo com erro
            } else {
                cpfInput.style.borderColor = ""; // Reseta a cor se estiver correto
            }
        });
    });
