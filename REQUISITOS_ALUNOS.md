# Guardiãs das Águas — Requisitos para os Alunos

Projeto Spring Boot + Thymeleaf + PostgreSQL para a ONG Guardiãs das Águas.  
O sistema já possui uma **base de autenticação JWT funcional** (módulo Blog) e **formulários legados** (Admin, Voluntário, Contato) que precisam ser **unificados e corrigidos**.

---

## ‍ Backend — Sistema de Login e Cadastro de Usuários

###  Problemas a Resolver (já existentes no código)

- `fragments.html:1-24` — Arquivo com **conflitos de Git não resolvidos** (`<<<<<<< HEAD`, `>>>>>>>`). Quebra o Thymeleaf.
- `PageController.java:24-27` — Rota `/login` renderiza `VolunForm.html` (cadastro de voluntário), não a página de login do blog.
- `PageController.java:31-34` — Rota `/register` renderiza `AdminForm.html` (cadastro de administrador legado), não a página de cadastro do blog.
- `application.properties:14` — Segredo JWT `abc123` extremamente fraco e exposto.
- `application.properties:1` — Credenciais do banco Neon PostgreSQL expostas em texto plano.
- `SecurityConfig.java:75` — `POST /api/posts` está público (comentário: *"Liberado o POST temporariamente para salvar posts em casa sem travar no token"*).
- `Repo.java` (Admin) — Senha armazenada **em texto plano** (campo `senha`), sem BCrypt.
- `ControlerVolun.java:31-32` — Senha do voluntário é concatenada no campo `links` junto com CPF, telefone e gênero, **em texto plano**.
- `api.js:1` — URL da API hardcoded para `http://localhost:8080/api`.

###  Tarefas Obrigatórias (Backend)

#### 1. Resolver conflitos de Git
- Corrigir `src/main/resources/templates/fragments.html` removendo todos os marcadores `<<<<<<<`, `=======`, `>>>>>>>`.
- Garantir que o HTML resultante seja válido (DOCTYPE, `<head>`, `<body>`, fechamento correto).

#### 2. Corrigir rotas do PageController
- **`/login`** deve renderizar `login.html` (página de login do blog com JWT).
- **`/register`** deve renderizar `register.html` (página de cadastro do blog com JWT).
- Mover as rotas dos formulários legados para caminhos separados:
  - `/AdminForm` → formulário de cadastro de administrador (já existe)
  - `/voluntario` → formulário de cadastro de voluntário (nova rota a ser criada)

#### 3. Unificar o sistema de cadastro/autenticação
- **Substituir** as tabelas legadas (`admin`, `tb_voluntario`) pelo modelo `User` + `Role` do módulo Blog.
- Todo cadastro (admin, voluntário, usuário comum) deve:
  - Utilizar a entidade `User` (`Blog/model/User.java`)
  - Criptografar senha com **BCrypt** (`BCryptPasswordEncoder`)
  - Atribuir a role correta (`ROLE_USER`, `ROLE_ADMIN`) conforme o tipo de formulário
- Os formulários `AdminForm.html` e `VolunForm.html` devem enviar os dados para endpoints REST (`/api/auth/register`) com os campos adequados.

#### 4. Implementar validações no registro de usuário
- **Email**: validar formato, verificar se já existe no banco (retornar erro 409).
- **Senha**: mínimo 8 caracteres, pelo menos 1 letra maiúscula, 1 número e 1 caractere especial.
- **Nome**: não vazio, mínimo 3 caracteres.
- **CPF**: validar formato e dígitos verificadores (para formulários de admin/voluntário).
- Retornar mensagens de erro claras em português no `AuthResponse` ou via `GlobalExceptionHandler`.

#### 5. Criar endpoint de Logout
- Implementar `POST /api/auth/logout`.
- Estratégia: **Blacklist de tokens** — armazenar tokens invalidados em uma tabela `token_blacklist` com data de expiração.
- O `JwtFilter` deve verificar se o token está na blacklist antes de autorizar.

#### 6. Criar endpoint de recuperação de senha
- `POST /api/auth/forgot-password` — recebe email, gera token de recuperação, salva na tabela `password_reset_tokens`.
- `POST /api/auth/reset-password` — recebe token + nova senha, valida e atualiza.
- (Opcional) Envio de email real via Spring Mail — podem simular com log no console.

