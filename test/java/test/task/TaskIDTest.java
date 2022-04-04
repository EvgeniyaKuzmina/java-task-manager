package test.task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.TaskID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskIDTest {

    static TaskID taskId;

    @BeforeAll
    static void beforeAll() {
        taskId = new TaskID();
    }

    // проверяем что получаем первый id 1
    @Test
    void shouldReturnId1() {
        assertEquals(1, taskId.getId());
    }

    // проверяем что получаем второй id 2
    @Test
    void shouldReturnId2() {
        assertEquals(2, taskId.getId());
    }

}