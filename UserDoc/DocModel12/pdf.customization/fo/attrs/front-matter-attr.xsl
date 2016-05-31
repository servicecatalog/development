<?xml version='1.0'?>

<!-- 
Copyright © 2004-2006 by Idiom Technologies, Inc. All rights reserved. 
IDIOM is a registered trademark of Idiom Technologies, Inc. and WORLDSERVER
and WORLDSTART are trademarks of Idiom Technologies, Inc. All other 
trademarks are the property of their respective owners. 

IDIOM TECHNOLOGIES, INC. IS DELIVERING THE SOFTWARE "AS IS," WITH 
ABSOLUTELY NO WARRANTIES WHATSOEVER, WHETHER EXPRESS OR IMPLIED,  AND IDIOM
TECHNOLOGIES, INC. DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
BUT NOT LIMITED TO WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
PURPOSE AND WARRANTY OF NON-INFRINGEMENT. IDIOM TECHNOLOGIES, INC. SHALL NOT
BE LIABLE FOR INDIRECT, INCIDENTAL, SPECIAL, COVER, PUNITIVE, EXEMPLARY,
RELIANCE, OR CONSEQUENTIAL DAMAGES (INCLUDING BUT NOT LIMITED TO LOSS OF 
ANTICIPATED PROFIT), ARISING FROM ANY CAUSE UNDER OR RELATED TO  OR ARISING 
OUT OF THE USE OF OR INABILITY TO USE THE SOFTWARE, EVEN IF IDIOM
TECHNOLOGIES, INC. HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 

Idiom Technologies, Inc. and its licensors shall not be liable for any
damages suffered by any person as a result of using and/or modifying the
Software or its derivatives. In no event shall Idiom Technologies, Inc.'s
liability for any damages hereunder exceed the amounts received by Idiom
Technologies, Inc. as a result of this transaction.

These terms and conditions supersede the terms and conditions in any
licensing agreement to the extent that such terms and conditions conflict
with those set forth herein.

This file is part of the DITA Open Toolkit project hosted on Sourceforge.net. 
See the accompanying license.txt file for applicable licenses.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    version="2.0">

    <xsl:attribute-set name="__frontmatter">
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="padding-top">
            <xsl:variable name="normalizedLocaleName">
                <xsl:value-of select="lower-case(/*/@xml:lang)"/>
            </xsl:variable>
            <xsl:choose>
                <xsl:when test="($normalizedLocaleName = 'ja-jp') or ($normalizedLocaleName = 'ja_jp')">-50pt</xsl:when>                
                <xsl:otherwise>-16pt</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>

    </xsl:attribute-set>

    <xsl:attribute-set name="__frontmatter__title" use-attribute-sets="common.title">
        <xsl:attribute name="space-before">80mm</xsl:attribute>
        <xsl:attribute name="space-before.conditionality">retain</xsl:attribute>
        <xsl:attribute name="font-size">22pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="line-height">140%</xsl:attribute>
    </xsl:attribute-set>

<!-- GS 2006-12-04: Commented out because the subtitle element is not used
    <xsl:attribute-set name="__frontmatter__subtitle" use-attribute-sets="common.title">
        <xsl:attribute name="font-size">18pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="line-height">140%</xsl:attribute>
    </xsl:attribute-set>
