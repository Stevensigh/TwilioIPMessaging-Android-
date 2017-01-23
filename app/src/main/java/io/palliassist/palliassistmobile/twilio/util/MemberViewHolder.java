package io.palliassist.palliassistmobile.twilio.util;

import android.view.View;
import android.widget.TextView;

import io.palliassist.palliassistmobile.R;
import com.twilio.ipmessaging.Member;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.member_item_layout)
public class MemberViewHolder extends ItemViewHolder<Member> {
	
	@ViewId(R.id.identity)
	TextView memberIdentity;

	@ViewId(R.id.member_sid)
	TextView memberSid;

	View view;


	public MemberViewHolder(View view) {
		super(view);
		this.view = view;
	}

	
	@Override
	public void onSetListeners() {
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OnMemberClickListener listener = getListener(OnMemberClickListener.class);
				if (listener != null) {
					listener.onMemberClicked(getItem());
				}
			}
		});
	}
	
	public interface OnMemberClickListener {
		void onMemberClicked(Member member);
	}

	@Override
	public void onSetValues(Member member, PositionInfo arg1) {
		this.memberIdentity.setText(member.getIdentity());
		//this.memberSid.setText(member.getSid());
		
	}

}
