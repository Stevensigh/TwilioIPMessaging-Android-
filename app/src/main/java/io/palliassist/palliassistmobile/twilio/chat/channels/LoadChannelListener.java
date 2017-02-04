package io.palliassist.palliassistmobile.twilio.chat.channels;

import com.twilio.chat.Channel;

import java.util.List;

public interface LoadChannelListener {

  void onChannelsFinishedLoading(List<Channel> channels);

}
