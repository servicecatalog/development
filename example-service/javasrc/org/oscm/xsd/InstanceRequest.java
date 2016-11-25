
/**
 * InstanceRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.1  Built on : Oct 19, 2009 (10:59:34 EDT)
 */
            
                package org.oscm.xsd;
            

            /**
            *  InstanceRequest bean class
            */
        
        public  class InstanceRequest
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = instanceRequest
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
                        * field for AttributeValue
                        * This was an Array!
                        */

                        
                                    protected org.oscm.xsd.ServiceAttribute[] localAttributeValue ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAttributeValueTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.oscm.xsd.ServiceAttribute[]
                           */
                           public  org.oscm.xsd.ServiceAttribute[] getAttributeValue(){
                               return localAttributeValue;
                           }

                           
                        


                               
                              /**
                               * validate the array for AttributeValue
                               */
                              protected void validateAttributeValue(org.oscm.xsd.ServiceAttribute[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param AttributeValue
                              */
                              public void setAttributeValue(org.oscm.xsd.ServiceAttribute[] param){
                              
                                   validateAttributeValue(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localAttributeValueTracker = true;
                                          } else {
                                             localAttributeValueTracker = true;
                                                 
                                          }
                                      
                                      this.localAttributeValue=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param org.oscm.xsd.ServiceAttribute
                             */
                             public void addAttributeValue(org.oscm.xsd.ServiceAttribute param){
                                   if (localAttributeValue == null){
                                   localAttributeValue = new org.oscm.xsd.ServiceAttribute[]{};
                                   }

                            
                                 //update the setting tracker
                                localAttributeValueTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localAttributeValue);
                               list.add(param);
                               this.localAttributeValue =
                             (org.oscm.xsd.ServiceAttribute[])list.toArray(
                            new org.oscm.xsd.ServiceAttribute[list.size()]);

                             }
                             

                        /**
                        * field for DefaultLocale
                        */

                        
                                    protected java.lang.String localDefaultLocale ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localDefaultLocaleTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getDefaultLocale(){
                               return localDefaultLocale;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param DefaultLocale
                               */
                               public void setDefaultLocale(java.lang.String param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localDefaultLocaleTracker = true;
                                       } else {
                                          localDefaultLocaleTracker = false;
                                              
                                       }
                                   
                                            this.localDefaultLocale=param;
                                    

                               }
                            

                        /**
                        * field for LoginUrl
                        */

                        
                                    protected java.lang.String localLoginUrl ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localLoginUrlTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getLoginUrl(){
                               return localLoginUrl;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param LoginUrl
                               */
                               public void setLoginUrl(java.lang.String param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localLoginUrlTracker = true;
                                       } else {
                                          localLoginUrlTracker = false;
                                              
                                       }
                                   
                                            this.localLoginUrl=param;
                                    

                               }
                            

                        /**
                        * field for OrganizationId
                        */

                        
                                    protected java.lang.String localOrganizationId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOrganizationIdTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOrganizationId(){
                               return localOrganizationId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OrganizationId
                               */
                               public void setOrganizationId(java.lang.String param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localOrganizationIdTracker = true;
                                       } else {
                                          localOrganizationIdTracker = false;
                                              
                                       }
                                   
                                            this.localOrganizationId=param;
                                    

                               }
                            

                        /**
                        * field for OrganizationName
                        */

                        
                                    protected java.lang.String localOrganizationName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOrganizationNameTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOrganizationName(){
                               return localOrganizationName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OrganizationName
                               */
                               public void setOrganizationName(java.lang.String param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localOrganizationNameTracker = true;
                                       } else {
                                          localOrganizationNameTracker = false;
                                              
                                       }
                                   
                                            this.localOrganizationName=param;
                                    

                               }
                            

                        /**
                        * field for ParameterValue
                        * This was an Array!
                        */

                        
                                    protected org.oscm.xsd.ServiceParameter[] localParameterValue ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localParameterValueTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return org.oscm.xsd.ServiceParameter[]
                           */
                           public  org.oscm.xsd.ServiceParameter[] getParameterValue(){
                               return localParameterValue;
                           }

                           
                        


                               
                              /**
                               * validate the array for ParameterValue
                               */
                              protected void validateParameterValue(org.oscm.xsd.ServiceParameter[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param ParameterValue
                              */
                              public void setParameterValue(org.oscm.xsd.ServiceParameter[] param){
                              
                                   validateParameterValue(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localParameterValueTracker = true;
                                          } else {
                                             localParameterValueTracker = true;
                                                 
                                          }
                                      
                                      this.localParameterValue=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param org.oscm.xsd.ServiceParameter
                             */
                             public void addParameterValue(org.oscm.xsd.ServiceParameter param){
                                   if (localParameterValue == null){
                                   localParameterValue = new org.oscm.xsd.ServiceParameter[]{};
                                   }

                            
                                 //update the setting tracker
                                localParameterValueTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localParameterValue);
                               list.add(param);
                               this.localParameterValue =
                             (org.oscm.xsd.ServiceParameter[])list.toArray(
                            new org.oscm.xsd.ServiceParameter[list.size()]);

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
                       InstanceRequest.this.serialize(parentQName,factory,xmlWriter);
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
                           namespacePrefix+":instanceRequest",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "instanceRequest",
                           xmlWriter);
                   }

               
                   }
                if (localAttributeValueTracker){
                                       if (localAttributeValue!=null){
                                            for (int i = 0;i < localAttributeValue.length;i++){
                                                if (localAttributeValue[i] != null){
                                                 localAttributeValue[i].serialize(new javax.xml.namespace.QName("http://oscm.org/xsd","attributeValue"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                            // write null attribute
                                                            java.lang.String namespace2 = "http://oscm.org/xsd";
                                                            if (! namespace2.equals("")) {
                                                                java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                                                                if (prefix2 == null) {
                                                                    prefix2 = generatePrefix(namespace2);

                                                                    xmlWriter.writeStartElement(prefix2,"attributeValue", namespace2);
                                                                    xmlWriter.writeNamespace(prefix2, namespace2);
                                                                    xmlWriter.setPrefix(prefix2, namespace2);

                                                                } else {
                                                                    xmlWriter.writeStartElement(namespace2,"attributeValue");
                                                                }

                                                            } else {
                                                                xmlWriter.writeStartElement("attributeValue");
                                                            }

                                                           // write the nil attribute
                                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                           xmlWriter.writeEndElement();
                                                    
                                                }

                                            }
                                     } else {
                                        
                                                // write null attribute
                                                java.lang.String namespace2 = "http://oscm.org/xsd";
                                                if (! namespace2.equals("")) {
                                                    java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                                                    if (prefix2 == null) {
                                                        prefix2 = generatePrefix(namespace2);

                                                        xmlWriter.writeStartElement(prefix2,"attributeValue", namespace2);
                                                        xmlWriter.writeNamespace(prefix2, namespace2);
                                                        xmlWriter.setPrefix(prefix2, namespace2);

                                                    } else {
                                                        xmlWriter.writeStartElement(namespace2,"attributeValue");
                                                    }

                                                } else {
                                                    xmlWriter.writeStartElement("attributeValue");
                                                }

                                               // write the nil attribute
                                               writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                               xmlWriter.writeEndElement();
                                        
                                    }
                                 } if (localDefaultLocaleTracker){
                                    namespace = "http://oscm.org/xsd";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"defaultLocale", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"defaultLocale");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("defaultLocale");
                                    }
                                

                                          if (localDefaultLocale==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("defaultLocale cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localDefaultLocale);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localLoginUrlTracker){
                                    namespace = "http://oscm.org/xsd";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"loginUrl", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"loginUrl");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("loginUrl");
                                    }
                                

                                          if (localLoginUrl==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("loginUrl cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localLoginUrl);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOrganizationIdTracker){
                                    namespace = "http://oscm.org/xsd";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"organizationId", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"organizationId");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("organizationId");
                                    }
                                

                                          if (localOrganizationId==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("organizationId cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOrganizationId);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOrganizationNameTracker){
                                    namespace = "http://oscm.org/xsd";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"organizationName", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"organizationName");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("organizationName");
                                    }
                                

                                          if (localOrganizationName==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("organizationName cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOrganizationName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localParameterValueTracker){
                                       if (localParameterValue!=null){
                                            for (int i = 0;i < localParameterValue.length;i++){
                                                if (localParameterValue[i] != null){
                                                 localParameterValue[i].serialize(new javax.xml.namespace.QName("http://oscm.org/xsd","parameterValue"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                            // write null attribute
                                                            java.lang.String namespace2 = "http://oscm.org/xsd";
                                                            if (! namespace2.equals("")) {
                                                                java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                                                                if (prefix2 == null) {
                                                                    prefix2 = generatePrefix(namespace2);

                                                                    xmlWriter.writeStartElement(prefix2,"parameterValue", namespace2);
                                                                    xmlWriter.writeNamespace(prefix2, namespace2);
                                                                    xmlWriter.setPrefix(prefix2, namespace2);

                                                                } else {
                                                                    xmlWriter.writeStartElement(namespace2,"parameterValue");
                                                                }

                                                            } else {
                                                                xmlWriter.writeStartElement("parameterValue");
                                                            }

                                                           // write the nil attribute
                                                           writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                           xmlWriter.writeEndElement();
                                                    
                                                }

                                            }
                                     } else {
                                        
                                                // write null attribute
                                                java.lang.String namespace2 = "http://oscm.org/xsd";
                                                if (! namespace2.equals("")) {
                                                    java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                                                    if (prefix2 == null) {
                                                        prefix2 = generatePrefix(namespace2);

                                                        xmlWriter.writeStartElement(prefix2,"parameterValue", namespace2);
                                                        xmlWriter.writeNamespace(prefix2, namespace2);
                                                        xmlWriter.setPrefix(prefix2, namespace2);

                                                    } else {
                                                        xmlWriter.writeStartElement(namespace2,"parameterValue");
                                                    }

                                                } else {
                                                    xmlWriter.writeStartElement("parameterValue");
                                                }

                                               // write the nil attribute
                                               writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                               xmlWriter.writeEndElement();
                                        
                                    }
                                 } if (localReferenceIdTracker){
                                    namespace = "http://oscm.org/xsd";
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
                             } if (localSubscriptionIdTracker){
                                    namespace = "http://oscm.org/xsd";
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

                 if (localAttributeValueTracker){
                             if (localAttributeValue!=null) {
                                 for (int i = 0;i < localAttributeValue.length;i++){

                                    if (localAttributeValue[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("http://oscm.org/xsd",
                                                                          "attributeValue"));
                                         elementList.add(localAttributeValue[i]);
                                    } else {
                                        
                                                elementList.add(new javax.xml.namespace.QName("http://oscm.org/xsd",
                                                                          "attributeValue"));
                                                elementList.add(null);
                                            
                                    }

                                 }
                             } else {
                                 
                                        elementList.add(new javax.xml.namespace.QName("http://oscm.org/xsd",
                                                                          "attributeValue"));
                                        elementList.add(localAttributeValue);
                                    
                             }

                        } if (localDefaultLocaleTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://oscm.org/xsd",
                                                                      "defaultLocale"));
                                 
                                        if (localDefaultLocale != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDefaultLocale));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("defaultLocale cannot be null!!");
                                        }
                                    } if (localLoginUrlTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://oscm.org/xsd",
                                                                      "loginUrl"));
                                 
                                        if (localLoginUrl != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLoginUrl));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("loginUrl cannot be null!!");
                                        }
                                    } if (localOrganizationIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://oscm.org/xsd",
                                                                      "organizationId"));
                                 
                                        if (localOrganizationId != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOrganizationId));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("organizationId cannot be null!!");
                                        }
                                    } if (localOrganizationNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://oscm.org/xsd",
                                                                      "organizationName"));
                                 
                                        if (localOrganizationName != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOrganizationName));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("organizationName cannot be null!!");
                                        }
                                    } if (localParameterValueTracker){
                             if (localParameterValue!=null) {
                                 for (int i = 0;i < localParameterValue.length;i++){

                                    if (localParameterValue[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("http://oscm.org/xsd",
                                                                          "parameterValue"));
                                         elementList.add(localParameterValue[i]);
                                    } else {
                                        
                                                elementList.add(new javax.xml.namespace.QName("http://oscm.org/xsd",
                                                                          "parameterValue"));
                                                elementList.add(null);
                                            
                                    }

                                 }
                             } else {
                                 
                                        elementList.add(new javax.xml.namespace.QName("http://oscm.org/xsd",
                                                                          "parameterValue"));
                                        elementList.add(localParameterValue);
                                    
                             }

                        } if (localReferenceIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://oscm.org/xsd",
                                                                      "referenceId"));
                                 
                                        if (localReferenceId != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localReferenceId));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("referenceId cannot be null!!");
                                        }
                                    } if (localSubscriptionIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://oscm.org/xsd",
                                                                      "subscriptionId"));
                                 
                                        if (localSubscriptionId != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSubscriptionId));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("subscriptionId cannot be null!!");
                                        }
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
        public static InstanceRequest parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            InstanceRequest object =
                new InstanceRequest();

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
                    
                            if (!"instanceRequest".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (InstanceRequest)org.oscm.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                 
                    
                    reader.next();
                
                        java.util.ArrayList list1 = new java.util.ArrayList();
                    
                        java.util.ArrayList list6 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://oscm.org/xsd","attributeValue").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list1.add(null);
                                                              reader.next();
                                                          } else {
                                                        list1.add(org.oscm.xsd.ServiceAttribute.Factory.parse(reader));
                                                                }
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone1 = false;
                                                        while(!loopDone1){
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
                                                                loopDone1 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("http://oscm.org/xsd","attributeValue").equals(reader.getName())){
                                                                    
                                                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                                          list1.add(null);
                                                                          reader.next();
                                                                      } else {
                                                                    list1.add(org.oscm.xsd.ServiceAttribute.Factory.parse(reader));
                                                                        }
                                                                }else{
                                                                    loopDone1 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setAttributeValue((org.oscm.xsd.ServiceAttribute[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                org.oscm.xsd.ServiceAttribute.class,
                                                                list1));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://oscm.org/xsd","defaultLocale").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setDefaultLocale(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://oscm.org/xsd","loginUrl").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setLoginUrl(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://oscm.org/xsd","organizationId").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOrganizationId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://oscm.org/xsd","organizationName").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOrganizationName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://oscm.org/xsd","parameterValue").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                                          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                          if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                              list6.add(null);
                                                              reader.next();
                                                          } else {
                                                        list6.add(org.oscm.xsd.ServiceParameter.Factory.parse(reader));
                                                                }
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone6 = false;
                                                        while(!loopDone6){
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
                                                                loopDone6 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("http://oscm.org/xsd","parameterValue").equals(reader.getName())){
                                                                    
                                                                      nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                                                      if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                                                          list6.add(null);
                                                                          reader.next();
                                                                      } else {
                                                                    list6.add(org.oscm.xsd.ServiceParameter.Factory.parse(reader));
                                                                        }
                                                                }else{
                                                                    loopDone6 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setParameterValue((org.oscm.xsd.ServiceParameter[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                org.oscm.xsd.ServiceParameter.class,
                                                                list6));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://oscm.org/xsd","referenceId").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setReferenceId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://oscm.org/xsd","subscriptionId").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSubscriptionId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
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
           
          