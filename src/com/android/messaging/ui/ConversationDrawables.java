/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.messaging.ui;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.android.messaging.Factory;
import com.android.messaging.R;
import com.android.messaging.util.ImageUtils;

/**
 * A singleton cache that holds tinted drawable resources for displaying messages, such as
 * message bubbles, audio attachments etc.
 */
public class ConversationDrawables {
    public static final int PRIMARY_COLOR      = 0;
    public static final int PRIMARY_COLOR_DARK = 1;
    public static final int ACCENT_COLOR       = 2;
    public static final int LETTER_TILE_COLOR  = 3;

    private static ConversationDrawables sInstance;

    // Cache the color filtered bubble drawables so that we don't need to create a
    // new one for each ConversationMessageView.
    private final Context mContext;
    private int mPrimaryColor;
    private int mPrimaryColorDark;
    private int mAccentColor;
    private int mIncomingAudioButtonColor;
    private int mIncomingErrorBubbleColor;
    private int mOutgoingBubbleColor;
    private int mSelectedBubbleColor;
    private int mLetterTileColor;
    private Drawable mIncomingBubbleDrawable;
    private Drawable mOutgoingBubbleDrawable;
    private Drawable mIncomingErrorBubbleDrawable;
    private Drawable mIncomingBubbleNoArrowDrawable;
    private Drawable mOutgoingBubbleNoArrowDrawable;
    private Drawable mAudioPlayButtonDrawable;
    private Drawable mAudioPlayButtonOutgoingNightDrawable;
    private Drawable mAudioPauseButtonDrawable;
    private Drawable mAudioPauseButtonOutgoingNightDrawable;
    private Drawable mIncomingAudioProgressBackgroundDrawable;
    private Drawable mOutgoingAudioProgressBackgroundDrawable;
    private Drawable mAudioProgressForegroundDrawable;
    private Drawable mFastScrollThumbDrawable;
    private Drawable mFastScrollThumbPressedDrawable;
    private Drawable mFastScrollPreviewDrawableLeft;
    private Drawable mFastScrollPreviewDrawableRight;

    public static ConversationDrawables get() {
        if (sInstance == null) {
            sInstance = new ConversationDrawables(Factory.get().getApplicationContext());
        }
        return sInstance;
    }

    private ConversationDrawables(final Context context) {
        mContext = context;
        // Pre-create all the drawables.
        updateDrawables();
    }

    public void updateDrawables() {
        final Resources resources = mContext.getResources();

        mPrimaryColor = resources.getColor(R.color.primary_color);
        mPrimaryColorDark = resources.getColor(R.color.primary_color_dark);
        mAccentColor = resources.getColor(R.color.accent_color);
        mIncomingErrorBubbleColor =
                resources.getColor(R.color.message_error_bubble_color_incoming);
        mIncomingAudioButtonColor =
                resources.getColor(R.color.message_audio_button_color_incoming);
        mOutgoingBubbleColor = resources.getColor(R.color.message_bubble_color_outgoing);
        mSelectedBubbleColor = resources.getColor(R.color.message_bubble_color_selected);
        mLetterTileColor = resources.getColor(R.color.letter_tile_default_color);
        mIncomingBubbleDrawable = resources.getDrawable(R.drawable.msg_bubble_incoming);
        mIncomingBubbleNoArrowDrawable =
                resources.getDrawable(R.drawable.message_bubble_incoming_no_arrow);
        mIncomingErrorBubbleDrawable = resources.getDrawable(R.drawable.msg_bubble_error);
        mOutgoingBubbleDrawable =  resources.getDrawable(R.drawable.msg_bubble_outgoing);
        mOutgoingBubbleNoArrowDrawable =
                resources.getDrawable(R.drawable.message_bubble_outgoing_no_arrow);
        mAudioPlayButtonDrawable = resources.getDrawable(R.drawable.ic_audio_play);
        mAudioPlayButtonOutgoingNightDrawable = resources.getDrawable(R.drawable.ic_audio_play_night);
        mAudioPauseButtonDrawable = resources.getDrawable(R.drawable.ic_audio_pause);
        mAudioPauseButtonOutgoingNightDrawable = resources.getDrawable(R.drawable.ic_audio_pause_night);
        mIncomingAudioProgressBackgroundDrawable =
                resources.getDrawable(R.drawable.audio_progress_bar_background_incoming);
        mOutgoingAudioProgressBackgroundDrawable =
                resources.getDrawable(R.drawable.audio_progress_bar_background_outgoing);
        mAudioProgressForegroundDrawable =
                resources.getDrawable(R.drawable.audio_progress_bar_progress);
        mFastScrollThumbDrawable = resources.getDrawable(R.drawable.fastscroll_thumb);
        mFastScrollThumbPressedDrawable =
                resources.getDrawable(R.drawable.fastscroll_thumb_pressed);
        mFastScrollPreviewDrawableLeft =
                resources.getDrawable(R.drawable.fastscroll_preview_left);
        mFastScrollPreviewDrawableRight =
                resources.getDrawable(R.drawable.fastscroll_preview_right);
    }

