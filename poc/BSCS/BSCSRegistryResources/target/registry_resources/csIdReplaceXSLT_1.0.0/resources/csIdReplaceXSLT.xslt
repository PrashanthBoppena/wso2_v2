<?xml version="1.0" encoding="UTF-8"?>
    <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        <xsl:param name="csId"/>
        
        <!-- Identity transform -->
        <xsl:template match="@*|node()">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:template>
        
        <!-- Replace ${ctx.csId} with the actual value -->
        <xsl:template match="req:csId">
            <xsl:copy>
                <xsl:value-of select="$csId"/>
            </xsl:copy>
        </xsl:template>
    </xsl:stylesheet>