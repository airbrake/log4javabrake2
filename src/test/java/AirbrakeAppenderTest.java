package io.airbrake.log4javabrake2;

import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import io.airbrake.javabrake.NoticeError;
import io.airbrake.javabrake.Airbrake;
import io.airbrake.javabrake.NoticeStackRecord;
import io.airbrake.javabrake.Notifier;

public class AirbrakeAppenderTest {
  Notifier notifier = new Notifier(0, "");
  Throwable exc = new IOException("hello from Java");
  TestAsyncSender sender = new TestAsyncSender();

  @BeforeClass
  public static void beforeClass() {
    LoggerContext ctx = (LoggerContext) LogManager.getContext(false);

    Configuration config = ctx.getConfiguration();
    Appender appender = AirbrakeAppender.createAppender("Airbrake", null);
    appender.start();
    config.addAppender(appender);

    AppenderRef ref = AppenderRef.createAppenderRef("Airbrake", null, null);
    AppenderRef[] refs = new AppenderRef[] {ref};

    LoggerConfig loggerConfig =
        LoggerConfig.createLogger(
            "false", Level.ERROR, "io.airbrake.log4javabrake2", "true", refs, null, config, null);
    loggerConfig.addAppender(appender, null, null);
    config.addLogger("io.airbrake.log4javabrake2", loggerConfig);
    ctx.updateLoggers();
  }

  @Before
  public void before() {
    notifier.setAsyncSender(sender);
    Airbrake.setNotifier(notifier);
  }

  @Test
  public void testLogException() {
    Logger logger = LogManager.getLogger("io.airbrake.log4javabrake2");
    logger.catching(exc);

    NoticeError err = sender.notice.errors.get(0);
    assertEquals("java.io.IOException", err.type);
    assertEquals("hello from Java", err.message);
  }

  @Test
  public void testLogMessage() {
    Logger logger = LogManager.getLogger("io.airbrake.log4javabrake2");
    logger.error("hello from Java");

    NoticeError err = sender.notice.errors.get(0);
    assertEquals("io.airbrake.log4javabrake2", err.type);
    assertEquals("hello from Java", err.message);

    NoticeStackRecord record = err.backtrace.get(0);
    assertEquals("testLogMessage", record.function);
    assertEquals("test/io/airbrake/log4javabrake2/AirbrakeAppenderTest.class", record.file);
    assertEquals(68, record.line);
  }
}
