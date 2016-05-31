<?xml version="1.0" encoding="UTF-8"?>
<!-- ######################################################################################### -->
<!--
*
*
*
-->
<!-- ######################################################################################### -->


<!-- ######################################################################################### -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
					xmlns:fo="http://www.w3.org/1999/XSL/Format"
					version="1.1">


<!-- ######################################################################################### -->
<xsl:attribute-set name="default.font">
	<xsl:attribute name="font-family">Sans</xsl:attribute>
</xsl:attribute-set>


<!-- ######################################################################################### -->
<xsl:attribute-set name="default.headline1">
	<xsl:attribute name="font-size">18pt</xsl:attribute>
	<xsl:attribute name="font-weight">bold</xsl:attribute>
</xsl:attribute-set>



    <xsl:attribute-set name="tm">
        <xsl:attribute name="border-left-width">0pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="tm__content">
        <xsl:attribute name="font-size">75%</xsl:attribute>
        <xsl:attribute name="baseline-shift">20%</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="tm__content__service">
        <xsl:attribute name="font-size">40%</xsl:attribute>
        <xsl:attribute name="baseline-shift">50%</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="author">
    </xsl:attribute-set>

    <xsl:attribute-set name="source">
    </xsl:attribute-set>

<!-- ######################################################################################### -->
<!-- Headings -->
    <xsl:attribute-set name="topic.title">
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="font-size">18pt</xsl:attribute>
        <xsl:attribute name="padding-top">-2pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="space-after">0</xsl:attribute>
        <!-- <xsl:attribute name="padding-bottom">4.5pt</xsl:attribute> -->
        <xsl:attribute name="margin-bottom">7.5pt</xsl:attribute>
        <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="topic.title__content">
        <xsl:attribute name="line-height">100%</xsl:attribute>
        <xsl:attribute name="border-left-width">0pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0pt</xsl:attribute>
        <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="topic.topic.title">
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="space-before">
            <xsl:call-template name="get-topic.topic.title-space-before"/>
        </xsl:attribute>
        <xsl:attribute name="space-after">6pt</xsl:attribute>
        <xsl:attribute name="font-size">16pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
    </xsl:attribute-set>

    <xsl:template name="get-topic.topic.title-space-before">
        <xsl:choose>
            <xsl:when test="count(../preceding-sibling::*[contains(@class, 'topic/topic')]) = 0">
                <xsl:choose>
                    <xsl:when test="../../*[contains(@class, 'topic/body')]">
                        <xsl:variable name="lastElementIsTableOrNoteOrCodeblock">                    
                            <xsl:apply-templates mode="lastElementIsTableOrNoteOrCodeblock" select="../../*[contains(@class, 'topic/body')]"/>
                        </xsl:variable>
                        <xsl:choose>
                            <xsl:when test="$lastElementIsTableOrNoteOrCodeblock = 'true'">
                                <xsl:text>17pt</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>12pt</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>12pt</xsl:text>
                    </xsl:otherwise>            
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="../preceding-sibling::*[1]/*[contains(@class, 'topic/body')]">
                        <xsl:variable name="lastElementIsTableOrNoteOrCodeblock">                    
                            <xsl:apply-templates mode="lastElementIsTableOrNoteOrCodeblock" select="../preceding-sibling::*[1]/*[contains(@class, 'topic/body')]"/>
                        </xsl:variable>
                        <xsl:choose>
                            <xsl:when test="$lastElementIsTableOrNoteOrCodeblock = 'true'">
                                <xsl:text>17pt</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>12pt</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>12pt</xsl:text>
                    </xsl:otherwise>            
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
   </xsl:template>

    <xsl:template match="*" mode="lastElementIsTableOrNoteOrCodeblock">
        <xsl:choose>
            <xsl:when test="not(child::*)">
                <xsl:value-of select="'false'" />
            </xsl:when>
            <xsl:when test="child::*[last()]/contains(@class, 'topic/table') or contains(@class, 'topic/note') or contains(@class, 'pr-d/codeblock')">
                <xsl:value-of select="'true'" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates mode="lastElementIsTableOrNoteOrCodeblock" select="child::*[last()]"/>
            </xsl:otherwise>
        </xsl:choose>   
    </xsl:template>

    <xsl:attribute-set name="topic.topic.title__content">
        <xsl:attribute name="border-left-width">0pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="topic.topic.topic.title">
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="space-before">12pt</xsl:attribute>
        <xsl:attribute name="space-after">6pt</xsl:attribute>
        <xsl:attribute name="font-size">14pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="topic.topic.topic.title__content">
    </xsl:attribute-set>

    <xsl:attribute-set name="topic.topic.topic.topic.title">
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="space-before">12pt</xsl:attribute>
        <xsl:attribute name="space-after">6pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="topic.topic.topic.topic.title__content">
    </xsl:attribute-set>

    <xsl:attribute-set name="topic.topic.topic.topic.topic.title">
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="margin-left">25pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="topic.topic.topic.topic.topic.title__content">
    </xsl:attribute-set>

    <xsl:attribute-set name="topic.topic.topic.topic.topic.topic.title">
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="margin-left">25pt</xsl:attribute>
        <xsl:attribute name="font-style">italic</xsl:attribute>
        <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="topic.topic.topic.topic.topic.topic.title__content">
    </xsl:attribute-set>

