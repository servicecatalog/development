<?xml version='1.0'?>

<!-- Copyright © 2004-2006 by Idiom Technologies, Inc. All rights reserved. 
    IDIOM is a registered trademark of Idiom Technologies, Inc. and WORLDSERVER 
    and WORLDSTART are trademarks of Idiom Technologies, Inc. All other trademarks 
    are the property of their respective owners. IDIOM TECHNOLOGIES, INC. IS 
    DELIVERING THE SOFTWARE "AS IS," WITH ABSOLUTELY NO WARRANTIES WHATSOEVER, 
    WHETHER EXPRESS OR IMPLIED, AND IDIOM TECHNOLOGIES, INC. DISCLAIMS ALL WARRANTIES, 
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO WARRANTIES OF MERCHANTABILITY 
    AND FITNESS FOR A PARTICULAR PURPOSE AND WARRANTY OF NON-INFRINGEMENT. IDIOM 
    TECHNOLOGIES, INC. SHALL NOT BE LIABLE FOR INDIRECT, INCIDENTAL, SPECIAL, 
    COVER, PUNITIVE, EXEMPLARY, RELIANCE, OR CONSEQUENTIAL DAMAGES (INCLUDING 
    BUT NOT LIMITED TO LOSS OF ANTICIPATED PROFIT), ARISING FROM ANY CAUSE UNDER 
    OR RELATED TO OR ARISING OUT OF THE USE OF OR INABILITY TO USE THE SOFTWARE, 
    EVEN IF IDIOM TECHNOLOGIES, INC. HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH 
    DAMAGES. Idiom Technologies, Inc. and its licensors shall not be liable for 
    any damages suffered by any person as a result of using and/or modifying 
    the Software or its derivatives. In no event shall Idiom Technologies, Inc.'s 
    liability for any damages hereunder exceed the amounts received by Idiom 
    Technologies, Inc. as a result of this transaction. These terms and conditions 
    supersede the terms and conditions in any licensing agreement to the extent 
    that such terms and conditions conflict with those set forth herein. This 
    file is part of the DITA Open Toolkit project hosted on Sourceforge.net. 
    See the accompanying license.txt file for applicable licenses. -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:opentopic="http://www.idiominc.com/opentopic"
    exclude-result-prefixes="opentopic" version="2.0">

    <xsl:include href="../attrs/front-matter-attr.xsl" />
    <xsl:variable name="noticesId">
        <xsl:value-of select="//*[contains(@class,' bookmap/frontmatter ')]/*[contains(@class,' bookmap/notices ')]/@id" />
    </xsl:variable>

    <xsl:template name="createFrontMatter">
        <fo:page-sequence master-reference="front-matter"
            format="i" xsl:use-attribute-sets="__force__page__count">
            <!-- <xsl:call-template name="insertFrontMatterStaticContents"/> -->

            <fo:static-content flow-name="front-header">
                <fo:block>
                    <fo:external-graphic content-width="scale-to-fit"
                        src="Customization/OpenTopic/common/artwork/TitlePageGraphic.png" />
                </fo:block>
            </fo:static-content>

            <fo:static-content flow-name="front-footer">
                <xsl:choose>
                    <xsl:when test="//*[contains(@class,' bkinfo/bkinfo ')][1]">

                        <!-- GS 2006-12-04: Set the document number, e.g. "B1WW-9999-09/0(00)" -->
                        <fo:block xsl:use-attribute-sets="__frontmatter__bknum">
                            <xsl:apply-templates
                                select="//*[contains(@class,' bkinfo/bkinfo ')]/*[contains(@class,' bkinfo/bkinfobody ')]/*[contains(@class,' bkinfo/bkid ')]/*[contains(@class,' bkinfo/bknum ')]" />
                        </fo:block>

                        <!-- GS 2006-12-04: Set the publication date, e.g. "January 2007" -->
                        <fo:block xsl:use-attribute-sets="__frontmatter__bkedition">
                            <xsl:apply-templates
                                select="//*[contains(@class,' bkinfo/bkinfo ')]/*[contains(@class,' bkinfo/bkinfobody ')]/*[contains(@class,' bkinfo/bkid ')]/*[contains(@class,' bkinfo/bkedition ')]" />
                        </fo:block>
                    </xsl:when>
                    <xsl:otherwise>
                        <fo:block xsl:use-attribute-sets="__frontmatter__bknum">
                            <fo:inline>
                                <xsl:value-of
                                    select="//*[contains(@class,' bookmap/bookmeta ')]/*[contains(@class,' bookmap/bookid ')]/*[contains(@class,' bookmap/booknumber ')]" />
                            </fo:inline>
                        </fo:block>
                        <fo:block xsl:use-attribute-sets="__frontmatter__bkedition">
                            <fo:inline>
                                <xsl:value-of
                                    select="//*[contains(@class,' bookmap/bookmeta ')]/*[contains(@class,' bookmap/bookid ')]/*[contains(@class,' bookmap/edition ')]" />
                            </fo:inline>
                        </fo:block>
                    </xsl:otherwise>
                </xsl:choose>
            </fo:static-content>

            <fo:flow flow-name="xsl-region-body">
                <fo:block xsl:use-attribute-sets="__frontmatter"> <!--   -->
                    <xsl:choose>
                        <xsl:when test="//*[contains(@class,' bkinfo/bkinfo ')][1]">
                                <!-- GS 2006-12-04: Set "Interstage" -->
                                <fo:block xsl:use-attribute-sets="__frontmatter__brand">
                                    <xsl:apply-templates
                                        select="//*[contains(@class,' bkinfo/bkinfo ')]/*[contains(@class,' topic/prolog ')]/*[contains(@class,' topic/metadata ')]/*[contains(@class,' topic/prodinfo ')]/*[contains(@class,' topic/brand ')]" />
                                </fo:block>

                                <!-- GS 2006-12-04: Set "Business Process Manager" -->
                                <fo:block xsl:use-attribute-sets="__frontmatter__prodname">
                                    <xsl:apply-templates
                                        select="//*[contains(@class,' bkinfo/bkinfo ')]/*[contains(@class,' topic/prolog ')]/*[contains(@class,' topic/metadata ')]/*[contains(@class,' topic/prodinfo ')]/*[contains(@class,' topic/prodname ')]" />

                                </fo:block>

                                <!-- GS 2006-12-04: Set the version, e.g. "V8.1" -->
                                <fo:block xsl:use-attribute-sets="__frontmatter__prognum">
                                    <xsl:apply-templates
                                        select="//*[contains(@class,' bkinfo/bkinfo ')]/*[contains(@class,' topic/prolog ')]/*[contains(@class,' topic/metadata ')]/*[contains(@class,' topic/prodinfo ')]/*[contains(@class,' topic/prognum ')]" />
                                </fo:block>

                                <!-- GS 2006-12-04: Set the book type, e.g. "Administration Guide" -->
                                <fo:block xsl:use-attribute-sets="__frontmatter__component">
                                    <xsl:apply-templates
                                        select="//*[contains(@class,' bkinfo/bkinfo ')]/*[contains(@class,' topic/prolog ')]/*[contains(@class,' topic/metadata ')]/*[contains(@class,' topic/prodinfo ')]/*[contains(@class,' topic/component ')]" />
                                </fo:block>


                                <!-- GS 2007-04-10: Set the application server for the EE Installation 
                                    Guides, e.g. "WebSphere" -->
                                <fo:block xsl:use-attribute-sets="__frontmatter__series">
                                    <xsl:apply-templates
                                        select="//*[contains(@class,' bkinfo/bkinfo ')]/*[contains(@class,' topic/prolog ')]/*[contains(@class,' topic/metadata ')]/*[contains(@class,' topic/prodinfo ')]/*[contains(@class,' topic/series ')]" />
                                </fo:block>


                                <!-- GS 2006-12-04: Create the copyright page -->
                                <fo:block xsl:use-attribute-sets="__frontmatter__copyright"
                                    break-before="page">

                                    <xsl:apply-templates
                                        select="//*[contains(@class,' bkinfo/bkinfo ')]/*[contains(@class,' bkinfo/bkinfobody ')]/*[contains(@class,' bkinfo/bkcover ')]" />

                                </fo:block>
                                <!-- set the title -->
                                <!-- <fo:block xsl:use-attribute-sets="__frontmatter__title" background-color="#ff0000">
                                    <xsl:apply-templates select="//*[contains(@class,' bkinfo/bkinfo 
                                    ')][1]/*[contains(@class,' topic/title ')]/node()"/>
                                </fo:block> -->
                        </xsl:when>

                        <!-- *instinctools changes -->
                        <xsl:when
                            test="//*[contains(@class,' bookmap/bookmeta ')]/*[contains(@class,' topic/prodinfo ')][1]">
                            
                            <fo:block xsl:use-attribute-sets="__frontmatter__brand">
                                    <xsl:apply-templates select="//*[contains(@class,' bookmap/bookmeta ')]/*[contains(@class,' topic/prodinfo ')]/*[contains(@class,' topic/brand ')]"/>
                            </fo:block>

                            <fo:block xsl:use-attribute-sets="__frontmatter__prodname">                                
                                    <xsl:apply-templates select="//*[contains(@class,' bookmap/bookmeta ')]/*[contains(@class,' topic/prodinfo ')]/*[contains(@class,' topic/prodname ')]" />
                            </fo:block>

                            <fo:block xsl:use-attribute-sets="__frontmatter__prognum">
                                    <xsl:apply-templates select="//*[contains(@class,' bookmap/bookmeta ')]/*[contains(@class,' topic/prodinfo ')]/*[contains(@class,' topic/prognum ')]" />
                            </fo:block>

                            <fo:block xsl:use-attribute-sets="__frontmatter__component">
                                    <xsl:apply-templates select="//*[contains(@class,' bookmap/bookmeta ')]/*[contains(@class,' topic/prodinfo ')]/*[contains(@class,' topic/component ')]" />
                            </fo:block>

                            <fo:block xsl:use-attribute-sets="__frontmatter__series" padding-top="6pt">
                                    <xsl:apply-templates select="//*[contains(@class,' bookmap/bookmeta ')]/*[contains(@class,' topic/prodinfo ')]/*[contains(@class,' topic/series ')]"/>
                            </fo:block>
                            
                            <fo:block xsl:use-attribute-sets="__frontmatter__copyright" break-before="page">
                                <xsl:apply-templates select="//topic[@id = $noticesId]" mode="noticesTopic"/>
                            </fo:block>

                        </xsl:when>

                        <xsl:when test="//*[contains(@class, ' map/map ')]/@title">
                            <xsl:value-of select="//*[contains(@class, ' map/map ')]/@title" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of
                                select="/descendant::*[contains(@class, ' topic/topic ')][1]/*[contains(@class, ' topic/title ')]" />
                        </xsl:otherwise>
                    </xsl:choose>

                </fo:block>
                <fo:block xsl:use-attribute-sets="__frontmatter__owner">
                    <xsl:choose>
                        <xsl:when test="//*[contains(@class,' bkinfo/bkowner ')]">
                            <xsl:apply-templates select="//*[contains(@class,' bkinfo/bkowner ')]" />
                        </xsl:when>
                        <xsl:when test="//*[contains(@class,' bookmap/bookowner ')]">
                            <!-- <xsl:apply-templates
                                select="//*[contains(@class,' bookmap/bookowner ')]" /> -->
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates
                                select="$map/*[contains(@class, ' map/topicmeta ')]" />
                        </xsl:otherwise>
                    </xsl:choose>
                </fo:block>
                <xsl:call-template name="processCopyrigth" />
            </fo:flow>
        </fo:page-sequence>
    </xsl:template>

    <xsl:template match="*[contains(@class, ' bookmap/bookowner ')]">
        <fo:block-container xsl:use-attribute-sets="__frontmatter__owner__container">
            <fo:block>
                <fo:inline>
                    <xsl:value-of select="@name" />
                </fo:inline>
            </fo:block>
        </fo:block-container>
    </xsl:template>

    <xsl:template match="*[contains(@class, ' bkinfo/bkowner ')]">
        <fo:block-container xsl:use-attribute-sets="__frontmatter__owner__container">
            <fo:block>
                <fo:inline>
                    <xsl:apply-templates
                        select="*[contains(@class, ' bkinfo/organization ')]/*[contains(@class, ' bkinfo/orgname ')]" />
                </fo:inline>
                &#xA0;
                <fo:inline>
                    <xsl:apply-templates
                        select="*[contains(@class, ' bkinfo/organization ')]/*[contains(@class, ' bkinfo/address ')]" />
                </fo:inline>
            </fo:block>
        </fo:block-container>
    </xsl:template>

    <xsl:template match="*[contains(@class, ' map/topicmeta ')]">
        <fo:block-container xsl:use-attribute-sets="__frontmatter__owner__container">
            <fo:block>
                <xsl:apply-templates />
            </fo:block>
        </fo:block-container>
    </xsl:template>

    <xsl:template name="getValueOrSpace">
        <xsl:variable name="value">
            <xsl:apply-templates />
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$value != ''">
                <xsl:apply-templates />         
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>&#xA0;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="*[contains(@class, ' topic/author ')]">
        <fo:block xsl:use-attribute-sets="author">
            <xsl:apply-templates />
        </fo:block>
    </xsl:template>

    <xsl:template match="*[contains(@class, ' topic/publisher ')]">
        <fo:block xsl:use-attribute-sets="publisher">
            <xsl:apply-templates />
        </fo:block>
    </xsl:template>

    <xsl:template match="*[contains(@class, ' topic/copyright ')]">
        <fo:block xsl:use-attribute-sets="copyright">
            <xsl:apply-templates />
        </fo:block>
    </xsl:template>

    <xsl:template match="*[contains(@class, ' topic/copyryear ')]">
        <fo:inline xsl:use-attribute-sets="copyryear">
            <xsl:value-of select="@year" />
            <xsl:text> </xsl:text>
        </fo:inline>
    </xsl:template>

    <xsl:template match="*[contains(@class, ' topic/copyrholder ')]">
        <fo:inline xsl:use-attribute-sets="copyrholder">
            <xsl:apply-templates />
        </fo:inline>
    </xsl:template>

    <xsl:template match="*[contains(@class, ' bkinfo/bksubtitle ')]"
        priority="+2">
        <fo:block xsl:use-attribute-sets="__frontmatter__subtitle">
            <xsl:apply-templates />
        </fo:block>
    </xsl:template>

    <xsl:template name="processCopyrigth">
        <xsl:apply-templates select="/bookmap/*[contains(@class,' topic/topic ')]"
            mode="process-preface" />
    </xsl:template>

    <xsl:template name="processTopicAbstract">
        <fo:block xsl:use-attribute-sets="topic" page-break-before="always">
            <xsl:if test="not(ancestor::*[contains(@class, ' topic/topic ')])">
                <fo:marker marker-class-name="current-topic-number">
                    <xsl:number format="1" />
                </fo:marker>
                <fo:marker marker-class-name="current-header">
                    <xsl:for-each select="child::*[contains(@class,' topic/title ')]">
                        <xsl:call-template name="getTitle" />
                    </xsl:for-each>
                </fo:marker>
            </xsl:if>
            <fo:inline>
                <xsl:call-template name="commonattributes" />
            </fo:inline>
            <fo:inline>
                <xsl:attribute name="id">
                    <xsl:call-template name="generate-toc-id" />
                </xsl:attribute>
            </fo:inline>
            <fo:block>
                <xsl:attribute name="border-bottom">3pt solid black</xsl:attribute>
                <xsl:attribute name="space-after">16.8pt</xsl:attribute>
            </fo:block>
            <fo:block xsl:use-attribute-sets="body__toplevel">
                <xsl:apply-templates select="*[not(contains(@class, ' topic/title '))]" />
            </fo:block>
        </fo:block>
    </xsl:template>

    <xsl:template match="*[contains(@class, ' topic/topic ')]"
        mode="process-preface">
        <xsl:param name="include" select="'true'" />
        <xsl:variable name="topicType">
            <xsl:call-template name="determineTopicType" />
        </xsl:variable>
        <xsl:if test="$topicType = 'topicAbstract'">
            <xsl:call-template name="processTopicAbstract" />
        </xsl:if>
    </xsl:template>

    <xsl:template match="*[contains(@class, ' topic/prodname ')]" >
    <fo:block xsl:use-attribute-sets="__frontmatter__prodname">
      <xsl:call-template name="getValueOrSpace"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="*[contains(@class, ' topic/brand ')]" >
    <fo:block xsl:use-attribute-sets="__frontmatter__brand">
      <xsl:call-template name="getValueOrSpace"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="*[contains(@class, ' topic/component ')]" >
    <fo:block xsl:use-attribute-sets="__frontmatter__component">
      <xsl:call-template name="getValueOrSpace"/>
    </fo:block>
  </xsl:template>


  <xsl:template match="*[contains(@class, ' topic/series ')]" >
    <fo:block xsl:use-attribute-sets="__frontmatter__series">
      <xsl:call-template name="getValueOrSpace"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="*[contains(@class, ' topic/prognum ')]" >
    <fo:block xsl:use-attribute-sets="__frontmatter__prognum">
      <xsl:call-template name="getValueOrSpace"/>
    </fo:block>
  </xsl:template>

    <xsl:template mode="noticesTopic" match="node()">
        <xsl:apply-templates select="body/*[1]"/>
    </xsl:template>

    <xsl:template match="//topic[@id = $noticesId]">

    </xsl:template>    
</xsl:stylesheet>