function toggleSenha() {
    const passInput = document.getElementById("senha");
    const eyeIcon = document.getElementById("togglePassword");
   
    if (passInput.type === "password") {
        passInput.type = "text";
        eyeIcon.classList.replace("fa-eye", "fa-eye-slash");
    } else {
        passInput.type = "password";
        eyeIcon.classList.replace("fa-eye-slash", "fa-eye");
    }
}


function cadastrar() {
    let user = document.getElementById("usuario").value;
    let email = document.getElementById("email").value;
    let pass = document.getElementById("senha").value;


    if (user === "" || email === "" || pass === "") {
        mostrarMensagem("A água exige preenchimento total!", "yellow");
        return;
    }


    localStorage.setItem("user", user);
    localStorage.setItem("pass", pass);
    mostrarMensagem("Essência guardada! Agora pode entrar.", "#00ff88");
}


function login() {
    const btnText = document.getElementById("btnText");
    const loader = document.getElementById("loader");
    let user = document.getElementById("usuario").value;
    let pass = document.getElementById("senha").value;
    let userSalvo = localStorage.getItem("user");
    let passSalvo = localStorage.getItem("pass");


    btnText.style.display = "none";
    loader.style.display = "block";


    setTimeout(() => {
        if (user === userSalvo && pass === passSalvo && user !== "") {
            localStorage.setItem("status", "logado");
           
            // EM VEZ DE RECARREGAR A PÁGINA, APENAS ESCONDEMOS O LOGIN:
            document.getElementById("tela-login").style.display = "none";
           
        } else {
            btnText.style.display = "block";
            loader.style.display = "none";
            mostrarMensagem("Correntes marítimas dizem: Dados Incorretos!", "#ff4d4d");
        }
    }, 1200);
}


function visitante() {
    localStorage.setItem("status", "visitante");
    // APENAS ESCONDE A TELA PARA MOSTRAR O SITE QUE JÁ ESTÁ ATRÁS:
    document.getElementById("tela-login").style.display = "none";
}




function mostrarMensagem(texto, cor) {
    const erroEl = document.getElementById("erro");
    erroEl.innerText = texto;
    erroEl.style.color = cor;
}
window.onload = function() {
    // Garante que o login apareça sempre que a página carregar (para testes)
    document.getElementById("tela-login").style.display = "flex";
}


