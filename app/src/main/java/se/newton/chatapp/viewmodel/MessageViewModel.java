package se.newton.chatapp.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.constraint.ConstraintSet;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import se.newton.chatapp.BR;
import se.newton.chatapp.R;
import se.newton.chatapp.activity.FullscreenImageActivity;
import se.newton.chatapp.model.Message;
import se.newton.chatapp.model.User;
import se.newton.chatapp.service.UserManager;

public class MessageViewModel extends BaseObservable {
    private User user;
    private Message message;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final RequestManager glideManager;

    public MessageViewModel(RequestManager glideManager, Message message) {
        this.message = message;
        if(message.getTimestamp() == null)
            message.setTimestamp(new Date());
        user = UserManager.getUser(glideManager, message.getUid());
        this.glideManager = glideManager;
    }

    public void setUser(User user) {
        this.user = user;
        notifyPropertyChanged(BR.user);
    }

    @Bindable
    public User getUser() {
        return user;
    }

    @Bindable
    public String getMessageText() {
        return message.getData();
    }

    @Bindable
    public String getTimestamp() {
        return dateFormat.format(message.getTimestamp());
    }

    public void setOrientation(View view) {
        String fUser = FirebaseAuth.getInstance().getUid();
        ViewGroup viewGroup = (ViewGroup) view;
        TransitionManager.beginDelayedTransition(viewGroup);
        if (message.getUid().equals(fUser)) {
            Log.d("ViewModel", "Message from you, attempting to shift constraints; uid:" + message.getUid() + ", fuid:" + fUser);
            ConstraintSet set = new ConstraintSet();
            set.clone(viewGroup.getContext(), R.layout.message_item_right);
            set.applyTo(viewGroup.findViewById(R.id.messageConstraintLayout));
        } else {
            ConstraintSet set = new ConstraintSet();
            set.clone(viewGroup.getContext(), R.layout.message_item);
            set.applyTo(viewGroup.findViewById(R.id.messageConstraintLayout));
        }
    }

    public void imageOnClick(View view){
        Context context = view.getContext();
        Intent intent = new Intent(context, FullscreenImageActivity.class);
        intent.putExtra("image_uri", message.getData());
        context.startActivity(intent);
    }
}
