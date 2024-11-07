package com.example.challengeodontoprev.utils

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DatabaseManager {
    private const val URL = "jdbc:oracle:thin:@//oracle.fiap.com.br:1521/ORCL"
    private const val USERNAME = "RM553478"
    private const val PASSWORD = "230196"

    init {
        try {
            Class.forName("oracle.jdbc.OracleDriver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection(URL, USERNAME, PASSWORD)
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    fun closeConnection(connection: Connection?) {
        try {
            connection?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}