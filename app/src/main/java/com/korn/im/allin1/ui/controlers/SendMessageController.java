package com.korn.im.allin1.ui.controlers;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.korn.im.allin1.R;

/**
 * Created by korn on 03.09.16.
 */
public class SendMessageController extends Controller implements View.OnClickListener {
    private ImageButton attachmentsBtn;
    private ImageButton sendBtn;
    private EditText messageText;
    private OnMessageListener onMessageListener;
    private ProgressBar sendingProgressBar;

    public SendMessageController(Activity activity) {
        super(activity);
    }

    @Override
    public void create() {
        attachmentsBtn = (ImageButton) getView(R.id.attachmentsBtn);
        sendBtn = (ImageButton) getView(R.id.sendButton);
        messageText = (EditText) getView(R.id.messageText);
        sendingProgressBar = (ProgressBar) getView(R.id.sendingProgressBar);
    }

    @Override
    public void restoreInstance(Bundle savedInstanceState) {

    }

    @Override
    public void prepare() {
        sendBtn.setOnClickListener(this);
        attachmentsBtn.setOnClickListener(this);
        messageText.setCursorVisible(true);
    }

    @Override
    public void update() {

    }

    @Override
    public void release() {
        messageText.setCursorVisible(false);
    }

    @Override
    public void saveInstance(Bundle outInstanceState) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void onClick(View v) {
        if(v == sendBtn) send();
        else showAttachments();
    }

    private void showAttachments() {
        Toast.makeText(getActivity(), "Attachments", Toast.LENGTH_SHORT).show();
    }

    private void send() {
        final String text = messageText.getText().toString();
        if(!text.matches(" +|") && onMessageListener != null)
            onMessageListener.onMessage(text.trim().replaceAll("  +", " "));
    }

    public void clear() {
        messageText.setText(null);
    }

    public void setOnMessageListener(OnMessageListener onMessageListener) {
        this.onMessageListener = onMessageListener;
    }

    public void setSendProgress(Long currentProgress) {
        this.sendingProgressBar.setProgress(currentProgress.intValue());
    }

    public void setMaxProgress(Long maxProgress) {
        this.sendingProgressBar.setMax(maxProgress.intValue());
    }

    public void setInProgress(boolean inProgress) {
        this.sendingProgressBar.setVisibility(inProgress ? View.VISIBLE : View.INVISIBLE);
    }

    public interface OnMessageListener {
        void onMessage(String message);
    }
}
