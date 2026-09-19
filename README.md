# Usuario API

API REST para gerenciamento de usuários, desenvolvida com Spring Boot. O projeto permite cadastro, autenticação JWT, consulta, atualização e exclusão de usuários, endereços e telefones, alteração de senha, consulta de CEP (ViaCEP + OpenFeign) e cache Redis.

## Tecnologias

- Java 17
- Spring Boot 4.1.1
- Spring Data JPA
- Spring Security + JWT
- Spring Cloud OpenFeign
- Spring Data Redis (Lettuce)
- PostgreSQL
- Redis
- Lombok
- SpringDoc OpenAPI (Swagger)
- Gradle
- Docker / Docker Compose

## Estrutura do projeto

```
src/main/java/com/javanauta/usuario/
├── UsuarioApplication.java
├── business/
│   ├── UsuarioService.java
│   ├── ViaCepService.java
│   ├── converter/
│   └── dto/
├── controller/
│   └── UsuarioController.java
└── infrastructure/
    ├── clients/          # Feign ViaCEP
    ├── config/           # OpenAPI e Redis
    ├── entity/
    ├── exceptions/
    ├── repository/
    └── security/
```

## Pré-requisitos

- JDK 17
- PostgreSQL em execução (ou Docker)
- Redis em execução na porta `6379` (ou Docker)
- Gradle (ou o wrapper `./gradlew`)

## Configuração

Credenciais e URLs em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/agendadorTarefa
spring.datasource.username=postgres
spring.datasource.password=admin

spring.data.redis.host=localhost
spring.data.redis.port=6379

viacep.url=https://viacep.com.br
```

Crie o banco no PostgreSQL (se não usar Docker):

```sql
CREATE DATABASE "agendadorTarefa";
```

## Redis

A consulta de CEP usa Redis como cache. A chave é `enderecos` + CEP (somente números) e o TTL é de **15 dias**.

Suba o Redis localmente, por exemplo:

```powershell
docker run -d --name redis-usuario -p 6379:6379 redis:7-alpine
```

Sem o Redis no ar, a busca de CEP falha na conexão.

## Como executar (local)

PostgreSQL e Redis precisam estar ligados antes do `bootRun`.

```bash
./gradlew bootRun
```

No Windows:

```powershell
.\gradlew.bat bootRun
```

A API sobe em `http://localhost:8080`.

## Docker Compose

Sobe PostgreSQL, Redis e o microserviço juntos:

```powershell
docker compose up -d --build
```

No Compose, o host do banco é `postgres` e o do Redis é `redis`.

## Documentação Swagger

```
http://localhost:8080/swagger-ui.html
```

## Autenticação

1. Cadastre um usuário com `POST /usuario`
2. Faça login com `POST /usuario/login`
3. Copie o valor do campo `authorization` (`Bearer ...`)
4. No Swagger, clique em **Authorize** e cole esse valor

Nos endpoints protegidos (Postman, Insomnia etc.):

```
Authorization: Bearer SEU_TOKEN
```

## Endpoints

| Método | Rota | Autenticação | Descrição |
|--------|------|--------------|-----------|
| POST | `/usuario` | Não | Cadastra usuário (senha em BCrypt) |
| POST | `/usuario/login` | Não | Login e JWT no campo `authorization` |
| GET | `/usuario?email=` | Sim | Busca usuário por e-mail |
| GET | `/usuario/todos` | Sim | Lista todos os usuários |
| PUT | `/usuario` | Sim | Atualiza dados do usuário logado |
| PUT | `/usuario/senha` | Sim | Altera a senha do usuário logado |
| DELETE | `/usuario/{email}` | Sim | Remove usuário por e-mail |
| POST | `/usuario/endereco` | Sim | Cadastra endereço do usuário logado |
| PUT | `/usuario/endereco?id=` | Sim | Atualiza endereço parcialmente |
| DELETE | `/usuario/endereco?id=` | Sim | Remove endereço por id |
| POST | `/usuario/telefone` | Sim | Cadastra telefone do usuário logado |
| PUT | `/usuario/telefone?id=` | Sim | Atualiza telefone parcialmente |
| DELETE | `/usuario/telefone?id=` | Sim | Remove telefone por id |
| GET | `/usuario/endereco/{cep}` | Sim | Consulta CEP na ViaCEP (com cache Redis) |

## Exemplos de requisição

### Cadastro de usuário

```http
POST /usuario
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "123456"
}
```

### Login

```http
POST /usuario/login
Content-Type: application/json

{
  "email": "joao@email.com",
  "senha": "123456"
}
```

Resposta:

```json
{
  "authorization": "Bearer eyJhbGciOiJIUzM4NCJ9..."
}
```

### Atualização parcial

```http
PUT /usuario
Authorization: Bearer SEU_TOKEN
Content-Type: application/json

{
  "nome": "João Atualizado"
}
```

### Alterar senha

```http
PUT /usuario/senha
Authorization: Bearer SEU_TOKEN
Content-Type: application/json

{
  "senha": "novaSenha123"
}
```

### Cadastro de endereço

```http
POST /usuario/endereco
Authorization: Bearer SEU_TOKEN
Content-Type: application/json

{
  "rua": "Rua A",
  "numero": "100",
  "complemento": "Apto 1",
  "cidade": "São Paulo",
  "estado": "SP",
  "cep": "01001000"
}
```

### Consulta de CEP

```http
GET /usuario/endereco/01001000
Authorization: Bearer SEU_TOKEN
```

Na primeira chamada busca a ViaCEP e grava no Redis. Nas seguintes, enquanto o cache estiver válido, responde pelo Redis.

### Exclusão de telefone e endereço

```http
DELETE /usuario/telefone?id=1
Authorization: Bearer SEU_TOKEN

DELETE /usuario/endereco?id=1
Authorization: Bearer SEU_TOKEN
```

## Build

```bash
./gradlew build
```

## Branches

- `master` — versão estável
- `develop` — integração de funcionalidades
- `feature/*` — desenvolvimento de novas features

## Autor

Projeto desenvolvido no curso **Java Full Stack - Javanauta**.
