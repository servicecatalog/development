<?xml version='1.0'?>


<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    version="1.0">

  <xsl:attribute-set name="linklist.title">
    <!--       <xsl:attribute name="font-weight">bold</xsl:attribute>   -->
    <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
  </xsl:attribute-set>

  <!--Common-->
  <xsl:attribute-set name="li.itemgroup">
    <xsl:attribute name="space-after">3pt</xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
  </xsl:attribute-set>

  <!--Unordered list-->
  <xsl:attribute-set name="ul">
    <xsl:attribute name="provisional-distance-between-starts">5mm</xsl:attribute>
    <xsl:attribute name="provisional-label-separation">1mm</xsl:attribute>
    <xsl:attribute name="space-after">
      <xsl:call-template name="getListSpaceAfter"/>
    </xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
    <!--        <xsl:attribute name="margin-left">-8pt</xsl:attribute>-->
    <xsl:attribute name="keep-with-previous.within-page">always</xsl:attribute>
    <xsl:attribute name="orphans">2</xsl:attribute>
    <xsl:attribute name="widows">2</xsl:attribute>
  </xsl:attribute-set>

  <xsl:template name="getListSpaceAfter">
    <xsl:variable name="lastElementIsP">
      <xsl:apply-templates mode="lastElementIsP" select="."/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$lastElementIsP = 'true'">
        <xsl:text>3pt</xsl:text>
      </xsl:when>
      <xsl:when test="ancestor::*[contains(@class, 'topic/li')]">
        <xsl:text>3pt</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>6pt</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="*" mode="lastElementIsP">
    <xsl:choose>
      <xsl:when test="not(child::*)">
        <xsl:value-of select="'false'" />
      </xsl:when>
      <xsl:when test="child::*[last()]/name() = 'p'">
        <xsl:value-of select="'true'" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates mode="lastElementIsP" select="child::*[last()]"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:attribute-set name="ul.li">
    <xsl:attribute name="space-after">3pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">
      <xsl:call-template name="getLiSpaceAfter"/>
    </xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:template name="getLiSpaceAfter">    
    <xsl:choose>
      <xsl:when test="ancestor::*[contains(@class, 'topic/note')] and (position() = 2 or position() = 1)" >
        <xsl:text>-5pt</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>0pt</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:attribute-set name="ul.li__label">
    <xsl:attribute name="keep-together.within-line">always</xsl:attribute>
    <xsl:attribute name="keep-with-next.within-line">always</xsl:attribute>
    <xsl:attribute name="end-indent">label-end()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="ul.li__label__content">
    <xsl:attribute name="text-align">left</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="ul.li__body">
    <xsl:attribute name="start-indent">body-start()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="ul.li__content">
  </xsl:attribute-set>

  <!--Ordered list-->
  <xsl:attribute-set name="ol">
    <xsl:attribute name="provisional-distance-between-starts">5mm</xsl:attribute>
    <xsl:attribute name="provisional-label-separation">1mm</xsl:attribute>
    <xsl:attribute name="space-after">
      <xsl:call-template name="getListSpaceAfter"/>
    </xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
    <!--		<xsl:attribute name="margin-left">-8pt</xsl:attribute>-->
    <xsl:attribute name="keep-with-previous.within-page">always</xsl:attribute>
    <xsl:attribute name="orphans">2</xsl:attribute>
    <xsl:attribute name="widows">2</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="ol.li">
    <xsl:attribute name="space-after">3pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">
      <xsl:call-template name="getLiSpaceAfter"/>
    </xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="ol.li__label">
    <xsl:attribute name="keep-together.within-line">always</xsl:attribute>
    <xsl:attribute name="keep-with-next.within-line">always</xsl:attribute>
    <xsl:attribute name="end-indent">label-end()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="ol.li__label__content">
    <xsl:attribute name="text-align">left</xsl:attribute>
    <!--   <xsl:attribute name="font-weight">bold</xsl:attribute>   -->
  </xsl:attribute-set>

  <xsl:attribute-set name="ol.li__body">
    <xsl:attribute name="start-indent">body-start()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="ol.li__content">
  </xsl:attribute-set>

  <!--Simple list-->
  <xsl:attribute-set name="sl">
    <xsl:attribute name="provisional-distance-between-starts">5mm</xsl:attribute>
    <xsl:attribute name="provisional-label-separation">1mm</xsl:attribute>
    <xsl:attribute name="space-after">9pt</xsl:attribute>
    <xsl:attribute name="space-before">9pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="sl.sli">
    <xsl:attribute name="space-after">1.5pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">
      <xsl:call-template name="getLiSpaceAfter"/>
    </xsl:attribute>
    <xsl:attribute name="space-before">1.5pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="sl.sli__label">
    <xsl:attribute name="keep-together.within-line">always</xsl:attribute>
    <xsl:attribute name="keep-with-next.within-line">always</xsl:attribute>
    <xsl:attribute name="end-indent">label-end()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="sl.sli__label__content">
    <xsl:attribute name="text-align">left</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="sl.sli__body">
    <xsl:attribute name="start-indent">body-start()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="sl.sli__content">
  </xsl:attribute-set>

  <!--Unordered steps-->
  <xsl:attribute-set name="steps-unordered">
    <xsl:attribute name="provisional-distance-between-starts">5mm</xsl:attribute>
    <xsl:attribute name="provisional-label-separation">1mm</xsl:attribute>
    <xsl:attribute name="space-after">3pt</xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
    <xsl:attribute name="keep-with-previous.within-page">always</xsl:attribute>
    <xsl:attribute name="orphans">2</xsl:attribute>
    <xsl:attribute name="widows">2</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="steps-unordered.step">
    <xsl:attribute name="space-after">3pt</xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="steps-unordered.step__label">
    <xsl:attribute name="keep-together.within-line">always</xsl:attribute>
    <xsl:attribute name="keep-with-next.within-line">always</xsl:attribute>
    <xsl:attribute name="end-indent">label-end()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="steps-unordered.step__label__content">
    <xsl:attribute name="text-align">left</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="steps-unordered.step__body">
    <xsl:attribute name="start-indent">body-start()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="steps-unordered.step__content">
  </xsl:attribute-set>

  <!--Ordered steps-->
  <xsl:attribute-set name="steps">
    <xsl:attribute name="provisional-distance-between-starts">5mm</xsl:attribute>
    <xsl:attribute name="provisional-label-separation">1mm</xsl:attribute>
    <xsl:attribute name="space-after">
      <xsl:call-template name="getListSpaceAfter"/>
    </xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
    <xsl:attribute name="keep-with-previous.within-page">always</xsl:attribute>
    <xsl:attribute name="orphans">2</xsl:attribute>
    <xsl:attribute name="widows">2</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="steps.step">
    <xsl:attribute name="space-after">3pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">
      <xsl:call-template name="getLiSpaceAfter"/>
    </xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
  </xsl:attribute-set>


  <xsl:attribute-set name="steps.step__label">
    <xsl:attribute name="keep-together.within-line">always</xsl:attribute>
    <xsl:attribute name="keep-with-next.within-line">always</xsl:attribute>
    <xsl:attribute name="end-indent">label-end()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="steps.step__label__content">
    <xsl:attribute name="text-align">left</xsl:attribute>
    <!--   <xsl:attribute name="font-weight">bold</xsl:attribute>   -->
  </xsl:attribute-set>

  <xsl:attribute-set name="steps.step__body">
    <xsl:attribute name="start-indent">body-start()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="steps.step__content">
  </xsl:attribute-set>

  <!--Substeps-->
  <xsl:attribute-set name="substeps">
    <xsl:attribute name="provisional-distance-between-starts">5mm</xsl:attribute>
    <xsl:attribute name="provisional-label-separation">1mm</xsl:attribute>
    <xsl:attribute name="space-after">3pt</xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
    <xsl:attribute name="keep-with-previous.within-page">always</xsl:attribute>
    <xsl:attribute name="orphans">2</xsl:attribute>
    <xsl:attribute name="widows">2</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="substeps.substep">
    <xsl:attribute name="space-after">3pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">
      <xsl:call-template name="getLiSpaceAfter"/>
    </xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="substeps.substep__label">
    <xsl:attribute name="keep-together.within-line">always</xsl:attribute>
    <xsl:attribute name="keep-with-next.within-line">always</xsl:attribute>
    <xsl:attribute name="end-indent">label-end()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="substeps.substep__label__content">
    <xsl:attribute name="text-align">left</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="substeps.substep__body">
    <xsl:attribute name="start-indent">body-start()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="substeps.substep__content">
  </xsl:attribute-set>

  <!--Choices-->
  <xsl:attribute-set name="choices">
    <xsl:attribute name="provisional-distance-between-starts">5mm</xsl:attribute>
    <xsl:attribute name="provisional-label-separation">1mm</xsl:attribute>
    <xsl:attribute name="space-after">3pt</xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
    <!--        <xsl:attribute name="margin-left">-8pt</xsl:attribute>-->
    <xsl:attribute name="keep-with-previous.within-page">always</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="choices.choice">
    <xsl:attribute name="space-after">3pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">
      <xsl:call-template name="getLiSpaceAfter"/>
    </xsl:attribute>
    <xsl:attribute name="space-before">3pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="choices.choice__label">
    <xsl:attribute name="keep-together.within-line">always</xsl:attribute>
    <xsl:attribute name="keep-with-next.within-line">always</xsl:attribute>
    <xsl:attribute name="end-indent">label-end()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="choices.choice__label__content">
    <xsl:attribute name="text-align">left</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="choices.choice__body">
    <xsl:attribute name="start-indent">body-start()</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="choices.choice__content">
  </xsl:attribute-set>

</xsl:stylesheet>
