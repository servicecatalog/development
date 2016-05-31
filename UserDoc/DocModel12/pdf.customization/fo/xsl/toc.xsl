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
    xmlns:exsl="http://exslt.org/common"
    xmlns:opentopic="http://www.idiominc.com/opentopic"
    xmlns:opentopic-index="http://www.idiominc.com/opentopic/index"
    extension-element-prefixes="exsl"
    exclude-result-prefixes="opentopic"
    version="2.0">

    <!-- import some settings -->
    <xsl:include href="toc-settings.xsl"/>

    <xsl:variable name="map" select="//opentopic:map"/>

    <!-- toc as node-tree  -->
    <xsl:variable name="tocTree">
        <xsl:apply-templates mode="tocTree">
            <xsl:with-param name="level" select="number('1')"/>
        </xsl:apply-templates>
    </xsl:variable>

    <xsl:variable name="fs_topicNumbers">
        <xsl:apply-templates select="exsl:node-set($tocTree)/*" mode="fs_topicNumbers"/>
    <!-- GS: 2008-08-26 modified -->
        <!-- Insert "Index" into the toc if index entries exist -->
        
        <xsl:if test="//opentopic-index:index.groups//opentopic-index:index.entry">
          <toc-entry type="index" fsTocType="index" level="1" guid="INDEX">
            <title>Index</title>
          </toc-entry>
        </xsl:if>
        
    <!-- end -->
    </xsl:variable>


    <!-- ######################################################################################### -->
    <!-- called by root-processing -->
    <xsl:template name="createTocPages">

        <!-- store toc in variable -->
        <xsl:variable name="toc">
            <!--<xsl:apply-templates select="exsl:node-set($tocTree)/*" mode="printTocEntry"/>-->
            <xsl:apply-templates select="exsl:node-set($fs_topicNumbers)/*" mode="printTocEntry"/>
        </xsl:variable>

        <fo:page-sequence master-reference="toc-sequence" initial-page-number="auto-odd">

            <!-- insert header and footer -->
            <xsl:call-template name="insertStaticContents">
                <xsl:with-param name="pagetype" select=" 'toc' "/>
            </xsl:call-template>

            <!-- TODO: region name -->
            <fo:flow flow-name="xsl-region-body" xsl:use-attribute-sets="default.font toc">

                <!-- generates headline -->
                <xsl:call-template name="createTocHeader"/>

                <!-- display content -->
                <fo:block xsl:use-attribute-sets="toc.block">
                    <xsl:copy-of select="exsl:node-set($toc)"/>
                </fo:block>

            </fo:flow>

        </fo:page-sequence>

    </xsl:template>

    <xsl:template name="createTocHeader">
        <fo:block xsl:use-attribute-sets="default.headline1 toc.headline">
            <!-- ??? attribute -->
            <xsl:attribute name="id">ID_TOC_00-0F-EA-40-0D-4D</xsl:attribute>
            <!-- get name from language file -->
            <xsl:call-template name="insertVariable">
                <xsl:with-param name="theVariableID" select="'Table of Contents'"/>
            </xsl:call-template>
        </fo:block>
    </xsl:template>

<!-- ######################################################################################### -->
<!-- generates display output -->
<xsl:template match="toc-entry" mode="printTocEntry">

    <xsl:variable name="fsTocType" select="@fsTocType"/>

    <xsl:if test="@fsTocType != 'suppress' ">

        <!-- some types are fs specific, i.e. glossary, so we need to map them -->
        <xsl:variable name="type">
            <xsl:choose>
                <xsl:when test="$fsTocType = 'glossary' ">glossary</xsl:when>
                <xsl:otherwise><xsl:value-of select="@type"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="number">
            <xsl:value-of select="@number"/>
