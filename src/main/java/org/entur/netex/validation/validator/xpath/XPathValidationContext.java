package org.entur.netex.validation.validator.xpath;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.sf.saxon.s9api.XdmNode;
import org.entur.netex.validation.validator.ValidationContext;
import org.entur.netex.validation.validator.id.IdVersion;
import org.entur.netex.validation.xml.NetexXMLParser;

/**
 * Context of the current NeTEx validation.
 */
public class XPathValidationContext implements ValidationContext {

  private final XdmNode xmlNode;
  private final NetexXMLParser netexXMLParser;
  private final String codespace;
  private final String fileName;
  private final Set<IdVersion> localIds;
  private final List<IdVersion> localRefs;
  private final Map<String, IdVersion> localIdsMap;

  /**
   * Build the validation context for a validation run
   * @param document the document being analyzed.
   * @param netexXMLParser the NeTEx parser.
   * @param codespace the current codespace.
   * @param fileName the name of the file being analyzed
   * @param localIds the set of NeTEx ids declared in the file.
   * @param localRefs the set of references to other NeTEx objects. They may refer to object in the same file or to external sources.
   */
  public XPathValidationContext(
    XdmNode document,
    NetexXMLParser netexXMLParser,
    String codespace,
    String fileName,
    Set<IdVersion> localIds,
    List<IdVersion> localRefs
  ) {
    this.xmlNode = document;
    this.netexXMLParser = netexXMLParser;
    this.codespace = Objects.requireNonNull(codespace);
    this.fileName = fileName;
    this.localIds = Objects.requireNonNull(localIds);
    this.localIdsMap =
      localIds
        .stream()
        .collect(
          Collectors.toMap(
            IdVersion::getId,
            Function.identity(),
            (existing, duplicate) -> existing
          )
        );
    this.localRefs = Objects.requireNonNull(localRefs);
  }

  public XdmNode getXmlNode() {
    return xmlNode;
  }

  @Override
  public String getFileName() {
    return fileName;
  }

  @Override
  public String getCodespace() {
    return codespace;
  }

  public Set<IdVersion> getLocalIds() {
    return localIds;
  }

  public List<IdVersion> getLocalRefs() {
    return localRefs;
  }

  public NetexXMLParser getNetexXMLParser() {
    return netexXMLParser;
  }

  public Map<String, IdVersion> getLocalIdsMap() {
    return localIdsMap;
  }
}
