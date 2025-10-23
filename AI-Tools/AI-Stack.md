# **MCP Server**

MCP handles connection to all ressources, llm just has to handle the connection to the MCP. This allows a standardised accessformat for the ai.

https://medium.com/@sebuzdugan/what-are-mcp-servers-the-new-ai-trend-explained-for-everyone-8936489c561f

<br>

# **LangChain:**

( stanrdardises the ai connection process and tool creating )

LangChain serves as a generic interface for almost any LLM and offers a central development environment to create LLM applications and integrate them with external data sources and software workflows.

You can treat the LiteLLM model like an OpenAI one in Langchain

https://www.ibm.com/de-de/think/topics/langchain

<br>

# **LiteLLM:**

( LiteLLM standardises the communication interface with the LLM and offers a reverse Proxy for managing multiple llms )

**Overrview:**

It standardizes different LLM APIs into a single, OpenAI-compatible interface — so you can treat any model (even local ones like Ollama) as if it were an OpenAI ChatCompletion object.

Also offers multiple dashboards to track the AIs.

**Proxy:**

The LiteLLM Proxy Server tackles the biggest problem head-on: managing multiple LLMs, embeddings, and guardrails across different use cases — all in one place.

The idea is simple: spin up a central proxy server to expose every model you use.

Proxy Deployment Supports:
* Docker
* Terraform
* Kubernetes
* Helm Charts

https://medium.com/mitb-for-all/a-gentle-introduction-to-litellm-649d48a0c2c7