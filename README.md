# KidsCoins API - Backend Parte 1

Sistema de educação financeira infantil gamificada - API REST

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.5**
- **PostgreSQL 15**
- **Spring Security + JWT**
- **Maven**

## 📦 Dependências Principais

- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL Driver
- JJWT (0.11.5) - JWT Token
- Lombok
- Validation

## 🔧 Pré-requisitos

- Java 17 ou superior
- PostgreSQL 15
- Maven 3.6+

## ⚙️ Configuração Local

### 1. Criar Database

```bash
# Conectar ao PostgreSQL
psql -U postgres

# Criar database
CREATE DATABASE educacao_financeira;
```

### 2. Configurar application.yml

O arquivo `src/main/resources/application.yml` já está configurado para desenvolvimento local:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/educacao_financeira
    username: postgres
    password: postgres
```

**Importante:** Ajuste `username` e `password` conforme sua instalação do PostgreSQL.

### 3. Variáveis de Ambiente (Opcional)

Para produção, configure a variável:

```bash
export JWT_SECRET=sua-chave-secreta-minimo-256-bits
```

## 🏃 Como Rodar

```bash
# Clonar o repositório
git clone <repo-url>
cd api

# Rodar com Maven
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 📡 Endpoints Disponíveis

### Autenticação (Público)

#### POST /api/auth/register
Registra um novo pai e cria família

**Request:**
```json
{
  "email": "pai@exemplo.com",
  "password": "senha123",
  "fullName": "João Silva",
  "familyName": "Família Silva"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "uuid-token",
  "user": {
    "id": "uuid",
    "email": "pai@exemplo.com",
    "fullName": "João Silva",
    "role": "PARENT",
    "familyId": "uuid",
    "avatarUrl": null
  }
}
```

#### POST /api/auth/login
Login de usuário

**Request:**
```json
{
  "email": "pai@exemplo.com",
  "password": "senha123"
}
```

**Response:** Igual ao /register

#### POST /api/auth/refresh
Atualiza access token usando refresh token

**Request:**
```json
{
  "refreshToken": "uuid-token"
}
```

**Response:** Igual ao /register (com novo accessToken)

---

### Usuários (Requer Autenticação)

**Header necessário:**
```
Authorization: Bearer <accessToken>
```

#### GET /api/users/me
Retorna dados do usuário autenticado

**Response:**
```json
{
  "id": "uuid",
  "email": "pai@exemplo.com",
  "fullName": "João Silva",
  "role": "PARENT",
  "familyId": "uuid",
  "avatarUrl": null
}
```

#### POST /api/users/children
Cria perfil de criança (apenas PARENT)

**Request:**
```json
{
  "fullName": "Maria Silva",
  "age": 8,
  "pin": "1234",
  "avatarUrl": "https://example.com/avatar.png"
}
```

**Response:**
```json
{
  "id": "uuid",
  "email": "maria.silva.12345678@child.local",
  "fullName": "Maria Silva",
  "role": "CHILD",
  "familyId": "uuid",
  "avatarUrl": "https://example.com/avatar.png"
}
```

#### GET /api/users/children
Lista todas as crianças da família (apenas PARENT)

**Response:**
```json
[
  {
    "id": "uuid",
    "email": "maria.silva.12345678@child.local",
    "fullName": "Maria Silva",
    "role": "CHILD",
    "familyId": "uuid",
    "avatarUrl": "https://example.com/avatar.png"
  }
]
```

## 🔐 Segurança

### JWT
- **Access Token:** Válido por 24 horas
- **Refresh Token:** Válido por 7 dias
- **Claims:** userId, email, role, familyId

### Senhas
- BCrypt com strength 12
- Mínimo 8 caracteres

### Autorização
- **PARENT:** Acesso total à família
- **CHILD:** Acesso apenas aos próprios dados

## 📂 Estrutura do Projeto

```
src/main/java/com/educacaofinanceira/
├── config/              # Configurações (Security, CORS)
├── controller/          # Controllers REST
├── service/             # Lógica de negócio
├── repository/          # Repositories JPA
├── model/               # Entidades
│   └── enums/          # Enums
├── dto/                 # DTOs
│   ├── request/        # Request DTOs
│   └── response/       # Response DTOs
├── security/            # JWT e autenticação
└── exception/           # Exceções customizadas
```

## 🗄️ Entidades

### User
- id, email, password, fullName
- role (PARENT/CHILD)
- family (FK)
- pin (4 dígitos para CHILD)
- avatarUrl
- createdAt, updatedAt

### Family
- id, name
- createdAt

### RefreshToken
- id, token, user (FK)
- expiresAt, revoked
- createdAt

## ✅ Checklist - Parte 1

- [x] Configuração inicial Spring Boot
- [x] Entidades criadas
- [x] Repositories implementados
- [x] JWT e Security configurados
- [x] Endpoints de autenticação
- [x] Endpoints de usuários
- [x] Exception handling global
- [x] README completo

## 🔜 Próximos Passos (Parte 2)

- Tarefas e atribuições
- Carteira virtual
- Recompensas e resgates
- Gamificação (XP, níveis, badges)
- Notificações

## 📝 Notas

- O projeto usa Hibernate DDL auto-update para criar/atualizar tabelas automaticamente
- Logs detalhados estão habilitados para desenvolvimento
- CORS está configurado para permitir todas origens (ajustar em produção)

## 🧑‍💻 Equipe

Projeto TCC - UNIP