<!--
            <xsl:choose>
                <xsl:when test="$fsTocType = 'appendix' ">
                    <xsl:number  level="multiple" count="*[@fsTocType = $fsTocType]" format="A.1.1"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:number  level="multiple" count="*[@fsTocType = $fsTocType]" format="1.1.1"/>
                </xsl:otherwise>
            </xsl:choose>-->
        </xsl:variable>

        

            <fo:list-block xsl:use-attribute-sets="toc.list">

                <fo:list-item xsl:use-attribute-sets="toc.list.item">

                    <!-- contains the chapter number -->
                    <fo:list-item-label xsl:use-attribute-sets="toc.list.item.label">
                        <fo:block xsl:use-attribute-sets="toc.chapter.number">
                            <fo:basic-link internal-destination="{concat('_OPENTOPIC_TOC_PROCESSING_', @guid)}" xsl:use-attribute-sets="toc.basic_link">
                                <xsl:choose>
                                    <!-- using call-template "insertVariable" to enable language specific prefix/suffix for level1 -->
                                    <xsl:when test="@level = '1' ">
                                        <xsl:call-template name="insertVariable">
                                            <xsl:with-param name="theVariableID" select="concat('toc.', $type, '.number')"/>
                                            <xsl:with-param name="theParameters">
                                                <number>
                                                    <xsl:value-of select="$number"/>
                                                </number>
                                            </xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="$number"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </fo:basic-link>    
                        </fo:block>
                    </fo:list-item-label>

                    <!-- contains title, leader and page-number -->
                    <fo:list-item-body xsl:use-attribute-sets="toc.list.item.body">
                        <fo:block xsl:use-attribute-sets="toc.chapter">
                                
                            <fo:inline xsl:use-attribute-sets="toc.chapter.title">
                                <fo:basic-link internal-destination="{concat('_OPENTOPIC_TOC_PROCESSING_', @guid)}" xsl:use-attribute-sets="toc.basic_link">
                                   
                                <xsl:choose>
                                    <!-- using call-template "insertVariable" to enable language specific prefix/suffix for level1 -->
                                    <xsl:when test="@level = '1' ">
                                        <xsl:call-template name="insertVariable">
                                            <xsl:with-param name="theVariableID" select="concat('toc.', $type, '.title')"/>
                                            <xsl:with-param name="theParameters">
                                                <title>
                                                    <xsl:value-of select="title"/>
                                                </title>
                                            </xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="title"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </fo:basic-link>
                            </fo:inline>
                            
                            <fo:leader xsl:use-attribute-sets="toc.chapter.leader"/>
                            
                            <fo:inline xsl:use-attribute-sets="toc.chapter.page_number">
                                <fo:basic-link internal-destination="{concat('_OPENTOPIC_TOC_PROCESSING_', @guid)}" xsl:use-attribute-sets="toc.basic_link">
                                    <fo:page-number-citation ref-id="{concat('_OPENTOPIC_TOC_PROCESSING_', @guid)}"/>
                                </fo:basic-link>
                            </fo:inline>
                            <fo:block/>
                        </fo:block>
                    </fo:list-item-body>

                </fo:list-item>

            </fo:list-block>


        <xsl:apply-templates select="toc-entry" mode="printTocEntry"/>

    </xsl:if>

</xsl:template>

<!-- 123 -->

<!--
    <xsl:template name="createToc">

        <xsl:variable name="toc">
            <xsl:apply-templates select="/" mode="toc"/>
        </xsl:variable>

        <xsl:if test="count(exsl:node-set($toc)/*) > 0">
            <fo:page-sequence master-reference="toc-sequence" format="i" xsl:use-attribute-sets="__force__page__count">

                <xsl:call-template name="insertTocStaticContents"/>

                <fo:flow flow-name="xsl-region-body">
                    <xsl:call-template name="createTocHeader"/>
                    <fo:block>
                        <xsl:copy-of select="exsl:node-set($toc)"/>
                    </fo:block>
                </fo:flow>

            </fo:page-sequence>
        </xsl:if>
    </xsl:template>

    <xsl:template match="/" mode="toc">
        <xsl:apply-templates mode="toc">
            <xsl:with-param name="include" select="'true'"/>
        </xsl:apply-templates>
    </xsl:template>

-->