#### 7. Criar perfil de usuário
- `GET /api/users/me` — retorna dados do usuário autenticado.
- `PUT /api/users/me` — permite editar nome e email.
- `PUT /api/users/me/password` — permite alterar senha (exige senha atual + nova senha).

#### 8. Proteger endpoints corretamente
- `POST /api/posts` → exige autenticação (`hasAnyRole('USER', 'ADMIN')`). Remover o `.permitAll()` temporário.
- `POST /api/comments/post/{postId}` → exige autenticação (já está).
- `PUT /api/posts/{id}` → apenas `ADMIN` ou o **autor** do post.
- `DELETE /api/posts/{id}` → apenas `ADMIN` ou o **autor** do post.

#### 9. Melhorias de segurança
- Mover `jwt.secret` para variável de ambiente (`${JWT_SECRET}`) — nunca hardcoded.
- Mover credenciais do banco para variáveis de ambiente (`${DB_URL}`, `${DB_USER}`, `${DB_PASSWORD}`).
- Gerar segredo JWT com pelo menos 256 bits (ex: usar `openssl rand -base64 32`).
- Adicionar `@Valid` em todos os endpoints que recebem body.
- Adicionar tratamento de erro 403 (Forbidden) no `GlobalExceptionHandler`.

#### 10. Criar DTOs e endpoints para os formulários legados unificados
- `POST /api/auth/register/admin` — cadastro de administrador (campos extras: CPF).
- `POST /api/auth/register/volunteer` — cadastro de voluntário (campos extras: CPF, telefone, gênero).
- Campos extras devem ser armazenados em uma nova tabela `user_profiles` (OneToOne com `User`) ou como colunas adicionais na tabela `users`.

---

##  Frontend — Sugestões para o Projeto

###  Funcionalidades que Podem Ser Adicionadas

#### Páginas Novas
- **Perfil do Usuário** (`profile.html`)
  - Exibir/editar nome, email, foto de perfil.
  - Histórico de comentários e posts do usuário.
  - Botão para alterar senha.

- **Recuperação de Senha** (`forgot-password.html`, `reset-password.html`)
  - Formulário de "Esqueci minha senha" com envio de email.
  - Página de redefinição com token na URL.

- **Dashboard do Voluntário** (`volunteer-dashboard.html`)
  - Lista de atividades/eventos em que o voluntário participa.
  - Status de inscrições.

- **Painel de Controle Ambiental** (`dashboard.html`)
  - Gráficos de qualidade da água (simulados ou de API externa).
  - Mapa interativo com marcadores dos núcleos MG e CE.
  - Cards com estatísticas (nº de voluntários, rios monitorados, ações realizadas).

- **Galeria de Fotos** (`galeria.html`)
  - Grid/masonry de imagens com lightbox.
  - Filtros por categoria (eventos, rios, equipe).

- **Calendário de Eventos** (`eventos.html` — expandir o existente)
  - Calendário interativo (usar biblioteca como FullCalendar).
  - Inscrição em eventos com confirmação.

#### Componentes e Melhorias de UI/UX
- **Modo escuro/claro** — toggle no navbar, preferência salva em `localStorage`.
- **Navbar responsivo** com menu hamburguer para mobile.
- **Skeleton loaders** — enquanto os dados da API carregam, exibir placeholders animados.
- **Toast notifications** — substituir `alert()` por notificações não-bloqueantes.
- **Scroll infinito ou paginação** na listagem de posts (`index.html`).
- **Busca global** — campo de busca no topo que pesquisa posts, notícias e eventos.
- **Validação em tempo real** nos formulários (feedback visual enquanto digita).
- **Confirmação de senha com indicador de força** (barra de progresso: fraca/média/forte).
- **Upload de imagem** nos posts (campo de capa/thumbnail para cada post).
- **Preview do post** no admin antes de publicar (renderização markdown ou HTML).

#### Acessibilidade (a11y)
- Navegação completa por teclado (tabindex, focus visível).
- Textos alternativos (`alt`) em todas as imagens.
- Labels e ARIA attributes em todos os formulários.
- Contraste mínimo WCAG AA em todas as cores.

