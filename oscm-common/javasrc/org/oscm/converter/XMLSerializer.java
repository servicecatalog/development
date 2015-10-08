/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2015 
 *******************************************************************************/

package org.oscm.converter;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class XMLSerializer {
    private static class BigDecimalPersistenceDelegate extends
            DefaultPersistenceDelegate {
        @Override
        protected boolean mutatesTo(Object oldInstance, Object newInstance) {
            return oldInstance.equals(newInstance);
        }

        @Override
        protected Expression instantiate(Object oldInstance, Encoder out) {
            BigDecimal bd = (BigDecimal) oldInstance;
            return new Expression(bd, bd.getClass(), "new",
                    new Object[] { bd.toString() });
        }
    }

    private static class EnumPersistenceDelegate extends
            DefaultPersistenceDelegate {
        @Override
        protected boolean mutatesTo(Object oldInstance, Object newInstance) {
            return oldInstance == newInstance;
        }

        @Override
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Enum<?> e = (Enum<?>) oldInstance;
            return new Expression(e, e.getClass(), "valueOf",
                    new Object[] { e.name() });
        }
    }

    public static String toXml(Object source) {
        return toXml(source, null);
    }

    /**
     * Serializes an object to an XML string.
     * 
     * @param source
     *            the object to serialize.
     * @return the XML string representing the object.
     * @throws UnsupportedEncodingException
     */
    public static synchronized String toXml(Object source, Class<?>[] types) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            XMLEncoder encoder = new XMLEncoder(out);

            setPersistenceDelegates(encoder, types);

            // Handle private Collections.xyz classes
            Object valueToWrite = source;
            if (valueToWrite.getClass() == Collections.EMPTY_LIST.getClass()) {
                valueToWrite = new ArrayList<Object>();
            }
            if (valueToWrite.getClass() == Collections.singletonList(null)
                    .getClass()) {
                valueToWrite = new ArrayList<Object>((Collection<?>) source);
            }
            if (valueToWrite.getClass() == Arrays.asList(new Object[] {})
                    .getClass()) {
                valueToWrite = new ArrayList<Object>((Collection<?>) source);
            }

            encoder.writeObject(valueToWrite);
            encoder.close();

        } finally {
            close(out);
        }

        String result = null;
        try {
            result = new String(out.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static void setPersistenceDelegates(XMLEncoder encoder,
            Class<?>[] types) {
        if (types != null && types.length > 0) {
            PersistenceDelegate persistenceDelegate = new EnumPersistenceDelegate();
            for (int i = 0; i < types.length; i++) {
                encoder.setPersistenceDelegate(types[i], persistenceDelegate);
            }
        }

        // Handle "BiGDecimal" manually (has no default constructor)
        encoder.setPersistenceDelegate(BigDecimal.class,
                new BigDecimalPersistenceDelegate());
    }

    /**
     * Close the closeable if it is not null.
     * 
     * @param closeable
     *            the closeable to close.
     */
    static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            // ignore, stream already closed
        }
    }

    public static Object toObject(String xml) {
        Object result = null;
        XMLDecoder decoder = null;
        try {
            decoder = new XMLDecoder(new ByteArrayInputStream(xml.getBytes()));
            result = decoder.readObject();
        } catch (Exception e) {
        } finally {
            if (decoder != null) {
                decoder.close();
            }
        }
        return result;
    }

}
