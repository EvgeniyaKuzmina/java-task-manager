package Task;

import java.util.List;

// хранит типы статусов задач и получает статус для Эпика
public enum Status {
    NEW,
    IN_PROGRESS,
    DONE;

    public static Status getStatusForEpic(List<SubTask> subTasks) {
        boolean statusNew = false;
        boolean statusDone = false;
        boolean statusInProgress= false;
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
        } else if (!statusNew && statusDone && !statusInProgress) {
            return DONE;
        }else {
            return NEW;
        }
    }
}

