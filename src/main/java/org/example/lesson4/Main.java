package org.example.lesson4;

import org.example.lesson4.entity.Product;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        try(Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/business",
                "postgres",
                "terrrr")) {
            //Создание запроса и печать его
            //printAllProducts(connection);

            //Получить данные из бд и преобразовать в объекты
            //Set<Product> allProducts = getAllProducts(connection);
            //System.out.println(allProducts);

            //Получение продуктов по имени
            Set<Product> productsByName = getProductsByName(connection,
                    "хлеб'; drop table shop.test; select'");
            System.out.println(productsByName);
        }
    }

    public static void printAllProducts(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from shop.product");
            printResultFromQuery(resultSet);
        }
    }

    public static int getColumnCount(ResultSet resultSet) throws SQLException {
       return resultSet.getMetaData().getColumnCount() + 1;
    }

    public static void printResultFromQuery(ResultSet resultSet) throws SQLException {
        //Сами данные
        while (resultSet.next()) {
            for (int i = 1; i < (getColumnCount(resultSet)); i++) {
                System.out.printf("%s - %s\n",
                        resultSet.getMetaData().getColumnName(i),
                        resultSet.getObject(i)
                );
            }
            System.out.println("________________________");
        }
    }

    public static Set<Product> getAllProducts(Connection connection) throws SQLException {
        Set<Product> products = new HashSet<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from shop.product");
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setPrice(resultSet.getBigDecimal("price"));
                product.setCompanyId(resultSet.getInt("company_id"));
                products.add(product);
            }
        }
        return products;
    }

    public static Set<Product> getProductsByName(Connection connection, String productName)
            throws SQLException {
        String sql = "select * from shop.product where name = ?";
        System.out.println(sql);
        Set<Product> products = new HashSet<>();

        try (PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
            prepareStatement.setObject(1, productName);
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setPrice(resultSet.getBigDecimal("price"));
                product.setCompanyId(resultSet.getInt("company_id"));
                products.add(product);
            }
        }
        return products;
    }
}