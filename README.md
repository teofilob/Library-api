# Library API

API Spring Boot 3.5.16 para gerenciamento de livros, com Java 25, Maven e banco H2.

## Executar com Docker Compose

Pré-requisito: Docker Desktop iniciado, usando containers Linux e Docker Compose v2.
Não é necessário instalar Java ou Maven na máquina para esta opção.

```powershell
docker compose up --build -d
docker compose logs -f api
```

O build executa os testes antes de gerar a imagem. A API fica disponível após a
mensagem `Started LibraryApplication` nos logs.

- API: http://localhost:8080/api/v1/books
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI: http://localhost:8080/v3/api-docs

O projeto utiliza H2 embutido e não depende de banco externo, cache ou mensageria.
No Compose, os dados ficam no volume `library-data`; o Hibernate cria e atualiza
o esquema automaticamente (`ddl-auto=update`, destinado ao uso local).
Fora do Compose, permanece o H2 em memória original.

Para usar outra porta no PowerShell:

```powershell
$env:APP_PORT = '8081'
docker compose up --build -d
```

Para parar, preservando os dados:

```powershell
docker compose down
```

`docker compose down -v` também apaga os volumes e os dados locais.

## Executar mvn install com Java 25

```powershell
docker compose run --rm maven
```

Esse serviço executa `mvn -B -ntp clean install`, com testes, produz o JAR em
`target/` e mantém as dependências no volume `maven-cache`. Ele não inicia junto
com a API. Para executar outro comando:

```powershell
docker compose run --rm maven mvn -B -ntp test
```

Evite executar builds locais e pelo container simultaneamente, pois compartilham
`target/`.

## Executar sem Docker

Instale um JDK 25 e configure `JAVA_HOME` para ele. Verifique a JVM usada pelo
Maven, inclusive a configuração Maven Runner da IDE:

```powershell
java -version
.\mvnw.cmd -version
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

Em Linux/macOS, use `sh ./mvnw`. O Wrapper baixa Maven e dependências na primeira
execução e precisa de acesso à internet. Se já houver Maven instalado, também
é possível usar `mvn clean install`.

## Java 25 e IntelliJ

Em File > Project Structure > Project, selecione o SDK 25.
Em Settings > Build, Execution, Deployment > Build Tools > Maven > Runner,
selecione JDK 25 no campo JRE. Confira também o JRE das configurações Maven
salvas em Run > Edit Configurations. Recarregue o projeto Maven após a atualização.

A compilação usa `--release 25`; executar Maven com um JDK anterior falhará.
O erro antigo `TypeTag :: UNKNOWN` foi tratado atualizando o Lombok e os demais
componentes da aplicação para versões compatíveis com Java 25.

Componentes: Spring Boot 3.5.16, Lombok 1.18.42, MapStruct 1.6.3 e Springdoc 2.8.17.
Os imports de persistência e validação usam Jakarta. A documentação agora usa
OpenAPI 3 via Springdoc; a configuração antiga do Springfox foi substituída.
Dependências sem uso (Jersey, Testcontainers e REST Docs/Asciidoctor) foram removidas.

Referências de compatibilidade:
- https://docs.spring.io/spring-boot/3.5/system-requirements.html
- https://projectlombok.org/changelog
- https://springdoc.org/v2/

Se já houver dados criados pelo H2 antigo, faça backup antes de executar esta
versão: a atualização do H2 1.x para 2.x pode exigir exportação/importação do banco.
Não apague o volume para migrar dados que deseja preservar.

## Validação da migração

`mvn clean install` executado com JDK 25: BUILD SUCCESS, 40 testes, nenhuma falha,
erro ou teste ignorado. Inclui inicialização Spring, criação/consulta/listagem/
exclusão de livros no H2, validação Jakarta e endpoints OpenAPI/Swagger UI.

`docker compose config --quiet` validou a configuração. O build e a execução dos
containers não foram verificados porque o motor Linux do Docker Desktop estava
indisponível nesta máquina.

## Contrato dos livros

A criação usa `CreateBookRequest` e as respostas usam `BookResponse`, ambos records.

```http
POST /api/v1/books
Content-Type: application/json

{"title":"Java","isbn":"978-0-306-40615-7"}
```

Retorna 201, com `Location: /api/v1/books/{id}` e corpo contendo `id`, `title` e
`isbn`. O ID é gerado pelo banco. Enviar `id` ou qualquer propriedade desconhecida
no POST retorna 400; clientes devem enviar somente `title` e `isbn`.

Título e ISBN são obrigatórios: valores ausentes, nulos, vazios ou contendo apenas
espaços são rejeitados com 400. O título é aparado nas extremidades e pode ter até
200 caracteres após normalização. O ISBN aceita ISBN-10 e ISBN-13 (prefixos 978/979),
com dígito verificador válido. Hífens e espaços são removidos; `x` vira `X`.
A resposta e o banco recebem o ISBN normalizado, sem separadores. Não há regra de
unicidade de ISBN, pois exemplares distintos podem compartilhar o mesmo ISBN.

```http
DELETE /api/v1/books/1
```

O DELETE não exige corpo e retorna 204. Um ID inexistente retorna 404, assim como
no GET por ID. Essas alterações substituem o contrato antigo que reutilizava
`BookDTO` para criação, resposta e exclusão.

## Cobertura de testes com JaCoCo

O JaCoCo 0.8.14 coleta cobertura durante os testes e gera relatórios na fase
`verify`, também executada por `install`:

```powershell
.\mvnw.cmd clean verify
# Ou, com Maven instalado:
mvn clean install
# Ou pelo Compose:
docker compose run --rm maven mvn -B -ntp clean verify
```

Abra `target/site/jacoco/index.html` no navegador para explorar pacotes, classes,
linhas e desvios condicionais cobertos. No PowerShell:

```powershell
Start-Process .\target\site\jacoco\index.html
```

Também são gerados `target/site/jacoco/jacoco.xml` (integrações de CI),
`target/site/jacoco/jacoco.csv` e `target/jacoco.exec` (dados da execução).
`mvn test` coleta os dados, mas não executa a fase `verify`; prefira `clean verify`
para gerar um relatório atualizado. `clean` remove os relatórios anteriores.

Não há exclusões manuais de classes nem percentual mínimo bloqueando o build.
O JaCoCo pode filtrar automaticamente código gerado, como métodos do Lombok;
cobertura alta não substitui a análise das assertions e dos cenários de teste.
