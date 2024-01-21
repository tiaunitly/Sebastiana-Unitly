/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.siswacrud;

/**
 *
 * @author sebastiana
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import java.sql.*;


public class SiswaCRUD {

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/datasiswa";
    static final String USER = "root";
    static final String PASS = "";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;

        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create a statement
            stmt = conn.createStatement();

            // Create table if not exists
            createTable(stmt);

            // Menu CRUD
            Scanner scanner = new Scanner(System.in);
            int choice;
            do {
                System.out.println("\n1. Tambah Siswa");
                System.out.println("2. Tampilkan Data Siswa");
                System.out.println("3. Update Data Siswa");
                System.out.println("4. Hapus Siswa");
                System.out.println("5. Keluar");
                System.out.print("Pilih menu (1-5): ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        tambahSiswa(conn, scanner);
                        break;
                    case 2:
                        tampilkanDataSiswa(stmt);
                        break;
                    case 3:
                        updateDataSiswa(conn, scanner);
                        break;
                    case 4:
                        hapusSiswa(conn, scanner);
                        break;
                }
            } while (choice != 5);

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
                // nothing we can do
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    private static void createTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS siswa (" +
                "nisn INT PRIMARY KEY," +
                "nama VARCHAR(255)," +
                "alamat VARCHAR(255)," +
                "jenis_kelamin VARCHAR(10)" +
                ")";
        stmt.executeUpdate(sql);
    }

    private static void tambahSiswa(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("NISN: ");
        int nisn = scanner.nextInt();
        scanner.nextLine();  // consume the newline character
        System.out.print("Nama: ");
        String nama = scanner.nextLine();
        System.out.print("Alamat: ");
        String alamat = scanner.nextLine();
        System.out.print("Jenis Kelamin: ");
        String jenisKelamin = scanner.nextLine();

        String sql = "INSERT INTO siswa (nisn, nama, alamat, jenis_kelamin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nisn);
            pstmt.setString(2, nama);
            pstmt.setString(3, alamat);
            pstmt.setString(4, jenisKelamin);

            pstmt.executeUpdate();
            System.out.println("Data siswa berhasil ditambahkan.");
        }
    }

    private static void tampilkanDataSiswa(Statement stmt) throws SQLException {
        String sql = "SELECT * FROM siswa";
        ResultSet rs = stmt.executeQuery(sql);

        System.out.println("\nData Siswa:");
        while (rs.next()) {
            System.out.println("NISN: " + rs.getInt("nisn"));
            System.out.println("Nama: " + rs.getString("nama"));
            System.out.println("Alamat: " + rs.getString("alamat"));
            System.out.println("Jenis Kelamin: " + rs.getString("jenis_kelamin"));
            System.out.println("-------------------------");
        }
    }

    private static void updateDataSiswa(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Masukkan NISN siswa yang ingin diupdate: ");
        int nisnToUpdate = scanner.nextInt();
        scanner.nextLine();  // consume the newline character

        String sql = "SELECT * FROM siswa WHERE nisn = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nisnToUpdate);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.print("Nama baru: ");
                String newNama = scanner.nextLine();
                System.out.print("Alamat baru: ");
                String newAlamat = scanner.nextLine();
                System.out.print("Jenis Kelamin baru: ");
                String newJenisKelamin = scanner.nextLine();

                sql = "UPDATE siswa SET nama = ?, alamat = ?, jenis_kelamin = ? WHERE nisn = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(sql)) {
                    updateStmt.setString(1, newNama);
                    updateStmt.setString(2, newAlamat);
                    updateStmt.setString(3, newJenisKelamin);
                    updateStmt.setInt(4, nisnToUpdate);

                    updateStmt.executeUpdate();
                    System.out.println("Data siswa berhasil diupdate.");
                }
            } else {
                System.out.println("Data siswa dengan NISN " + nisnToUpdate + " tidak ditemukan.");
            }
        }
    }

    private static void hapusSiswa(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Masukkan NISN siswa yang ingin dihapus: ");
        int nisnToDelete = scanner.nextInt();

        String sql = "DELETE FROM siswa WHERE nisn = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nisnToDelete);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Data siswa berhasil dihapus.");
            } else {
                System.out.println("Data siswa dengan NISN " + nisnToDelete + " tidak ditemukan.");
            }
        }
    }
}
