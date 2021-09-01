package io.airbrake.log4javabrake2;
import static java.lang.Runtime.getRuntime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.NoticeError;

@Plugin(name = "Airbrake", category = "Core", elementType = "appender", printObject = true)
public class AirbrakeAppender extends AbstractAppender {
  private ExecutorService executorService = Executors.newFixedThreadPool(1);
  Notifier notifier;
  String env;

  protected AirbrakeAppender(
      String name, Filter filter, int projectId, String projectKey, String env) {
    super(name, filter, null, true);
    if (projectId != 0 && projectKey != null) {
      this.notifier = new Notifier(projectId, projectKey);
    }
    this.env = env;
    getRuntime().addShutdownHook(new Thread(this::shutdown));
  }

  @Override
  public void append(LogEvent event) {
    Notice notice = newNotice(event);
    if (this.env != null) {
      notice.setContext("environment", this.env);
    }
    notice.setContext("severity", formatLevel(event.getLevel()));
    if (event.getContextStack() != null) {
      notice.setParam("contextStack", event.getContextStack().asList());
    }
    if (event.getContextData() != null) {
      notice.setParam("contextData", event.getContextData());
    }
    if (event.getMarker() != null) {
      notice.setParam("marker", event.getMarker().getName());
    }
    this.send(notice);
  }

  @PluginFactory
  public static AirbrakeAppender createAppender(
      @PluginAttribute("name") String name,
      @PluginElement("Filter") final Filter filter,
      @PluginAttribute("projectId") int projectId,
      @PluginAttribute("projectKey") String projectKey,
      @PluginAttribute("env") String env) {
    if (name == null) {
      LOGGER.error("No name provided for AirbrakeAppender");
      return null;
    }
    return new AirbrakeAppender(name, filter, projectId, projectKey, env);
  }

  static Notice newNotice(LogEvent event) {
    Throwable throwable = event.getThrown();
    if (throwable != null) {
      return new Notice(throwable);
    }

    Message eventMessage = event.getMessage();
    throwable = eventMessage.getThrowable();
    if (throwable != null) {
      return new Notice(throwable);
    }

    String type = event.getLoggerName();
    String message = eventMessage.getFormattedMessage();
    StackTraceElement[] stackTrace = null;

    if (event.getSource() != null) {
      stackTrace = new StackTraceElement[1];
      stackTrace[0] = event.getSource();
    }

    NoticeError err = new NoticeError(type, message, stackTrace);

    List<NoticeError> errors = new ArrayList<>();
    errors.add(err);

    return new Notice(errors);
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

  void send(Notice notice) {
    CompletableFuture<Notice> result = CompletableFuture.supplyAsync(() -> {
      if (this.notifier != null) {
        return this.notifier.sendSync(notice);
      }
      return Airbrake.sendSync(notice);
    }, executorService);

    // try {
    //   result.get(3, TimeUnit.SECONDS);
    // } catch(Exception e){}
  }

  void shutdown() {
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
    } catch (InterruptedException ex) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
