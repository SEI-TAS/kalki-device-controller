package edu.cmu.sei.kalki.dc;

import edu.cmu.sei.kalki.db.database.Postgres;
import edu.cmu.sei.kalki.db.utils.Config;
import edu.cmu.sei.kalki.db.utils.TestDB;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

public abstract class BaseTest {
//
//    @BeforeAll
//    public static void getConfig() {
//        try {
//            Config.load("config.json");
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.exit(-1);
//        }
//    }

    @BeforeEach
    public void resetDB() {
        try{
            Postgres.setLoggingLevel(Level.SEVERE);

            TestDB.recreateTestDB();
            TestDB.initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        DeviceController.main(null);
        insertData();
    }

    @AfterEach
    public void closeConnections() {
        Postgres.cleanup();
    }

    public abstract void insertData();

}
