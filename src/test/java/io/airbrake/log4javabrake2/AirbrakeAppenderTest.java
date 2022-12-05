package io.airbrake.log4javabrake2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.airbrake.javabrake.Notifier;
import io.airbrake.javabrake.Airbrake;
import io.airbrake.javabrake.Config;
import io.airbrake.javabrake.NoticeError;
import io.airbrake.javabrake.NoticeStackFrame;

public class AirbrakeAppenderTest {
 
  static Notifier notifier;
  Throwable exc = new IOException("hello from Java");
  MockSyncSender sender = new MockSyncSender();

  @BeforeAll
  public static void beforeClass() {

    Config conf = new Config();
    notifier = new Notifier(conf);

    LoggerContext ctx = (LoggerContext) LogManager.getContext(false);

    Configuration config = ctx.getConfiguration();
    Appender appender = AirbrakeAppender.createAppender("Airbrake", null, 0, null, null);
    appender.start();
    config.addAppender(appender);

    LoggerConfig loggerConfig = new LoggerConfig("io.airbrake.log4javabrake2", Level.ERROR, false);
    loggerConfig.addAppender(appender, null, null);
    config.addLogger("io.airbrake.log4javabrake2", loggerConfig);
    ctx.updateLoggers();
  }

  @BeforeEach
  public void before() {
    notifier.setSyncSender(sender);
    Airbrake.setNotifier(notifier);
  }

  @Test
  public void testLogException() {
    Logger logger = LogManager.getLogger("io.airbrake.log4javabrake2");
    logger.catching(exc);
    shortPause();

    NoticeError err = sender.notice.errors.get(0);
    assertEquals("java.io.IOException", err.type);
    assertEquals("hello from Java", err.message);
  }

  @Test
  public void testLogMessage() {
    Logger logger = LogManager.getLogger("io.airbrake.log4javabrake2");
    logger.error("hello from Java");
    shortPause();

    NoticeError err = sender.notice.errors.get(0);
    assertEquals("io.airbrake.log4javabrake2", err.type);
    assertEquals("hello from Java", err.message);

  NoticeStackFrame frame = err.backtrace[0];
  assertEquals("testLogMessage", frame.function);
  assertTrue(frame.file.contains("test/io/airbrake/log4javabrake2/AirbrakeAppenderTest.class"));
  assertEquals(69, frame.line);
  }

  // shortPause sleeps the thread for a tiny amount of time to prevent exits
  // before the executorService can do the work under test.
  void shortPause() {
    try { Thread.sleep(100); } catch(Exception e){}
  }
}