#### Progressive Web App (PWA)
- `manifest.json` para instalação em dispositivos móveis.
- Service Worker para cache offline das páginas principais.
- Ícones e splash screen para Android/iOS.

#### Integrações Externas
- **API de qualidade da água** — integrar com dados públicos (ex: ANA, IGAM) e exibir no dashboard.
- **Compartilhamento em redes sociais** — botões de share nos posts e notícias.
- **Mapa com Leaflet.js ou Google Maps** — mostrar núcleos MG/CE, rios monitorados e pontos de coleta.
- **Newsletter** — formulário de inscrição que salva emails em uma tabela `subscribers`.

#### Refatorações e Organização
- Separar o JavaScript inline dos templates HTML em arquivos `.js` dedicados.
- Criar um sistema de componentes reutilizáveis com Thymeleaf fragments (header, footer, navbar, cards).
- Corrigir o `fragments.html` e usá-lo efetivamente nos templates via `th:replace` / `th:include`.

---

##  Resumo das Prioridades

###  Obrigatório (nota mínima)
| Prioridade | Tarefa |
|---|---|
|  Crítica | Resolver conflitos do Git no `fragments.html` |
|  Crítica | Corrigir rotas `/login` e `/register` no `PageController` |
|  Crítica | Unificar cadastro: usar `User` + BCrypt para todos os formulários |
|  Crítica | Fechar `POST /api/posts` (exigir autenticação) |
|  Alta | Validar email, senha forte, nome no registro |
|  Alta | Mover secrets para variáveis de ambiente |

###  Desejável (nota extra)
| Prioridade | Tarefa |
|---|---|
|  Média | Implementar logout com blacklist de tokens |
|  Média | Criar endpoint de recuperação de senha |
|  Média | Criar perfil de usuário (GET/PUT /api/users/me) |
|  Média | CRUD de posts: autor pode editar/excluir seu próprio post |
|  Baixa | Página de perfil no frontend |
|  Baixa | Modo escuro/claro |
|  Baixa | Dashboard com gráficos e mapa interativo |

---

##  Observações Técnicas

- **Stack**: Java 21, Spring Boot 4.0.6, Spring Security, Spring Data JPA, Thymeleaf, PostgreSQL, Maven.
- **Frontend**: Thymeleaf + JavaScript vanilla (sem React/Vue/Angular). Animações com anime.js.
- **Testes**: Cypress configurado no `package.json` para testes E2E.
- **Banco**: PostgreSQL no Neon Cloud (já provisionado). JPA `ddl-auto=update`.
- **Autenticação**: JWT stateless com filtro `JwtFilter`. Roles: `ROLE_USER` e `ROLE_ADMIN`.
- **Arquivos de entrada**:
  - `OmniApplication.java` — main class
  - `Blog/config/SecurityConfig.java` — regras de segurança
  - `Blog/controller/AuthController.java` — endpoints de auth (registro e login)
  - `Blog/service/AuthService.java` — lógica de autenticação
  - `Blog/security/JwtFilter.java` — filtro de tokens
  - `Blog/security/jwt/JwtService.java` — geração/validação de JWT
  - `Blog/controller/PageController.java` — rotas MVC (Thymeleaf)
  - `Blog/model/User.java` — entidade de usuário
  - `static/js/api.js` — cliente HTTP
  - `static/js/auth.js` — gerenciamento de sessão no frontend


##codigos:<nav class="header-nav"> 
      <ul class="nav-links"> 
        <li><a th:href="@{/pagina1}">Início</a></li>
        <li><a th:href="@{/noticias}">Notícias</a></li>
        <li><a th:href="@{/projeto}">Projetos</a></li> 
        <li><a th:href="@{/evento}">Eventos</a></li> 
        <li class="dropdown-item"> 
          <details> 
            <summary id="Nucleos">Nossos Núcleos</summary> 
            <div class="dropdown-content" role="menu">
              <a th:href="@{/mg}" role="menuitem">MG</a> 
              <a th:href="@{/ce}" role="menuitem">CE</a> 
            </div>
          </details> 
        </li>
        <li><a th:href="@{/contatos}">Contatos</a></li>
      </ul>
    </nav>
