# Fintech Legacy Credit - Análise de Crédito com Spring Boot

Aplicação de análise de crédito desenvolvida com Spring Boot 4.0.3, JPA, H2 Database e DevTools.

# ☠️ Problemas encontrados

# Controller:

## SolicitacaoCreditoController.analisarSolicitacao() --- NATAN KAINAK
Arquivo: SolicitacaoCreditoController.java

- Feature Envy: Usa HashMap<String, Object> em vez de DTOs (SolicitacaoResponse), perdendo a clareza do contrato da API.
  O Controller precisa saber exatamente quais são as chaves ("valor", "cliente", "aprovado") para montar a resposta, em vez de apenas retornar um objeto ResultadoAnalise
- Inconsistência de URL: Mapeamento no controller diverge do que está sendo testado.
- Aceita valores negativos ou strings vazias sem erro. Poderia ter um '@NotNull(message = "O valor é obrigatório")' por exemplo.
- O código recebe Double(pode ser null) mas o método recebe double (que não pode ser null). Isso gera o risco de se ter um NullPointerException caso o valor venha como null.
- O catch (Exception e) esconde bugs reais do sistema.

## Dívida Técnica - Vitor Parente

### Code smells Testes Frágeis

**Explicação:**  
Quando um teste depende da data atual, do estado do banco ou da configuração do ambiente, ele deixa de ser totalmente previsível. Isso significa que dois desenvolvedores executando o mesmo teste podem obter resultados diferentes. Testes determinísticos devem sempre produzir o mesmo resultado para a mesma entrada, independentemente do ambiente ou do momento da execução.

**Problema:**  
Testes não confiáveis dificultam manutenção, reduzem a confiança na suíte de testes e podem falhar aleatoriamente mesmo sem alteração no código.

Exemplo:
`assertTrue(true);`

**Problema:**  
O teste sempre passa, independentemente do comportamento do sistema. Isso cria uma falsa sensação de segurança e não valida nenhuma regra de negócio real.
- Cobertura enganosa – testamos o método inteiro em vez de unidades; uma falha pode ocultar outro problema.
- Dificuldade de manutenção – qualquer pequena mudança na lógica exige atualização de muitos testes.
- Impedimento de refatoração – a alta complexidade e o acoplamento dificultam extrair comportamentos para classes menores.


### Code smells no Endpoint `POST /solicitacoes/processar-lote`

O controller apresenta alguns problemas de design que aumentam o acoplamento e dificultam manutenção e testes:

- **Captura genérica de `Exception`**  
  O uso de `try/catch` amplo esconde erros específicos e dificulta identificar a causa real de falhas. O ideal é tratar exceções específicas ou usar um handler global.

- **Uso de `HashMap` para resposta**  
  A resposta é montada manualmente com `HashMap`, usando strings fixas como chave. Isso pode gerar erros de digitação e falta de padronização. O mais adequado seria utilizar um DTO próprio para a resposta.

- **Responsabilidade excessiva no controller**  
  O controller calcula `clientes.size()` e monta a mensagem de retorno. Essas regras poderiam estar no serviço ou em uma classe responsável pela resposta, deixando o controller apenas como intermediador da requisição.

- **Falta de validação de entrada**  
  Não há verificação se a lista de clientes é nula ou vazia. Isso pode gerar `NullPointerException` ou comportamento inesperado.

- **Alto acoplamento ao serviço**  
  O controller depende diretamente de `AnaliseCreditoService`, dificultando testes unitários isolados e incentivando testes de integração mais pesados.

# Model:
OK

# Repository:
OK

# Service Analise de crédito: - Venicius
- Long Method: ~59 linhas centralizando validação, regras e IO.
- Deep Nesting: 5 níveis de if aninhados. Dificulta a compreensão do fluxo.

# Service Processador de  - Kelvin
**Linhas 18–24 apresentam um *code smell*:**

```java
if (cep.startsWith("85")) { // Paraná
    frete = 10.0;
} else if (cep.startsWith("01")) { // SP
    frete = 20.0;
} else {
    frete = 50.0;
}
```
**Linhas 35 apresenta um *code smell*:**
```
System.out.println("INSERT INTO PEDIDOS VALUES (" + cliente + ", " + (valor + frete + imposto) + ")");
```

**Justificativa:**

O trecho apresenta alguns problemas de manutenção e design:

* **Valores hardcoded (Magic Numbers / Magic Strings):** os prefixos de CEP (`"85"`, `"01"`) e os valores de frete (`10.0`, `20.0`, `50.0`) estão definidos diretamente no código, dificultando a compreensão e manutenção.
* **Baixa escalabilidade:** caso seja necessário adicionar novas regiões ou alterar valores de frete, será preciso modificar diretamente o código-fonte.
* **Acoplamento da regra de negócio:** as regras de cálculo de frete estão fixas na implementação, o que dificulta futuras mudanças ou expansões da lógica.
* **Vulnerabilidade de SQL injection:** Concatenação direta de strings em comandos SQL.

### Testes
OK

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 4.0.3**
  - Spring Web MVC
  - Spring Data JPA
  - Spring Validation
- **H2 Database** (Banco de dados em memória para testes e desenvolvimento)
- **Spring Boot DevTools** (Hot reload)
- **Lombok** (Redução de boilerplate)
- **JUnit 5** (Testes)
- **Mockito** (Mock de dependências)

## 📋 Pré-requisitos

- Java 21 instalado
- Maven 3.8+ instalado
- Git (opcional)

