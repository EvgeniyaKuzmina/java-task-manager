package task;

public enum TasksType {
    TASK ("TASK"),
    EPIC ("EPIC"),
    SUBTASK ("SUBTASK");

    private final String taskType;

    TasksType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskType() {
        return taskType;
    }
}
