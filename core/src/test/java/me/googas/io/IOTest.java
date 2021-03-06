package me.googas.io;

import java.io.IOException;
import lombok.NonNull;
import me.googas.io.mocks.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IOTest {

  @NonNull static final Logger logger = LoggerFactory.getLogger(IOTest.class);
  @NonNull private static TestingMocks mocks = new TestingMocks();

  @BeforeAll
  static void prepareMocks() {
    IOTest.mocks =
        TestingFiles.Contexts.JSON
            .read(TestingFiles.Resources.MOCKS, TestingMocks.class)
            .handle((e) -> IOTest.logger.error(e, () -> "Could not load mocking resources"))
            .provide()
            .orElseThrow(() -> new NullPointerException("Mocks could not be loaded"));
  }

  @Test
  @Order(0)
  void writeAndRead() {
    int index = 0;
    Person person = IOTest.mocks.getPersons().get(index);
    boolean written =
        TestingFiles.WRITE
            .write(TestingFiles.Contexts.JSON, IOTest.mocks.getPersons().get(index))
            .handle(e -> IOTest.logger.error(e, () -> "Could not write person in index " + index))
            .provide()
            .orElse(false);
    Assertions.assertTrue(written);
    String personJson =
        TestingFiles.WRITE
            .read(TestingFiles.Contexts.TXT)
            .handle(e -> IOTest.logger.error(e, () -> "Could not read person in index " + index))
            .provide()
            .orElse(null);
    Assertions.assertEquals(TestingFiles.Contexts.JSON.getGson().toJson(person), personJson);
  }

  @Test
  @Order(1)
  void copy() {
    StarboxFile file = new StarboxFile(TestingFiles.DIR, "write-copy.txt");
    Assertions.assertTrue(file.copy(TestingFiles.WRITE).provide().orElse(false));
    String original =
        TestingFiles.WRITE
            .read(TestingFiles.Contexts.TXT)
            .handle(e -> IOTest.logger.error(e, () -> "Could not read original file"))
            .provide()
            .orElse(null);
    Assertions.assertNotNull(original);
    String copy =
        file.read(TestingFiles.Contexts.TXT)
            .handle(e -> IOTest.logger.error(e, () -> "Could not read copied file"))
            .provide()
            .orElse(null);
    Assertions.assertEquals(original, copy);
  }

  @Test
  @Order(2)
  void copyDirectory() throws IOException {
    StarboxFile copy = new StarboxFile(StarboxFile.DIR, "testing-copy");
    copy.copyDirectory(TestingFiles.DIR);
    Assertions.assertTrue(copy.exists());
    Assertions.assertNotNull(copy.listFiles());
    Assertions.assertEquals(TestingFiles.DIR.listFiles().length, copy.listFiles().length);
  }

  @Test
  @Order(3)
  void deleteAll() throws IOException {
    StarboxFile copy = new StarboxFile(StarboxFile.DIR, "testing-copy");
    Assertions.assertTrue(copy.deleteAll());
    Assertions.assertTrue(TestingFiles.DIR.deleteAll());
    Assertions.assertFalse(copy.exists());
    Assertions.assertFalse(TestingFiles.DIR.exists());
  }
}
