package exceptions;

//всё равно так и не поняла смысл этого исключения((
//Если мы не используем throws в самом методе save, потому что throws, как я поняла, используется для проверяемых исключений в основном
// по ТЗ метод save должен кидать собственное непроверяемое исключение ManagerSaveException, если он кидает исключение,
// то конструкция try-catch кажется нужна, чтобы обработать это выбрасываемое исключение.
public class ManagerSaveException extends Error {
    String message;

    public ManagerSaveException (String message) {
        this.message = message;
    }

}
