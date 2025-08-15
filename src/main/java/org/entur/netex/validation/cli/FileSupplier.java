package org.entur.netex.validation.cli;

import java.io.IOException;

public interface FileSupplier {
  FileEntry get() throws IOException;
}
