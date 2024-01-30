<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:syn="http://ws.apache.org/ns/synapse">
<xsl:output method="xml" indent="yes"/>
 
  <!-- Identity template: copies all elements and attributes as-is -->
<xsl:template match="@*|node()">
<xsl:copy>
<xsl:apply-templates select="@*|node()"/>
</xsl:copy>
</xsl:template>
 
  <!-- Remove member tag with value in curly braces {} or empty dateTime.iso8601 or empty string -->
<xsl:template match="member[
    (value/*[normalize-space() = ''])
    or
    (value[starts-with(normalize-space(), '{') and substring(normalize-space(), string-length(normalize-space()), 1) = '}']) 

  ]"/>
 
</xsl:stylesheet>