# Guardiãs das Águas — Requisitos para Entrega do Projeto

> **Última atualização: 04/07/2026**
> Pessoal, reescrevi esse documento do zero depois de analisar TODO o repositório arquivo por arquivo. Está tudo aqui: o que já funciona, o que está quebrado e o que vocês precisam entregar. Leiam com calma, sigam a ordem de prioridade e foquem primeiro no que é **obrigatório**. Qualquer dúvida, me chamem no Classroom.

---

## 📋 COMO RODAR O PROJETO

```bash
# 1. Clonar o repositório
git clone <url-do-repo>

# 2. Rodar o projeto (conecta direto no banco Neon PostgreSQL)
./mvnw spring-boot:run

# 3. Acessar no navegador
http://localhost:8080

# 4. Rodar com Docker (alternativa)
docker compose up --build
# Acessa em http://localhost:8084
```

**⚠️ IMPORTANTE:** O projeto roda com PostgreSQL no Neon Cloud (já provisionado). NÃO tem H2 em memória. Se o banco Neon estiver fora do ar, o projeto não sobe.

---

## 🗂️ ESTRUTURA DO PROJETO — Mapa Completo

Pra ninguém se perder, aqui está como o repositório está organizado:

```
guardias/
├── src/main/java/com/project/omni/
│   ├── OmniApplication.java                    ← Classe main do Spring Boot
│   ├── WebConfig.java                           ← ViewControllers + ResourceHandlers
│   │
│   ├── Blog/                                    ← Módulo principal (Blog + Auth JWT)
│   │   ├── config/
│   │   │   ├── SecurityConfig.java              ← Spring Security + JWT Filter
│   │   │   └── DataInitializer.java             ← Cria roles ROLE_USER e ROLE_ADMIN no boot
│   │   ├── controller/
│   │   │   ├── PageController.java              ← ⚠️ Rotas MVC (login e register ERRADOS)
│   │   │   ├── AuthController.java              ← REST: POST /api/auth/login e /register
│   │   │   ├── PostController.java              ← REST: CRUD de posts (/posts)
│   │   │   └── CommentController.java           ← REST: Comentários (/api/comments)
│   │   ├── dto/
│   │   │   ├── request/  → LoginRequest, RegisterRequest, PostRequest, CommentRequest
│   │   │   └── response/ → AuthResponse, PostResponse, CommentResponse
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java      ← Handlers de 404, validação, runtime
│   │   │   └── ResourceNotFoundException.java
│   │   ├── model/
│   │   │   ├── User.java                        ← Entidade users (id, name, email, password, roles)
│   │   │   ├── Role.java                        ← Entidade roles (ROLE_USER, ROLE_ADMIN)
│   │   │   ├── Post.java                        ← Entidade de postagens
│   │   │   └── Comment.java                     ← Entidade de comentários
│   │   ├── repository/
│   │   │   ├── UserRepository.java, RoleRepository.java
│   │   │   ├── PostRepository.java, CommentRepository.java
│   │   ├── security/
│   │   │   ├── JwtFilter.java                   ← Filtro JWT (Header + Cookie)
│   │   │   ├── UserDetailsServiceImpl.java      ← ⚠️ Busca em DUAS tabelas (admin + users)
│   │   │   └── jwt/JwtService.java              ← Geração/validação de tokens JWT
│   │   └── service/
│   │       ├── AuthService.java                 ← Lógica de registro e login (BCrypt + JWT)
│   │       ├── PostService.java, CommentService.java
│   │
│   ├── Admin/                                   ← Módulo legado de administradores
│   │   ├── Repo.java                            ← ❌ Entidade admin (SENHA EM TEXTO PLANO!)
│   │   ├── Repository_admin.java
│   │   ├── ControllerAd.java                    ← ❌ Login alternativo com senha em texto plano
│   │   └── SecurityConfigAdmin.java             ← ❌ Libera /admin/** sem autenticação
│   │
│   ├── Volun/                                   ← Módulo legado de voluntários
│   │   ├── V.java                               ← Entidade tb_voluntario
│   │   ├── Repository_Voluntário.java
│   │   ├── ControlerVolun.java                  ← ❌ Concatena CPF+tel+gênero no campo "links"
│   │   └── SecurityConfigV.java                 ← Desativado (comentado)
│   │
│   └── contatos/                                ← Módulo de feedback/contato
│       ├── Feed.java                            ← Entidade tb_feed
│       ├── Repository_feed.java
│       ├── Controler.java                       ← POST /contato/enviar
│       └── SecurityConfigF.java
│
├── src/main/resources/
│   ├── application.properties                   ← Perfil dev + Neon PostgreSQL
│   ├── application-dev.properties               ← Neon PostgreSQL (NÃO é H2!)
│   ├── application-prod.properties              ← ❌ Aponta pra MySQL (ERRADO)
│   ├── static/
│   │   ├── css/   → 15 arquivos CSS
│   │   ├── js/    → api.js, auth.js, cadastro.js, contato.js, home.js, inicio.js, Validar_CPF.js
│   │   ├── img/   → Imagens do projeto
│   │   ├── font/  → Fontes GlacialIndifference
│   │   └── video/ → videoheader.mp4
│   └── templates/
│       ├── index.html, pagina1.html             ← Páginas iniciais (2 versões)
│       ├── login.html                           ← Login do Blog (JWT via JS)
│       ├── register.html                        ← Cadastro do Blog (JWT via JS)
│       ├── AdminForm.html                       ← Login do Admin legado (senha texto plano)
│       ├── AdminCadastro.html                   ← Cadastro de novo admin
│       ├── VolunForm.html                       ← Formulário de voluntário
│       ├── admin-dashboard.html                 ← Dashboard admin (676 linhas, funcional)
│       ├── noticias.html, noticiaAberta.html    ← Listagem e leitura de notícias
│       ├── contatos.html                        ← Formulário de contato
│       ├── Evento.html, MG.html, CE.html        ← Eventos e Núcleos
│       ├── Projeto.html, orgaoambiental.html
│       ├── post.html                            ← Blog post individual
│       └── fragments.html                       ← ❌ QUEBRADO (conflitos de Git)
│
├── src/test/
│   ├── TestedeSucesso.java                      ← ❌ Não compila (import errado)
│   └── Testedeerro.java                         ← ❌ Não compila (import errado)
│
├── pom.xml                                      ← Java 21, Spring Boot 4.0.7
├── Dockerfile + docker-compose.yml              ← Deploy com Docker + Neon
└── uploads/                                     ← Pasta de uploads de imagens
```

