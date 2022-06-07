package io.airbrake.log4javabrake2;

import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.SyncSender;

class MockSyncSender implements SyncSender {
  public Notice notice;

  @Override
  public Notice send(Notice notice) {
    this.notice = notice;
    return notice;
  }

  @Override
  public void setHost(String host) {}
}
