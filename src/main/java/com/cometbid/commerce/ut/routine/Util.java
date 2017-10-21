/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.routine;

/**
 *
 * @author Gbenga
 */
import com.cometbid.commerce.ut.qualifiers.JavaUtilLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

class Util {

    private static Properties properties = null;

    public static String subscriptionsUrl = null;
    public static String regionsUrl = null;
    public static String statesProvincesUrl1 = null;
    public static String statesProvincesUrl2 = null;
    public static String statesProvincesUrl3 = null;
    public static String statesProvincesUrl4 = null;
    public static String currenciesUrl = null;
    public static String countriesUrl = null;
    public static String languagesUrl = null;
    public static String callingCodesUrl = null;
    public static String diallingCodesUrl = null;
    public static String phoneFormatsUrl = null;

    @Inject
    @JavaUtilLogger
    private Logger logger;

    public static Properties getProperties() {
        if (properties != null) {
            return properties;
        }

        InputStream inputStream = null;
        String CONFIG_FILENAME = "ConfigurationParameters.properties";
        try {
            properties = new Properties();
            inputStream = Util.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME);

            if (inputStream == null) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE,
                        "Sorry, unable to find property file: {0}", CONFIG_FILENAME);
            }
            //load a properties file from class path, inside static method
            properties.load(inputStream);

            Logger.getLogger(Util.class.getName()).log(Level.SEVERE,
                    "Loading property file content done successfully");

            return properties;
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE,
                    "Error occured while loading property file: {0}", CONFIG_FILENAME);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {

                    Logger.getLogger(Util.class.getName()).log(Level.SEVERE,
                            "Error occured closing connection to property file {0}", CONFIG_FILENAME);
                }
            }
        }
        return null;
    }

    public static void loadURLProperties() {

        properties = getProperties();

        if (properties != null) {
            subscriptionsUrl = properties.getProperty("SUBSCRIPTIONS_URL");
            regionsUrl = properties.getProperty("REGIONS_URL");

            statesProvincesUrl1 = MessageFormat.format(properties.getProperty("STATES_PROVINCES_URL"), 1, Integer.toString(1000));
            statesProvincesUrl2 = MessageFormat.format(properties.getProperty("STATES_PROVINCES_URL"), 2, Integer.toString(1000));
            statesProvincesUrl3 = MessageFormat.format(properties.getProperty("STATES_PROVINCES_URL"), 3, Integer.toString(1000));
            statesProvincesUrl4 = MessageFormat.format(properties.getProperty("STATES_PROVINCES_URL"), 4, Integer.toString(1000));
            currenciesUrl = properties.getProperty("CURRENCIES_URL");
            countriesUrl = properties.getProperty("COUNTRIES_URL");
            languagesUrl = properties.getProperty("LANGUAGES_URL");
            callingCodesUrl = properties.getProperty("CALLINGCODES_URL");
            diallingCodesUrl = properties.getProperty("DIALLINGCODES_URL");
            phoneFormatsUrl = properties.getProperty("PHONEFORMATS_URL");

            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, "Reading property file done successfully");
        }
    }

    public boolean isPropertyAltered() {

        boolean altered = false;
        properties = getProperties();

        if (properties != null) {
            altered = properties.getProperty("REST_URL_ALTERED").equalsIgnoreCase("TRUE");
        }
        return altered;
    }

    public static String getHttpResponse(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        StringBuilder outputBuilder = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            outputBuilder.append(output);
        }
        conn.disconnect();
        return outputBuilder.toString();
    }
}
