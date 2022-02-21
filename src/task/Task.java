package task;

import java.util.Objects;

public class Task {

    protected final Long id;
    protected String name;
    protected String description;
    protected Status status;

    public Task(Long id, String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static Task fromString(String value) {
        String[] splitedLine = value.split(",");
        Long id = Long.parseLong(splitedLine[0]);
        String name = splitedLine[2];
        String description = splitedLine[4];
        String status = splitedLine[3];
        return new Task(id,name,description, Status.toStatus(status));

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null) return false;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s\n", id, TasksType.TASK, name, status, description);

    }
}