/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 14.10.2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.webservices.handler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class LoginHandlerTest {
    
    private LoginHandler loginHandler;
    private SOAPMessageContext context;
    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;
    
    @Before
    public void setup() throws Exception{
        
        loginHandler = spy(new LoginHandler());
        context = mock(SOAPMessageContext.class);
        
        // DB mocked
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        
        doReturn(dataSource).when(loginHandler).getDataSource();
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
    }
    
    @Test
    public void testOutbundTypeOk(){
        
        //given
        when(context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(true);

        //when
        boolean result = loginHandler.handleMessage(context);
        
        //then
        assertTrue(result);        
    }
    
    @Test
    public void testLoginWithUserInTheContextOnly() throws Exception{
        
        when(context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);

        doReturn("user12345").when(loginHandler).getUserIdFromContext(context);
        doReturn(null).when(loginHandler).getTenantIdFromContext(context);
        doReturn(null).when(loginHandler).getOrganizationIdFromContext(context);

        //when
        loginHandler.handleMessage(context);
        
        //then
        AbstractKeyQuery query = loginHandler.getKeyQuery();
        String queryStatement = query.getStatement();
        
        assertTrue(query instanceof UserKeyQuery);  
        assertTrue(queryStatement.contains("userid"));
        assertFalse(queryStatement.contains("organizationid"));
        assertFalse(queryStatement.contains("tenantid"));
    }
    
    @Test
    public void testLoginWithUserAndTenantInTheContextOnly() throws Exception{
        
        when(context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);

        doReturn("user12345").when(loginHandler).getUserIdFromContext(context);
        doReturn("sampleTenant").when(loginHandler).getTenantIdFromContext(context);
        doReturn(null).when(loginHandler).getOrganizationIdFromContext(context);

        //when
        loginHandler.handleMessage(context);
        
        //then
        AbstractKeyQuery query = loginHandler.getKeyQuery();
        String queryStatement = query.getStatement();
        
        assertTrue(query instanceof UserKeyForTenantQuery);  
        assertTrue(queryStatement.contains("userid"));
        assertFalse(queryStatement.contains("organizationid"));
        assertTrue(queryStatement.contains("tenantid"));
    }
    
    @Test
    public void testLoginWithUserAndOrgInTheContextOnly() throws Exception{
        
        when(context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);

        doReturn("user12345").when(loginHandler).getUserIdFromContext(context);
        doReturn(null).when(loginHandler).getTenantIdFromContext(context);
        doReturn("org123").when(loginHandler).getOrganizationIdFromContext(context);

        //when
        loginHandler.handleMessage(context);
        
        //then
        AbstractKeyQuery query = loginHandler.getKeyQuery();
        String queryStatement = query.getStatement();
        
        assertTrue(query instanceof UserKeyForOrganizationQuery);  
        assertTrue(queryStatement.contains("userid"));
        assertTrue(queryStatement.contains("organizationid"));
        assertFalse(queryStatement.contains("tenantid"));
    }
    
    @Test
    public void testLoginWithEmptyContextParameters() throws Exception{
        
        when(context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);

        doReturn(null).when(loginHandler).getUserIdFromContext(context);
        doReturn(null).when(loginHandler).getTenantIdFromContext(context);
        doReturn(null).when(loginHandler).getOrganizationIdFromContext(context);
        
        //when
        boolean result = loginHandler.handleMessage(context);
        
        //then
        assertFalse(result);
        
        AbstractKeyQuery query = loginHandler.getKeyQuery();
        assertEquals(0, query.getKey());
    }
    
    @Test
    public void testLoginOk() throws Exception{
        
        String userKey = "1000";
        when(context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);  
        doReturn(userKey).when(loginHandler).getUserKeyFromContext(context);
        doNothing().when(loginHandler).login(userKey);
        doNothing().when(loginHandler).addPrincipal(context, userKey);
        
        //when
        boolean result = loginHandler.handleMessage(context);
        
        //then
        assertTrue(result);
    }
    
}
