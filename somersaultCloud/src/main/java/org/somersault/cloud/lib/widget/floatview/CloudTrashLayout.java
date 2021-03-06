/*
 * Copyright Txus Ballesteros 2015 (@txusballesteros)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
 */
package org.somersault.cloud.lib.widget.floatview;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.somersault.cloud.lib.R;


public class CloudTrashLayout extends FrameLayout {

    public static final int VIBRATION_DURATION_IN_MS = 70;
    private boolean magnetismApplied = false;
    private boolean attachedToWindow = false;
    private boolean isVibrateInThisSession = false;
    private CloudCoordinator layoutCoordinator;

    public CloudTrashLayout(Context context) {
        super(context);
    }

    public CloudTrashLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CloudTrashLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        attachedToWindow = false;
    }

    @Override
    public void setVisibility(int visibility) {
        if (attachedToWindow) {
            if (visibility != getVisibility()) {
                if (visibility == VISIBLE) {
                    playAnimation(R.animator.cloud_trash_shown_animator);


                } else {
                    playAnimation(R.animator.cloud_trash_hide_animator);
                }
            }
        }
        super.setVisibility(visibility);
    }

    public void applyMagnetism() {
        if (!magnetismApplied) {
            magnetismApplied = true;
            playAnimation(R.animator.cloud_trash_shown_magnetism_animator);
        }
    }

    public void vibrate() {
        if (isVibrateInThisSession) {
            return;
        }
        final Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATION_DURATION_IN_MS);
        isVibrateInThisSession = true;
    }

    public void releaseMagnetism() {
        if (magnetismApplied) {
            magnetismApplied = false;
            playAnimation(R.animator.cloud_trash_hide_magnetism_animator);
        }
        isVibrateInThisSession = false;
    }

    private void playAnimation(int animationResourceId) {
        if (isInEditMode()) {
            return;
        }
        AnimatorSet animator = (AnimatorSet) AnimatorInflater
                .loadAnimator(getContext(), animationResourceId);
        animator.setTarget(getChildAt(0));
        animator.start();
    }

    public void setLayoutCoordinator(CloudCoordinator layoutCoordinator) {
        this.layoutCoordinator = layoutCoordinator;
    }

    CloudCoordinator getLayoutCoordinator() {
        return layoutCoordinator;
    }
}