<!-- ######################################################################################### -->
	
	<xsl:attribute-set name="section.title">
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="space-before">0</xsl:attribute>
        <xsl:attribute name="space-after">6pt</xsl:attribute>
        <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
	</xsl:attribute-set>

    <!-- <xsl:template name="get-section.title-space-before">
        <xsl:choose>
            <xsl:when test="../preceding-sibling::*[1]">
                <xsl:variable name="lastElementIsTableOrNoteOrCodeblock">                    
                    <xsl:apply-templates mode="lastElementIsTableOrNoteOrCodeblock" select="../preceding-sibling::*[1]"/>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$lastElementIsTableOrNoteOrCodeblock = 'true'">
                        <xsl:text>15pt</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>12pt</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>15pt</xsl:text>
            </xsl:otherwise>            
        </xsl:choose>
    </xsl:template> -->
    
    <xsl:attribute-set name="example.title">
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
        <xsl:attribute name="space-after">5pt</xsl:attribute>
        <xsl:attribute name="width">435.35pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="fig">
        <xsl:attribute name="keep-with-previous.within-page">always</xsl:attribute>
        <xsl:attribute name="keep-together.within-page">always</xsl:attribute>
   </xsl:attribute-set>

<!-- ######################################################################################### -->
<!-- Caption -->
    <xsl:attribute-set name="fig.title">
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
        <xsl:attribute name="space-before">0pt</xsl:attribute>
        <xsl:attribute name="space-after">6pt</xsl:attribute>
        <xsl:attribute name="keep-with-previous.within-page">always</xsl:attribute>
    </xsl:attribute-set>

<!-- ######################################################################################### -->

    <xsl:attribute-set name="topic">
        <xsl:attribute name="font-size">10pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="titlealts">
        <xsl:attribute name="background-color">#f0f0d0</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-color">black</xsl:attribute>
        <xsl:attribute name="border-width">thin</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="navtitle">
        <xsl:attribute name="font-family">Sans</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="navtitle__label">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="searchtitle">
    </xsl:attribute-set>

    <xsl:attribute-set name="searchtitle__label">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="body__toplevel">
        <xsl:attribute name="margin-left">0pt</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="body__secondLevel">
        <xsl:attribute name="margin-left">0pt</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>

    </xsl:attribute-set>

    <xsl:attribute-set name="body">
        <xsl:attribute name="margin-left">0pt</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="shortdesc">
    </xsl:attribute-set>

    <xsl:attribute-set name="topic__shortdesc" use-attribute-sets="body">
    </xsl:attribute-set>

    <xsl:attribute-set name="section" use-attribute-sets="base-font">
        <xsl:attribute name="line-height">12pt</xsl:attribute>
        <xsl:attribute name="space-before">
            <xsl:call-template name="get-section-space-before"/>
        </xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:template name="get-section-space-before">
        <xsl:choose>
            <xsl:when test="title">
                <xsl:choose>
                    <xsl:when test="preceding-sibling::*[1]">
                        <xsl:variable name="lastElementIsTableOrNoteOrCodeblock">
                            <xsl:apply-templates mode="lastElementIsTableOrNoteOrCodeblock" select="preceding-sibling::*[1]"/>
                        </xsl:variable>
                        <xsl:choose>
                            <xsl:when test="$lastElementIsTableOrNoteOrCodeblock = 'true'">
                                <xsl:text>15pt</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>12pt</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:when test="not(preceding-sibling::*) and contains(../@class, ' topic/body ')">
                        <xsl:text>15pt</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>12pt</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>0pt</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    