---

## 🔴 PARTE 1 — PROBLEMAS CRÍTICOS (obrigatório corrigir)

Esses são bugs reais que existem no código **agora**. Precisam ser resolvidos antes de qualquer feature nova.

---

### 1. `fragments.html` — Conflitos de Git NÃO resolvidos

**Arquivo:** `src/main/resources/templates/fragments.html` (linhas 16-23)

O arquivo tem marcadores de merge do Git que **quebram o Thymeleaf**:

```html
<!-- ISSO ESTÁ NO CÓDIGO AGORA: -->
<<<<<<< HEAD


</body></html>
=======
</body>
</html>
>>>>>>> 81b185c9b7ab19af49cb01d7d127aedcb04c0181
```

**O que fazer:** Remover TODOS os marcadores (`<<<<<<<`, `=======`, `>>>>>>>`) e deixar o HTML limpo:

```html
</body>
</html>
```

---

### 2. `PageController.java` — Rotas `/login` e `/register` apontam para os templates ERRADOS

**Arquivo:** `Blog/controller/PageController.java` (linhas 22-31)

```java
// PROBLEMA: /login deveria abrir login.html, mas abre VolunForm.html (formulário de voluntário!)
@GetMapping("/login")
public String login() {
    return "VolunForm";  // ← ERRADO
}

// PROBLEMA: /register deveria abrir register.html, mas abre AdminForm.html (login do admin legado!)
@GetMapping("/register")
public String register() {
    return "AdminForm";  // ← ERRADO
}
```

**⚠️ ATENÇÃO:** O `WebConfig.java` (linha 19-20) TAMBÉM registra essas rotas via `addViewController`. Isso causa **conflito de rotas**. Vocês precisam:

1. **No `PageController.java`**: Alterar `/login` para retornar `"login"` e `/register` para retornar `"register"`
2. **OU** remover essas rotas do `PageController` e deixar só no `WebConfig` (que já está correto)

O `WebConfig.java` já mapeia corretamente:
```java
registry.addViewController("/login").setViewName("login");
registry.addViewController("/register").setViewName("register");
```

Então a solução mais limpa é **remover as rotas duplicadas do `PageController`**.

---

### 3. Senha do Admin armazenada em TEXTO PLANO

**Arquivo:** `Admin/Repo.java` — O campo `senha` é uma String sem BCrypt.

