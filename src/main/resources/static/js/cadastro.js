document.getElementById("formCadastro").addEventListener("submit", function(e){
	let nome = document.querySelector("input[name='nome']").value;
	let email = document.querySelector("input[name:'email']").value;
	let senha = document.querySelector("input[name:'senha']").value;
	
	if(nome.length < 3){
		alert("Nome deve ter pelo menos 3 caracteres");
		e.preventDefault();
		return;
	}
	if(!email.includes("@")){
		alert("Email inválido");
		e.preventDefault();
	}
	if(senha.lenght < 4){
    alert("Senha muito curta");
	e.preventDefault();  
	}
})