package com.ecarezone.android.doctor.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.Chat;
import com.ecarezone.android.doctor.model.database.ChatDbApi;
import com.ecarezone.android.doctor.model.rest.UploadImageResponse;
import com.ecarezone.android.doctor.utils.ImageUtil;
import com.ecarezone.android.doctor.utils.SinchUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L&T Technology Services.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;
    private List<Chat> mMessages;
    private SimpleDateFormat mFormatter;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mChatUser, mChatText, mChartTime;
        public ImageView mChartImage;
        public ProgressBar mProgressBar;

        public ViewHolder(View v) {
            super(v);
            mChatUser = (TextView) v.findViewById(R.id.chatUser);
            mChatText = (TextView) v.findViewById(R.id.chatText);
            mChartTime = (TextView) v.findViewById(R.id.chatTime);
            mChartImage = (ImageView) v.findViewById(R.id.chatImage);
            mProgressBar = (ProgressBar) v.findViewById(R.id.chat_image_spinner);
        }
    }

    public void addMessage(final Chat message) {
        if(message != null) {
            mMessages.add(message);
        }
        notifyDataSetChanged();
    }

    public void getChatHistory(String userName) {
        mMessages = ChatDbApi.getInstance(mContext).getChatHistory(userName);
        if (mMessages == null) {
            mMessages = new ArrayList<Chat>();
        }
        ChatDbApi.getInstance(mContext).updateChatReadStatus(userName,ChatDbApi.CHAT_READ_STATUS);
        Log.i("ChatAdapter", "size::" + mMessages.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int i) {
        return mMessages.get(i).getChatType().equals(ChatDbApi.CHAT_INCOMING) ? DIRECTION_INCOMING : DIRECTION_OUTGOING;
    }

    public ChatAdapter(Context context) {
        mMessages = new ArrayList<Chat>();
        mFormatter = new SimpleDateFormat("HH:mm");
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        int direction = viewType;
        int res = 0;
        if (direction == DIRECTION_INCOMING) {
            res = R.layout.chat_list_item_incoming;
        } else if (direction == DIRECTION_OUTGOING) {
            res = R.layout.chat_list_item_outgoing;
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(res, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Chat chat = mMessages.get(position);
        String message = chat.getMessageText();
        String name = null;
        String senderUserId;
        if (chat.getChatType().equals(ChatDbApi.CHAT_OUTGOING)) {
            name = "ME";
            senderUserId = LoginInfo.userName;
        } else {
            name = chat.getChatUserId();
            senderUserId = chat.getChatUserId();
        }
        holder.mProgressBar.setVisibility(View.GONE);

        if (senderUserId.equals(LoginInfo.userName) &&
                chat.getDeviceImagePath() != null) {
            holder.mChartImage.setVisibility(View.VISIBLE);
            holder.mChatText.setVisibility(View.GONE);
            if (!chat.isChatSending()) {
                Picasso.with(mContext)
                        .load("file://" + chat.getDeviceImagePath())
                        .config(Bitmap.Config.RGB_565).fit()
                        .centerCrop()
                        .into(holder.mChartImage);
            } else if (chat.getDiscImageFile() != null) {
                Picasso.with(mContext)
                        .load(chat.getDiscImageFile())
                        .config(Bitmap.Config.RGB_565).fit()
                        .centerCrop()
                        .into(holder.mChartImage);
            } else {
                new ImageUploadDiscTask(holder).execute(chat);
            }

        } else if (chat.getInComingImageUrl() != null) {
            holder.mChartImage.setVisibility(View.VISIBLE);
            holder.mChatText.setVisibility(View.GONE);
            Picasso.with(mContext)
                    .load(chat.getInComingImageUrl())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            holder.mProgressBar.setVisibility(View.GONE);
                            holder.mChartImage.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            holder.mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            holder.mProgressBar.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            holder.mChatText.setText(chat.getMessageText());
            holder.mChatText.setVisibility(View.VISIBLE);
            holder.mChartImage.setVisibility(View.GONE);
        }
        holder.mChatUser.setText(name);
        holder.mChartTime.setText(mFormatter.format(chat.getTimeStamp()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    /*AsyncTask for uploading image to server*/
    private class ImageUploadTask extends AsyncTask<File, Void, UploadImageResponse> {
        ViewHolder holder;
        File file;
        Chat chat;

        ImageUploadTask(ViewHolder holder, Chat chat) {
            this.holder = holder;
            this.chat = chat;
        }

        @Override
        protected UploadImageResponse doInBackground(File... strings) {
            file = strings[0];
            return ImageUtil.uploadChatImage(file);
        }

        @Override
        protected void onPostExecute(UploadImageResponse uploadImageResponse) {
            SinchUtil.getSinchServiceInterface().sendMessage(chat.getChatUserId(), uploadImageResponse.data.avatarUrl);
        }
    }

    /*AsyncTask for uploading image to disc*/
    private class ImageUploadDiscTask extends AsyncTask<Chat, Void, File> {
        Chat chat;
        ViewHolder holder;

        ImageUploadDiscTask(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        protected File doInBackground(Chat... chats) {
            chat = chats[0];
            File file = ImageUtil.uploadChatImageToDisc(chat.getDeviceImagePath());
            return file;
        }

        @Override
        protected void onPostExecute(File file) {

            chat.setDiscImageFile(file);
            Picasso.with(mContext)
                    .load(file)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            holder.mProgressBar.setVisibility(View.GONE);
                            holder.mChartImage.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            holder.mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            holder.mProgressBar.setVisibility(View.VISIBLE);
                        }
                    });

            new ImageUploadTask(holder, chat).execute(file);
        }
    }
}
