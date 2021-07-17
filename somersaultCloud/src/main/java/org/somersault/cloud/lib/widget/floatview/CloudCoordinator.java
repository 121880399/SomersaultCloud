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

import android.view.View;

import org.somersault.cloud.lib.core.Cloud;


public final class CloudCoordinator {

    private static CloudCoordinator INSTANCE;
    private CloudTrashLayout trashView;

    private static CloudCoordinator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CloudCoordinator();
        }
        return INSTANCE;
    }

    private CloudCoordinator() {
    }

    public void notifyPositionChanged(CloudView bubble) {
        if (trashView == null) {
            return;
        }
        trashView.setVisibility(View.VISIBLE);
        if (isOverTrash(bubble)) {
            trashView.applyMagnetism();
            trashView.vibrate();
            applyTrashMagnetism(bubble);
        } else {
            trashView.releaseMagnetism();
        }
    }

    private void applyTrashMagnetism(CloudView bubble) {
        View trashContentView = getTrashContent();
        int trashCenterX = (trashContentView.getLeft() + (trashContentView.getMeasuredWidth() / 2));
        int trashCenterY = (trashContentView.getTop() + (trashContentView.getMeasuredHeight() / 2));
        int x = (trashCenterX - (bubble.getMeasuredWidth() / 2));
        int y = (trashCenterY - (bubble.getMeasuredHeight() / 2));
        bubble.setX(x);
        bubble.setY(y);
    }

    private boolean isOverTrash(CloudView bubble) {
        if (trashView.getVisibility() != View.VISIBLE) {
            return false;
        }
        boolean result = false;
        View trashContentView = getTrashContent();
        int trashWidth = trashContentView.getMeasuredWidth();
        int trashHeight = trashContentView.getMeasuredHeight();
        int trashLeft = (trashContentView.getLeft() - (trashWidth / 2));
        int trashRight = (trashContentView.getLeft() + trashWidth + (trashWidth / 2));
        int trashTop = (trashContentView.getTop() - (trashHeight / 2));
        int trashBottom = (trashContentView.getTop() + trashHeight + (trashHeight / 2));
        int bubbleWidth = bubble.getMeasuredWidth();
        int bubbleHeight = bubble.getMeasuredHeight();
        int bubbleLeft = (int) bubble.getX();
        int bubbleRight = bubbleLeft + bubbleWidth;
        int bubbleTop = (int) bubble.getY();
        int bubbleBottom = bubbleTop + bubbleHeight;
        if (bubbleLeft >= trashLeft && bubbleRight <= trashRight) {
            if (bubbleTop >= trashTop && bubbleBottom <= trashBottom) {
                result = true;
            }
        }
        return result;
    }

    public void notifyBubbleRelease(CloudView bubble) {
        if (trashView == null) {
            return;
        }
        if (isOverTrash(bubble)) {
            Cloud.Companion.getInstance().remove();
        }
        trashView.setVisibility(View.GONE);
    }

    private View getTrashContent() {
        return trashView.getChildAt(0);
    }

    public static class Builder {

        private CloudCoordinator layoutCoordinator;

        public Builder() {
            layoutCoordinator = getInstance();
        }

        public Builder setTrashView(CloudTrashLayout trashView) {
            layoutCoordinator.trashView = trashView;
            return this;
        }

        public CloudCoordinator build() {
            return layoutCoordinator;
        }

    }
}