**Arquivo:** `Admin/ControllerAd.java` (linha 74):
```java
// Compara senha em TEXTO PLANO — VULNERABILIDADE GRAVE
if (admin.getSenha().trim().equals(senha)) {
```

E pior — tem um **backdoor hardcoded** no código (linhas 56-65):
```java
// ESCAPE SUPREMO DE TESTES: login fixo no código
if ("admin@omni.com".equals(email) && "123456".equals(senha)) {
    // Gera token JWT direto sem verificar banco
}
```

**O que fazer:**
- Remover o backdoor `admin@omni.com` / `123456`
- Migrar o login do admin para usar o `AuthService` do módulo Blog (que já usa BCrypt)
- Ou, no mínimo, criptografar as senhas na tabela `admin` com BCrypt

---

### 4. Voluntário — CPF, telefone e senha concatenados no campo `links`

**Arquivo:** `Volun/ControlerVolun.java` (linhas 32-34):
```java
novoV.setLinks("CPF: " + cpf + " | Tel: " + phone + " | Gênero: " + gender);
// A SENHA nem é salva! O formulário pede senha (VolunForm.html), 
// mas o controller NÃO recebe o campo password.
```

**O que fazer:**
- Criar campos separados na entidade `V.java` (ou renomear para `Voluntario`): `cpf`, `telefone`, `genero`
- Se o voluntário precisar de login, salvar a senha com BCrypt usando o `AuthService`

---

### 5. `SecurityConfigAdmin.java` — Admin acessível SEM autenticação

**Arquivo:** `Admin/SecurityConfigAdmin.java` (linhas 14-18):
```java
@Order(1)  // Prioridade MÁXIMA — é aplicado ANTES do SecurityConfig do Blog
public class SecurityConfigAdmin {
    @Bean
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/AdminForm", "/admin/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())  // ← TUDO LIBERADO!
            .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
```

Isso significa que **qualquer pessoa** pode acessar `/admin-dashboard`, `/admin/novo-admin` e todas as rotas admin sem fazer login. A `@Order(1)` garante que essa regra é aplicada ANTES da `SecurityConfig` do Blog.

**O que fazer:** No mínimo, exigir autenticação nas rotas admin:
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/AdminForm").permitAll()  // Só a tela de login é pública
    .anyRequest().authenticated()
)
```

---

### 6. `UserDetailsServiceImpl.java` — Login com `{noop}` (bypass de BCrypt)

**Arquivo:** `Blog/security/UserDetailsServiceImpl.java` (linhas 28-41):
```java
// Busca primeiro na tabela legada "admin" com senha em TEXTO PLANO
var adminOpt = repositoryAdmin.findByEmail(email);
if (adminOpt.isPresent()) {
    // Usa {noop} para dizer ao Spring Security: "não criptografe esta senha"
    String senhaTratada = "{noop}" + admin.getSenha();
    return new User(admin.getEmail(), senhaTratada, authorities);
}
```

**Problema:** Se alguém se cadastrar pelo Blog (que usa BCrypt) com o mesmo email de um admin legado, o login vai tentar comparar uma senha BCrypt com `{noop}` e nunca vai funcionar.

**O que fazer:** Unificar a autenticação para usar APENAS a tabela `users` com BCrypt. A tabela legada `admin` deveria ser migrada.

---

### 7. `register.html` — Links hardcoded com `.html`

**Arquivo:** `templates/register.html` (linhas 39, 43-44, 48, 87):
```html
<a href="login.html">Entrar</a>                    <!-- Deveria ser /login -->
<script src="js/api.js"></script>                    <!-- Deveria usar th:src -->
<script src="js/auth.js"></script>                   <!-- Deveria usar th:src -->
window.location.href = 'index.html';                 <!-- Deveria ser / -->
window.location.replace('/admin-dashboard.html');     <!-- Deveria ser /admin-dashboard -->
```

**O que fazer:** Usar links de rotas do Spring Boot (sem `.html`) e usar `th:src` nos scripts.

---

### 8. `application-prod.properties` — Aponta para MySQL (ERRADO)

**Arquivo:** `src/main/resources/application-prod.properties`:
```properties
# ERRADO: Usando MySQL em vez de PostgreSQL!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

**O que fazer:** Corrigir para PostgreSQL:
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

---

### 9. Credenciais do banco expostas no repositório