<!-- ######################################################################################### -->
<!-- adding this node to toc-tree -->
<xsl:template match="*[contains(@class, ' topic/topic ') and not(contains(@class, ' bkinfo/bkinfo '))]" mode="tocTree">
    <xsl:param name="level"/>

    <xsl:variable name="id" select="@id"/>

    <!-- type of entry taken from  attr class or attr refclass from "root"-node of this node -->
    <xsl:variable name="type">
        <xsl:call-template name="f_toc_getTocEntryType">
            <xsl:with-param name="class">
                <xsl:value-of select="//*[descendant-or-self::*[@id = $id]]/@class"/>
            </xsl:with-param>
            <xsl:with-param name="refclass">
                <xsl:value-of select="//*[descendant-or-self::*[@id = $id]]/@refclass"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="title">
        <xsl:value-of select="title"/>
    </xsl:variable>

    <!-- maps types to handle different types as same, i.e. abstract and chapter  -->
    <xsl:variable name="fsTocType">
        <xsl:call-template name="f_toc_mapTocEntryType">
            <xsl:with-param name="type" select="$type"/>
            <xsl:with-param name="title" select="$title"/>
        </xsl:call-template>
    </xsl:variable>


    <toc-entry>
        <xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
        <xsl:attribute name="guid"><xsl:value-of select="generate-id()"/></xsl:attribute>
        <xsl:attribute name="class"><xsl:value-of select="@class"/></xsl:attribute>
        <xsl:attribute name="refclass"><xsl:value-of select="@refclass"/></xsl:attribute>
        <xsl:attribute name="level"><xsl:value-of select="$level"/></xsl:attribute>
        <xsl:attribute name="type"><xsl:value-of select="$type"/></xsl:attribute>
        <xsl:attribute name="fsTocType"><xsl:value-of select="$fsTocType"/></xsl:attribute>
        <title>
            <xsl:value-of select="$title"/>
        </title>

        <xsl:variable name="maxLevelDepth">
            <xsl:choose>
                <xsl:when test="$type = 'preface' "><xsl:value-of select="$v_toc_maxLevelDepthPreface"/></xsl:when>
                <xsl:when test="$type = 'chapter' "><xsl:value-of select="$v_toc_maxLevelDepthChapter"/></xsl:when>
                <xsl:when test="$type = 'abstract' "><xsl:value-of select="$v_toc_maxLevelDepthAbstract"/></xsl:when>
                <xsl:when test="$type = 'appendix' "><xsl:value-of select="$v_toc_maxLevelDepthAppendix"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="number('0')"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

<!--
    <xsl:message>title: <xsl:value-of select="$title"/></xsl:message>
    <xsl:message>type: <xsl:value-of select="$type"/></xsl:message>
    <xsl:message>fstype: <xsl:value-of select="$fsTocType"/></xsl:message>
    <xsl:message>level: <xsl:value-of select="$level"/></xsl:message>
    <xsl:message>maxlevel: <xsl:value-of select="$maxLevelDepth"/></xsl:message>
-->

        <!-- only apply templates if a descendant will be added to tocTree -->



        <xsl:if test="$level + 1 &lt;= $maxLevelDepth">
            <xsl:apply-templates mode="tocTree">
                <xsl:with-param name="level" select="$level + 1"/>
            </xsl:apply-templates>
        </xsl:if>

    </toc-entry>

</xsl:template>
    
<!-- ######################################################################################### -->
<!-- node is not added to tocTree -->
<xsl:template match="node()" mode="tocTree">
    <xsl:param name="level"/>

    <!-- maybe child-nodes? -->
    <xsl:apply-templates mode="tocTree">
        <xsl:with-param name="level" select="$level"/>
    </xsl:apply-templates>

</xsl:template>

<!-- ######################################################################################### -->
<xsl:template match="toc-entry" mode="fs_topicNumbers">

<xsl:variable name="fsTocType" select="@fsTocType"/>

    <xsl:variable name="number">
        <xsl:choose>
            <xsl:when test="$fsTocType = 'appendix' ">
                <xsl:number  level="multiple" count="*[@fsTocType = $fsTocType]" format="A.1.1"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:number  level="multiple" count="*[@fsTocType = $fsTocType]" format="1.1.1"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <toc-entry>
        <xsl:attribute name="class"><xsl:value-of select="@class"/></xsl:attribute>
        <xsl:attribute name="refclass"><xsl:value-of select="@refclass"/></xsl:attribute>
        <xsl:attribute name="level"><xsl:value-of select="@level"/></xsl:attribute>
        <xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
        <xsl:attribute name="fsTocType"><xsl:value-of select="@fsTocType"/></xsl:attribute>
        <xsl:attribute name="number"><xsl:value-of select="$number"/></xsl:attribute>
        <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
        <xsl:attribute name="guid"><xsl:value-of select="@guid"/></xsl:attribute>
        <xsl:copy-of select="title"/>

        <xsl:apply-templates mode="fs_topicNumbers"/>

    </toc-entry>



