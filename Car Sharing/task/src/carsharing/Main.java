package carsharing;

import carsharing.dao.*;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {

    private static final CompanyDAO companyDAO = new CompanyDAOImpl();
    private static final CarDAO carDAO = new CarDAOImpl();
    private static final CustomerDAO customerDAO = new CustomerDAOImpl();

    public static void main(String[] args) throws SQLException, FileNotFoundException {
        String dbName = null;
        if (args.length > 2 && "-databaseFileName".equals(args[1])) {
            dbName = args[2];
        } else {
            dbName = "carsharing";
        }

        File dbFile = new File("./src/carsharing/db");
        boolean created = dbFile.mkdir();
        if (!created) {
            System.out.println("Directory already exists.");
        }
        var dbInstance = DbManager.getInstance(dbName);
        dbInstance.initDb();

        Menu mainMenu = new Menu("");
        Menu managerMenu = new Menu("Log in as a manager");

        mainMenu.addOption(managerMenu);
        mainMenu.addOption(new MenuComponent("Log in as a customer") {
            @Override
            public void execute() {
                printCustomers();
            }
        });
        mainMenu.addOption(new MenuComponent("Create a customer") {
            @Override
            public void execute() {
                createCustomer();
            }
        });
        mainMenu.addExitOption("Exit");

        managerMenu.addOption(new MenuComponent("Company list") {
            @Override
            public void execute() {
                printCompanies(null);
            }
        });
        managerMenu.addOption(new MenuComponent("Create a company") {
            @Override
            public void execute() {
                createCompany();
            }
        });
        managerMenu.addExitOption("Back");

        mainMenu.execute();
    }

    public static void createCustomer() {
        var scanner = new Scanner(System.in);

        System.out.println("Enter the customer name: ");
        String customer = scanner.nextLine();

        customerDAO.insertCustomer(new Customer(0, customer, null));
    }

    public static void printCustomers() {
        var customers = customerDAO.findCustomersSorted();

        if (customers.isEmpty()) {
            System.out.println("The customer list is empty!");
        } else {
            System.out.println("Choose a customer: ");
            IntStream.range(0, customers.size())
                    .forEach(index ->
                            System.out.printf("%d. %s\n", index + 1, customers.get(index)));

            System.out.println("0. Back");

            Scanner scanner = new Scanner(System.in);
            try {
                int id = scanner.nextInt();

                if (id == 0) {
                    return;
                }

                if (id < 1 || id > customers.size()) {
                    System.out.println("Incorrect input!");
                } else {
                    customerMenu(customers.get(id - 1));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public static void customerMenu(Customer customer) {
        Menu customerMenu = new Menu("");

        customerMenu.addExitOption("Back");

        customerMenu.addOption(new MenuComponent("Rent a car") {
            @Override
            public void execute() {
                rentCar(customer);
            }
        });

        customerMenu.addOption(new MenuComponent("Return a rented car") {
            @Override
            public void execute() {
                if (!customerDAO.returnCar(customer)) {
                    System.out.println("You didn't rent a car!");
                } else {
                    System.out.println("You've returned a rented car!");
                }
            }
        });

        customerMenu.addOption(new MenuComponent("My rented car") {
            @Override
            public void execute() {
                var res = customerDAO.getRentedCar(customer);
                if (res == null) {
                    System.out.println("You didn't rent a car!");
                } else {
                    System.out.printf("Your rented car:\n" +
                            "%s\nCompany:\n%s\n", res[0], res[1]);
                }
            }
        });

        customerMenu.execute();
    }

    public static void rentCar(Customer customer) {
        if (customer.getRented_car_id() != null) {
            System.out.println("You've already rented a car!");
        } else {
            printCompanies(customer);
        }
    }

    public static void printCompanies(Customer customer) {
        var companies = companyDAO.findAllSorted();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!");
        } else {
            System.out.println("Choose a company:");
            IntStream.range(0, companies.size())
                    .forEach(index ->
                            System.out.println((index + 1) + ". " + companies.get(index)));

            System.out.println("0. Back");

            Scanner scanner = new Scanner(System.in);
            try {
                int id = scanner.nextInt();

                if (id == 0) {
                    return;
                }

                if (id < 1 || id > companies.size()) {
                    System.out.println("Incorrect input!");
                } else if (customer == null) {
                    companyCarMenu(companies.get(id - 1));
                } else {
                    chooseCar(customer, companies.get(id - 1));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public static void companyCarMenu(Company company) {
        Menu carMenu = new Menu("");

        carMenu.addExitOption("Back");
        carMenu.addOption(new MenuComponent("Car list") {
            @Override
            public void execute() {
                printCars(company);
            }
        });

        carMenu.addOption(new MenuComponent("Create a car") {
            @Override
            public void execute() {
                createCar(company);
            }
        });

        carMenu.execute();
    }

    public static void chooseCar(Customer customer, Company company) {
        var cars = carDAO.findAllSorted(company);

        if (cars.isEmpty()) {
            System.out.printf("No available cars in the %s company\n", company);
        } else {
            System.out.println("Choose a car:");
            IntStream.range(0, cars.size())
                    .forEach(index ->
                            System.out.printf("%d. %s\n", index + 1, cars.get(index)));

            System.out.println("0. Back");

            Scanner scanner = new Scanner(System.in);
            try {
                int id = scanner.nextInt();

                if (id == 0) {
                    return;
                }

                if (id < 1 || id > cars.size()) {
                    System.out.println("Incorrect input!");
                } else {
                    customerDAO.rentCar(customer, cars.get(id - 1));
                    System.out.printf("You rented '%s'\n", cars.get(id - 1));
                }
            } catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printCars(Company company) {
        var cars = carDAO.findAllSorted(company);

        if (cars.isEmpty()) {
            System.out.println("The car list is empty!");
        } else {
            System.out.printf("%s cars:\n", company.getName());
            IntStream.range(0, cars.size())
                    .forEach(index ->
                            System.out.printf("%d. %s\n", index + 1, cars.get(index)));
        }
    }

    public static void createCar(Company company) {
        var scanner = new Scanner(System.in);

        System.out.println("Enter the car name: ");
        String carName = scanner.nextLine();

        carDAO.insertCar(new Car(0, carName, company.getId()));
    }

    public static void createCompany() {
        var scanner = new Scanner(System.in);

        System.out.println("Enter the company name: ");
        String companyName = scanner.nextLine();

        companyDAO.insertCompany(new Company(0, companyName));
    }
}