<!-- ######################################################################################### -->
<!-- Formatting of examples -->

    <xsl:attribute-set name="example">
        <!-- xsl:attribute name="start-indent">45pt</xsl:attribute -->
        <!-- xsl:attribute name="line-height">12pt</xsl:attribute -->
        <!-- xsl:attribute name="space-before">0.6em</xsl:attribute -->
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <!-- xsl:attribute name="margin-left">0.5in</xsl:attribute -->
        <!-- xsl:attribute name="margin-right">0.5in</xsl:attribute -->
        <!-- xsl:attribute name="border">thin solid black</xsl:attribute -->
        <!-- xsl:attribute name="padding">5pt</xsl:attribute -->
        <!-- xsl:attribute name="width">435.35pt</xsl:attribute -->
    </xsl:attribute-set>
    
    <!-- <xsl:attribute-set name="example.fujitsu" use-attribute-sets="base-font">
        <xsl:attribute name="line-height"><xsl:value-of select="$default-line-height"/></xsl:attribute>
        <xsl:attribute name="space-before">0.6em</xsl:attribute>
        <xsl:attribute name="padding">5pt</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
    </xsl:attribute-set> -->

<!-- ######################################################################################### -->

    <xsl:attribute-set name="desc">
        <xsl:attribute name="border-left-width">0pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="prolog">
        <xsl:attribute name="start-indent">72pt</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
    </xsl:attribute-set>

<!-- ######################################################################################### -->
<!-- Paragraph -->
    <xsl:attribute-set name="p">
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="text-indent">0em</xsl:attribute>
        <xsl:attribute name="space-before">3pt</xsl:attribute>
        <xsl:attribute name="space-after">3pt</xsl:attribute>
    </xsl:attribute-set>

<!-- ######################################################################################### -->

    <xsl:attribute-set name="lq">
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="space-before">10pt</xsl:attribute>
        <xsl:attribute name="padding-left">6pt</xsl:attribute>
        <xsl:attribute name="start-indent">92pt</xsl:attribute>
        <xsl:attribute name="end-indent">92pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-color">black</xsl:attribute>
        <xsl:attribute name="border-width">thin</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="lq_simple">
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="space-before">10pt</xsl:attribute>
        <xsl:attribute name="space-after">10pt</xsl:attribute>
        <xsl:attribute name="padding-left">6pt</xsl:attribute>
        <xsl:attribute name="start-indent">92pt</xsl:attribute>
        <xsl:attribute name="end-indent">92pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-color">black</xsl:attribute>
        <xsl:attribute name="border-width">thin</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="lq_link">
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="space-after">10pt</xsl:attribute>
        <xsl:attribute name="end-indent">92pt</xsl:attribute>
        <xsl:attribute name="text-align">right</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="color">black</xsl:attribute>
        <xsl:attribute name="font-style">italic</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="lq_title">
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="space-after">10pt</xsl:attribute>
        <xsl:attribute name="end-indent">92pt</xsl:attribute>
        <xsl:attribute name="text-align">right</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-style">italic</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="q">
        <xsl:attribute name="border-left-width">0pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="figgroup">
        <xsl:attribute name="border-left-width">0pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0pt</xsl:attribute>
    </xsl:attribute-set>

<!-- ######################################################################################### -->
<!-- Formatting of Notes -->

    <xsl:attribute-set name="note" use-attribute-sets="common.block">
        <xsl:attribute name="margin-top">3pt</xsl:attribute>
        <xsl:attribute name="space-after">3pt</xsl:attribute>
		<xsl:attribute name="space-before">3pt</xsl:attribute>
		<xsl:attribute name="margin-bottom">3pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="note__table" use-attribute-sets="common.block">
        <xsl:attribute name="space-before">6pt</xsl:attribute>
        <xsl:attribute name="space-after">6pt</xsl:attribute>
        <xsl:attribute name="background-color">#E5E5E5</xsl:attribute>
        <xsl:attribute name="border-top-width">1pt</xsl:attribute>
        <xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0</xsl:attribute>
        <xsl:attribute name="border-left-width">0</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-color">black</xsl:attribute>
        <xsl:attribute name="start-indent">0pt</xsl:attribute>
        <xsl:attribute name="keep-together.within-page">always</xsl:attribute>
        <!--<xsl:attribute name="width">435.35pt</xsl:attribute>-->
    </xsl:attribute-set>
  
    <xsl:attribute-set name="note__image__column">
        <xsl:attribute name="column-number">1</xsl:attribute>
        <xsl:attribute name="column-width">32pt</xsl:attribute>
    </xsl:attribute-set>
  
    <xsl:attribute-set name="note__text__column">
      <xsl:attribute name="column-number">2</xsl:attribute>
    </xsl:attribute-set>  

    <xsl:attribute-set name="note__image__entry">
        <xsl:attribute name="padding-right">5pt</xsl:attribute>
        <xsl:attribute name="start-indent">0pt</xsl:attribute>
        <xsl:attribute name="margin-top">6pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="note__text__entry">
        <xsl:attribute name="start-indent">0pt</xsl:attribute>
        <xsl:attribute name="padding-right">3pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="note__label">
        <xsl:attribute name="border-left-width">0pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>



