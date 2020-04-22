package edu.cmu.sei.kalki.dc.utils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sebastianecheverria on 8/2/17.
 */
public class Config
{
    public static Map<String, String> data = new HashMap<>();

    public static void load(String configFilePath) throws IOException
    {
        System.out.println(System.getProperty("user.dir"));
        InputStream fs = new FileInputStream(configFilePath);
        JSONTokener parser = new JSONTokener(fs);
        JSONObject config = new JSONObject(parser);

        Iterator<String> configData = config.keys();
        while(configData.hasNext())
        {
            String paramName = configData.next();
            String paramValue = config.getString(paramName);
            data.put(paramName, paramValue);
        }

        fs.close();
    }
}