-->
    <xsl:attribute-set name="__frontmatter__owner">
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="space-before">36pt</xsl:attribute>
        <xsl:attribute name="font-size">11pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="line-height">normal</xsl:attribute>
    </xsl:attribute-set>

    <!-- GS 2006-12-04: Attributes for "Interstage" -->
    <xsl:attribute-set name="__frontmatter__brand">
        <xsl:attribute name="margin-top">141pt</xsl:attribute>       
        <xsl:attribute name="margin-left">27pt</xsl:attribute>       
        <xsl:attribute name="font-family">Frontmatter</xsl:attribute>
        <xsl:attribute name="font-size">28pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="line-height">normal</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>

    <!-- GS 2006-12-04: Attributes for "Business Process Manager" -->
    <xsl:attribute-set name="__frontmatter__prodname">
        <xsl:attribute name="margin-top">0pt</xsl:attribute>       
        <xsl:attribute name="margin-left">27pt</xsl:attribute>       
        <xsl:attribute name="font-family">Frontmatter</xsl:attribute>
        <xsl:attribute name="font-size">28pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="line-height">normal</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>

    <!-- GS 2006-12-04: Attributes for the version, e.g. "V8.1" -->
    <xsl:attribute-set name="__frontmatter__prognum">
        <xsl:attribute name="margin-top">0pt</xsl:attribute>
        <xsl:attribute name="margin-left">27pt</xsl:attribute>       
        <xsl:attribute name="font-family">Frontmatter</xsl:attribute>
        <xsl:attribute name="font-size">28pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="line-height">normal</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>

    <!-- GS 2006-12-04: Attributes for the book type, e.g. "Administration Guide" -->
    <xsl:attribute-set name="__frontmatter__component">
        <xsl:attribute name="margin-top">130pt</xsl:attribute>       
        <xsl:attribute name="margin-left">27pt</xsl:attribute>       
        <xsl:attribute name="font-family">Frontmatter</xsl:attribute>
        <xsl:attribute name="font-size">34pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="line-height">normal</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>

    <!-- GS 2007-04-10: Attributes for the application server, e.g. "WebSphere"; required for the EE Installation Guides only -->
    <xsl:attribute-set name="__frontmatter__series">
        <xsl:attribute name="margin-top">0pt</xsl:attribute>       
        <xsl:attribute name="margin-left">27pt</xsl:attribute>       
        <xsl:attribute name="margin-right">43pt</xsl:attribute>  
        <xsl:attribute name="font-family">Frontmatter</xsl:attribute>
        <xsl:attribute name="font-size">32pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="line-height">normal</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>


    <!-- GS 2006-12-04: Attributes for the document number, e.g. "B1WW-9999-09/0(00)" -->
    <xsl:attribute-set name="__frontmatter__bknum">
        <xsl:attribute name="font-size">14pt</xsl:attribute>
<!--        <xsl:attribute name="margin-top">255pt</xsl:attribute>       -->
        <xsl:attribute name="margin-right">43pt</xsl:attribute>       
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="line-height">normal</xsl:attribute>
        <xsl:attribute name="text-align">right</xsl:attribute>
    </xsl:attribute-set>

    <!-- GS 2006-12-04: Attributes for the publication date, e.g. "January 2007" -->
    <xsl:attribute-set name="__frontmatter__bkedition">
        <xsl:attribute name="font-size">16pt</xsl:attribute>
    <!--    <xsl:attribute name="margin-top">2pt</xsl:attribute>       -->
        <xsl:attribute name="margin-right">43pt</xsl:attribute>       
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="line-height">normal</xsl:attribute>
        <xsl:attribute name="text-align">right</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__frontmatter__copyright">
        <xsl:attribute name="font-size">30pt</xsl:attribute>
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="line-height">normal</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="text-indent">0em</xsl:attribute>
        <xsl:attribute name="space-before">3pt</xsl:attribute>
        <xsl:attribute name="margin-top">16pt</xsl:attribute>

        <xsl:attribute name="space-after">3pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__frontmatter__owner__container">
        <xsl:attribute name="position">absolute</xsl:attribute>
        <xsl:attribute name="top">210mm</xsl:attribute>
        <xsl:attribute name="bottom">20mm</xsl:attribute>
        <xsl:attribute name="right">20mm</xsl:attribute>
        <xsl:attribute name="left">20mm</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__frontmatter__owner__container_content">
        <xsl:attribute name="text-align">center</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__frontmatter__mainbooktitle">
        <!--<xsl:attribute name=""></xsl:attribute>-->
    </xsl:attribute-set>

    <xsl:attribute-set name="__frontmatter__booklibrary">
        <!--<xsl:attribute name=""></xsl:attribute>-->
    </xsl:attribute-set>

	<xsl:attribute-set name="bookmap.summary">
		<xsl:attribute name="font-size">9pt</xsl:attribute>
	</xsl:attribute-set>

</xsl:stylesheet>