    public int getDefaultPrimaryColor() {
        return mPrimaryColor;
    }

    public int getDefaultAccentColor() {
        return mAccentColor;
    }

    public int getDefaultLetterTileColor() {
        return mLetterTileColor;
    }

    private int getAudioButtonColor(final boolean incoming, final String identifier) {
        return incoming ? mIncomingAudioButtonColor : getContactThemeColor(identifier, ACCENT_COLOR);
    }

    public int getContactThemeColor(final String identifier, final int colorDefault) {
        int defaultColor = mPrimaryColor;
        if (colorDefault == PRIMARY_COLOR_DARK) {
            defaultColor = mPrimaryColorDark;
        } else if (colorDefault == ACCENT_COLOR) {
            defaultColor = mAccentColor;
        } else if (colorDefault == LETTER_TILE_COLOR) {
            defaultColor = mLetterTileColor;
        }

        if (TextUtils.isEmpty(identifier)) {
            return defaultColor;
        } else {
            TypedArray colors = mContext.getResources().obtainTypedArray(colorDefault == PRIMARY_COLOR_DARK
                    ? R.array.letter_tile_colors_dark : R.array.letter_tile_colors);
            int color = Math.abs(identifier.hashCode()) % colors.length();
            return colors.getColor(color, defaultColor);
        }
    }

    public Drawable getBubbleDrawable(final boolean selected, final boolean incoming,
            final boolean needArrow, final boolean isError, final String identifier) {
        final Drawable protoDrawable;
        if (needArrow) {
            if (incoming) {
                protoDrawable = isError && !selected ?
                        mIncomingErrorBubbleDrawable : mIncomingBubbleDrawable;
            } else {
                protoDrawable = mOutgoingBubbleDrawable;
            }
        } else if (incoming) {
            protoDrawable = mIncomingBubbleNoArrowDrawable;
        } else {
            protoDrawable = mOutgoingBubbleNoArrowDrawable;
        }

        int color;
        if (selected) {
            color = mSelectedBubbleColor;
        } else if (incoming) {
            if (isError) {
                color = mIncomingErrorBubbleColor;
            } else {
                color = getContactThemeColor(identifier, ACCENT_COLOR);
            }
        } else {
            color = mOutgoingBubbleColor;
        }

        return ImageUtils.getTintedDrawable(mContext, protoDrawable, color);
    }

    public Drawable getPlayButtonDrawable(final boolean incoming, final String identifier) {
        return ImageUtils.getTintedDrawable(
                mContext, incoming ? mAudioPlayButtonDrawable : getPlayButtonOutgoingDrawable(),
                getAudioButtonColor(incoming, identifier));
    }

    public Drawable getPauseButtonDrawable(final boolean incoming, final String identifier) {
        return ImageUtils.getTintedDrawable(
                mContext,  incoming ? mAudioPauseButtonDrawable : getPauseButtonOutgoingDrawable(),
                getAudioButtonColor(incoming, identifier));
    }

    private Drawable getPlayButtonOutgoingDrawable() {
        final UiModeManager uiManager = (UiModeManager) mContext.getSystemService(
                Context.UI_MODE_SERVICE);
        boolean isDayTheme = uiManager.getNightMode() == 1;
        if (isDayTheme) {
            return mAudioPlayButtonDrawable;
        } else {
            return mAudioPlayButtonOutgoingNightDrawable;
        }
    }

    private Drawable getPauseButtonOutgoingDrawable() {
        final UiModeManager uiManager = (UiModeManager) mContext.getSystemService(
                Context.UI_MODE_SERVICE);
        boolean isDayTheme = uiManager.getNightMode() == 1;
        if (isDayTheme) {
            return mAudioPauseButtonDrawable;
        } else {
            return mAudioPauseButtonOutgoingNightDrawable;
        }
    }

    public Drawable getAudioProgressDrawable(final boolean incoming, final String identifier) {
        return ImageUtils.getTintedDrawable(
                mContext, mAudioProgressForegroundDrawable, getAudioButtonColor(incoming, identifier));
    }

    public Drawable getAudioProgressBackgroundDrawable(final boolean incoming) {
        return incoming ? mIncomingAudioProgressBackgroundDrawable :
            mOutgoingAudioProgressBackgroundDrawable;
    }

    public Drawable getFastScrollThumbDrawable(final boolean pressed, final String identifier) {
        if (pressed) {
            return ImageUtils.getTintedDrawable(mContext, mFastScrollThumbPressedDrawable,
                    getContactThemeColor(identifier, ACCENT_COLOR));
        } else {
            return mFastScrollThumbDrawable;
        }
    }

    public Drawable getFastScrollPreviewDrawable(boolean positionRight, final String identifier) {
        Drawable protoDrawable = positionRight ? mFastScrollPreviewDrawableRight :
            mFastScrollPreviewDrawableLeft;
        return ImageUtils.getTintedDrawable(mContext, protoDrawable, getContactThemeColor(identifier, ACCENT_COLOR));
    }
}
