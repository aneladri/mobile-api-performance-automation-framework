# Lab 09 – Git Workflow

## Goal

By the end of this lab, you will understand:

* What Git is
* Why Git is used
* What a repository is
* What a branch is
* What a commit is
* What staging means
* How merging works
* What cherry-picking is
* How MAPAF development workflow operates

---

# What is Git?

Git is a version control system.

It tracks:

* Code changes
* File changes
* History
* Contributors

Without Git:

```text id="git1"
project-final.zip
project-final-final.zip
project-final-final-v2.zip
```

With Git:

```text id="git2"
Meaningful history
Reusable workflow
Traceability
```

---

# Why Do We Need Git?

Git helps teams:

```text id="git3"
Collaborate
Review changes
Rollback mistakes
Track history
```

---

# What is a Repository?

A repository (repo) stores:

```text id="git4"
Code
History
Branches
Tags
```

Example:

```bash id="git5"
git clone <repository-url>
```

---

# What is a Branch?

A branch is an isolated line of development.

Example:

```text id="git6"
develop

feature/login

feature/healing-engine
```

---

# Why Use Branches?

Without branches:

```text id="git7"
Everyone changes the same code.
```

With branches:

```text id="git8"
Safe development
Independent work
```

---

# Create a Branch

Example:

```bash id="git9"
git checkout -b feature/new-feature
```

Meaning:

```text id="git10"
Create branch
↓
Switch to branch
```

---

# What is a Commit?

A commit is a snapshot of your changes.

Example:

```bash id="git11"
git commit -m "Add login test"
```

Think of it as:

```text id="git12"
Save point
```

for your code.

---

# What is Staging?

Before a commit:

```bash id="git13"
git add file.java
```

Git stages changes.

Meaning:

```text id="git14"
Prepare file for commit
```

---

# Typical Development Flow

```bash id="git15"
git status

git add .

git commit -m "Meaningful message"

git push
```

---

# What is a Merge?

A merge combines branches.

Example:

```text id="git16"
feature/login
↓
develop
```

Command:

```bash id="git17"
git merge feature/login
```

---

# What is a Pull Request?

A Pull Request (PR) is a request to merge changes.

Purpose:

```text id="git18"
Code Review
Approval
Quality Control
```

---

# What is Cherry Picking?

Cherry-picking copies a specific commit from one branch to another.

Example:

```text id="git19"
Branch A

Commit A
Commit B

Branch B

Only need Commit A
```

Command:

```bash id="git20"
git cherry-pick <commit-id>
```

---

# Why Use Cherry Pick?

Example:

```text id="git21"
Commit accidentally created on develop
```

Need it on:

```text id="git22"
feature branch
```

Cherry-pick moves the commit safely.

---

# Real MAPAF Example

We experienced:

```text id="git23"
HealingBudgetGuard commit
```

created on:

```text id="git24"
develop
```

Needed on:

```text id="git25"
feature/cost-controlled-locator-healing
```

Solution:

```bash id="git26"
git cherry-pick <commit-id>
```

---

# What is git status?

Most important Git command.

Execute:

```bash id="git27"
git status
```

Shows:

```text id="git28"
Modified files

Untracked files

Branch information
```

---

# What is git log?

Shows commit history.

Execute:

```bash id="git29"
git log --oneline
```

Example:

```text id="git30"
e12c5a9 Add healing budget guard
d5e7f21 Add performance report generator
```

---

# Conflict Resolution

Conflicts occur when:

```text id="git31"
Two branches modify the same code
```

Git cannot decide which version is correct.

Example:

```text id="git32"

Then:

```bash id="git33"
git add .
git commit
```

---

# Common Mistakes

## Mistake

Working directly on develop.

Correct:

```text id="git34"
Create feature branch
```

---

## Mistake

Not checking status.

Correct:

```bash id="git35"
git status
```

before every commit.

---

## Mistake

Large commits.

Wrong:

```text id="git36"
50 unrelated changes
```

Correct:

```text id="git37"
Small focused commits
```

---

# MAPAF Development Workflow

Standard workflow:

```text id="git38"
develop
↓
feature branch
↓
commit
↓
push
↓
PR
↓
review
↓
merge
```

---

# Troubleshooting

## Problem

```text id="git39"
Wrong branch
```

Check:

```bash id="git40"
git branch --show-current
```

---

## Problem

```text id="git41"
Lost changes
```

Check:

```bash id="git42"
git status
```

---

## Problem

```text id="git43"
Cherry-pick paused
```

Continue:

```bash id="git44"
git cherry-pick --continue
```

---

# Daily Git Checklist

Before coding:

```bash id="git45"
git status

git branch --show-current
```

After coding:

```bash id="git46"
git add .
git commit -m "message"
git push
```

Before merge:

```bash id="git47"
git status
```

Must be:

```text id="git48"
working tree clean
```

---

# Checkpoint

The trainee should be able to answer:

1. What is Git?
2. What is a branch?
3. What is a commit?
4. What is staging?
5. What is a merge?
6. What is a Pull Request?
7. What is cherry-picking?
8. When should cherry-picking be used?
9. Why is git status important?
10. What is the MAPAF development workflow?