## 🔧 Configuração

### 1. Clonar ou extrair o projeto

```bash
cd fintech-legacy-credit
```

### 2. Instalar dependências

```bash
mvn clean install
```

### 3. Executar a aplicação

#### Opção 1: Via Maven
```bash
mvn spring-boot:run
```

#### Opção 2: Via IDE (IntelliJ IDEA)
1. Clique com botão direito em `Main.java`
2. Selecione "Run 'Main'"

#### Opção 3: Compilar e executar JAR
```bash
mvn clean package
java -jar target/fintech-legacy-credit-1.0-SNAPSHOT.jar
```

## 🌐 Acessar a Aplicação

A aplicação estará disponível em: **http://localhost:8080**

### Console H2 Database
Acesse o console do banco de dados em: **http://localhost:8080/api/h2-console**
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User**: `sa`
- **Password**: (deixe em branco)

## 📚 Endpoints da API

### 1. Analisar Solicitação de Crédito
```http
POST http://localhost:8080/api/solicitacoes/analisar
```

**Parâmetros:**
- `cliente` (String): Nome do cliente
- `valor` (Double): Valor solicitado
- `score` (Integer): Score de crédito (0-1000)
- `negativado` (Boolean, opcional): Cliente negativado? (padrão: false)
- `tipoConta` (String, opcional): PF ou PJ (padrão: PF)

**Exemplo:**
```bash
curl -X POST "http://localhost:8080/api/solicitacoes/analisar?cliente=João%20Silva&valor=5000&score=750&negativado=false&tipoConta=PF"
```

**Resposta:**
```json
{
  "cliente": "João Silva",
  "valor": 5000.0,
  "score": 750,
  "aprovado": true,
  "mensagem": "Solicitação aprovada"
}
```

### 2. Processar Lote de Solicitações
```http
POST http://localhost:8080/api/solicitacoes/processar-lote
Content-Type: application/json

["Cliente1", "Cliente2", "Cliente3"]
```

**Resposta:**
```json
{
  "mensagem": "Lote processado com sucesso",
  "totalClientes": "3"
}
```

### 3. Obter Solicitações por Cliente
```http
GET http://localhost:8080/api/solicitacoes/por-cliente/{cliente}
```

**Exemplo:**
```bash
curl "http://localhost:8080/api/solicitacoes/por-cliente/João%20Silva"
```

### 4. Obter Solicitações Aprovadas
```http
GET http://localhost:8080/api/solicitacoes/aprovadas
```

### 5. Obter Solicitações Reprovadas
```http
GET http://localhost:8080/api/solicitacoes/reprovadas
```

### 6. Obter Total de Aprovados por Tipo
```http
GET http://localhost:8080/api/solicitacoes/total-aprovados/{tipoConta}
```

**Exemplo:**
```bash
curl "http://localhost:8080/api/solicitacoes/total-aprovados/PF"
```

### 7. Obter Solicitações por Período
```http
GET http://localhost:8080/api/solicitacoes/por-periodo?inicio=2024-01-01T00:00:00&fim=2024-12-31T23:59:59
```

### 8. Health Check
```http
GET http://localhost:8080/api/solicitacoes/saude
```

**Resposta:**
```json
{
  "status": "ok",
  "mensagem": "Aplicação funcionando corretamente"
}
```

## 🧪 Executar Testes

### Testes Unitários
```bash
mvn test
```

### Testes de Integração
```bash
mvn verify
```

### Testes com Coverage
```bash
mvn clean test jacoco:report
```

## 📊 Estrutura do Projeto

```
fintech-legacy-credit/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/nogueiranogueira/aularefatoracao/
│   │   │       ├── Main.java (Spring Boot Application)
│   │   │       ├── controller/
│   │   │       │   └── SolicitacaoCreditoController.java
│   │   │       ├── service/
│   │   │       │   └── AnaliseCreditoService.java
│   │   │       ├── repository/
│   │   │       │   └── SolicitacaoCreditoRepository.java
│   │   │       └── model/
│   │   │           └── SolicitacaoCredito.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── br.com.nogueiranogueira.aularefatoracao.TestAnaliseCreditoService.java
│           └── SolicitacaoCreditoIntegrationTest.java
└── pom.xml
```

## 🔐 Regras de Negócio

### Critérios de Aprovação

**Pessoa Física (PF):**
- Score mínimo: 500
- Não pode estar negativado
- Se valor > R$ 5.000, score deve ser > 800
- Não aprovado em finais de semana (requer aprovação manual)

**Pessoa Jurídica (PJ):**
- Score mínimo: 500
- Não pode estar negativado
- Se valor > R$ 50.000, score deve ser > 700

## 📝 Logs

Os logs são configurados em diferentes níveis:
- **INFO**: Informações gerais da aplicação
- **DEBUG**: Informações detalhadas para debug
- **WARN**: Avisos e solicitações reprovadas
- **ERROR**: Erros de processamento

Veja `application.properties` para configurar os níveis de log.

## 🆘 Troubleshooting

### Porta 8080 em uso
```bash
# Mude a porta no application.properties
server.port=8081
```

### Erro de dependências Maven
```bash
mvn clean install -U
```

### Limpar cache do Maven
```bash
mvn clean install
```

## 📄 Licença

Este projeto é parte de um exercício de refatoração de código legado.

## 👨‍💻 Autor

Desenvolvido como exemplo de aplicação Spring Boot com boas práticas de desenvolvimento.
