/*
 * Copyright (C) 2007-2008 Esmertec AG. Copyright (C) 2007-2008 The Android Open
 * Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package info.guardianproject.otr.app.im.app;

import info.guardianproject.otr.app.im.R;
import info.guardianproject.otr.app.im.provider.Imps;
import info.guardianproject.otr.app.im.ui.RoundedAvatarDrawable;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactView extends FrameLayout {
    static final String[] CONTACT_PROJECTION = { Imps.Contacts._ID, Imps.Contacts.PROVIDER,
                                                Imps.Contacts.ACCOUNT, Imps.Contacts.USERNAME,
                                                Imps.Contacts.NICKNAME, Imps.Contacts.TYPE,
                                                Imps.Contacts.SUBSCRIPTION_TYPE,
                                                Imps.Contacts.SUBSCRIPTION_STATUS,
                                                Imps.Presence.PRESENCE_STATUS,
                                                Imps.Presence.PRESENCE_CUSTOM_STATUS,
                                                Imps.Chats.LAST_MESSAGE_DATE,
                                                Imps.Chats.LAST_UNREAD_MESSAGE,
                                                Imps.Contacts.AVATAR_DATA
                                                
    };
    
    static final String[] CONTACT_PROJECTION_LIGHT = { Imps.Contacts._ID, Imps.Contacts.PROVIDER,
                                                 Imps.Contacts.ACCOUNT, Imps.Contacts.USERNAME,
                                                 Imps.Contacts.NICKNAME, Imps.Contacts.TYPE,
                                                 Imps.Contacts.SUBSCRIPTION_TYPE,
                                                 Imps.Contacts.SUBSCRIPTION_STATUS,
                                                 Imps.Presence.PRESENCE_STATUS,
                                                 Imps.Presence.PRESENCE_CUSTOM_STATUS,
                                                 Imps.Chats.LAST_MESSAGE_DATE,
                                                 Imps.Chats.LAST_UNREAD_MESSAGE
                                                 
     };

    static final int COLUMN_CONTACT_ID = 0;
    static final int COLUMN_CONTACT_PROVIDER = 1;
    static final int COLUMN_CONTACT_ACCOUNT = 2;
    static final int COLUMN_CONTACT_USERNAME = 3;
    static final int COLUMN_CONTACT_NICKNAME = 4;
    static final int COLUMN_CONTACT_TYPE = 5;
    static final int COLUMN_SUBSCRIPTION_TYPE = 6;
    static final int COLUMN_SUBSCRIPTION_STATUS = 7;
    static final int COLUMN_CONTACT_PRESENCE_STATUS = 8;
    static final int COLUMN_CONTACT_CUSTOM_STATUS = 9;
    static final int COLUMN_LAST_MESSAGE_DATE = 10;
    static final int COLUMN_LAST_MESSAGE = 11;
    static final int COLUMN_AVATAR_DATA = 12;

    private ImApp app = null;
    private static Drawable BG_DARK;
    private static Drawable BG_LIGHT;
    static Drawable AVATAR_DEFAULT = null;
    static Drawable AVATAR_DEFAULT_GROUP = null;
    
    public ContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
     
        app = ((ImApp)((Activity) getContext()).getApplication());
        
        if (BG_DARK == null)
        {
            BG_DARK = getResources().getDrawable(R.drawable.message_view_rounded_dark);
            BG_LIGHT = getResources().getDrawable(R.drawable.message_view_rounded_light);
            
        }
        
    }

    static class ViewHolder 
    {

        TextView mLine1;
        TextView mLine2;
        ImageView mAvatar;
        ImageView mStatusIcon;
        ImageView mEncryptionIcon;
        View mContainer;
    }

    public void bind(Cursor cursor, String underLineText, boolean scrolling) {
        bind(cursor, underLineText, true, scrolling);
    }

    
    public void bind(Cursor cursor, String underLineText, boolean showChatMsg, boolean scrolling) {
        

        ViewHolder holder = (ViewHolder)getTag();
        
        if (holder.mContainer != null)
            if (app.isThemeDark())
            {
                holder.mContainer.setBackgroundDrawable(BG_DARK);                
            }
            else
            {
                holder.mContainer.setBackgroundDrawable(BG_LIGHT);
            }
       
        
        long providerId = cursor.getLong(COLUMN_CONTACT_PROVIDER);
        String address = cursor.getString(COLUMN_CONTACT_USERNAME);
        String displayName = cursor.getString(COLUMN_CONTACT_NICKNAME);
        int type = cursor.getInt(COLUMN_CONTACT_TYPE);
        String statusText = cursor.getString(COLUMN_CONTACT_CUSTOM_STATUS);
        String lastMsg = cursor.getString(COLUMN_LAST_MESSAGE);

        int presence = cursor.getInt(COLUMN_CONTACT_PRESENCE_STATUS);

        int subType = cursor.getInt(COLUMN_SUBSCRIPTION_TYPE);
        int subStatus = cursor.getInt(COLUMN_SUBSCRIPTION_STATUS);
        
        String nickname = displayName;
        
        if (nickname == null)
            nickname = address;
        
        BrandingResources brandingRes = app.getBrandingResource(providerId);

        
        if (!TextUtils.isEmpty(underLineText)) {
            // highlight/underline the word being searched
            String lowercase = nickname.toLowerCase();
            int start = lowercase.indexOf(underLineText.toLowerCase());
            if (start >= 0) {
                int end = start + underLineText.length();
                SpannableString str = new SpannableString(nickname);
                str.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                holder.mLine1.setText(str);

            }
            else
                holder.mLine1.setText(nickname);

        }
        else
            holder.mLine1.setText(nickname);
        
        if (holder.mStatusIcon != null)
        {
            Drawable statusIcon = brandingRes.getDrawable(PresenceUtils.getStatusIconId(presence));
            statusIcon.setBounds(0, 0, statusIcon.getIntrinsicWidth(),
                    statusIcon.getIntrinsicHeight());
            holder.mStatusIcon.setImageDrawable(statusIcon);
        }
        
        if (holder.mAvatar != null)
        {
            if (Imps.Contacts.TYPE_GROUP == type) {

                holder.mAvatar.setVisibility(View.VISIBLE);

                if (AVATAR_DEFAULT_GROUP == null)
                    AVATAR_DEFAULT_GROUP = new RoundedAvatarDrawable(BitmapFactory.decodeResource(getResources(),
                            R.drawable.group_chat));
                    
                    holder.mAvatar.setImageDrawable(AVATAR_DEFAULT_GROUP);
                
                holder.mStatusIcon.setVisibility(View.GONE);
                
            }
            else if (cursor.getColumnIndex(Imps.Contacts.AVATAR_DATA)!=-1)
            {
                holder.mAvatar.setVisibility(View.GONE);        

                Drawable avatar = DatabaseUtils.getAvatarFromCursor(cursor, COLUMN_AVATAR_DATA, ImApp.DEFAULT_AVATAR_WIDTH,ImApp.DEFAULT_AVATAR_HEIGHT);
                 
                if (avatar != null)
                    holder.mAvatar.setImageDrawable(avatar);
                else 
                {
                    if (AVATAR_DEFAULT == null)
                    AVATAR_DEFAULT = new RoundedAvatarDrawable(BitmapFactory.decodeResource(getResources(),
                            R.drawable.avatar_unknown));
                    
                    holder.mAvatar.setImageDrawable(AVATAR_DEFAULT);
                    
                }
                
                holder.mAvatar.setVisibility(View.VISIBLE);

            }
            else
            {
                //holder.mAvatar.setImageDrawable(getContext().getResources().getDrawable(R.drawable.avatar_unknown));
                holder.mAvatar.setVisibility(View.GONE);
               
                
                
            }
        }
        
        if (showChatMsg && lastMsg != null) {

           
            if (holder.mLine2 != null)
                holder.mLine2.setText(android.text.Html.fromHtml(lastMsg).toString());
                        
        }
        else 
        {
            if (holder.mLine2 != null)                
            {
                
                if (statusText == null || statusText.length() == 0)
                {
                    if (Imps.Contacts.TYPE_GROUP == type) 
                    {
                        statusText = getContext().getString(R.string.menu_new_group_chat);
                    }
                    else
                    {
                        statusText = brandingRes.getString(PresenceUtils.getStatusStringRes(presence));
                    }
                }
                
                holder.mLine2.setText(statusText);
                
            }
            
        }
        

        if (subType == Imps.ContactsColumns.SUBSCRIPTION_TYPE_INVITATIONS)
        {
        //    if (holder.mLine2 != null)
          //      holder.mLine2.setText("Contact List Request");
        }
        
        holder.mLine1.setVisibility(View.VISIBLE);
       
    }
    
    /*
    private String queryGroupMembers(ContentResolver resolver, long groupId) {
        String[] projection = { Imps.GroupMembers.NICKNAME };
        Uri uri = ContentUris.withAppendedId(Imps.GroupMembers.CONTENT_URI, groupId);
        Cursor c = resolver.query(uri, projection, null, null, null);
        StringBuilder buf = new StringBuilder();
        if (c != null) {
            while (c.moveToNext()) {
                buf.append(c.getString(0));
                if (!c.isLast()) {
                    buf.append(',');
                }
            }
        }
        c.close();
        
        return buf.toString();
    }*/
    

}
