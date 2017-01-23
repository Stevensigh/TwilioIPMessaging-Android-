package io.palliassist.palliassistmobile.twilio.util;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.palliassist.palliassistmobile.LoginActivity;
import io.palliassist.palliassistmobile.R;
import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

import com.twilio.ipmessaging.*;


@LayoutId(R.layout.message_item_layout)
public class MessageViewHolder extends ItemViewHolder<Message> {

    @ViewId(R.id.body)
    TextView body;

    @ViewId(R.id.txtInfo)
    TextView txtInfo;

    @ViewId(R.id.singleMessageContainer)
    LinearLayout singleMessageContainer;

    View view;

    public MessageViewHolder(View view) {
        super(view);
        this.view = view;
    }
/*
    @Override
    public void onSetListeners() {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnMessageClickListener listener = getListener(OnMessageClickListener.class);
                if (listener != null) {
                    listener.onMessageClicked(getItem());
                }
            }
        });
    }
*/
    @Override
    public void onSetValues(Message message, PositionInfo pos) {
        StringBuffer textInfo = new StringBuffer();
        String dateString = message.getTimeStamp();
        if(dateString != null) {
            textInfo.append(message.getAuthor()).append(":").append(dateString);
        } else {
            textInfo.append(message.getAuthor());
        }
        txtInfo.setText(textInfo.toString());
        body.setText(message.getMessageBody());

        //singleMessageContainer.setGravity(Gravity.RIGHT);

        boolean left = (message.getAuthor().compareTo(LoginActivity.local_user) ==0)? true:false;
        body.setBackgroundResource(left ? R.drawable.bubble_a : R.drawable.bubble_b);
        singleMessageContainer.setGravity(left ? Gravity.LEFT : Gravity.RIGHT);

    }

    public interface OnMessageClickListener {
        void onMessageClicked(Message message);
    }

    public void onMessageClicked(Channel channel) {
        System.out.println("¯\\_(ツ)_/¯");
    }
}
