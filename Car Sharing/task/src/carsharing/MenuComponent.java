package carsharing;

public abstract class MenuComponent {
    private final String name;

    public abstract void execute();

    public MenuComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