</xsl:template>


    <xsl:template match="text()[.='topicChapter']" mode="toc-prefix-text">
        <xsl:param name="id"/>
        <xsl:variable name="topicChapters">
            <xsl:copy-of select="$map//*[contains(@class, ' bookmap/chapter ')]"/>
        </xsl:variable>
        <xsl:variable name="chapterNumber">
            <xsl:number format="1" value="count($topicChapters/*[@id = $id]/preceding-sibling::*) + 1"/>
        </xsl:variable>
        <xsl:call-template name="insertVariable">
            <xsl:with-param name="theVariableID" select="'Table of Contents Chapter'"/>
            <xsl:with-param name="theParameters">
                <number>
                    <xsl:value-of select="$chapterNumber"/>
                </number>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="text()[.='topicAppendix']" mode="toc-prefix-text">
        <xsl:param name="id"/>
        <xsl:variable name="topicAppendixes">
            <xsl:copy-of select="$map//*[contains(@class, ' bookmap/appendix ')]"/>
        </xsl:variable>
        <xsl:variable name="appendixNumber">
            <xsl:number format="A" value="count($topicAppendixes/*[@id = $id]/preceding-sibling::*) + 1"/>
        </xsl:variable>
        <xsl:call-template name="insertVariable">
            <xsl:with-param name="theVariableID" select="'Table of Contents Appendix'"/>
            <xsl:with-param name="theParameters">
                <number>
                    <xsl:value-of select="$appendixNumber"/>
                </number>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="text()[.='topicPart']" mode="toc-prefix-text">
        <xsl:param name="id"/>
        <xsl:variable name="topicParts">
            <xsl:copy-of select="$map//*[contains(@class, ' bookmap/part ')]"/>
        </xsl:variable>
        <xsl:variable name="partNumber">
            <xsl:number format="I" value="count($topicParts/*[@id = $id]/preceding-sibling::*) + 1"/>
        </xsl:variable>
        <xsl:call-template name="insertVariable">
            <xsl:with-param name="theVariableID" select="'Table of Contents Part'"/>
            <xsl:with-param name="theParameters">
                <number>
                    <xsl:value-of select="$partNumber"/>
                </number>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="text()[.='topicPreface']" mode="toc-prefix-text">
        <xsl:call-template name="insertVariable">
            <xsl:with-param name="theVariableID" select="'Table of Contents Preface'"/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="text()[.='topicNotices']" mode="toc-prefix-text">
        <xsl:call-template name="insertVariable">
            <xsl:with-param name="theVariableID" select="'Table of Contents Notices'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="node()" mode="toc-prefix-text" />


    <xsl:template match="text()[. = 'topicChapter']" mode="toc-topic-text">
        <xsl:param name="tocItemContent"/>
        <xsl:param name="currentNode"/>
        <xsl:for-each select="$currentNode">
        <fo:block xsl:use-attribute-sets="__toc__chapter__content">
            <xsl:copy-of select="$tocItemContent"/>
        </fo:block>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="text()[. = 'topicAppendix']" mode="toc-topic-text">
        <xsl:param name="tocItemContent"/>
        <xsl:param name="currentNode"/>
        <xsl:for-each select="$currentNode">
        <fo:block xsl:use-attribute-sets="__toc__appendix__content">
            <xsl:copy-of select="$tocItemContent"/>
        </fo:block>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="text()[. = 'topicPart']" mode="toc-topic-text">
        <xsl:param name="tocItemContent"/>
        <xsl:param name="currentNode"/>
        <xsl:for-each select="$currentNode">
        <fo:block xsl:use-attribute-sets="__toc__part__content">
            <xsl:copy-of select="$tocItemContent"/>
        </fo:block>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="text()[. = 'topicPreface']" mode="toc-topic-text">
        <xsl:param name="tocItemContent"/>
        <xsl:param name="currentNode"/>
        <xsl:for-each select="$currentNode">
        <fo:block xsl:use-attribute-sets="__toc__preface__content">
            <xsl:copy-of select="$tocItemContent"/>
        </fo:block>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="text()[. = 'topicNotices']" mode="toc-topic-text">
        <!-- Disabled, because now the Notices appear before the TOC -->
        <!--<xsl:param name="tocItemContent"/>
        <fo:block xsl:use-attribute-sets="__toc__notices__content">
            <xsl:copy-of select="$tocItemContent"/>
        </fo:block>-->
    </xsl:template>
    
    <xsl:template match="node()" mode="toc-topic-text">
        <xsl:param name="tocItemContent"/>
        <xsl:param name="currentNode"/>
        <xsl:for-each select="$currentNode">
        <fo:block xsl:use-attribute-sets="__toc__topic__content">
            <xsl:copy-of select="$tocItemContent"/>
        </fo:block>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="node()" mode="toc">
        <xsl:param name="include"/>
        <xsl:apply-templates mode="toc">
            <xsl:with-param name="include" select="$include"/>
        </xsl:apply-templates>
    </xsl:template>

