package org.entur.netex.validation.validator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.entur.netex.validation.validator.id.VersionOnLocalNetexIdValidator;
import org.entur.netex.validation.xml.NetexXMLParser;
import org.junit.jupiter.api.Test;

class NetexValidatorsRunnerTest {

  @Test
  void testDescriptions() {
    XPathValidator xPathValidator = new VersionOnLocalNetexIdValidator();
    NetexValidatorsRunner runner = NetexValidatorsRunner
      .of()
      .withNetexXMLParser(new NetexXMLParser())
      .withXPathValidators(List.of(xPathValidator))
      .build();
    Set<String> ruleDescriptions = runner.getRuleDescriptions();
    assertNotNull(ruleDescriptions);
    Set<String> xpathRuleDescriptions = xPathValidator
      .getRules()
      .stream()
      .map(ValidationRule::name)
      .collect(Collectors.toUnmodifiableSet());
    assertEquals(xpathRuleDescriptions, ruleDescriptions);
  }
}