**Arquivo:** `application.properties` (linhas 4-6) e `application-dev.properties`:
```properties
# NUNCA façam isso em produção!
spring.datasource.url=jdbc:postgresql://ep-soft-term-apur2p4j-pooler.c-7.us-east-1.aws.neon.tech/neondb
spring.datasource.username=neondb_owner
spring.datasource.password=npg_WmdS1V5PrLGg
```

**Arquivo:** `docker-compose.yml` (linhas 11-16): Mesmas credenciais + JWT secret expostos.

**O que fazer:** Usar variáveis de ambiente (o `application.properties` já tem fallback com `${SPRING_DATASOURCE_URL:...}`, mas o valor padrão ainda é a credencial real). No mínimo, criem um `.env` que NÃO vá pro Git.

---

### 10. Testes não compilam

**Arquivos:** `src/test/TestedeSucesso.java` e `src/test/Testedeerro.java`

- `TestedeSucesso.java` — Não tem declaração de pacote, importa `RepositoryFeed` e `Feed` sem pacote completo
- `Testedeerro.java` — Classe se chama `testedeerro` (minúsculo, diferente do nome do arquivo)
- Ambos usam `@MockBean` do `spring-boot-test` que pode estar deprecado no Spring Boot 4.x

**O que fazer:** Corrigir os imports, mover os arquivos para `src/test/java/com/project/omni/` e renomear as classes.

---

### 11. Dependência `postgresql` duplicada no `pom.xml`

**Arquivo:** `pom.xml` — `org.postgresql:postgresql` aparece 2 vezes (linhas 24-28 e 53-57).

**O que fazer:** Remover uma das duas.

---

## 🟡 PARTE 2 — MELHORIAS DE PRIORIDADE MÉDIA (importante para a nota)

---

### 12. Unificar o sistema de autenticação

**Situação atual:** Existem **DOIS sistemas de login** completamente separados:

| Sistema | Tabela | Senha | Endpoint | Template |
|---------|--------|-------|----------|----------|
| Blog | `users` | BCrypt | `POST /api/auth/login` (REST/JWT) | `login.html` |
| Admin legado | `admin` | Texto plano | `POST /admin/login-api` (REST/JWT) | `AdminForm.html` |

O `UserDetailsServiceImpl` tenta os dois, mas usando `{noop}` para a tabela legada.

**O que fazer:**
- Escolher UM sistema (recomendo o do Blog, que já usa BCrypt)
- Migrar os admins da tabela `admin` para a tabela `users` com `ROLE_ADMIN`
- Remover o módulo `Admin/` inteiro (ou adaptá-lo para usar o `AuthService`)
- Atualizar o `UserDetailsServiceImpl` para buscar APENAS na tabela `users`

---

### 13. Implementar validações no registro

**Arquivo:** `Blog/dto/request/RegisterRequest.java` — Atualmente só valida:
- `@NotBlank` no name
- `@NotBlank @Email` no email
- `@NotBlank @Size(min = 6)` na password

**Falta:**
- Senha forte (pelo menos 1 maiúscula, 1 número, 1 caractere especial)
- Verificar se email já existe (o `AuthService` já faz isso, mas retorna `RuntimeException` genérica)
- Validar CPF nos formulários de admin/voluntário
- Retornar mensagens de erro em português

---

### 14. Implementar logout

**Situação atual:** O `auth.logout()` no JavaScript só limpa o `localStorage`. O token JWT continua válido no servidor até expirar (24h).

**O que fazer:**
- Criar `POST /api/auth/logout` que adicione o token numa blacklist
- O `JwtFilter` deve verificar se o token está na blacklist antes de autorizar
- Ou usar tokens com expiração curta + refresh tokens

---

### 15. Criar endpoint de perfil de usuário

- `GET /api/users/me` — Retorna dados do usuário autenticado
- `PUT /api/users/me` — Editar nome e email
- `PUT /api/users/me/password` — Alterar senha (exige senha atual)
- Criar template `perfil.html` no frontend

---

### 16. Proteger `POST /posts` corretamente

**Arquivo:** `SecurityConfig.java` (linhas 70-73) — O POST de posts exige autenticação via JWT, mas a `SecurityConfigAdmin` (Order 1) libera `/admin/**` sem autenticação. Se a rota de criar post for acessada via painel admin, passa direto.

**O que fazer:** Garantir que TODAS as operações de escrita exijam JWT válido.

---

### 17. Recuperação de senha

- `POST /api/auth/forgot-password` — Recebe email, gera token de recuperação
- `POST /api/auth/reset-password` — Recebe token + nova senha, atualiza
- Podem simular o envio de email com log no console
- Criar templates `forgot-password.html` e `reset-password.html`

