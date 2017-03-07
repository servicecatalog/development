Implementation-Title: Xerces2 Java
Implementation-Vendor: The Apache Software Foundation
Implementation-Vendor-Id: org.apache
Implementation-Version: 2.11.0



Xerces2 Java Parser 2.11.0 Release	
		
 	

Welcome to the future! Xerces2 is the next generation of high performance, fully compliant XML parsers in the Apache Xerces family. This new version of Xerces introduces the Xerces Native Interface (XNI), a complete framework for building parser components and configurations that is extremely modular and easy to program.

The Apache Xerces2 parser is the reference implementation of XNI but other parser components, configurations, and parsers can be written using the Xerces Native Interface. For complete design and implementation documents, refer to the XNI Manual.

Xerces2 is a fully conforming XML Schema 1.0 processor. A partial experimental implementation of the XML Schema 1.1 Structures and Datatypes Working Drafts (December 2009) and an experimental implementation of the XML Schema Definition Language (XSD): Component Designators (SCD) Candidate Recommendation (January 2010) are provided for evaluation. For more information, refer to the XML Schema page.

Xerces2 also provides a complete implementation of the Document Object Model Level 3 Core and Load/Save W3C Recommendations and provides a complete implementation of the XML Inclusions (XInclude) W3C Recommendation. It also provides support for OASIS XML Catalogs v1.1.

Xerces2 is able to parse documents written according to the XML 1.1 Recommendation, except that it does not yet provide an option to enable normalization checking as described in section 2.13 of this specification. It also handles namespaces according to the XML Namespaces 1.1 Recommendation, and will correctly serialize XML 1.1 documents if the DOM level 3 load/save APIs are in use.

		
	Features of This Release	
		
 	

The Xerces2 Java Parser 2.11.0 supports the following standards and APIs:

    eXtensible Markup Language (XML) 1.0 Fourth Edition Recommendation
    Namespaces in XML 1.0 Second Edition Recommendation
    eXtensible Markup Language (XML) 1.1 Second Edition Recommendation
    Namespaces in XML 1.1 Second Edition Recommendation
    XML Inclusions (XInclude) Version 1.0 Second Edition Recommendation
    Document Object Model (DOM) Level 3 Core, Load and Save, (DOM) Level 2 Core, Events, and Traversal and Range Recommendations
    Element Traversal First Edition Recommendation
    Simple API for XML (SAX) 2.0.2 Core and Extensions
    Java APIs for XML Processing (JAXP) 1.4
    Streaming API For XML (StAX) 1.0 Event API (javax.xml.stream.events)
    XML Schema 1.0 Structures and Datatypes Second Edition Recommendations
    XML Schema 1.1 Structures and Datatypes Working Drafts (December 2009)
    XML Schema Definition Language (XSD): Component Designators (SCD) Candidate Recommendation (January 2010)


		
	Backwards compatibility	
		
 	

According to the DOM Level 3 specification and DOM Level 2 errata the createElementNS and createAttributeNS methods convert empty string namespaceURI to null. Please, let us know if this change affects your application.

		
	Changes from Previous Release	
		
 	

The Xerces2 Java Parser 2.11.0 expands on its experimental support for XML Schema 1.1 by providing implementations for the simplified complex type restriction rules (also known as subsumption), xs:override and a few other XML Schema 1.1 features. This release also introduces experimental support for XML Schema Component Designators (SCD). It fixes several bugs which were present in the previous release and also includes a few other minor enhancements.

For a more complete list of changes, refer to the Release Information page.

		
	Changes from Xerces 1.x	
		
 	

Xerces2 is a nearly complete rewrite of the Xerces 1.x codebase in order to make the code cleaner, more modular, and easier to maintain. It includes a completely redesigned and rewritten XML Schema validation engine. Applications using only the standard interfaces such as JAXP, DOM, and SAX should not see any differences.

		
	License Information	
		
 	

The Xerces-J 2.11.0 release is available in source code and precompiled binary (JAR files) form. Both Xerces-J packages are made available under the Apache Software License.