<!-- ######################################################################################### -->

   <xsl:attribute-set name="note__label__note">
    </xsl:attribute-set>

    <xsl:attribute-set name="note__label__tip">
    </xsl:attribute-set>

    <xsl:attribute-set name="note__label__fastpath">
    </xsl:attribute-set>

    <xsl:attribute-set name="note__label__restriction">
    </xsl:attribute-set>

    <xsl:attribute-set name="note__label__important">
    </xsl:attribute-set>

    <xsl:attribute-set name="note__label__remember">
    </xsl:attribute-set>

    <xsl:attribute-set name="note__label__attention">
    </xsl:attribute-set>

    <xsl:attribute-set name="note__label__caution">
    </xsl:attribute-set>

    <xsl:attribute-set name="note__label__danger">
    </xsl:attribute-set>

    <xsl:attribute-set name="note__label__other">
    </xsl:attribute-set>

    <xsl:attribute-set name="pre">
        <xsl:attribute name="space-before">1.2em</xsl:attribute>
        <xsl:attribute name="space-after">0.8em</xsl:attribute>
        <xsl:attribute name="white-space-treatment">preserve</xsl:attribute>
        <xsl:attribute name="white-space-collapse">false</xsl:attribute>
        <xsl:attribute name="linefeed-treatment">preserve</xsl:attribute>
        <xsl:attribute name="wrap-option">wrap</xsl:attribute>
        <xsl:attribute name="background-color">#f0f0f0</xsl:attribute>
        <xsl:attribute name="font-family">Monospaced</xsl:attribute>
        <xsl:attribute name="line-height">106%</xsl:attribute>
        <xsl:attribute name="font-size">9pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__spectitle">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__border__top">
        <xsl:attribute name="border-top-color">black</xsl:attribute>
        <xsl:attribute name="border-top-width">thin</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__border__bot">
        <xsl:attribute name="border-bottom-color">black</xsl:attribute>
        <xsl:attribute name="border-bottom-width">thin</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__border__sides">
        <xsl:attribute name="border-left-color">black</xsl:attribute>
        <xsl:attribute name="border-left-width">thin</xsl:attribute>
        <xsl:attribute name="border-right-color">black</xsl:attribute>
        <xsl:attribute name="border-right-width">thin</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__border__all">
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-color">black</xsl:attribute>
        <xsl:attribute name="border-width">thin</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="lines">
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="space-before">0.8em</xsl:attribute>
        <xsl:attribute name="space-after">0.8em</xsl:attribute>
