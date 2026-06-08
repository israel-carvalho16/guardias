document.getElementById("formLogin").addEventListener("submit", function(e) {
    
    let email = document.querySelector("input[name='username']").value;
    let senha = document.querySelector("input[name='password']").value;
    
    if (email === "" || senha === "") {
        alert("Preencha todos os campos!");
        e.preventDefault();
    }
});