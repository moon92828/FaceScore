package demo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.widget.EaseSwitchButton;

import demo.Constant;
import demo.DemoHelper;
import demo.model.DemoModel;
import demo.R;
import demo.parse.UserProfileManager;

/**
 * Created by moon9 on 2016/3/3.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    /**
     * 设置声音布局
     */
    private RelativeLayout rl_switch_sound;
    /**
     * 设置震动布局
     */
    private RelativeLayout rl_switch_vibrate;
    /**
     * 设置扬声器布局
     */
    private RelativeLayout rl_switch_speaker;


    /**
     * 声音和震动中间的那条线
     */
    private TextView textview1, textview2;

    private LinearLayout blacklistContainer;

    private LinearLayout userProfileContainer;

    /**
     * 退出按钮
     */
    private Button logoutBtn;

    private RelativeLayout rl_switch_chatroom_leave;

    private RelativeLayout rl_switch_delete_msg_when_exit_group;
    private RelativeLayout rl_switch_auto_accept_group_invitation;

    private EaseSwitchButton notifiSwitch;
    private EaseSwitchButton soundSwitch;
    private EaseSwitchButton vibrateSwitch;
    private EaseSwitchButton speakerSwitch;
    private EaseSwitchButton ownerLeaveSwitch;
    private EaseSwitchButton switch_delete_msg_when_exit_group;
    private EaseSwitchButton switch_auto_accept_group_invitation;
    private DemoModel settingsModel;
    private EMOptions chatOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        rl_switch_sound = (RelativeLayout) getView().findViewById(R.id.rl_switch_sound);
        rl_switch_vibrate = (RelativeLayout) getView().findViewById(R.id.rl_switch_vibrate);
        rl_switch_speaker = (RelativeLayout) getView().findViewById(R.id.rl_switch_speaker);
        rl_switch_chatroom_leave = (RelativeLayout) getView().findViewById(R.id.rl_switch_chatroom_owner_leave);
        rl_switch_delete_msg_when_exit_group = (RelativeLayout) getView().findViewById(R.id.rl_switch_delete_msg_when_exit_group);
        rl_switch_auto_accept_group_invitation = (RelativeLayout) getView().findViewById(R.id.rl_switch_auto_accept_group_invitation);

        soundSwitch = (EaseSwitchButton) getView().findViewById(R.id.switch_sound);
        vibrateSwitch = (EaseSwitchButton) getView().findViewById(R.id.switch_vibrate);
        speakerSwitch = (EaseSwitchButton) getView().findViewById(R.id.switch_speaker);
        ownerLeaveSwitch = (EaseSwitchButton) getView().findViewById(R.id.switch_owner_leave);
        switch_delete_msg_when_exit_group = (EaseSwitchButton) getView().findViewById(R.id.switch_delete_msg_when_exit_group);
        switch_auto_accept_group_invitation = (EaseSwitchButton) getView().findViewById(R.id.switch_auto_accept_group_invitation);


        logoutBtn = (Button) getView().findViewById(R.id.btn_logout);
        if(!TextUtils.isEmpty(EMClient.getInstance().getCurrentUser())){
            logoutBtn.setText(getString(R.string.button_logout) + "(" + EMClient.getInstance().getCurrentUser() + ")");
        }

        blacklistContainer = (LinearLayout) getView().findViewById(R.id.ll_black_list);
        userProfileContainer = (LinearLayout) getView().findViewById(R.id.ll_user_profile);

        settingsModel = DemoHelper.getInstance().getModel();
        chatOptions = EMClient.getInstance().getOptions();

        blacklistContainer.setOnClickListener(this);
        userProfileContainer.setOnClickListener(this);
        rl_switch_sound.setOnClickListener(this);
        rl_switch_vibrate.setOnClickListener(this);
        rl_switch_speaker.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        rl_switch_chatroom_leave.setOnClickListener(this);
        rl_switch_delete_msg_when_exit_group.setOnClickListener(this);
        rl_switch_auto_accept_group_invitation.setOnClickListener(this);

        // 是否打开声音
        // sound notification is switched on or not?
        if (settingsModel.getSettingMsgSound()) {
            soundSwitch.openSwitch();
        } else {
            soundSwitch.closeSwitch();
        }

        // 是否打开震动
        // vibrate notification is switched on or not?
        if (settingsModel.getSettingMsgVibrate()) {
            vibrateSwitch.openSwitch();
        } else {
            vibrateSwitch.closeSwitch();
        }

        // 是否打开扬声器
        // the speaker is switched on or not?
        if (settingsModel.getSettingMsgSpeaker()) {
            speakerSwitch.openSwitch();
        } else {
            speakerSwitch.closeSwitch();
        }

        // 是否允许聊天室owner leave
        if(settingsModel.isChatroomOwnerLeaveAllowed()){
            ownerLeaveSwitch.openSwitch();
        }else{
            ownerLeaveSwitch.closeSwitch();
        }

        // delete messages when exit group?
        if(settingsModel.isDeleteMessagesAsExitGroup()){
            switch_delete_msg_when_exit_group.openSwitch();
        } else {
            switch_delete_msg_when_exit_group.closeSwitch();
        }

        if (settingsModel.isAutoAcceptGroupInvitation()) {
            switch_auto_accept_group_invitation.openSwitch();
        } else {
            switch_auto_accept_group_invitation.closeSwitch();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_switch_sound:
                if (soundSwitch.isSwitchOpen()) {
                    soundSwitch.closeSwitch();
                    settingsModel.setSettingMsgSound(false);
                } else {
                    soundSwitch.openSwitch();
                    settingsModel.setSettingMsgSound(true);
                }
                break;
            case R.id.rl_switch_vibrate:
                if (vibrateSwitch.isSwitchOpen()) {
                    vibrateSwitch.closeSwitch();
                    settingsModel.setSettingMsgVibrate(false);
                } else {
                    vibrateSwitch.openSwitch();
                    settingsModel.setSettingMsgVibrate(true);
                }
                break;
            case R.id.rl_switch_speaker:
                if (speakerSwitch.isSwitchOpen()) {
                    speakerSwitch.closeSwitch();
                    settingsModel.setSettingMsgSpeaker(false);
                } else {
                    speakerSwitch.openSwitch();
                    settingsModel.setSettingMsgVibrate(true);
                }
                break;
            case R.id.rl_switch_chatroom_owner_leave:
                if(ownerLeaveSwitch.isSwitchOpen()){
                    ownerLeaveSwitch.closeSwitch();
                    settingsModel.allowChatroomOwnerLeave(false);
                    chatOptions.allowChatroomOwnerLeave(false);
                }else{
                    ownerLeaveSwitch.openSwitch();
                    settingsModel.allowChatroomOwnerLeave(true);
                    chatOptions.allowChatroomOwnerLeave(true);
                }
                break;
            case R.id.rl_switch_delete_msg_when_exit_group:
                if(switch_delete_msg_when_exit_group.isSwitchOpen()){
                    switch_delete_msg_when_exit_group.closeSwitch();
                    settingsModel.setDeleteMessagesAsExitGroup(false);
                    chatOptions.setDeleteMessagesAsExitGroup(false);
                }else{
                    switch_delete_msg_when_exit_group.openSwitch();
                    settingsModel.setDeleteMessagesAsExitGroup(true);
                    chatOptions.setDeleteMessagesAsExitGroup(true);
                }
                break;
            case R.id.rl_switch_auto_accept_group_invitation:
                if(switch_auto_accept_group_invitation.isSwitchOpen()){
                    switch_auto_accept_group_invitation.closeSwitch();
                    settingsModel.setAutoAcceptGroupInvitation(false);
                    chatOptions.setAutoAcceptGroupInvitation(false);
                }else{
                    switch_auto_accept_group_invitation.openSwitch();
                    settingsModel.setAutoAcceptGroupInvitation(true);
                    chatOptions.setAutoAcceptGroupInvitation(true);
                }
                break;
            case R.id.btn_logout: //退出登陆
                logout();
                break;
            case R.id.ll_black_list:
                startActivity(new Intent(getActivity(), BlacklistActivity.class));
                break;
            case R.id.ll_user_profile:
                startActivity(new Intent(getActivity(), UserProfileActivity.class).putExtra("setting", true)
                        .putExtra("username", EMClient.getInstance().getCurrentUser()));
                break;
            default:
                break;
        }

    }

    void logout() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoHelper.getInstance().logout(false,new EMCallBack() {

            @Override
            public void onSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // 重新显示登陆页面
                        ((MainActivity) getActivity()).finish();
                        startActivity(new Intent(getActivity(), LoginActivity.class));

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        Toast.makeText(getActivity(), "unbind devicetokens failed", Toast.LENGTH_SHORT).show();


                    }
                });
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(((MainActivity)getActivity()).isConflict){
            outState.putBoolean("isConflict", true);
        }else if(((MainActivity)getActivity()).getCurrentAccountRemoved()){
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }
}
