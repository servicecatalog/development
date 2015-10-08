parser grammar FWPolicyParser;

options {
    tokenVocab=FWPolicyLexer;
}

@header {package org.oscm.app.iaas.fwpolicy; 

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.oscm.app.iaas.data.FWPolicy;
import org.oscm.app.iaas.data.FWPolicy.Protocol;
}


policies returns [List<FWPolicy> pList] 
        @init {
            $pList = new ArrayList<FWPolicy>();
        }
        @after{
            for (Iterator<FWPolicy> it = $pList.iterator(); it.hasNext(); ) {
                FWPolicy p = it.next();
                if (p.getDstPort() != null && p.getDstPort().indexOf(",") > 0) {
                    String portList = p.getDstPort();
                    StringTokenizer tk = new StringTokenizer(portList, ",");
                    p.setDstPort((String) tk.nextElement());
                    while (tk.hasMoreElements()) {
                        String port = (String) tk.nextElement();
                        FWPolicy pol = p.clone();
                        pol.setDstPort(port);
                        $pList.add(pol);
                        it = $pList.iterator(); // due to concurrent modification exception
                    }
                }

                if (p.getSrcPort().indexOf(",") > 0) {
                    String portList = p.getSrcPort();
                    StringTokenizer tk = new StringTokenizer(portList, ",");
                    p.setSrcPort((String) tk.nextElement());
                    while (tk.hasMoreElements()) {
                        String port = (String) tk.nextElement();
                        FWPolicy pol = p.clone();
                        pol.setSrcPort(port);
                        $pList.add(pol);
                        it = $pList.iterator(); // due to concurrent modification exception
                    }
                }
            }
        }
        :
            policy[$pList] (SEMICOLON policy[$pList])* EOF
        ;

policy [List<FWPolicy> pList] locals[ FWPolicy p ]
        @init {
            $p = new FWPolicy();
            $p.setAction(Action.Accept);
            $p.setSrc(null);
            $p.setSrcPort("any");
            $p.setDst(null);
            $p.setDstPort("any");
            $p.setProtocol(Protocol.TCP);
            $p.setDstService("NONE");
            $p.setDstType("IP");
        }
        @after{
            $pList.add($p);
        }
        :   
         source_zone[$p] (source_ip[$p])? (source_port[$p])? ARROW dest_zone[$p] (dest_service[$p] | (dest_ip[$p])? (dest_port[$p])? (protocol[$p])? )
        ;

source_zone [FWPolicy p]:
	(ZONE)
	{  
            $p.setSrcZone($ZONE.getText());  
        }
	;

source_ip [FWPolicy p]:
	(IP)
        {   
            // remove the double quotes
            String ip = $IP.getText();
            if (ip.indexOf("\"") == 0) {
                ip = ip.substring(1, ip.length() - 1);
            }
            $p.setSrc(ip);
        }
	;

source_port [FWPolicy p]:
	(PORT)
        {
            // remove the leading colon
            String port = $PORT.getText().substring(1);
            $p.setSrcPort(port); 
        }
	;

dest_zone [FWPolicy p]:
	(ZONE)
	{
            $p.setDstZone($ZONE.getText());  
        }
	;

dest_service [FWPolicy p]:
	(SERVICE)
        {
            // remove the left and right parenthesis
            String service = $SERVICE.getText();
            service = service.substring(1, service.length() - 1);
            $p.setDstService(service);
            $p.setDstType(null);
            $p.setDstPort(null);
            $p.setProtocol(null);
        }
	;

dest_ip [FWPolicy p]:
	(IP)
        {
            // remove the double quotes
            String ip = $IP.getText();
            if (ip.indexOf("\"") == 0) {
                ip = ip.substring(1, ip.length() - 1);
            }
            $p.setDst(ip);
        }
	;

dest_port [FWPolicy p]:
	(PORT)
	{  
            // remove the leading colon
            String port = $PORT.getText().substring(1);
            $p.setDstPort(port); 
        }
	;

protocol [FWPolicy p]:
	(PROTOCOL)
	{  
            // remove the leading hashmark
            String ptcl = $PROTOCOL.getText().substring(1);
            if( "tcp".equals(ptcl) ) $p.setProtocol(Protocol.TCP); 
            else if( "udp".equals(ptcl) ) $p.setProtocol(Protocol.UDP); 
            else if( "tcpudp".equals(ptcl) ) $p.setProtocol(Protocol.TCP_UDP); 
            else if( "icmp".equals(ptcl) ) $p.setProtocol(Protocol.ICMP); 
        }
	;
