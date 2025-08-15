package org.entur.netex.validation.cli;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFileSupplier {

  private final File zipFile;
  private final Comparator<String> sharedDataFirstComparator;
  private List<String> sortedXmlFileNames;

  public ZipFileSupplier(File zipFile) {
    this.zipFile = zipFile;
    this.sharedDataFirstComparator =
      (a, b) -> {
        String nameA = a.toLowerCase();
        String nameB = b.toLowerCase();
        if (nameA.startsWith("_") && !nameB.startsWith("_")) return -1;
        if (!nameA.startsWith("_") && nameB.startsWith("_")) return 1;
        return nameA.compareTo(nameB);
      };
  }

  private <T> T iterateZipFile(ZipEntryProcessor<T> processor) throws IOException {
    try (ZipInputStream zipStream = new ZipInputStream(new FileInputStream(zipFile))) {
      ZipEntry entry;
      while ((entry = zipStream.getNextEntry()) != null) {
        if (entry.getName().contains("..")) {
          throw new IOException("Unsafe ZIP entry: " + entry.getName());
        }
        T result = processor.process(entry, zipStream);
        if (result != null) {
          return result;
        }
      }
      return null;
    }
  }

  private interface ZipEntryProcessor<T> {
    T process(ZipEntry entry, ZipInputStream stream) throws IOException;
  }

  public List<String> sortedXmlFileNames() throws IOException {
    if (sortedXmlFileNames != null) {
      return sortedXmlFileNames;
    }
    sortedXmlFileNames = new ArrayList<>();
    iterateZipFile((entry, stream) -> {
      if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".xml")) {
        sortedXmlFileNames.add(entry.getName());
      }
      return null;
    });

    sortedXmlFileNames.sort(sharedDataFirstComparator);
    return sortedXmlFileNames;
  }

  public Stream<FileSupplier> createFileSuppliers() throws IOException {
    List<String> xmlFileNames = sortedXmlFileNames();

    return xmlFileNames
      .stream()
      .map(fileName ->
        () ->
          iterateZipFile((entry, zipStream) -> {
            if (entry.getName().equals(fileName)) {
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              byte[] buffer = new byte[4096];
              int length;
              while ((length = zipStream.read(buffer)) > 0) {
                baos.write(buffer, 0, length);
              }
              return new FileEntry(fileName, baos.toByteArray());
            }
            return null; // Continue iteration
          })
      );
  }

  public boolean isEmpty() throws IOException {
    return sortedXmlFileNames().isEmpty();
  }
}