<!-- ######################################################################################### -->
<xsl:template name="f_toc_getTocEntryType">
    <xsl:param name="class"/>
    <xsl:param name="refclass"/>

    <xsl:choose>

        <xsl:when test="contains($class, ' bookmap/preface ')">
            <xsl:text>preface</xsl:text>
        </xsl:when>
        <xsl:when test="contains($class, ' bookmap/chapter ')">
            <xsl:text>chapter</xsl:text>
        </xsl:when>
        <xsl:when test="contains($class, ' bookmap/appendix ')">
            <xsl:text>appendix</xsl:text>
        </xsl:when>
        <xsl:when test="contains($class, ' bookmap/abstract ')">
            <xsl:text>abstract</xsl:text>
        </xsl:when>

        <xsl:when test="contains($refclass, ' bookmap/preface ')">
            <xsl:text>preface</xsl:text>
        </xsl:when>
        <xsl:when test="contains($refclass, ' bookmap/chapter ')">
            <xsl:text>chapter</xsl:text>
        </xsl:when>
        <xsl:when test="contains($refclass, ' bookmap/appendix ')">
            <xsl:text>appendix</xsl:text>
        </xsl:when>
        <xsl:when test="contains($refclass, ' bookmap/abstract ')">
            <xsl:text>abstract</xsl:text>
        </xsl:when>

        <xsl:otherwise>
            <xsl:message>unknown type: class="<xsl:value-of select="$class"/>" refclass="<xsl:value-of select="$refclass"/>"</xsl:message>
            <xsl:text>simple</xsl:text>
        </xsl:otherwise>
    </xsl:choose>

</xsl:template>

<!-- ######################################################################################### -->
<xsl:template name="f_toc_mapTocEntryType">
    <xsl:param name="type"/>
    <xsl:param name="title"/>

<!-- GS 2008-sep-09: Inserted to enable language-specific processing of the glossary title -->  
    <xsl:variable name="glossaryTitle"> 
        <xsl:call-template name="insertVariable">
                    <xsl:with-param name="theVariableID" select="'header.glossary'"/>
                </xsl:call-template>
    </xsl:variable>
    
    <xsl:choose>

        <!-- no title? drop it! -->
        <xsl:when test="$title = '' ">
            <xsl:text>suppress</xsl:text>
        </xsl:when>

<!-- GS 2008-sep-09: New -->
        <xsl:when test="$title = $glossaryTitle">
            <xsl:text>glossary</xsl:text>
        </xsl:when>
        
<!-- GS 2008-sep-09: Disabled
        <xsl:when test="$title = 'Glossary' ">
            <xsl:text>glossary</xsl:text>
        </xsl:when>
-->
        
<!-- jko: 2006-04-21 disabled
        <xsl:when test="$type = 'chapter' or $type = 'abstract' or $type = 'preface' ">
-->
<!-- jko: 2006-04-21 new -->
        <xsl:when test="$type = 'chapter'">
<!-- end -->
            <xsl:text>chapter</xsl:text>
        </xsl:when>

        <xsl:when test="$type = 'appendix' ">
            <xsl:text>appendix</xsl:text>
        </xsl:when>

        <!-- jko: 2006-04-21 new -->
        <xsl:when test="$type = 'abstract' ">
            <xsl:text>abstract</xsl:text>
        </xsl:when>

        <xsl:when test="$type = 'preface' ">
            <xsl:text>preface</xsl:text>
        </xsl:when>
        <!-- end -->
        <xsl:otherwise>
            <xsl:text>unknown</xsl:text>
        </xsl:otherwise>

    </xsl:choose>

</xsl:template>

<!-- ######################################################################################### -->

</xsl:stylesheet>
