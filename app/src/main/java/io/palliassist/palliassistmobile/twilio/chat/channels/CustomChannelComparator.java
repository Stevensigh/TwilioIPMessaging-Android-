package io.palliassist.palliassistmobile.twilio.chat.channels;

import com.twilio.chat.Channel;
import io.palliassist.palliassistmobile.R;
import java.util.Comparator;

import io.palliassist.palliassistmobile.twilio.application.TwilioApplication;

public class CustomChannelComparator implements Comparator<Channel> {
  private String defaultChannelName;

  CustomChannelComparator() {
    defaultChannelName =
        TwilioApplication.get().getResources().getString(R.string.default_channel_name);
  }

  @Override
  public int compare(Channel lhs, Channel rhs) {
    if (lhs.getFriendlyName().contentEquals(defaultChannelName)) {
      return -100;
    } else if (rhs.getFriendlyName().contentEquals(defaultChannelName)) {
      return 100;
    }
    return lhs.getFriendlyName().toLowerCase().compareTo(rhs.getFriendlyName().toLowerCase());
  }
}