---

## 🟢 PARTE 3 — FRONTEND (melhorias e polimento)

### ✅ Já funciona no frontend

| # | Página | Status |
|---|--------|--------|
| 1 | Homepage (`index.html` / `pagina1.html`) | ✅ Funciona (2 versões!) |
| 2 | Login do Blog (`login.html`) | ✅ Funciona com JWT |
| 3 | Cadastro do Blog (`register.html`) | ✅ Funciona, mas links com `.html` |
| 4 | Login do Admin (`AdminForm.html`) | ✅ Funciona (senha texto plano) |
| 5 | Dashboard Admin (`admin-dashboard.html`) | ✅ Completo (676 linhas, CRUD posts, voluntários, dark mode) |
| 6 | Cadastro Admin (`AdminCadastro.html`) | ✅ Funciona |
| 7 | Voluntário (`VolunForm.html`) | ✅ Funciona (formulário bonito) |
| 8 | Notícias (`noticias.html`) | ✅ Listagem |
| 9 | Notícia aberta (`noticiaAberta.html`) | ✅ Leitura individual |
| 10 | Post do Blog (`post.html`) | ✅ Funciona |
| 11 | Contato (`contatos.html`) | ✅ Formulário funcional |
| 12 | Eventos (`Evento.html`) | ✅ Página criada |
| 13 | Núcleos MG e CE | ✅ Páginas criadas |
| 14 | Projeto (`Projeto.html`) | ✅ Página criada |
| 15 | Órgãos Ambientais (`orgaoambiental.html`) | ✅ Página criada |

### ❌ Sugestões de melhoria

#### Prioridade Alta (impactam a nota)
- **Resolver as 2 versões da homepage** — `index.html` e `pagina1.html` existem ao mesmo tempo. `WebConfig` mapeia `/` para `index` e `PageController` mapeia `/` para `pagina1`. Decidam qual é a oficial.
- **Limpar os `System.out.println` de debug** — O `UserDetailsServiceImpl.java` e o `ControllerAd.java` estão cheios de prints com "=== DIRETRIZ DE LOGIN CRÍTICA ===" e senhas no console. Removam antes de entregar.
- **Padronizar uso de Thymeleaf** — Alguns templates usam `th:src` e `th:href`, outros usam caminhos estáticos (`href="css/style.css"`). Padronizem para `th:` em tudo.

#### Prioridade Média
- **Paginação no Blog** — Atualmente carrega TODOS os posts de uma vez
- **Busca no Blog** — Campo de busca por título
- **Validação frontend nos formulários** — Usar JavaScript para validar antes de enviar
- **Toast/notificações** — Substituir `alert()` por notificações não-bloqueantes
- **Confirmação de senha com indicador de força** no cadastro

#### Prioridade Baixa
- **Unificar CSS** — São 15 arquivos CSS, vários com conteúdo duplicado
- **PWA** — `manifest.json` + service worker
- **Mapa interativo** — Leaflet.js com os núcleos MG/CE
- **Galeria de fotos** — Tem MUITAS imagens soltas em `static/Núcleo - Uberlândia-MG/` que poderiam virar uma galeria

---

## 📊 RESUMO: O que TEM vs. O que FALTA

| Área | ✅ Tem | ❌ Falta |
|------|-------|---------|
| **Auth JWT** | AuthService (BCrypt), JwtFilter, JwtService, login.html, register.html | Unificar com admin legado, logout com blacklist |
| **Auth Admin** | Login com senha texto plano, backdoor hardcoded | Migrar pra BCrypt, remover backdoor |
| **Segurança** | BCrypt no Blog, JWT com expiração 24h | Rotas admin abertas, credenciais expostas, `{noop}` |
| **Models** | User, Role, Post, Comment, Repo(admin), V(volun), Feed | Campos do voluntário concatenados |
| **CRUD Posts** | Create, Read, Update (via POST), Delete no dashboard | Autor do post não é verificado |
| **Frontend** | 15+ templates, dashboard completo com dark mode | fragments.html quebrado, links `.html`, 2 homepages |
| **Testes** | 2 arquivos (não compilam) | Testes que funcionem, testes de auth |
| **Deploy** | Dockerfile + docker-compose.yml | Prod apontando pra MySQL, credenciais expostas |

---

## ✅ CHECKLIST RÁPIDO PARA ENTREGA

