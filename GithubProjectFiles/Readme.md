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
gh issue list --repo Jenga-PMS/Jenga --state all --limit 1000 --json number,title,body,state,assignees,labels,milestone,comments,createdAt,updatedAt,projectItems > all-issues.json
```

Size, Estimate and Priority could not be extracted