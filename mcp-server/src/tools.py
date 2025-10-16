from langchain.tools import tool
import requests

API_BASE_URL = "https://api.your-jira-clone.com/v1"

@tool
def create_ticket(title: str, description: str) -> str:
    """Creates a new ticket with a title and description. Returns the new ticket's ID."""
    # ... API call logic ...
    return "Ticket PROJ-123 created successfully."