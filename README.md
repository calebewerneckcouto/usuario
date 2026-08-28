# Usuario API

API REST para gerenciamento de usuários, desenvolvida com Spring Boot. O projeto permite cadastro, autenticação JWT, consulta, atualização parcial e exclusão de usuários, além de endereços e telefones vinculados.

## Tecnologias

- Java 17
- Spring Boot 4.1.1
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL 9.5+
- Lombok
- SpringDoc OpenAPI (Swagger)
- Gradle

## Estrutura do projeto

```
src/main/java/com/javanauta/usuario/
├── UsuarioApplication.java
├── business/
│   ├── UsuarioService.java
│   ├── converter/
│   └── dto/
├── controller/
│   └── UsuarioController.java
└── infrastructure/
    ├── config/
    ├── entity/
    ├── exceptions/
    ├── repository/
    └── security/
```

## Pré-requisitos

- JDK 17
- PostgreSQL instalado e em execução
- Gradle (ou use o wrapper `./gradlew`)

## Configuração do banco

Crie o banco de dados no PostgreSQL:

```sql
CREATE DATABASE "agendadorTarefa";
```

Configure as credenciais em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/agendadorTarefa
spring.datasource.username=postgres
spring.datasource.password=admin
```

## Como executar

```bash
./gradlew bootRun
```

No Windows:

```powershell
.\gradlew.bat bootRun
```

A aplicação sobe em `http://localhost:8080`.

## Documentação Swagger

Após iniciar a aplicação, acesse:

```
http://localhost:8080/swagger-ui.html
```

## Autenticação

1. Cadastre um usuário com `POST /usuario`
2. Faça login com `POST /usuario/login`
3. Copie o valor do campo `token`
4. No Swagger, clique em **Authorize** e informe: `Bearer SEU_TOKEN`

Nos endpoints protegidos (Postman, Insomnia etc.), envie o header:

```
Authorization: Bearer SEU_TOKEN
```

## Endpoints

| Método | Rota | Autenticação | Descrição |
|--------|------|--------------|-----------|
| POST | `/usuario` | Não | Cadastra usuário |
| POST | `/usuario/login` | Não | Login e geração de JWT |
| GET | `/usuario?email=` | Sim | Busca usuário por e-mail |
| GET | `/usuario/todos` | Sim | Lista todos os usuários |
| PUT | `/usuario` | Sim | Atualiza dados do usuário logado |
| DELETE | `/usuario/{email}` | Sim | Remove usuário por e-mail |
| POST | `/usuario/endereco` | Sim | Cadastra endereço do usuário logado |
| PUT | `/usuario/endereco?id=` | Sim | Atualiza endereço parcialmente |
| POST | `/usuario/telefone` | Sim | Cadastra telefone do usuário logado |
| PUT | `/usuario/telefone?id=` | Sim | Atualiza telefone parcialmente |

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

### Atualização parcial

Envie apenas o campo que deseja alterar:

```http
PUT /usuario
Authorization: Bearer SEU_TOKEN
Content-Type: application/json

{
  "nome": "João Atualizado"
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
