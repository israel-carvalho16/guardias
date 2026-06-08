function login() {
    const btnText = document.getElementById("btnText");
    const loader = document.getElementById("loader");
    let user = document.getElementById("usuario").value;
    let pass = document.getElementById("senha").value;
    
    let userSalvo = localStorage.getItem("user");
    let passSalvo = localStorage.getItem("pass");

    // DEFINA AS CREDENCIAIS DO ADMIN AQUI
    const ADMIN_USER = "admin";
    const ADMIN_PASS = "12345";

    btnText.style.display = "none";
    loader.style.display = "block";

    setTimeout(() => {
        // 1. VERIFICA SE É ADMIN
        if (user === ADMIN_USER && pass === ADMIN_PASS) {
            localStorage.setItem("status", "admin");
            document.getElementById("tela-login").style.display = "none";
            alert("Bem-vindo, Administrador!");
            // Aqui você pode mostrar botões que só o admin vê
        } 
        // 2. VERIFICA SE É USUÁRIO COMUM
        else if (user === userSalvo && pass === passSalvo && user !== "") {
            localStorage.setItem("status", "logado");
            document.getElementById("tela-login").style.display = "none";
        } 
        // 3. FALHA NO LOGIN
        else {
            btnText.style.display = "block";
            loader.style.display = "none";
            mostrarMensagem("Dados Incorretos!", "#ff4d4d");
        }
    }, 1200);
}
