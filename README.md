# 🎮 API Games - Sistema de Gerenciamento de Catálogo de Jogos

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Render](https://img.shields.io/badge/Deploy-Render-46E3B7?style=for-the-badge)

## 📖 Sobre o Projeto
Esta é uma API RESTful desenvolvida em **Java** com **Spring Boot**, focada no gerenciamento de um catálogo completo de videogames. O sistema permite o cadastro de jogos e a vinculação rigorosa de cada título às suas respectivas Desenvolvedoras (fabricantes) e Plataformas (consoles), garantindo a consistência e integridade das informações.

O projeto foi construído com uma forte mentalidade de **Qualidade de Software**, implementando validações de entrada rigorosas (evitando requisições `Bad Request` malformadas) e garantindo a integridade referencial dos dados através do mapeamento relacional do JPA.

## 🚀 Status e Deploy
O projeto está hospedado em nuvem (Cloud) com pipeline de CI/CD configurada via Docker e Render.

- **URL da API (Produção):** [Acessar API Games](https://api-games-94ep.onrender.com/games)
- **Documentação Interativa (Swagger):** [Acessar Swagger UI](https://api-games-94ep.onrender.com/swagger-ui/index.html)
  > *Nota: Como a aplicação está hospedada no plano gratuito do Render, o primeiro acesso pode levar até 50 segundos para "acordar" o servidor (Cold Start). Os acessos subsequentes são instantâneos.*

## ⚙️ Tecnologias Utilizadas
- **Backend:** Java 17, Spring Boot 3.2.5
- **Banco de Dados:** H2 Database (In-Memory) com Spring Data JPA
- **Documentação:** SpringDoc OpenAPI (Swagger)
- **Gerenciador de Dependências:** Maven
- **Infraestrutura/DevOps:** Docker (Multi-stage build), Hospedagem Render

## 🛡️ Arquitetura e Qualidade (QA)
A API foi desenhada seguindo o padrão **MVC (Model-View-Controller)** adaptado para APIs REST, separando claramente as responsabilidades:
- **Controllers:** Camada de recepção e validação de contratos HTTP (Verbos `GET`, `POST`, etc).
- **Entities:** Mapeamento Objeto-Relacional (ORM). Garantia de que as regras do banco de dados reflitam o código Java.
- **Validações de Entrada:** Prevenção de Erros 400. Campos obrigatórios e tipagem estrita.
- **Integridade Referencial:** Prevenção de Erros 500. O sistema bloqueia a criação de "Jogos Órfãos", exigindo que a Plataforma e a Desenvolvedora existam no banco antes de cadastrar o jogo.

---

## 📚 Como testar e usar a API

Para testar a API através do [Swagger](https://api-games-94ep.onrender.com/swagger-ui/index.html) ou via Postman/Insomnia, é necessário respeitar o fluxo do banco de dados relacional.

### Passo 1: Cadastrar a Plataforma
O sistema exige que a plataforma do jogo exista previamente.
- **Endpoint:** `POST /platforms`
- **Body (JSON):**
```json
{
  "name": "Nintendo Switch",
  "company": "Nintendo"
}
Passo 2: Cadastrar a Desenvolvedora
A fabricante também precisa existir para o vínculo ser feito.

Endpoint: POST /developers

Body (JSON):

JSON
{
  "name": "Nintendo",
  "country": "Japão"
}
Passo 3: Cadastrar o Jogo
Com as entidades base criadas (IDs gerados), você pode registrar o jogo vinculando-o com segurança.

Endpoint: POST /games

Body (JSON):

JSON
{
  "name": "Super Mario Odyssey",
  "price": 299.90,
  "genre": "ACTION",
  "platforms": [
    {
      "id": 1
    }
  ],
  "developer": {
    "id": 1
  }
}
Passo 4: Listar os Jogos
Endpoint: GET /games

### Passo 4: Listar os Jogos (Com Paginação)
A API implementa paginação nativa para garantir alta performance em grandes volumes de dados.
- **Endpoint:** `GET /games`
- **Parâmetros Opcionais:** `?page=0&size=10&sort=name,asc`
- O retorno divide os dados no array `"content"` e fornece metadados de navegação no objeto `"pageable"`, facilitando a integração com interfaces Frontend (como Angular).
Retorna o JSON completo com a lista de jogos, suas respectivas plataformas e desenvolvedoras.

💻 Como executar o projeto localmente
Caso queira rodar o projeto na sua máquina para desenvolvimento:

Pré-requisitos:

Java 17 instalado

Maven instalado (ou utilizar o Wrapper incluso)

Git

Passo a passo:

Clone este repositório:

Bash
git clone [https://github.com/CauaBrandao/API-GAMES.git](https://github.com/CauaBrandao/API-GAMES.git)
Entre na pasta do projeto final:

Bash
cd API-GAMES/games-api-final
Execute o projeto usando o Maven Wrapper:

Bash
./mvnw spring-boot:run
A aplicação estará rodando na porta 8080. Acesse http://localhost:8080/swagger-ui/index.html.
