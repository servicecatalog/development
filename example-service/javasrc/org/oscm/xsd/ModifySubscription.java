
/**
 * ModifySubscription.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.1  Built on : Oct 19, 2009 (10:59:34 EDT)
 */
            
                package org.oscm.xsd;
            

            /**
            *  ModifySubscription bean class
            */
        
        public  class ModifySubscription
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = modifySubscription
                Namespace URI = http://oscm.org/xsd
                Namespace Prefix = ns1
                */
            

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if(namespace.equals("http://oscm.org/xsd")){
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        

                        /**
                        * field for InstanceId
                        */

                        
                                    protected java.lang.String localInstanceId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localInstanceIdTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getInstanceId(){
                               return localInstanceId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param InstanceId
                               */
                               public void setInstanceId(java.lang.String param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localInstanceIdTracker = true;
                                       } else {
                                          localInstanceIdTracker = false;
                                              
                                       }
                                   
                                            this.localInstanceId=param;
                                    

                               }
                            

                        /**
                        * field for SubscriptionId
                        */

                        
                                    protected java.lang.String localSubscriptionId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSubscriptionIdTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getSubscriptionId(){
                               return localSubscriptionId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SubscriptionId
                               */
                               public void setSubscriptionId(java.lang.String param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localSubscriptionIdTracker = true;
                                       } else {
                                          localSubscriptionIdTracker = false;
                                              
                                       }
                                   
                                            this.localSubscriptionId=param;
                                    

                               }
                            

                        /**
                        * field for ReferenceId
                        */

                        
                                    protected java.lang.String localReferenceId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localReferenceIdTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getReferenceId(){
                               return localReferenceId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ReferenceId
                               */
                               public void setReferenceId(java.lang.String param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localReferenceIdTracker = true;
                                       } else {
                                          localReferenceIdTracker = false;
                                              
                                       }
                                   
                                            this.localReferenceId=param;
                                    

                               }
                            

                        /**
                        * field for ParameterValues
                        * This was an Array!
                        */

                        
                                    protected org.oscm.xsd.ServiceParameter[] localParameterValues ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localParameterValuesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.oscm.xsd.ServiceParameter[]
                           */
                           public  org.oscm.xsd.ServiceParameter[] getParameterValues(){
                               return localParameterValues;
                           }

                           
                        


                               
                              /**
                               * validate the array for ParameterValues
                               */
                              protected void validateParameterValues(org.oscm.xsd.ServiceParameter[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param ParameterValues
                              */
                              public void setParameterValues(org.oscm.xsd.ServiceParameter[] param){
                              
                                   validateParameterValues(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localParameterValuesTracker = true;
                                          } else {
                                             localParameterValuesTracker = false;
                                                 
                                          }
                                      
                                      this.localParameterValues=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param org.oscm.xsd.ServiceParameter
                             */
                             public void addParameterValues(org.oscm.xsd.ServiceParameter param){
                                   if (localParameterValues == null){
                                   localParameterValues = new org.oscm.xsd.ServiceParameter[]{};
                                   }

                            
                                 //update the setting tracker
                                localParameterValuesTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localParameterValues);
                               list.add(param);
                               this.localParameterValues =
                             (org.oscm.xsd.ServiceParameter[])list.toArray(
                            new org.oscm.xsd.ServiceParameter[list.size()]);

                             }
                             

                        /**
                        * field for AttributeValues
                        * This was an Array!
                        */

                        
                                    protected org.oscm.xsd.ServiceAttribute[] localAttributeValues ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAttributeValuesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.oscm.xsd.ServiceAttribute[]
                           */
                           public  org.oscm.xsd.ServiceAttribute[] getAttributeValues(){
                               return localAttributeValues;
                           }

                           
                        


                               
                              /**
                               * validate the array for AttributeValues
                               */
                              protected void validateAttributeValues(org.oscm.xsd.ServiceAttribute[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param AttributeValues
                              */
                              public void setAttributeValues(org.oscm.xsd.ServiceAttribute[] param){
                              
                                   validateAttributeValues(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localAttributeValuesTracker = true;
                                          } else {
                                             localAttributeValuesTracker = false;
                                                 
                                          }
                                      
                                      this.localAttributeValues=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param org.oscm.xsd.ServiceAttribute
                             */
                             public void addAttributeValues(org.oscm.xsd.ServiceAttribute param){
                                   if (localAttributeValues == null){
                                   localAttributeValues = new org.oscm.xsd.ServiceAttribute[]{};
                                   }

                            
                                 //update the setting tracker
                                localAttributeValuesTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localAttributeValues);
                               list.add(param);
                               this.localAttributeValues =
                             (org.oscm.xsd.ServiceAttribute[])list.toArray(
                            new org.oscm.xsd.ServiceAttribute[list.size()]);

                             }
                             

                        /**
                        * field for RequestingUser
                        */

                        
                                    protected org.oscm.xsd.User localRequestingUser ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localRequestingUserTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.oscm.xsd.User
                           */
                           public  org.oscm.xsd.User getRequestingUser(){
                               return localRequestingUser;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param RequestingUser
                               */
                               public void setRequestingUser(org.oscm.xsd.User param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localRequestingUserTracker = true;
                                       } else {
                                          localRequestingUserTracker = false;
                                              
                                       }
                                   
                                            this.localRequestingUser=param;
                                    

                               }
                            

     /**
     * isReaderMTOMAware
     * @return true if the reader supports MTOM
     */
   public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
        boolean isReaderMTOMAware = false;
        
        try{
          isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
        }catch(java.lang.IllegalArgumentException e){
          isReaderMTOMAware = false;
        }
        return isReaderMTOMAware;
   }
     
     
        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
       public org.apache.axiom.om.OMElement getOMElement (
               final javax.xml.namespace.QName parentQName,
               final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException{


        
               org.apache.axiom.om.OMDataSource dataSource =
                       new org.apache.axis2.databinding.ADBDataSource(this,parentQName){

                 public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
                       ModifySubscription.this.serialize(parentQName,factory,xmlWriter);
                 }
               };
               return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
               parentQName,factory,dataSource);
            
       }

         public void serialize(final javax.xml.namespace.QName parentQName,
                                       final org.apache.axiom.om.OMFactory factory,
                                       org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                           serialize(parentQName,factory,xmlWriter,false);
         }

         public void serialize(final javax.xml.namespace.QName parentQName,
                               final org.apache.axiom.om.OMFactory factory,
                               org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter,
                               boolean serializeType)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
            
                


                java.lang.String prefix = null;
                java.lang.String namespace = null;
                

                    prefix = parentQName.getPrefix();
                    namespace = parentQName.getNamespaceURI();

                    if ((namespace != null) && (namespace.trim().length() > 0)) {
                        java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                        if (writerPrefix != null) {
                            xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                        } else {
                            if (prefix == null) {
                                prefix = generatePrefix(namespace);
                            }

                            xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);
                        }
                    } else {
                        xmlWriter.writeStartElement(parentQName.getLocalPart());
                    }
                
                  if (serializeType){
               

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"http://oscm.org/xsd");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":modifySubscription",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "modifySubscription",
                           xmlWriter);
                   }

               
                   }
                if (localInstanceIdTracker){
                                    namespace = "";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"instanceId", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"instanceId");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("instanceId");
                                    }
                                

                                          if (localInstanceId==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("instanceId cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localInstanceId);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSubscriptionIdTracker){
                                    namespace = "";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"subscriptionId", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"subscriptionId");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("subscriptionId");
                                    }
                                

                                          if (localSubscriptionId==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("subscriptionId cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localSubscriptionId);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localReferenceIdTracker){
                                    namespace = "";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"referenceId", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"referenceId");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("referenceId");
                                    }
                                

                                          if (localReferenceId==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("referenceId cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localReferenceId);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localParameterValuesTracker){
                                       if (localParameterValues!=null){
                                            for (int i = 0;i < localParameterValues.length;i++){
                                                if (localParameterValues[i] != null){
                                                 localParameterValues[i].serialize(new javax.xml.namespace.QName("","parameterValues"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("parameterValues cannot be null!!");
                                        
                                    }
                                 } if (localAttributeValuesTracker){
                                       if (localAttributeValues!=null){
                                            for (int i = 0;i < localAttributeValues.length;i++){
                                                if (localAttributeValues[i] != null){
                                                 localAttributeValues[i].serialize(new javax.xml.namespace.QName("","attributeValues"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("attributeValues cannot be null!!");
                                        
                                    }
                                 } if (localRequestingUserTracker){
                                            if (localRequestingUser==null){
                                                 throw new org.apache.axis2.databinding.ADBException("requestingUser cannot be null!!");
                                            }
                                           localRequestingUser.serialize(new javax.xml.namespace.QName("","requestingUser"),
                                               factory,xmlWriter);
                                        }
                    xmlWriter.writeEndElement();
               

        }

         /**
          * Util method to write an attribute with the ns prefix
          */
          private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                      java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
              if (xmlWriter.getPrefix(namespace) == null) {
                       xmlWriter.writeNamespace(prefix, namespace);
                       xmlWriter.setPrefix(prefix, namespace);

              }

              xmlWriter.writeAttribute(namespace,attName,attValue);

         }

        /**
          * Util method to write an attribute without the ns prefix
          */
          private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                      java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
                if (namespace.equals(""))
              {
                  xmlWriter.writeAttribute(attName,attValue);
              }
              else
              {
                  registerPrefix(xmlWriter, namespace);
                  xmlWriter.writeAttribute(namespace,attName,attValue);
              }
          }


           /**
             * Util method to write an attribute without the ns prefix
             */
            private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                             javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

                java.lang.String attributeNamespace = qname.getNamespaceURI();
                java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
                if (attributePrefix == null) {
                    attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
                }
                java.lang.String attributeValue;
                if (attributePrefix.trim().length() > 0) {
                    attributeValue = attributePrefix + ":" + qname.getLocalPart();
                } else {
                    attributeValue = qname.getLocalPart();
                }

                if (namespace.equals("")) {
                    xmlWriter.writeAttribute(attName, attributeValue);
                } else {
                    registerPrefix(xmlWriter, namespace);
                    xmlWriter.writeAttribute(namespace, attName, attributeValue);
                }
            }
        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }

                if (prefix.trim().length() > 0){
                    xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                                 javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }

                        if (prefix.trim().length() > 0){
                            stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


         /**
         * Register a namespace prefix
         */
         private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
                java.lang.String prefix = xmlWriter.getPrefix(namespace);

                if (prefix == null) {
                    prefix = generatePrefix(namespace);

                    while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                    }

                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }

                return prefix;
            }


  
        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                    throws org.apache.axis2.databinding.ADBException{


        
                 java.util.ArrayList elementList = new java.util.ArrayList();
                 java.util.ArrayList attribList = new java.util.ArrayList();

                 if (localInstanceIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "instanceId"));
                                 
                                        if (localInstanceId != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localInstanceId));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("instanceId cannot be null!!");
                                        }
                                    } if (localSubscriptionIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "subscriptionId"));
                                 
                                        if (localSubscriptionId != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSubscriptionId));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("subscriptionId cannot be null!!");
                                        }
                                    } if (localReferenceIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "referenceId"));
                                 
                                        if (localReferenceId != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localReferenceId));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("referenceId cannot be null!!");
                                        }
                                    } if (localParameterValuesTracker){
                             if (localParameterValues!=null) {
                                 for (int i = 0;i < localParameterValues.length;i++){

                                    if (localParameterValues[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "parameterValues"));
                                         elementList.add(localParameterValues[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("parameterValues cannot be null!!");
                                    
                             }

                        } if (localAttributeValuesTracker){
                             if (localAttributeValues!=null) {
                                 for (int i = 0;i < localAttributeValues.length;i++){

                                    if (localAttributeValues[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "attributeValues"));
                                         elementList.add(localAttributeValues[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("attributeValues cannot be null!!");
                                    
                             }

                        } if (localRequestingUserTracker){
                            elementList.add(new javax.xml.namespace.QName("",
                                                                      "requestingUser"));
                            
                            
                                    if (localRequestingUser==null){
                                         throw new org.apache.axis2.databinding.ADBException("requestingUser cannot be null!!");
                                    }
                                    elementList.add(localRequestingUser);
                                }

                return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
            
            

        }

  

     /**
      *  Factory class that keeps the parse method
      */
    public static class Factory{

        
        

        /**
        * static method to create the object
        * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
        *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
        * Postcondition: If this object is an element, the reader is positioned at its end element
        *                If this object is a complex type, the reader is positioned at the end element of its outer element
        */
        public static ModifySubscription parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            ModifySubscription object =
                new ModifySubscription();

            int event;
            java.lang.String nillableValue = null;
            java.lang.String prefix ="";
            java.lang.String namespaceuri ="";
            try {
                
                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                
                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
                  java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                        "type");
                  if (fullTypeName!=null){
                    java.lang.String nsPrefix = null;
                    if (fullTypeName.indexOf(":") > -1){
                        nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                    }
                    nsPrefix = nsPrefix==null?"":nsPrefix;

                    java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                    
                            if (!"modifySubscription".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (ModifySubscription)org.oscm.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                 
                    
                    reader.next();
                
                        java.util.ArrayList list4 = new java.util.ArrayList();
                    
                        java.util.ArrayList list5 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","instanceId").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setInstanceId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","subscriptionId").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSubscriptionId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","referenceId").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setReferenceId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","parameterValues").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list4.add(org.oscm.xsd.ServiceParameter.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone4 = false;
                                                        while(!loopDone4){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone4 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","parameterValues").equals(reader.getName())){
                                                                    list4.add(org.oscm.xsd.ServiceParameter.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone4 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setParameterValues((org.oscm.xsd.ServiceParameter[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                org.oscm.xsd.ServiceParameter.class,
                                                                list4));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","attributeValues").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list5.add(org.oscm.xsd.ServiceAttribute.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone5 = false;
                                                        while(!loopDone5){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone5 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","attributeValues").equals(reader.getName())){
                                                                    list5.add(org.oscm.xsd.ServiceAttribute.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone5 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setAttributeValues((org.oscm.xsd.ServiceAttribute[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                org.oscm.xsd.ServiceAttribute.class,
                                                                list5));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","requestingUser").equals(reader.getName())){
                                
                                                object.setRequestingUser(org.oscm.xsd.User.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                  
                            while (!reader.isStartElement() && !reader.isEndElement())
                                reader.next();
                            
                                if (reader.isStartElement())
                                // A start element we are not expecting indicates a trailing invalid property
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                            



            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

        }//end of factory class

        

        }
           
          