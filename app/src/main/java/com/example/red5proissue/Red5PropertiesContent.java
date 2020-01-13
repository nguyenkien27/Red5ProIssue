package com.example.red5proissue;


import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by azupko on 2/4/16.
 */
public class Red5PropertiesContent {

    public static List<Red5PropertiesItem> ITEMS;// = new ArrayList<Red5PropertiesItem>();

    public static final String TAG = Red5PropertiesContent.class.getSimpleName();

    public static Element properties;

    public static Element localProperties;

    public static HashMap<String, String> setProperties;

    public static void LoadTests(InputStream stream) {

        ITEMS = new ArrayList<>();
        SetTestItem(-1);

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);

            doc.getDocumentElement().normalize();

            Log.d(TAG, "GOT THE DOC: " + doc.getDocumentElement().getNodeName());
            NodeList props = doc.getDocumentElement().getElementsByTagName("Properties");

            properties = (Element) props.item(0);

            //Populate ITEMS with all the Tests found in this XML
            NodeList tests = doc.getDocumentElement().getElementsByTagName("Test");
            for (int i = 0; i < tests.getLength(); i++) {
                Element testElement = (Element) tests.item(i);
                Red5PropertiesItem test = new Red5PropertiesItem("" + i, testElement);//.getAttribute("title") , testElement.toString() );
                ITEMS.add(test);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static boolean GetPropertyBool(String id) {

        String prop = GetPropertyString(id);
        if (prop == null)
            return false;

        return prop.equals("true");
    }

    //Get an Int PROPERTY from the tests.xml - localProperty has preference
    public static int GetPropertyInt(String id) {

        String prop = GetPropertyString(id);
        if (prop == null)
            return -1;

        return Integer.parseInt(prop);

    }

    public static float GetPropertyFloat(String id) {
        String prop = GetPropertyString(id);
        if (prop == null)
            return -1f;

        return Float.parseFloat(prop);
    }

    //Get an String PROPERTY from the tests.xml - localProperty has preference
    public static String GetPropertyString(String id) {

        if (setProperties != null && setProperties.containsKey(id)) {
            return setProperties.get(id);
        }

        if (localProperties != null) {
            NodeList nodes = localProperties.getElementsByTagName(id);
            if (nodes.getLength() > 0) {
                return nodes.item(0).getTextContent();
            }
        }

        NodeList nodes = properties.getElementsByTagName(id);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }

        return null;
    }

    public static String getFormattedPortSetting(String port) {
        if (port.equals("80") || port.equals("443") || port.length() == 0) {
            return "";
        }
        return ":" + port;
    }

    public static void SetPropertyString(String id, String value) {

        if (setProperties == null) {
            setProperties = new HashMap<String, String>();
        }

        setProperties.put(id, value);
    }

    public static void SetTestItem(int id) {
        if (id < 0) {
            localProperties = null;
            return;
        }

        localProperties = ITEMS.get(id).getLocalProperties();
    }
}
