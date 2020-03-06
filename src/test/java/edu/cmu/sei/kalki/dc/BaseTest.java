package edu.cmu.sei.kalki.dc;

import edu.cmu.sei.kalki.db.database.Postgres;
import edu.cmu.sei.kalki.db.utils.Config;
import edu.cmu.sei.kalki.db.utils.TestDB;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import java.sql.SQLException;
import java.util.logging.Level;

public abstract class BaseTest {

    @BeforeAll
    public static void getConfig() {
        try {
            Config.load("config.json");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @BeforeEach
    public void resetDB() {
        try{
            Postgres.setLoggingLevel(Level.SEVERE);

            TestDB.recreateTestDB();
            TestDB.initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        insertData();
        DeviceController.main(null);
    }

    @AfterEach
    public void closeConnections() {
        Postgres.cleanup();
    }

    protected int sendToApiEndpoint(JSONObject payload, String apiEndpoint){
        try {
            URL fullUrl = new URL(apiEndpoint);
            HttpURLConnection httpCon = (HttpURLConnection) fullUrl.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Accept", "application/json");

            try(OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream())){
                out.write(payload.toString());
            }

            httpCon.disconnect();

            return httpCon.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public abstract void insertData();

}
