package org.example.lesson5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/java_academy",
                "postgres",
                "terrrr")) {
            connection.setAutoCommit(false);
            try {
                String orderNumber = "4F";
                createOrder(connection, orderNumber);

                long startTime = System.currentTimeMillis();
                createOrderDetail(connection, "картошка фри", 2, orderNumber);
                Scanner scanner = new Scanner(System.in);
                System.out.println("Подтвердите оплату!");
                String answer = scanner.nextLine();

                long secondForPay = (System.currentTimeMillis() - startTime) / 1000;
                if (secondForPay > 10) {
                    System.out.println("Очень долго подтверждали заказ. Произошла отмена");
                    connection.rollback();
                } else {
                    connection.createStatement()
                            .execute("drop table orders");
                    System.out.println("Успешно, заказ принят");
                    connection.commit();
                }
            } catch (Exception e) {
                System.out.println("Произошло исключение");
                System.out.println(e.getMessage());
                connection.rollback();
            }
        }
    }

    private static void createOrder(Connection connection, String orderNumber) throws SQLException {
        String sqlPattern = "insert into orders (order_no, is_paid) values (?, false)";
        try (PreparedStatement statement = connection.prepareStatement(sqlPattern)) {
            statement.setString(1, orderNumber);
            int countChangedRows = statement.executeUpdate();
        }
    }

    private static void createOrderDetail(Connection connection,
                                          String productName,
                                          int countProduct,
                                          String orderNumber) throws SQLException {
        String sqlPattern =
                """
                        insert into order_details (name, qty, order_id) values (
                        	?,
                        	?,
                        	(select id from orders where order_no = ? order by id desc limit 1)
                        )
                        """;
        try (PreparedStatement statement = connection.prepareStatement(sqlPattern)) {
            statement.setString(1, productName);
            statement.setInt(2, countProduct);
            statement.setString(3, orderNumber);
            int countChangedRows = statement.executeUpdate();
        }
    }
}
