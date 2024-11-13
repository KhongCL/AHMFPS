public class TestManagerMenu {
    public static void main(String[] args) {
        User manager = new Manager("admin", "admin123");
        new ManagerMenu(manager).setVisible(true);
    }
}