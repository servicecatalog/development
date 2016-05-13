/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * Provider class for message body writer and reader with gson.
 * 
 * @author miethaner
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonMessageProvider implements MessageBodyWriter<Representation>,
        MessageBodyReader<Representation> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Representation readFrom(Class<Representation> type,
            Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {

        InputStreamReader reader = new InputStreamReader(entityStream,
                StandardCharsets.UTF_8);

        try {
            Gson gson = new Gson();
            return gson.fromJson(reader, genericType);

        } catch (JsonSyntaxException e) {
            throw WebException.badRequest().build(); // TODO add more info

        } finally {
            reader.close();
        }
    }

    @Override
    public long getSize(Representation rep, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public void writeTo(Representation rep, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {

        OutputStreamWriter writer = new OutputStreamWriter(entityStream,
                StandardCharsets.UTF_8);

        Logger logger = Logger.getLogger(getClass().getName());
        logger.info(type.getName() + ", " + genericType.toString());

        try {
            Gson gson = new GsonBuilder().setVersion(rep.getVersion()).create();
            gson.toJson(rep, genericType, writer);

        } catch (JsonSyntaxException e) {
            throw WebException.internalServerError().build(); // TODO add more
                                                              // info
        } finally {
            writer.close();
        }

    }

}
