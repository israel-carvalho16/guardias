
// Verifica se veio erro de CPF inválido pelo redirect do backend
const params = new URLSearchParams(window.location.search);
if (params.get('erro') === 'cpf_invalido') {
    document.getElementById('erro-cpf-backend').style.display = 'block';
}
const cpfInput = document.getElementById('volun-cpf');
const cpfErroMsg = document.createElement('span');
cpfErroMsg.style.color = '#ff4d4f';
cpfErroMsg.style.fontSize = '0.85em';
cpfErroMsg.style.display = 'none';
cpfInput.parentNode.appendChild(cpfErroMsg);


// Mesma lógica do backend, espelhada em JS
function isCpfValido(cpf) {
    const cpfLimpo = cpf.replace(/[^0-9]/g, '');


    if (cpfLimpo.length !== 11) return false;
    if (/^(\d)\1{10}$/.test(cpfLimpo)) return false; // sequências repetidas


    const numeros = cpfLimpo.split('').map(Number);


    let soma = 0;
    for (let i = 0; i < 9; i++) {
        soma += numeros[i] * (10 - i);
    }
    let resto = soma % 11;
    const digito1 = resto < 2 ? 0 : 11 - resto;
    if (digito1 !== numeros[9]) return false;


    soma = 0;
    for (let i = 0; i < 10; i++) {
        soma += numeros[i] * (11 - i);
    }
    resto = soma % 11;
    const digito2 = resto < 2 ? 0 : 11 - resto;
    return digito2 === numeros[10];
}


// Máscara automática enquanto digita: 000.000.000-00
cpfInput.addEventListener('input', () => {
    let valor = cpfInput.value.replace(/\D/g, '').slice(0, 11);
    valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
    valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
    valor = valor.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    cpfInput.value = valor;
});


// Valida quando o usuário sai do campo
cpfInput.addEventListener('blur', () => {
    if (cpfInput.value && !isCpfValido(cpfInput.value)) {
        cpfInput.setCustomValidity('CPF inválido');
        cpfErroMsg.textContent = 'CPF inválido. Verifique o número digitado.';
        cpfErroMsg.style.display = 'block';
        cpfInput.style.borderColor = '#ff4d4f';
    } else {
        cpfInput.setCustomValidity('');
        cpfErroMsg.style.display = 'none';
        cpfInput.style.borderColor = '';
    }
});


// Bloqueia o envio do form se o CPF ainda estiver inválido
document.querySelector('form').addEventListener('submit', (e) => {
    if (!isCpfValido(cpfInput.value)) {
        e.preventDefault();
        cpfInput.setCustomValidity('CPF inválido');
        cpfInput.reportValidity();
        cpfErroMsg.textContent = 'CPF inválido. Verifique o número digitado.';
        cpfErroMsg.style.display = 'block';
        cpfInput.style.borderColor = '#ff4d4f';
    }
});

