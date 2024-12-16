package org.entur.netex.validation.test.xpath.support;

import static org.entur.netex.validation.test.xpath.support.XPathTestSupport.NETEX_XML_PARSER;

import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.xpath.XPathRuleValidationContext;

public class TestValidationContextBuilder {

  private final XdmNode document;
  private String codespace = XPathTestSupport.TEST_CODESPACE;
  private String filename = XPathTestSupport.TEST_FILENAME;

  public TestValidationContextBuilder(XdmNode xdmNode) {
    this.document = xdmNode;
  }

  public static TestValidationContextBuilder ofDocument(XdmNode document) {
    return new TestValidationContextBuilder(document);
  }

  public static TestValidationContextBuilder ofNetexFragment(
    String netexFragment
  ) {
    return ofDocument(XPathTestSupport.parseDocument(netexFragment));
  }

  public TestValidationContextBuilder withCodespace(String codespace) {
    this.codespace = codespace;
    return this;
  }

  public TestValidationContextBuilder withFilename(String filename) {
    this.filename = filename;
    return this;
  }

  public XPathRuleValidationContext build() {
    return new XPathRuleValidationContext(
      document,
      NETEX_XML_PARSER,
      codespace,
      filename
    );
  }
}
