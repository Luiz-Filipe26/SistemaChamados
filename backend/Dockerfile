# Etapa 1: Construir o projeto
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app

# Copia o arquivo pom.xml e baixa as dependências do Maven
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia todo o código-fonte do projeto
COPY src ./src

# Compila o projeto e gera o JAR
RUN mvn clean package -DskipTests

# Etapa 2: Criar a imagem final
FROM openjdk:17-jdk-slim

# Atualizar o apt-get e instalar curl
RUN apt-get update && apt-get install -y curl

# Diretório onde o JAR será copiado
WORKDIR /app

# Copia o JAR gerado na etapa de build
COPY --from=build /app/target/ticket-manager-0.0.1-SNAPSHOT.jar /app/ticket-manager.jar

# Define a porta que o container vai expor
EXPOSE 8080

# Comando para iniciar o Spring Boot
ENTRYPOINT ["java", "-jar", "/app/ticket-manager.jar"]
