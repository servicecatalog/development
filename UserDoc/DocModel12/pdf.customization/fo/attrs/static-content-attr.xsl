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

<!-- ######################################################################################### -->
<!-- contains the header [block] -->
<xsl:attribute-set name="static.header">
    <!-- === line: start === -->
    <!-- margin-bottom: 40 + width -->
    <xsl:attribute name="margin-bottom">41.5pt</xsl:attribute>
    <xsl:attribute name="border-bottom-style">solid</xsl:attribute> 
    <xsl:attribute name="border-bottom-width">1.5pt</xsl:attribute> 
    <xsl:attribute name="border-bottom-color">rgb(128,128,128)</xsl:attribute>
    <!-- === line: start end -->
    
    <!-- padding-bottom = 60 - margin-bottom -->
    <xsl:attribute name="padding-bottom">18.5pt</xsl:attribute>
<!--    <xsl:attribute name="padding-right">6pt</xsl:attribute>
    <xsl:attribute name="padding-left">6pt</xsl:attribute>
-->    <xsl:attribute name="start-indent">0pt</xsl:attribute>
    <xsl:attribute name="end-indent">0pt</xsl:attribute>
    <xsl:attribute name="text-align">end</xsl:attribute>
</xsl:attribute-set>


<!-- ######################################################################################### -->
<!-- contains the footer [block] -->
<xsl:attribute-set name="static.footer">
</xsl:attribute-set>


<!-- ######################################################################################### -->
<!-- line and content [list-block] -->
<xsl:attribute-set name="static.footer.list">

    <!-- === line: start === -->
    <!-- margin-top: 60 + width -->
    <xsl:attribute name="margin-top">61.5pt</xsl:attribute>
    <xsl:attribute name="border-top-style">solid</xsl:attribute>    
    <xsl:attribute name="border-top-width">1.5pt</xsl:attribute>    
    <xsl:attribute name="border-top-color">rgb(128,128,128)</xsl:attribute>
    <!-- === line: start end -->
    
    <!-- padding-top = 80 - margin-top -->
    <xsl:attribute name="padding-top">18.5pt</xsl:attribute>
    <xsl:attribute name="start-indent">0pt</xsl:attribute>
    <xsl:attribute name="end-indent">0pt</xsl:attribute>
</xsl:attribute-set>


<!-- ######################################################################################### -->
<!-- content [list-item] -->
<xsl:attribute-set name="static.footer.list.item">
</xsl:attribute-set>


<!-- ######################################################################################### -->
<!-- contains doc-title [list-item-label] -->
<xsl:attribute-set name="static.footer.list.item.label">
    <!-- don't remove or list will not be displayed correctly --> 
    <xsl:attribute name="end-indent">1pt</xsl:attribute>
    <xsl:attribute name="text-align">start</xsl:attribute>
</xsl:attribute-set>


<!-- ######################################################################################### -->
<xsl:attribute-set name="static.footer.list.item.body">
    <!-- don't remove or list will not be displayed correctly --> 
    <xsl:attribute name="start-indent">1pt</xsl:attribute>
    <xsl:attribute name="text-align">end</xsl:attribute>
</xsl:attribute-set>


<!-- ######################################################################################### -->

</xsl:stylesheet>