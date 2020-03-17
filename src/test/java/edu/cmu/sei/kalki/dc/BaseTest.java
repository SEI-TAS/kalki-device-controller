package edu.cmu.sei.kalki.dc;

import edu.cmu.sei.kalki.db.database.Postgres;
import edu.cmu.sei.kalki.db.utils.Config;
import edu.cmu.sei.kalki.db.utils.TestDB;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

public abstract class BaseTest {

    @BeforeEach
    public void setup() {
        try{
            Postgres.setLoggingLevel(Level.SEVERE);

            TestDB.recreateTestDB();
            TestDB.initialize();

            Config.load("config.json");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        insertData();
    }

    @AfterEach
    public void closeConnections() {
        Postgres.cleanup();
    }

    public abstract void insertData();

}
