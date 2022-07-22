[![CI (Ubuntu)](https://github.com/tsdl-oss/java/actions/workflows/main.yml/badge.svg)](https://github.com/tsdl-oss/java/actions/workflows/main.yml)

### Initial Setup

* Clone the repository.
* Execute `mvn clean install`

### Changing Version of Project Components

* Update value of `${revision}` property in the root module's [pom.xml](pom.xml)
* Execute `mvn clean install` to deploy the artifacts with the new versions into the local repository  \
  (This makes it possible to build child modules individually because its module dependencies will be found in the local
  repository)

### Available IntelliJ Run Configurations

| **Name**                   | **File**                               | **Type**    | **Description** | **Command** |
|----------------------------|----------------------------------------|-------------|-----------------|-------------|
| TestDataCreator            | `./run/TestDataCreator.run.xml`        | Application |                 |             |
| QueryValidator             | `./run/QueryValidator.run.xml`         | Application |                 |             |
| Compose Deployment         | `./run/ComposeDeployment.run.xml`      | Docker      |                 |             |
| Compose Build + Deployment | `./run/ComposeBuildDeployment.run.xml` | Docker      |                 |             |
| All Tests                  | `./run/AllTests.run.xml`               | JUnit       |                 |             |
| Package                    | `./run/Package.run.xml`                | Maven       |                 |             |
| Install                    | `./run/Install.run.xml`                | Maven       |                 |             |
| ANTLR Generate             | `./run/ANTLRGenerate.run.xml`          | Maven       |                 |             |
| TsdlQueryService           | `./run/TsdlQueryService.run.xml`       | Spring Boot |                 |             |