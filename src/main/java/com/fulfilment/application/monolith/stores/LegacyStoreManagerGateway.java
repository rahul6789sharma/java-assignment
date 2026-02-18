package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LegacyStoreManagerGateway {

  private static final Logger LOGGER =
      Logger.getLogger(LegacyStoreManagerGateway.class.getName());

  public void createStoreOnLegacySystem(Store store) {
    // Emulates sending store data to a legacy system by writing to a temp file.
    writeToFile(store);
  }

  public void updateStoreOnLegacySystem(Store store) {
    writeToFile(store);
  }

  private void writeToFile(Store store) {
    try {
      Path tempFile = Files.createTempFile(store.name, ".txt");
      LOGGER.debugv("Temporary file created at: {0}", tempFile);

      String content =
          "Store created. [ name ="
              + store.name
              + " ] [ items on stock ="
              + store.quantityProductsInStock
              + "]";
      Files.write(tempFile, content.getBytes());
      LOGGER.debugv("Data written to temporary file: {0}", tempFile);

      Files.delete(tempFile);
      LOGGER.debug("Temporary file deleted.");
    } catch (Exception e) {
      LOGGER.errorv(e, "Failed to propagate store to legacy system: name={0}", store.name);
      // Best-effort sync: do not fail the main request; log for ops to retry or fix.
    }
  }
}