Usem isso como guia. Marquem conforme forem fazendo:

### 🔴 Obrigatório (sem isso, reprova)
- [ ] Resolver conflitos de Git no `fragments.html`
- [ ] Corrigir rotas `/login` e `/register` (PageController vs WebConfig)
- [ ] Remover o backdoor `admin@omni.com` / `123456` do `ControllerAd.java`
- [ ] Corrigir links com `.html` no `register.html`
- [ ] Resolver conflito das 2 homepages (`index.html` vs `pagina1.html`)
- [ ] Remover `System.out.println` com senhas no console

### 🟡 Importante (melhora muito a nota)
- [ ] Unificar autenticação: todos os logins devem usar BCrypt + tabela `users`
- [ ] Proteger rotas admin (exigir autenticação no `SecurityConfigAdmin`)
- [ ] Corrigir `application-prod.properties` (MySQL → PostgreSQL)
- [ ] Criar campos separados no voluntário (CPF, telefone, gênero) em vez de concatenar
- [ ] Implementar validações no registro (senha forte, email único com mensagem clara)
- [ ] Padronizar uso de `th:src` e `th:href` em todos os templates
- [ ] Remover dependência `postgresql` duplicada no `pom.xml`
- [ ] Corrigir e rodar os testes (`TestedeSucesso` e `Testedeerro`)

### 🟢 Diferencial (nota extra)
- [ ] Implementar logout com blacklist de tokens
- [ ] Criar endpoint de perfil (`/api/users/me`)
- [ ] Criar recuperação de senha
- [ ] Adicionar paginação e busca no blog
- [ ] Substituir `alert()` por toast notifications
- [ ] Galeria de fotos (tem imagens soltas que podem ser aproveitadas)

---

## 🗺️ MAPA DOS ARQUIVOS-CHAVE

Para facilitar a navegação de vocês, aqui estão os arquivos mais importantes e onde encontrar cada coisa:

| O que procurar | Arquivo |
|----------------|---------|
| Configuração de segurança principal | `Blog/config/SecurityConfig.java` |
| Config de segurança do admin (ABERTA!) | `Admin/SecurityConfigAdmin.java` |
| Rotas MVC (Thymeleaf) | `Blog/controller/PageController.java` + `WebConfig.java` |
| Login/Registro REST (JWT) | `Blog/controller/AuthController.java` |
| Lógica de autenticação | `Blog/service/AuthService.java` |
| Busca de usuário para login | `Blog/security/UserDetailsServiceImpl.java` |
| Filtro JWT | `Blog/security/JwtFilter.java` |
| Geração de token | `Blog/security/jwt/JwtService.java` |
| Inicialização de roles | `Blog/config/DataInitializer.java` |
| Login admin legado (texto plano) | `Admin/ControllerAd.java` |
| Cadastro voluntário | `Volun/ControlerVolun.java` |
| API client no frontend | `static/js/api.js` |
| Gerenciamento de sessão no frontend | `static/js/auth.js` |
| Properties do projeto | `application.properties`, `application-dev.properties` |

---

## 💡 DICAS FINAIS

1. **Sempre rodem `./mvnw spring-boot:run`** e testem no `localhost:8080` antes de commitar.

2. **O login do Blog** (`/login`) usa JWT via JavaScript. O login do admin (`/AdminForm`) usa JWT com senha em texto plano. São fluxos DIFERENTES. Entendam a diferença antes de mexer.

3. **A rota raiz `/`** está mapeada em 2 lugares: `PageController` retorna `pagina1` e `WebConfig` retorna `index`. O Spring pode usar qualquer um dos dois dependendo da ordem de carregamento. Resolvam isso.

4. **O `DataInitializer.java`** já cria as roles `ROLE_USER` e `ROLE_ADMIN` automaticamente no boot. Vocês NÃO precisam inserir manualmente.

5. **Para criar um admin via API:** Façam um POST para `/api/auth/register` e depois alterem a role no banco diretamente. Ou criem um `CommandLineRunner` que cadastre um admin padrão.

6. **Cuidado ao mexer no `SecurityConfig`** — existem 3 SecurityFilterChains: `SecurityConfigAdmin` (Order 1), `SecurityConfig` do Blog, e `SecurityConfigV` (desativado). A ordem importa!

7. **Commit frequente** — façam commits pequenos e descritivos. Nada de um commit gigante com tudo no final.

8. **Perguntas?** Me mandem pelo Classroom ou durante as aulas. Boa sorte e bom trabalho! 💪
