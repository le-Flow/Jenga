
# Imports
import os 
from fastapi import FastAPI
from langchain_community.chat_models import ChatLiteLLM
from langchain.agents import AgentExecutor, create_react_agent
from langchain import hub
from pydantic import BaseModel

# Tool Imports
from src.tools import create_ticket

# Load the API key from environment variables
google_api_key = os.getenv("GOOGLE_API_KEY")

# Setup LLM, tools, prompt, and agent executor
llm = ChatLiteLLM(model="gemini-pro", api_key=google_api_key)
tools = [create_ticket]
prompt = hub.pull("hwchase17/react")
agent = create_react_agent(llm, tools, prompt)
agent_executor = AgentExecutor(agent=agent, tools=tools, verbose=True)

# Setup FastAPI 
app = FastAPI(title="MCP Server")
class Query(BaseModel):
    text: str

@app.post("/invoke")
async def invoke_agent(query: Query):
    response = await agent_executor.ainvoke({"input": query.text})
    return {"response": response["output"]}