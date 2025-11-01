# Jenga Backend

Jenga backend using Quarkus, the Supersonic Subatomic Java Framework.

## Documentation

Swagger: [URL](http://localhost:8080/swagger)

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

### JWT Keypair Setup

For JWT authentication, a **keypair** (private and public keys) must be generated and stored under the following locations, when not running in dev mode:

* `src/main/resources/privateKey.pem`
* `src/main/resources/publicKey.pem`

You can generate the keypair using the following commands:

```shell script
# Generate the private key
openssl genrsa -out privateKey.pem 2048 

# Generate the public key from the private key
openssl rsa -in privateKey.pem -pubout -out publicKey.pem
```

Ensure that these files are correctly placed in the `src/main/resources` directory before running or packaging the application.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/jenga-0.0.1-SNAPSHOT-runner`

<br>

## Configuring Quarkus Langchain4j with Gemini

To run Langchain4j with the Gemini model in your Quarkus application, you need to configure your API key and model properties.

### 1\. Set Your Gemini API Key (in `.env`)

Your API key must be set as an environment variable in a `.env` file located at the root of your project. Quarkus will automatically load this file.

**`.env`**

```dotenv
QUARKUS_LANGCHAIN4J_AI_GEMINI_API_KEY=your_actual_api_key_goes_here
```

### 2\. Configure `application.properties`

Your `application.properties` file should then reference this environment variable to inject the API key. Here, you also specify which Gemini model you intend to use.

**`src/main/resources/application.properties`**

```properties
# Gemini AI Model Configuration
# -----------------------------

# Reference the API key from the .env file.
# The ':${}' syntax provides an empty default, but .env will supply the value.
quarkus.langchain4j.ai.gemini.api-key=${QUARKUS_LANGCHAIN4J_AI_GEMINI_API_KEY:}

# Set the desired chat model.
# This value can be changed to any other supported Gemini model.
quarkus.langchain4j.ai.gemini.chat-model.model-id=gemini-1.5-flash
```

# How to Export GitHub Issues

This guide covers the basic steps to export all issues from a repository to a JSON file using the official GitHub CLI.

**Regarding Ticket:** [github.com/Jenga-PMS/Jenga/issues/56](https://github.com/Jenga-PMS/Jenga/issues/56)

---

### Introduction

With the official GitHub CLI, you can easily export all issues. The following command will save the data to a **JSON** file (not CSV).

### Step 1: Install the GitHub CLI

If you are on a Mac, you can use Homebrew.

```bash
brew install gh
```

### Step 2: Log In

You will need to authenticate with your GitHub account. This command requests the `read:project` scope, which is necessary to access project information.

```bash
gh auth login -h github.com -s read:project
```

### Step 3: Export All Issues

Run this command to fetch all issues (both open and closed) and save them to a file named `issues.json`.

```bash
gh issue list --repo Jenga-PMS/Jenga --state all --json number,title,body,state,assignees,labels,milestone,comments,createdAt,updatedAt,projectItems > all-issues.json
```

Size, Estimate and Priority could not be extracted
