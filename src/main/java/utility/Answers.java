package utility;

public enum Answers {
    WRONG_ID("Ошибка в ID"),
    NO_EPIC_WITH_ID("Эпика с таким id нет"),
    NO_SUBTASK_WITH_ID("Подзадачи с таким Id нет"),
    NO_TASKS_WITH_ID("Задач с таким id нет"),
    TASKS_ALREADY_EXIST("Такая задача уже есть"),
    CHANGE_DATA_START_OR_DURATION("Нужно изменить дату старта или продолжительность выполнения"),
    REMOVE_TASK("Задача удалена"),
    REMOVE_EPIC("Эпик удалён"),
    REMOVE_SUBTASK("Подзадача удалена");


    private final String answer;

    Answers(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }
}
