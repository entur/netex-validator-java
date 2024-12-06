package org.entur.netex.validation.validator.xpath;

import java.util.Objects;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.xml.NetexXMLParser;

/**
 * The validation context for an XPath validation rule.
 */
public class XPathRuleValidationContext {

  private final XdmNode xmlNode;
  private final NetexXMLParser netexXMLParser;
  private final String codespace;
  private final String fileName;

  /**
   *
   * @param document the NeTEx document or NeTEx document part.
   * @param netexXMLParser the NeTEx parser.
   * @param codespace the current codespace.
   * @param fileName the current filename.
   */
  public XPathRuleValidationContext(
    XdmNode document,
    NetexXMLParser netexXMLParser,
    String codespace,
    String fileName
  ) {
    this.xmlNode = Objects.requireNonNull(document);
    this.netexXMLParser = Objects.requireNonNull(netexXMLParser);
    this.codespace = Objects.requireNonNull(codespace);
    this.fileName = fileName;
  }

  public XdmNode getXmlNode() {
    return xmlNode;
  }

  public String getFileName() {
    return fileName;
  }

  public String getCodespace() {
    return codespace;
  }

  public NetexXMLParser getNetexXMLParser() {
    return netexXMLParser;
  }

  public boolean isCommonFile() {
    return fileName != null && fileName.startsWith("_");
  }

  public boolean isLineFile() {
    return !isCommonFile();
  }
}
