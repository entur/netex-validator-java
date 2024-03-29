package org.entur.netex.validation.xml;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.validation.Schema;
import org.entur.netex.validation.exception.NetexValidationException;
import org.rutebanken.netex.validation.NeTExValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Manage the different versions of the NeTEX XML schema.
 * Parsed instances of the NeTEx schemas are cached.
 */
public final class NetexSchemaRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(
    NetexSchemaRepository.class
  );

  private final Map<NeTExValidator.NetexVersion, Schema> netexSchema;

  public NetexSchemaRepository() {
    this.netexSchema = new ConcurrentHashMap<>();
  }

  /**
   * Return an XML schema for a particular NeTEx version.
   * @param version the NeTEx version
   * @return an XML schema for a particular NeTEx version.
   */
  public Schema getNetexSchema(NeTExValidator.NetexVersion version) {
    return netexSchema.computeIfAbsent(
      version,
      netexVersion -> createNetexSchema(version)
    );
  }

  private static Schema createNetexSchema(NeTExValidator.NetexVersion version) {
    LOGGER.info(
      "Initializing Netex schema version {}, this may take a few seconds",
      version
    );
    try {
      return new NeTExValidator(version).getSchema();
    } catch (IOException | SAXException e) {
      throw new NetexValidationException("Could not create NeTEx schema", e);
    }
  }

  /**
   * Identify the version of NeTEx used in a given document.
   * @param content the NeTEx document
   * @return the NeTEx version.
   */
  public static NeTExValidator.NetexVersion detectNetexSchemaVersion(
    byte[] content
  ) {
    String profileVersion =
      PublicationDeliveryVersionAttributeReader.findPublicationDeliveryVersion(
        content
      );
    String netexSchemaVersion = getSchemaVersion(profileVersion);

    if (netexSchemaVersion != null) {
      switch (netexSchemaVersion) {
        case "1.04":
          return NeTExValidator.NetexVersion.V1_0_4beta;
        case "1.07":
          return NeTExValidator.NetexVersion.V1_0_7;
        case "1.08":
          return NeTExValidator.NetexVersion.v1_0_8;
        case "1.09":
          return NeTExValidator.NetexVersion.v1_0_9;
        case "1.10":
          return NeTExValidator.NetexVersion.v1_10;
        case "1.11":
          return NeTExValidator.NetexVersion.v1_11;
        case "1.12":
          return NeTExValidator.NetexVersion.v1_12;
        case "1.13":
          return NeTExValidator.NetexVersion.v1_13;
        case "1.14":
          return NeTExValidator.NetexVersion.v1_14;
        case "1.15":
          return NeTExValidator.NetexVersion.v1_15;
        default:
      }
    }
    return null;
  }

  private static String getSchemaVersion(String fullProfileString) {
    if (fullProfileString != null) {
      String[] split = fullProfileString.split(":");

      if (split.length == 3) {
        return split[0];
      }
    }
    return null;
  }
}
