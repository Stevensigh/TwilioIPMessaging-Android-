package io.palliassist.palliassistmobile.twilio.chat.listener;

public interface TaskCompletionListener<T, U> {

  void onSuccess(T t);

  void onError(U u);
}
