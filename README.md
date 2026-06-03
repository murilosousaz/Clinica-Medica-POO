# Sistema de Clínica Médica

Projeto desenvolvido para a disciplina **Programação Estruturada e Orientada a Objetos**, da Universidade Estadual do Ceará (UECE).

O sistema simula o funcionamento de uma clínica médica, permitindo o gerenciamento de pacientes, médicos, enfermeiros, consultas, triagens, avaliações, contas, prontuários, lista de espera, estatísticas e exportação/importação de dados em CSV.

---

## Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Requisitos da Disciplina Atendidos](#requisitos-da-disciplina-atendidos)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura do Projeto](#arquitetura-do-projeto)
- [Principais Entidades](#principais-entidades)
- [Regras de Negócio](#regras-de-negócio)
- [Como Executar](#como-executar)
- [Acessos do Sistema](#acessos-do-sistema)
- [Swagger / Documentação da API](#swagger--documentação-da-api)
- [Exportação e Importação CSV](#exportação-e-importação-csv)
- [Fluxo Sugerido para Apresentação](#fluxo-sugerido-para-apresentação)
- [Conceitos de POO Utilizados](#conceitos-de-poo-utilizados)
- [Uso de IA](#uso-de-ia)
- [Autores](#autores)

---

## Sobre o Projeto

O **Sistema de Clínica Médica** foi desenvolvido com o objetivo de aplicar conceitos de Programação Orientada a Objetos em um sistema completo, com interface gráfica, persistência de dados, tratamento de exceções e organização em camadas.

A aplicação possui uma interface web servida diretamente pelo Spring Boot. Ao acessar o sistema, o usuário é direcionado inicialmente para a tela de login. Após o acesso, é exibida uma dashboard com visão geral dos dados e um menu lateral com módulos separados por entidade.

O projeto utiliza PostgreSQL como banco principal e também possui exportação/importação de dados em arquivos CSV, atendendo ao requisito da disciplina relacionado ao armazenamento em arquivos.

---

## Funcionalidades

O sistema possui as seguintes funcionalidades principais:

- Login demonstrativo de médicos e pacientes;
- Dashboard com visão geral do sistema;
- Cadastro e listagem de pacientes;
- Cadastro e listagem de médicos;
- Cadastro e listagem de enfermeiros;
- Pesquisa de médicos por nome, especialidade e plano de saúde;
- Agendamento de consultas;
- Cancelamento de consultas;
- Controle de limite diário de consultas por médico;
- Limite especial para pediatras;
- Lista de espera para dias lotados;
- Promoção automática de pacientes da lista de espera;
- Registro de triagens;
- Realização completa de consultas médicas;
- Registro de sintomas, diagnóstico, tratamento, medicamentos, exames e observações;
- Visualização do prontuário do paciente;
- Avaliação textual da consulta;
- Avaliação por estrelas;
- Cálculo de média de avaliações dos médicos;
- Geração e consulta de contas;
- Estatísticas do sistema;
- Exportação de dados para CSV;
- Importação de dados via CSV;
- Documentação da API via Swagger/OpenAPI.

---

## Requisitos da Disciplina Atendidos

O trabalho da disciplina exige:

- Programação Orientada a Objetos;
- Interface gráfica para entrada e saída de informações;
- Armazenamento de dados em arquivos `.csv` ou `.txt`;
- Tratamento de exceções;
- Login de médicos e pacientes;
- Agendamento e cancelamento de consultas;
- Realização de consultas;
- Avaliação de consultas;
- Visualização de prontuário;
- Estatísticas do sistema;
- Uso de herança, polimorfismo, encapsulamento, classes abstratas e coleções.

Este projeto atende esses pontos por meio de:

- Entidades Java organizadas por domínio;
- Interface web em HTML, CSS e JavaScript;
- Backend em Spring Boot;
- Exportação/importação CSV;
- Tratamento de erros centralizado;
- Especializações de médicos;
- Lista de espera;
- Prontuário e avaliações;
- Dashboard e estatísticas;
- Documentação da API com Swagger.

---

## Tecnologias Utilizadas

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Lombok
- Maven
- Springdoc OpenAPI / Swagger

### Frontend

- HTML5
- CSS3
- JavaScript puro

### Infraestrutura

- Docker
- Docker Compose
- PostgreSQL em container

---

## Arquitetura do Projeto

O projeto segue uma organização em camadas:

```text
src
├── main
│   ├── java
│   │   └── br
│   │       └── uece
│   │           └── clinica
│   │               ├── application
│   │               ├── domain
│   │               ├── infrastructure
│   │               └── presentation
│   └── resources
│       ├── static
│       │   ├── index.html
│       │   ├── styles.css
│       │   └── app.js
│       └── application.yml
├── test
docker-compose.yml
pom.xml
README.md
```

### Camadas principais

#### Domain

Contém as entidades e regras centrais do domínio, como `Paciente`, `Medico`, `Consulta`, `Avaliacao`, `Conta`, `Triagem` e especialidades médicas.

#### Application

Contém os services responsáveis pelas regras de negócio, como agendamento, cancelamento, lista de espera, realização de consulta, avaliação e exportação CSV.

#### Infrastructure

Contém configurações técnicas, integração com banco de dados, repositórios e configurações de Swagger.

#### Presentation

Contém os controllers da API REST, DTOs e mappers usados para comunicação com o frontend.

#### Static

Contém o frontend web da aplicação.

---

## Principais Entidades

### Paciente

Representa o usuário atendido pela clínica.

Principais informações:

- Nome;
- CPF;
- Telefone;
- E-mail;
- Data de nascimento;
- Plano de saúde;
- Histórico de consultas.

### Médico

Representa o profissional responsável por realizar consultas.

Principais informações:

- Nome;
- CRM;
- Especialidade;
- Planos de saúde atendidos;
- Valor da consulta particular;
- Lista de avaliações.

### Enfermeiro

Representa o profissional que apoia o fluxo de atendimento e triagem.

Principais informações:

- Nome;
- COREN;
- Telefone;
- E-mail.

### Consulta

Representa um agendamento ou atendimento médico.

Principais informações:

- Médico;
- Paciente;
- Data;
- Status;
- Sintomas;
- Diagnóstico;
- Tratamento;
- Medicamentos;
- Exames;
- Observações;
- Valor pago.

### Triagem

Representa a avaliação inicial do paciente.

Principais informações:

- Paciente;
- Enfermeiro;
- Queixa principal;
- Classificação de risco;
- Pressão arterial;
- Temperatura;
- Frequência cardíaca;
- Observações.

### Avaliação

Representa a avaliação feita pelo paciente após a consulta.

Principais informações:

- Texto da avaliação;
- Quantidade de estrelas;
- Médico avaliado;
- Consulta relacionada.

### Conta

Representa uma cobrança gerada para pacientes sem plano de saúde.

Principais informações:

- Paciente;
- Valor;
- Situação do pagamento.

### Lista de Espera

Representa pacientes aguardando vaga em uma consulta quando a agenda do médico está cheia.

---

## Regras de Negócio

### Agendamento

Para agendar uma consulta, o paciente seleciona um médico e uma data.

Regras:

- Médico comum atende no máximo 3 pacientes por dia;
- Pediatra atende no máximo 2 pacientes por dia;
- Caso o dia esteja lotado, o paciente pode entrar na lista de espera;
- A lista de espera respeita a ordem de entrada.

### Cancelamento

Ao cancelar uma consulta:

- A consulta é removida ou marcada como cancelada;
- O sistema verifica se existe paciente na lista de espera;
- O primeiro paciente da fila assume automaticamente a vaga disponível.

### Realização da Consulta

Durante a consulta, o médico informa:

- Sintomas;
- Diagnóstico;
- Tratamento sugerido;
- Medicamentos;
- Exames solicitados;
- Observações gerais.

Essas informações são armazenadas no prontuário do paciente.

### Prontuário

O prontuário contém o histórico de consultas realizadas pelo paciente, incluindo:

- Data da consulta;
- Médico responsável;
- Sintomas;
- Diagnóstico;
- Medicamentos;
- Exames solicitados;
- Valor da consulta.

### Avaliação

Após a consulta, o paciente pode avaliar o atendimento:

- Escrevendo uma avaliação textual;
- Atribuindo uma nota de 1 a 5 estrelas.

As avaliações impactam a média do médico.

### Conta

Caso o paciente não possua plano de saúde, o sistema pode gerar uma conta para pagamento da consulta.

---

## Como Executar

### Pré-requisitos

Antes de executar o projeto, instale:

- Java 21 ou superior;
- Docker;
- Docker Compose;
- Git.

Verifique as versões:

```bash
java -version
docker --version
docker compose version
git --version
```

---

### 1. Clonar o repositório

```bash
git clone https://github.com/murilosousaz/Clinica-Medica-POO.git
cd Clinica-Medica-POO
```

Caso o projeto já esteja na máquina:

```bash
cd /home/murilo/IdeaProjects/Clinica-Medica-POO
```

---

### 2. Subir o banco PostgreSQL

```bash
docker compose up -d
```

Verifique se o container está rodando:

```bash
docker ps
```

Para ver os logs do banco:

```bash
docker logs clinica-postgres-db
```

---

### 3. Compilar o projeto

```bash
./mvnw clean package
```

Se estiver usando Linux e o Maven Wrapper não tiver permissão de execução:

```bash
chmod +x mvnw
./mvnw clean package
```

---

### 4. Executar a aplicação

```bash
./mvnw spring-boot:run
```

---

### 5. Acessar a aplicação

Abra no navegador:

```text
http://localhost:8080
```

A primeira tela exibida será a tela de login.

---

## Acessos do Sistema

### Interface Web

```text
http://localhost:8080
```

### Swagger UI

```text
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON

```text
http://localhost:8080/api-docs
```

---

## Swagger / Documentação da API

O sistema possui documentação da API com Swagger/OpenAPI.

Com a aplicação rodando, acesse:

```text
http://localhost:8080/swagger-ui.html
```

No Swagger é possível visualizar e testar endpoints relacionados a:

- Pacientes;
- Médicos;
- Enfermeiros;
- Consultas;
- Triagens;
- Avaliações;
- Contas;
- Estatísticas;
- CSV.

---

## Exportação e Importação CSV

O sistema mantém PostgreSQL como banco principal, mas também permite exportar e importar dados em CSV.

Os arquivos CSV são armazenados na pasta:

```text
dados/
```

Exemplos de arquivos gerados:

```text
dados/pacientes.csv
dados/medicos.csv
dados/consultas.csv
dados/avaliacoes.csv
dados/contas.csv
dados/lista_espera.csv
```

### Exportar dados

Endpoint:

```http
POST /api/csv/exportar
```

### Importar dados

Endpoint:

```http
POST /api/csv/importar
```

Essas operações também podem ser acessadas pelo frontend, na tela **CSV & API**.

---

## Principais Endpoints

### Pacientes

```http
GET /api/pacientes
POST /api/pacientes
GET /api/pacientes/{id}
PUT /api/pacientes/{id}
DELETE /api/pacientes/{id}
GET /api/pacientes/{id}/prontuario
```

### Médicos

```http
GET /api/medicos
POST /api/medicos
GET /api/medicos/{id}
PUT /api/medicos/{id}
DELETE /api/medicos/{id}
```

### Enfermeiros

```http
GET /api/enfermeiros
POST /api/enfermeiros
GET /api/enfermeiros/{id}
PUT /api/enfermeiros/{id}
DELETE /api/enfermeiros/{id}
```

### Consultas

```http
GET /api/consultas
POST /api/consultas
GET /api/consultas/{id}
PUT /api/consultas/{id}/realizar
DELETE /api/consultas/{id}
GET /api/consultas/paciente/{pacienteId}/prontuario
```

### Triagens

```http
GET /api/triagens
POST /api/triagens
GET /api/triagens/{id}
```

### Avaliações

```http
GET /api/avaliacoes
POST /api/avaliacoes
```

### CSV

```http
POST /api/csv/exportar
POST /api/csv/importar
```

---

## Fluxo Sugerido para Apresentação

Durante a apresentação, recomenda-se seguir o seguinte roteiro:

1. Apresentar o objetivo do sistema;
2. Mostrar a tela de login;
3. Entrar no sistema;
4. Mostrar a dashboard;
5. Cadastrar um paciente;
6. Cadastrar um médico;
7. Pesquisar médicos por especialidade ou plano;
8. Agendar uma consulta;
9. Demonstrar o limite diário de consultas;
10. Demonstrar a lista de espera;
11. Cancelar uma consulta;
12. Mostrar a promoção automática da lista de espera;
13. Registrar uma triagem;
14. Realizar uma consulta completa;
15. Visualizar o prontuário do paciente;
16. Avaliar a consulta;
17. Mostrar as estatísticas;
18. Exportar os dados em CSV;
19. Abrir o Swagger;
20. Explicar os conceitos de POO utilizados.

---

## Conceitos de POO Utilizados

### Classes e Objetos

As entidades do sistema são representadas por classes Java, como:

- `Paciente`;
- `Medico`;
- `Consulta`;
- `Triagem`;
- `Avaliacao`;
- `Conta`.

Cada registro manipulado pelo sistema corresponde a um objeto dessas classes.

### Encapsulamento

Os atributos das classes são protegidos e acessados por métodos, permitindo maior controle sobre os dados e regras da aplicação.

### Herança

O sistema utiliza herança para representar especialidades médicas.

Exemplos:

- `Medico`;
- `Pediatra`;
- `Cardiologista`;
- `Dermatologista`;
- `ClinicoGeral`.

### Polimorfismo

Cada especialidade médica pode ter comportamentos próprios.

Exemplo:

- Médico comum atende até 3 pacientes por dia;
- Pediatra atende até 2 pacientes por dia.

Dessa forma, o sistema pode tratar médicos de forma genérica, mas respeitando comportamentos específicos.

### Classes Abstratas

Classes de base podem ser utilizadas para representar comportamentos comuns e permitir especializações.

### Coleções

O sistema utiliza coleções para manipular listas de:

- Pacientes;
- Médicos;
- Consultas;
- Avaliações;
- Contas;
- Itens da lista de espera.

### Tratamento de Exceções

O sistema trata situações como:

- Consulta inexistente;
- Agenda lotada;
- Dados inválidos;
- Operações não permitidas;
- Paciente duplicado;
- Falhas de importação/exportação CSV.

---

## Comandos Úteis

### Parar os containers

```bash
docker compose down
```

### Parar e remover volumes

```bash
docker compose down -v
```

### Subir banco novamente

```bash
docker compose up -d
```

### Rodar build

```bash
./mvnw clean package
```

### Rodar aplicação

```bash
./mvnw spring-boot:run
```

### Ver status do Git

```bash
git status
```

### Criar commit

```bash
git add .
git commit -m "Atualiza sistema de clínica médica"
```

### Enviar para o GitHub

```bash
git push origin main
```

---

## Possíveis Problemas

### Erro de conexão com PostgreSQL

Se aparecer erro como:

```text
Connection to localhost:5432 refused
```

Verifique se o container do banco está rodando:

```bash
docker ps
```

Se não estiver, execute:

```bash
docker compose up -d
```

### Porta 5432 ocupada

Se a porta `5432` estiver ocupada, altere o `docker-compose.yml` e o `application.yml` para usar outra porta, como `5433`.

### Erro de permissão no Maven Wrapper

Execute:

```bash
chmod +x mvnw
```

---

## Uso de IA

A IA foi utilizada como apoio durante o desenvolvimento, principalmente para:

- Corrigir erros de build;
- Auxiliar na organização do backend;
- Melhorar a interface gráfica;
- Revisar a aderência aos requisitos da disciplina;
- Gerar documentação auxiliar;
- Apoiar a criação do Swagger;
- Sugerir melhorias de estrutura e apresentação.

As decisões finais, execução, testes e entendimento do sistema são responsabilidade da equipe.

---

## Autores

Projeto desenvolvido para fins acadêmicos na disciplina **Programação Estruturada e Orientada a Objetos**.

Equipe:

- Murilo Sousa
- Lucas Mendes
- Maycon Alves

---

## Licença

Projeto acadêmico desenvolvido para a UECE.
