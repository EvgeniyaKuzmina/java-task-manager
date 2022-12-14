# Task Manager
Service based on Java 11

## Task tracker for organizing collaboration on tasks. 
Application has three types of task:
 * subtask
 * epic
 * task

#### Subtask
It is part of epic, can not be an independent task.

#### Epic 
Common big task which can be separate to different small subtask. 

#### Task
It is independent task

---

All types of tasks have:
 1. title 
 2. description 
 3. id 
 4. status 

Tasks can have next status:
 * NEW — the task has just been created, but it has not yet been started
 * IN_PROGRESS — the task is being worked on
 * DONE — the task is completed
 
 If all subtasks have status DONE — so  common epic has status DONE
 
 If all subtasks have status NEW — so common  epic has status NEW
 
 If some epic`s subtasks have status NEW, some have DONE, or IN_PROGRESS — common epic has status IN_PROGRESS
