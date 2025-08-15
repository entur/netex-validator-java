package org.entur.netex.validation.cli;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFileSupplier {

  private final File zipFile;
  private final Comparator<String> sharedDataFirstComparator;

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

  public List<String> sortedXmlFileNames() throws IOException {
    List<String> xmlFileNames = new ArrayList<>();

    try (ZipInputStream zipStream = new ZipInputStream(new FileInputStream(zipFile))) {
      ZipEntry entry;
      while ((entry = zipStream.getNextEntry()) != null) {
        if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".xml")) {
          xmlFileNames.add(entry.getName());
        }
      }
    }

    xmlFileNames.sort(sharedDataFirstComparator);
    return xmlFileNames;
  }

  public Stream<FileSupplier> createFileSuppliers() throws IOException {
    List<String> xmlFileNames = sortedXmlFileNames();

    return xmlFileNames
      .stream()
      .map(fileName ->
        () -> {
          try (
            ZipInputStream zipStream = new ZipInputStream(new FileInputStream(zipFile))
          ) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
              if (entry.getName().equals(fileName)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int length;
                while ((length = zipStream.read(buffer)) > 0) {
                  baos.write(buffer, 0, length);
                }
                return new FileEntry(fileName, baos.toByteArray());
              }
            }
            throw new IOException("File not found in ZIP: " + fileName);
          } catch (IOException e) {
            return null;
          }
        }
      );
  }

  public boolean isEmpty() throws IOException {
    return sortedXmlFileNames().isEmpty();
  }
}
