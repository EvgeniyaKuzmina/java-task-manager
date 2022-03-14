package task;

import java.util.List;
import java.util.Locale;

// хранит типы статусов задач
public enum Status {

    NEW ("NEW"),
    IN_PROGRESS ("IN_PROGRESS"),
    DONE ("DONE");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public static Status getStatusForEpic(List<SubTask> subTasks) {
        boolean statusNew = false;
        boolean statusDone = false;
        boolean statusInProgress = false;
        if (subTasks.size() == 0) {
            return NEW;
        }
        for (SubTask subtask : subTasks) {
            if (subtask.getStatus().equals(NEW)) {
                statusNew = true;
            } else if (subtask.getStatus().equals(DONE)) {
                statusDone = true;
            } else if (subtask.getStatus().equals(IN_PROGRESS)) {
                statusInProgress = true;
            }
        }
        if (statusNew && (statusDone || statusInProgress)) {
            return IN_PROGRESS;
        } else if (!statusNew && !statusDone && statusInProgress) {
            return IN_PROGRESS;
        } else if (!statusNew && statusDone && !statusInProgress) {
            return DONE;
        } else {
            return NEW;
        }
    }
    public static Status toStatus(String taskStatus) {
        taskStatus = taskStatus.toUpperCase(Locale.ROOT);
        if (taskStatus.equals(Status.NEW.status)) {
            return Status.NEW;
        } else if (taskStatus.equals((Status.IN_PROGRESS.status))) {
            return Status.IN_PROGRESS;
        } else {
            return Status.DONE;
        }

    }
}
