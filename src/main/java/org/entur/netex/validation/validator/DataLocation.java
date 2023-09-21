package org.entur.netex.validation.validator;

public class DataLocation {

  public static final DataLocation EMPTY_LOCATION = new DataLocation();

  private String objectId;
  private String fileName;
  private Integer lineNumber;
  private Integer columNumber;

  private DataLocation() {}

  public DataLocation(
    String objectId,
    String fileName,
    Integer lineNumber,
    Integer columNumber
  ) {
    this.objectId = objectId;
    this.fileName = fileName;
    this.lineNumber = lineNumber;
    this.columNumber = columNumber;
  }

  public String getObjectId() {
    return objectId;
  }

  public String getFileName() {
    return fileName;
  }

  public Integer getLineNumber() {
    return lineNumber;
  }

  public Integer getColumNumber() {
    return columNumber;
  }
}
