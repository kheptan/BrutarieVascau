package com.example.kp.brutarievascau;

import android.os.Environment;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kp on 07/03/16.
 */
public class CreateXml {
    /** Empty Constructor*/
    public CreateXml() {
    }

    static XmlPullParserFactory factory;
    static XmlSerializer serializer;

    OutputStream outputStream;
    File file;
    static String filename;

    public static CreateXml newInstance(String argFilename) throws IOException, XmlPullParserException {
        CreateXml newXML = new CreateXml();

        XmlPullParserFactory arg_factory = XmlPullParserFactory.newInstance(
                System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
        XmlSerializer arg_serializer = arg_factory.newSerializer();

        factory = arg_factory;
        serializer = arg_serializer;
        filename = argFilename;

        try {
            serializer = factory.newSerializer();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return newXML;
    }


    public  boolean openFile() throws NullPointerException {
        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            path.mkdirs();

            file = new File(path, filename);
            if (file != null) {
                try {
                    outputStream = new FileOutputStream(file);
                    serializer.setOutput(outputStream,"UTF-8");
                    serializer.flush();
                    outputStream.close();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
