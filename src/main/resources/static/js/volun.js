document.getElementById('form-voluntario').addEventListener('submit', async (e) => {
    e.preventDefault(); // Impede o recarregamento da página

    // Captura os dados do formulário
    const formData = new FormData(e.target);
    const dados = Object.fromEntries(formData.entries());

    try {
        const response = await fetch('/api/auth/volun-form', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json' // Avisa o Spring que é um JSON
            },
            body: JSON.stringify(dados) // Transforma os campos em JSON
        });

        if (response.ok) {
            alert('Inscrição enviada com sucesso!');
            window.location.href = '/pagina1';
        } else {
            document.getElementById('erro-cpf-backend').style.display = 'block';
        }
    } catch (error) {
        console.error('Erro ao enviar:', error);
    }
});