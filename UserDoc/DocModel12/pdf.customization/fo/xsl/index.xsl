<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:opentopic-index="http://www.idiominc.com/opentopic/index">
    
    <xsl:import href="base_common_clone/dita-utilities.xsl"/>
    
    <xsl:template name="createIndex">
        <xsl:apply-templates select="/" mode="generated-matter"/>
    </xsl:template>

    <xsl:template match="/" mode="generated-matter">
        <xsl:if test="count(//opentopic-index:index.entry) > 0">
            <fo:page-sequence master-reference="common-page-with-columns">
                
                <xsl:call-template name="insertStaticContents">
                    <xsl:with-param name="pagetype" select="'index'"/>
                </xsl:call-template>

                <fo:flow flow-name="xsl-region-body">
                    <fo:block xsl:use-attribute-sets="topic">
                        <xsl:call-template name="commonattributes"/>
                        <fo:marker marker-class-name="current-topic-number">
                        </fo:marker>
                        <fo:marker marker-class-name="current-header">
                            Index
                        </fo:marker>                    
                    </fo:block>
                
                    <fo:block id="_OPENTOPIC_TOC_PROCESSING_INDEX">
                    </fo:block>
                
                    <fo:block xsl:use-attribute-sets="__index__label" span="all">
                        <xsl:call-template name="insertVariable">
                            <xsl:with-param name="theVariableID" select="'header.index'"/>
                        </xsl:call-template>
                    </fo:block>
                    
                    <fo:block>
                        <xsl:apply-templates select="/" mode="index"/>
                    </fo:block>
                    
                </fo:flow>
            </fo:page-sequence>
        </xsl:if>
	</xsl:template>
	
	<xsl:template match="/" mode="index">        
        <xsl:variable name="indexterms" select="//opentopic-index:index.entry[not(ancestor::opentopic-index:index.group)][not(contains(../name(),'opentopic-index:index.entry'))]"/>
		<xsl:variable name="alphabet">
			<xsl:for-each select="$indexterms">
				<xsl:sort select="normalize-space(@value)" case-order="upper-first"/>
				<xsl:variable name="pos" select="position()"/>
				<xsl:variable name="curText" select="upper-case(string(normalize-space(@value)))"/>
				<xsl:choose>
					<xsl:when test="$pos = 1">
						<xsl:value-of select="substring($curText,1,1)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:for-each select="$indexterms">
							<xsl:sort select="normalize-space(@value)" case-order="upper-first"/>
							<xsl:variable name="nextpos" select="position()"/>
							<xsl:variable name="nextText" select="upper-case(string(normalize-space(@value)))"/>
							<xsl:if test="$nextpos = ($pos - 1) and not(substring($curText, 1, 1) = substring($nextText, 1, 1))">
								<xsl:value-of select="substring($curText,1,1)"/>
							</xsl:if>
						</xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:variable>
		<xsl:call-template name="searchIndexes">
			<xsl:with-param name="letters" select="upper-case(string($alphabet))"/>
		</xsl:call-template>
	</xsl:template>


	<xsl:template match="*[contains(@class,' bookmap/indexlist ')]" mode="alphabet">
		<xsl:element name="indexterm">
			<xsl:value-of select="normalize-space(text())"/>
		</xsl:element>
	</xsl:template>

    <xsl:template match="//opentopic-index:index.entry[not(ancestor::opentopic-index:index.group)]">
        <xsl:variable name="id">
            <xsl:value-of select="generate-id()" />
        </xsl:variable>
        <fo:inline id="INDEXTERM_{$id}">
            <xsl:value-of select="@value"/>
            <xsl:apply-templates/>
        </fo:inline>
    </xsl:template>

    <!-- <xsl:template match="//opentopic-index:index.entry[not(ancestor::opentopic-index:index.group)]/opentopic-index:formatted-value/text()"/>     -->

	<xsl:template name="searchIndexes">
		<xsl:param name="letters" select="''"/>
		<xsl:variable name="letter" select="substring($letters,1,1)"/>
		<xsl:if test="not($letter = '')">
            
            <xsl:variable name="indexterms"
                select="//opentopic-index:index.entry[not(ancestor::opentopic-index:index.group)][not(contains(../name(),'opentopic-index:index.entry'))][starts-with(upper-case(normalize-space(@value)), $letter)]"/>
			
            <xsl:if test="count($indexterms) > 0">
				<fo:block xsl:use-attribute-sets="index.entry">
					<fo:block xsl:use-attribute-sets="__index__letter-group">
						<xsl:value-of select="upper-case($letter)"/>
					</fo:block>
					<xsl:for-each select="$indexterms">
						<xsl:sort order="ascending" case-order="upper-first" select="normalize-space(@value)"/>
						<xsl:variable name="pos" select="position()"/>
						<xsl:variable name="currentText" select="upper-case(normalize-space(@value))"/>
						<xsl:variable name="primary">
							<xsl:choose>
								<xsl:when test="$pos = 1">
									<xsl:value-of select="upper-case(normalize-space(@value))"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="not(upper-case(normalize-space($indexterms[$pos - 1]/@value)) = $currentText)">
										<xsl:value-of select="upper-case(normalize-space(@value))"/>
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:if test="not($primary = '')">
							<fo:block>
								<fo:inline>
									<xsl:value-of select="normalize-space(@value)"/>
								</fo:inline>

                                <xsl:for-each
                                    select="//opentopic-index:index.entry[not(ancestor::opentopic-index:index.group)][not(contains(../name(),'opentopic-index:index.entry'))][upper-case(normalize-space(@value)) = $primary][not(.//*[contains(name(),'opentopic-index:index.entry')]) or (normalize-space(string(.//*[contains(name(),'opentopic-index:index.entry')][1]/@value))) = '']">
									<xsl:variable name="id">
										<xsl:value-of select="generate-id()"/>
									</xsl:variable>
									<xsl:text>, </xsl:text>
									<fo:basic-link internal-destination="INDEXTERM_{$id}">
										<fo:page-number-citation ref-id="INDEXTERM_{$id}"/>
									</fo:basic-link>
								</xsl:for-each>

                                <xsl:for-each
                                    select="//opentopic-index:index.entry[not(ancestor::opentopic-index:index.group)][not(contains(../name(),'opentopic-index:index.entry'))][upper-case(normalize-space(@value)) = $primary]//*[contains(name(),'opentopic-index:index.entry')][not(normalize-space(@value)='')]">

									<fo:block xsl:use-attribute-sets="index.entry__content">
										<xsl:variable name="id">
											<xsl:value-of select="generate-id()"/>
										</xsl:variable>
										<fo:inline>
											<xsl:value-of select="normalize-space(@value)"/>
										</fo:inline>
										<xsl:text>, </xsl:text>
										<fo:basic-link internal-destination="INDEXTERM_{$id}">
											<fo:page-number-citation ref-id="INDEXTERM_{$id}"/>
										</fo:basic-link>
									</fo:block>
								</xsl:for-each>
								<!--fo:leader leader-pattern="dots"/-->
							</fo:block>
						</xsl:if>
					</xsl:for-each>
				</fo:block>
			</xsl:if>
			<xsl:if test="not(substring($letters, 2) = '')">
				<xsl:call-template name="searchIndexes">
					<xsl:with-param name="letters" select="substring($letters, 2)"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>