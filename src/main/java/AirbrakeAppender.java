package io.airbrake.log4javabrake2;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import io.airbrake.javabrake.Notifier;
import io.airbrake.javabrake.Airbrake;
import io.airbrake.javabrake.NoticeError;
import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.NoticeStackRecord;

@Plugin(name = "AirbrakeAppender", category = "Core", elementType = "appender", printObject = true)
public class AirbrakeAppender extends AbstractAppender {
  protected AirbrakeAppender(String name, Filter filter) {
    super(name, filter, null, true);
  }

  @Override
  public void append(LogEvent event) {
    Throwable throwable = event.getThrown();
    if (throwable != null) {
      Airbrake.report(throwable);
      return;
    }

    Message eventMessage = event.getMessage();
    throwable = eventMessage.getThrowable();
    if (throwable != null) {
      Airbrake.report(throwable);
      return;
    }

    String type = event.getLoggerName();
    String message = eventMessage.getFormattedMessage();

    List<NoticeStackRecord> backtrace = null;
    if (event.getSource() != null) {
      backtrace = new ArrayList<>();
      StackTraceElement source = event.getSource();
      backtrace.add(new NoticeStackRecord(source));
    }

    NoticeError err = new NoticeError(type, message, backtrace);

    List<NoticeError> errors = new ArrayList<>();
    errors.add(err);

    Notice notice = new Notice(errors);
    notice.setContext("level", formatLevel(event.getLevel()));
    if (event.getContextStack() != null) {
      notice.setParam("contextStack", event.getContextStack().asList());
    }
    if (event.getContextData() != null) {
      notice.setParam("contextData", event.getContextData());
    }
    if (event.getMarker() != null) {
      notice.setParam("marker", event.getMarker().getName());
    }

    Airbrake.send(notice);
  }

  @PluginFactory
  public static AirbrakeAppender createAppender(
      @PluginAttribute("name") String name, @PluginElement("Filter") final Filter filter) {
    if (name == null) {
      LOGGER.error("No name provided for AirbrakeAppender");
      return null;
    }
    return new AirbrakeAppender(name, filter);
  }

  static String formatLevel(Level level) {
    if (level.isMoreSpecificThan(Level.FATAL)) {
      return "critical";
    }
    if (level.isMoreSpecificThan(Level.ERROR)) {
      return "error";
    }
    if (level.isMoreSpecificThan(Level.WARN)) {
      return "warn";
    }
    if (level.isMoreSpecificThan(Level.INFO)) {
      return "info";
    }
    if (level.isMoreSpecificThan(Level.DEBUG)) {
      return "debug";
    }
    return "trace";
  }
}