<!--        <xsl:attribute name="white-space-treatment">ignore-if-after-linefeed</xsl:attribute>-->
        <xsl:attribute name="white-space-collapse">true</xsl:attribute>
        <xsl:attribute name="linefeed-treatment">preserve</xsl:attribute>
        <xsl:attribute name="wrap-option">wrap</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="keyword">
        <xsl:attribute name="border-left-width">0pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="term">
        <xsl:attribute name="border-left-width">0pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0pt</xsl:attribute>
        <xsl:attribute name="font-style">italic</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="ph">
        <xsl:attribute name="border-left-width">0pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="boolean">
        <xsl:attribute name="border-left-width">0pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0pt</xsl:attribute>
        <xsl:attribute name="color">black</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="state">
        <xsl:attribute name="border-left-width">0pt</xsl:attribute>
        <xsl:attribute name="border-right-width">0pt</xsl:attribute>
        <xsl:attribute name="color">red</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="alt">
    </xsl:attribute-set>

    <xsl:attribute-set name="object">
    </xsl:attribute-set>

    <xsl:attribute-set name="param">
    </xsl:attribute-set>

    <xsl:attribute-set name="draft-comment">
        <xsl:attribute name="background-color">#FF99FF</xsl:attribute>
        <xsl:attribute name="color">#CC3333</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-color">black</xsl:attribute>
        <xsl:attribute name="border-width">thin</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="draft-comment__label">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="required-cleanup">
        <xsl:attribute name="background">yellow</xsl:attribute>
        <xsl:attribute name="color">#CC3333</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="required-cleanup__label">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="fn">
        <xsl:attribute name="font-size">8pt</xsl:attribute>
        <xsl:attribute name="color">purple</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="fn__id">
        <xsl:attribute name="font-style">italic</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="fn__callout">
        <xsl:attribute name="keep-with-previous.within-line">always</xsl:attribute>
        <xsl:attribute name="baseline-shift">super</xsl:attribute>
        <xsl:attribute name="font-size">75%</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="fn__body">
        <xsl:attribute name="provisional-distance-between-starts">8mm</xsl:attribute>
        <xsl:attribute name="provisional-label-separation">2mm</xsl:attribute>
        <xsl:attribute name="line-height">1.2</xsl:attribute>
        <xsl:attribute name="font-size">9pt</xsl:attribute>
        <xsl:attribute name="start-indent">0pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__align__left">
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="display-align">before</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__align__right">
        <xsl:attribute name="text-align">right</xsl:attribute>
        <xsl:attribute name="display-align">before</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__align__center">
        <xsl:attribute name="text-align">center</xsl:attribute>
        <xsl:attribute name="display-align">before</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__align__justify">
        <xsl:attribute name="text-align">justify</xsl:attribute>
        <xsl:attribute name="display-align">before</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="indextermref">
        <xsl:attribute name="font-style">italic</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="cite">
        <xsl:attribute name="font-style">italic</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="concept">
    </xsl:attribute-set>

    <xsl:attribute-set name="conbody">
    </xsl:attribute-set>

    <xsl:attribute-set name="topichead">
    </xsl:attribute-set>

    <xsl:attribute-set name="topicgroup">
    </xsl:attribute-set>

    <xsl:attribute-set name="topicmeta">
    </xsl:attribute-set>

    <xsl:attribute-set name="searchtitle">
    </xsl:attribute-set>

    <xsl:attribute-set name="searchtitle__label">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="publisher">
    </xsl:attribute-set>

    <xsl:attribute-set name="copyright">
    </xsl:attribute-set>

    <xsl:attribute-set name="copyryear">
    </xsl:attribute-set>

    <xsl:attribute-set name="copyrholder">
    </xsl:attribute-set>

    <xsl:attribute-set name="critdates">
    </xsl:attribute-set>

    <xsl:attribute-set name="created">
    </xsl:attribute-set>

    <xsl:attribute-set name="revised">
    </xsl:attribute-set>

    <xsl:attribute-set name="permissions">
    </xsl:attribute-set>

    <xsl:attribute-set name="category">
    </xsl:attribute-set>

    <xsl:attribute-set name="audience">
    </xsl:attribute-set>

    <xsl:attribute-set name="keywords">
    </xsl:attribute-set>

    <xsl:attribute-set name="prodinfo">
    </xsl:attribute-set>

    <xsl:attribute-set name="prodname">
    </xsl:attribute-set>

    <xsl:attribute-set name="vrmlist">
    </xsl:attribute-set>

    <xsl:attribute-set name="vrm">
    </xsl:attribute-set>

    <xsl:attribute-set name="brand">
    </xsl:attribute-set>

    <xsl:attribute-set name="series">
    </xsl:attribute-set>

    <xsl:attribute-set name="platform">
    </xsl:attribute-set>

    <xsl:attribute-set name="prognum">
    </xsl:attribute-set>

    <xsl:attribute-set name="featnum">
    </xsl:attribute-set>

    <xsl:attribute-set name="component">
    </xsl:attribute-set>

    <xsl:attribute-set name="othermeta">
    </xsl:attribute-set>

    <xsl:attribute-set name="resourceid">
    </xsl:attribute-set>

    <xsl:attribute-set name="reference">
        <!-- <xsl:attribute name="margin-bottom">3.5pt</xsl:attribute> -->
        <!-- <xsl:attribute name="space-after">3.5pt</xsl:attribute> -->
    </xsl:attribute-set>

    <xsl:attribute-set name="refbody">
    </xsl:attribute-set>

    <xsl:attribute-set name="refsyn">
    </xsl:attribute-set>

    <xsl:attribute-set name="task">
        <!-- <xsl:attribute name="padding-bottom">
            <xsl:choose>
                <xsl:when test="following-sibling::*[1][contains(@class, ' task/task ')]">
                    <xsl:value-of select="'-20pt'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'0'"/>
                </xsl:otherwise>

                <xsl:when test="not(false)">
                    <xsl:message>11111111111</xsl:message>
                    <xsl:message>
                        <xsl:value-of select="following-sibling::*[1]/@class"/>
                    </xsl:message>
                    <xsl:message>11111111111</xsl:message>
                    <xsl:value-of select="'0'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'1pt'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>-->
    </xsl:attribute-set> 

    <xsl:attribute-set name="taskbody">
    </xsl:attribute-set>

    <xsl:attribute-set name="prereq">
    </xsl:attribute-set>

    <xsl:attribute-set name="context">
    </xsl:attribute-set>

    <xsl:attribute-set name="cmd">
    </xsl:attribute-set>

    <xsl:attribute-set name="info">
        <xsl:attribute name="space-before">3pt</xsl:attribute>
        <xsl:attribute name="space-after">3pt</xsl:attribute>
        <xsl:attribute name="keep-with-previous.within-page">always</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="tutorialinfo">
    </xsl:attribute-set>

    <xsl:attribute-set name="stepresult">
        <xsl:attribute name="keep-with-previous.within-page">always</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="result">
    </xsl:attribute-set>

    <xsl:attribute-set name="postreq">
    </xsl:attribute-set>

    <xsl:attribute-set name="stepxmp">
        <xsl:attribute name="keep-with-previous.within-page">always</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="metadata">
    </xsl:attribute-set>

    <xsl:attribute-set name="image__float">
    </xsl:attribute-set>

    <xsl:attribute-set name="image__block">
    </xsl:attribute-set>

    <xsl:attribute-set name="image__inline">
    </xsl:attribute-set>

    <xsl:attribute-set name="image">
        <xsl:attribute name="keep-with-next.within-page">always</xsl:attribute>
        <xsl:attribute name="padding-top">12pt</xsl:attribute>
  		<xsl:attribute name="padding-bottom">12pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__unresolved__conref">
        <xsl:attribute name="color">#CC3333</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__fo__root">
        <xsl:attribute name="font-family">Sans</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="__force__page__count">
        <xsl:attribute name="force-page-count">
            <xsl:choose>
                <xsl:when test="name(/*) = 'bookmap'">
                    <xsl:value-of select="'auto'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'auto'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:attribute-set>
    
    
    

    <!-- Attributes for definition lists and their entries-->    
        
    <xsl:attribute-set name="dl">
    </xsl:attribute-set>

    <xsl:attribute-set name="dlentry">
    </xsl:attribute-set>

    <xsl:attribute-set name="dt">
        <xsl:attribute name="space-before">3pt</xsl:attribute>
        <xsl:attribute name="space-after">3pt</xsl:attribute>
	<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="dd">
        <xsl:attribute name="space-before">3pt</xsl:attribute>
        <xsl:attribute name="space-after">3pt</xsl:attribute>
	<xsl:attribute name="margin-left">5mm</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="dl__body">
    </xsl:attribute-set>

    <xsl:attribute-set name="dl.dlhead">
    </xsl:attribute-set>

    <xsl:attribute-set name="dlentry.dt">
    </xsl:attribute-set>

    <xsl:attribute-set name="dlentry.dt__content">
    </xsl:attribute-set>

    <xsl:attribute-set name="dlentry.dd">
    </xsl:attribute-set>

    <xsl:attribute-set name="dlentry.dd__content">
    </xsl:attribute-set>

    <xsl:attribute-set name="dl.dlhead__row">
    </xsl:attribute-set>

    <xsl:attribute-set name="dlhead.dthd__cell">
    </xsl:attribute-set>

    <xsl:attribute-set name="dlhead.dthd__content">
    </xsl:attribute-set>

    <xsl:attribute-set name="dlhead.ddhd__cell">
    </xsl:attribute-set>

    <xsl:attribute-set name="dlhead.ddhd__content">
    </xsl:attribute-set>


</xsl:stylesheet>