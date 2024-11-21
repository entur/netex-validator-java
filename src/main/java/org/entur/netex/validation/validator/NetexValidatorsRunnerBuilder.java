package org.entur.netex.validation.validator;

import java.util.List;
import java.util.function.Function;
import org.entur.netex.validation.validator.jaxb.CommonDataRepositoryLoader;
import org.entur.netex.validation.validator.jaxb.JAXBValidationContext;
import org.entur.netex.validation.validator.jaxb.JAXBValidator;
import org.entur.netex.validation.validator.jaxb.NetexDataCollector;
import org.entur.netex.validation.validator.jaxb.NetexDataRepository;
import org.entur.netex.validation.validator.jaxb.StopPlaceRepository;
import org.entur.netex.validation.validator.schema.NetexSchemaValidator;
import org.entur.netex.validation.xml.NetexXMLParser;

public class NetexValidatorsRunnerBuilder {

  private NetexXMLParser netexXMLParser;
  private NetexSchemaValidator netexSchemaValidator = null;
  private CommonDataRepositoryLoader commonDataRepository = null;
  private NetexDataRepository netexDataRepository = null;
  private Function<JAXBValidationContext, StopPlaceRepository> stopPlaceRepository =
    null;
  private List<XPathValidator> xPathValidators = List.of();
  private List<JAXBValidator> jaxbValidators = List.of();
  private List<DatasetValidator> datasetValidators = List.of();
  private List<NetexDataCollector> netexDataCollectors = List.of();

  NetexValidatorsRunnerBuilder() {}

  public NetexXMLParser getNetexXMLParser() {
    return netexXMLParser;
  }

  public NetexSchemaValidator getNetexSchemaValidator() {
    return netexSchemaValidator;
  }

  public CommonDataRepositoryLoader getCommonDataRepository() {
    return commonDataRepository;
  }

  public NetexDataRepository getNetexDataRepository() {
    return netexDataRepository;
  }

  public Function<JAXBValidationContext, StopPlaceRepository> getStopPlaceRepository() {
    return stopPlaceRepository;
  }

  public List<XPathValidator> getXPathValidators() {
    return xPathValidators;
  }

  public List<JAXBValidator> getJaxbValidators() {
    return jaxbValidators;
  }

  public List<DatasetValidator> getDatasetValidators() {
    return datasetValidators;
  }

  public List<NetexDataCollector> getNetexDataCollectors() {
    return netexDataCollectors;
  }

  public NetexValidatorsRunnerBuilder withNetexXMLParser(
    NetexXMLParser netexXMLParser
  ) {
    this.netexXMLParser = netexXMLParser;
    return this;
  }

  public NetexValidatorsRunnerBuilder withXPathValidators(
    List<XPathValidator> xPathValidators
  ) {
    this.xPathValidators = xPathValidators;
    return this;
  }

  public NetexValidatorsRunnerBuilder withNetexSchemaValidator(
    NetexSchemaValidator netexSchemaValidator
  ) {
    this.netexSchemaValidator = netexSchemaValidator;
    return this;
  }

  public NetexValidatorsRunnerBuilder withCommonDataRepository(
    CommonDataRepositoryLoader commonDataRepository
  ) {
    this.commonDataRepository = commonDataRepository;
    return this;
  }

  public NetexValidatorsRunnerBuilder withNetexDataRepository(
    NetexDataRepository netexDataRepository
  ) {
    this.netexDataRepository = netexDataRepository;
    return this;
  }

  public NetexValidatorsRunnerBuilder withStopPlaceRepository(
    StopPlaceRepository stopPlaceRepository
  ) {
    this.stopPlaceRepository = jaxbValidationContext -> stopPlaceRepository;
    return this;
  }

  public NetexValidatorsRunnerBuilder withStopPlaceRepository(
    Function<JAXBValidationContext, StopPlaceRepository> stopPlaceRepository
  ) {
    this.stopPlaceRepository = stopPlaceRepository;
    return this;
  }

  public NetexValidatorsRunnerBuilder withDatasetValidators(
    List<DatasetValidator> datasetValidators
  ) {
    this.datasetValidators = datasetValidators;
    return this;
  }

  public NetexValidatorsRunnerBuilder withJaxbValidators(
    List<JAXBValidator> jaxbValidators
  ) {
    this.jaxbValidators = jaxbValidators;
    return this;
  }

  public NetexValidatorsRunnerBuilder withNetexDataCollectors(
    List<NetexDataCollector> netexDataCollectors
  ) {
    this.netexDataCollectors = netexDataCollectors;
    return this;
  }

  public NetexValidatorsRunner build() {
    return new NetexValidatorsRunner(this);
  }
}
