package org.oscm.example.common;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.oscm.xsd.ServiceParameter;

public class ServiceParameterDAO {

    private final static String SUFFIX = ".properties";

    public void store(File dir, ServiceParameter[] parameters)
            throws IOException {

        if (dir == null || parameters == null) {
            return;
        }

        Properties prop = new Properties();
        for (ServiceParameter sp : parameters) {
            if (sp.getValue() != null) {
                prop.put(sp.getParameterId(), sp.getValue());
            }
        }

        FileOutputStream out = null;
        try {
            String name = dir.getCanonicalPath() + SUFFIX;
            out = new FileOutputStream(name);
            prop.store(out, "");
        } finally {
            closeStream(out);
        }
    }

    private void closeStream(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException exception) {
            // ignore, wanted to close stream anyway
        }
    }

    public Properties load(File dir) throws IOException {
        Properties prop = new Properties();
        if (dir != null) {
            String name = dir.getCanonicalPath() + SUFFIX;
            FileInputStream in = null;
            try {
                in = new FileInputStream(name);
                prop.load(in);
            } finally {
                closeStream(in);
            }
        }
        return prop;
    }

    public void delete(File dir) throws IOException {
        if (dir == null) {
            return;
        }
        String name = dir.getCanonicalPath() + SUFFIX;
        File file = new File(name);
        file.delete();
    }

}
