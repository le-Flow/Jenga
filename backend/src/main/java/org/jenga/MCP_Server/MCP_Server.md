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