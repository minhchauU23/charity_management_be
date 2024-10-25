package dev.ptit.charitymanagement.service.notification;

import java.util.List;

public interface NotificationSubscriber {
    String subscribe( List<String> registration, String topic);
    String unSubscribe(List<String> registration, String topic);
    boolean support(Class<?> type);
}
