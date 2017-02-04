package io.palliassist.palliassistmobile.twilio.chat.messages;

public interface ChatMessage {

  String getMessageBody();

  String getAuthor();

  String getTimeStamp();
}
