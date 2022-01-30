package carsharing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Menu extends MenuComponent {
    private List<MenuComponent> options = new ArrayList<>();
    private String exitOption;

    private void printMenu() {
        IntStream.range(0, options.size())
                .forEach(index ->
                        System.out.println((index + 1) + ". " + options.get(index)));
        System.out.println("0. " + exitOption);
    }

    public Menu(String name) {
        super(name);
    }

    public void addOption(MenuComponent child) {
        options.add(child);
    }

    public void addExitOption(String name) {
        exitOption = name;
    }

    @Override
    public void execute() {
        do {
            printMenu();
            var sc = new Scanner(System.in);
            try {
                int input = sc.nextInt();
                if (input == 0) {
                    break;
                }

                if (input > options.size() || input < 1) {
                    System.out.println("Incorrect option");
                    continue;
                }

                options.get(input - 1).execute();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } while (true);
    }
}
