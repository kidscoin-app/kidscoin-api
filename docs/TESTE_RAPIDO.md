# 🧪 Guia de Teste Rápido - API KidsCoins

## 1️⃣ Iniciar a Aplicação

```bash
# Certifique-se que o PostgreSQL está rodando
# Database: educacao_financeira

mvn spring-boot:run
```

Aplicação rodando em: `http://localhost:8080`

---

## 2️⃣ Testar Endpoints (use Postman, Insomnia ou curl)

### 1. Registrar um Pai

```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "joao@teste.com",
  "password": "senha123",
  "fullName": "João Silva",
  "familyName": "Família Silva"
}
```

**Resposta esperada:** Status 200
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "uuid-aqui",
  "user": {
    "id": "uuid",
    "email": "joao@teste.com",
    "fullName": "João Silva",
    "role": "PARENT",
    "familyId": "uuid",
    "avatarUrl": null
  }
}
```

**Copie o `accessToken` para os próximos testes!**

---

### 2. Login

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "joao@teste.com",
  "password": "senha123"
}
```

**Resposta esperada:** Status 200 (mesma estrutura do register)

---

### 3. Buscar Dados do Usuário Logado

```bash
GET http://localhost:8080/api/users/me
Authorization: Bearer SEU_ACCESS_TOKEN_AQUI
```

**Resposta esperada:** Status 200
```json
{
  "id": "uuid",
  "email": "joao@teste.com",
  "fullName": "João Silva",
  "role": "PARENT",
  "familyId": "uuid",
  "avatarUrl": null
}
```

---

### 4. Criar uma Criança

```bash
POST http://localhost:8080/api/users/children
Authorization: Bearer SEU_ACCESS_TOKEN_AQUI
Content-Type: application/json

{
  "fullName": "Maria Silva",
  "age": 8,
  "pin": "1234",
  "avatarUrl": "https://i.pravatar.cc/150?img=1"
}
```

**Resposta esperada:** Status 200
```json
{
  "id": "uuid",
  "email": "maria.silva.12345678@child.local",
  "fullName": "Maria Silva",
  "role": "CHILD",
  "familyId": "uuid",
  "avatarUrl": "https://i.pravatar.cc/150?img=1"
}
```

---

### 5. Listar Crianças da Família

```bash
GET http://localhost:8080/api/users/children
Authorization: Bearer SEU_ACCESS_TOKEN_AQUI
```

**Resposta esperada:** Status 200
```json
[
  {
    "id": "uuid",
    "email": "maria.silva.12345678@child.local",
    "fullName": "Maria Silva",
    "role": "CHILD",
    "familyId": "uuid",
    "avatarUrl": "https://i.pravatar.cc/150?img=1"
  }
]
```

---

### 6. Refresh Token

```bash
POST http://localhost:8080/api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "SEU_REFRESH_TOKEN_AQUI"
}
```

**Resposta esperada:** Status 200 (novo accessToken)

---

## 🔍 Validações a Testar

### Validação de Email

```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "email-invalido",
  "password": "senha123",
  "fullName": "João Silva",
  "familyName": "Família Silva"
}
```

**Resposta esperada:** Status 400 - "Email inválido"

---

### Senha Curta

```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "teste@teste.com",
  "password": "123",
  "fullName": "João Silva",
  "familyName": "Família Silva"
}
```

**Resposta esperada:** Status 400 - "Senha deve ter no mínimo 8 caracteres"

---

### Email Duplicado

```bash
# Tente registrar o mesmo email duas vezes
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "joao@teste.com",
  "password": "senha123",
  "fullName": "João Silva 2",
  "familyName": "Família Silva 2"
}
```

**Resposta esperada:** Status 400 - "Email já cadastrado"

---

### Credenciais Inválidas

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "joao@teste.com",
  "password": "senha-errada"
}
```

**Resposta esperada:** Status 403 - "Credenciais inválidas"

---

### Sem Token de Autenticação

```bash
GET http://localhost:8080/api/users/me
# SEM Header Authorization
```

**Resposta esperada:** Status 403 ou 401 - Não autorizado

---

### PIN com Formato Inválido

```bash
POST http://localhost:8080/api/users/children
Authorization: Bearer SEU_ACCESS_TOKEN_AQUI
Content-Type: application/json

{
  "fullName": "Pedro Silva",
  "age": 10,
  "pin": "12",
  "avatarUrl": null
}
```

**Resposta esperada:** Status 400 - "PIN deve ter exatamente 4 dígitos"

---

## ✅ Checklist de Testes

- [ ] Registro de pai funciona
- [ ] Login funciona
- [ ] Token JWT é gerado
- [ ] GET /me retorna dados do usuário
- [ ] Criar criança funciona
- [ ] Listar crianças funciona
- [ ] Refresh token funciona
- [ ] Validação de email funciona
- [ ] Validação de senha funciona
- [ ] Email duplicado é rejeitado
- [ ] Credenciais inválidas retornam erro
- [ ] Requisições sem token são rejeitadas
- [ ] Validação de PIN funciona

---

## 💡 Dicas

1. **Use Postman Collections:** Crie uma collection com todos os endpoints
2. **Salve o Token:** Configure variável de ambiente para o accessToken
3. **Database:** Use DBeaver ou pgAdmin para visualizar os dados
4. **Logs:** Veja o console do Spring Boot para debug

---

## 🗄️ Verificar Database

```sql
-- Ver famílias criadas
SELECT * FROM families;

-- Ver usuários
SELECT id, email, full_name, role, family_id FROM users;

-- Ver refresh tokens
SELECT id, token, user_id, expires_at, revoked FROM refresh_tokens;
```

---

**Tudo funcionando? Pronto para a Parte 2!** 🚀
