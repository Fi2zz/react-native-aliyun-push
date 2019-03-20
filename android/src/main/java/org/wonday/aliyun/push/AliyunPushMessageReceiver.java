/**
 * Copyright (c) 2017-present, Wonday (@wonday.org)
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.wonday.aliyun.push;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Map;

import javax.annotation.Nullable;

public class AliyunPushMessageReceiver extends MessageReceiver {
    public final static String TAG = "AliyunPush";

    public static ReactApplicationContext context;
    public static AliyunPushMessageReceiver instance;

    private final String ALIYUN_PUSH_TYPE_MESSAGE = "message";
    private final String ALIYUN_PUSH_TYPE_NOTIFICATION = "notification";

    private static int mCounter = 0;

    public AliyunPushMessageReceiver() {
        super();
        instance = this;
    }

    public static int getCounter() {
        return mCounter < 0 ? 0 : mCounter;
    }

    public static void setCounter(int counter) {
        mCounter = counter < 0 ? 0 : counter;
    }

    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {

        super.onMessage(context, cPushMessage);

        WritableMap params = Arguments.createMap();
        params.putString("messageId", cPushMessage.getMessageId());
        params.putString("body", cPushMessage.getContent());
        params.putString("title", cPushMessage.getTitle());
        params.putString("type", ALIYUN_PUSH_TYPE_MESSAGE);

        sendEvent("aliyunPushReceived", params);

        mCounter++;
        Log.d(TAG, "onMessage: " + mCounter);
    }

    @Override
    protected void onNotification(Context context, String title, String content, Map<String, String> extraMap) {
        FLog.d(ReactConstants.TAG, "onNotification.");

        super.onNotification(context, title, content, extraMap);

        WritableMap params = Arguments.createMap();
        params.putString("body", content);
        params.putString("title", title);

        WritableMap extraWritableMap = Arguments.createMap();
        for (Map.Entry<String, String> entry : extraMap.entrySet()) {
            extraWritableMap.putString(entry.getKey(),entry.getValue());
        }
        params.putMap("extras", extraWritableMap);

        params.putString("type", ALIYUN_PUSH_TYPE_NOTIFICATION);

        sendEvent("aliyunPushReceived", params);

        mCounter++;
        Log.d(TAG, "onNotification: " + mCounter);
    }

    @Override
    protected void onNotificationOpened(Context context, String title, String content, String extraMap) {
        FLog.d(ReactConstants.TAG, "onNotificationOpened.");

        super.onNotificationOpened(context, title, content, extraMap);

        WritableMap params = Arguments.createMap();
        params.putString("body", content);
        params.putString("title", title);
        params.putString("extraStr", extraMap);

        params.putString("type", ALIYUN_PUSH_TYPE_NOTIFICATION);
        params.putString("actionIdentifier", "opened");

        sendEvent("aliyunPushReceived", params);

        mCounter--;
        Log.d(TAG, "onNotificationOpened: " + mCounter);
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String content, String extraMap) {
        FLog.d(ReactConstants.TAG, "onNotificationClickedWithNoAction.");

        super.onNotificationOpened(context, title, content, extraMap);

        WritableMap params = Arguments.createMap();
        params.putString("body", content);
        params.putString("title", title);
        params.putString("extraStr", extraMap);

        params.putString("type", ALIYUN_PUSH_TYPE_NOTIFICATION);
        params.putString("actionIdentifier", "opened");

        sendEvent("aliyunPushReceived", params);

        mCounter--;
        Log.d(TAG, "onNotificationClickedWithNoAction: " + mCounter);
    }

    @Override
    protected void onNotificationRemoved(Context context, String messageId){
        FLog.d(ReactConstants.TAG, "onNotificationRemoved: messageId=" +  messageId);

        super.onNotificationRemoved(context, messageId);

        WritableMap params = Arguments.createMap();
        params.putString("messageId", messageId);

        params.putString("type", ALIYUN_PUSH_TYPE_NOTIFICATION);
        params.putString("actionIdentifier", "removed");

        sendEvent("aliyunPushReceived", params);

        mCounter--;
        Log.d(TAG, "onNotificationRemoved: " + mCounter);
    }

    @Override
    public void onNotificationReceivedInApp(Context context, String title, String content, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        FLog.d(ReactConstants.TAG, "onNotificationReceivedInApp");

        super.onNotificationReceivedInApp(context, title, content, extraMap, openType, openActivity, openUrl);

        WritableMap params = Arguments.createMap();
        params.putString("content", content);
        params.putString("title", title);
        params.putString("openType", String.valueOf(openType));
        params.putString("openActivity", openActivity);
        params.putString("openUrl", openUrl);

        WritableMap extraWritableMap = Arguments.createMap();
        for (Map.Entry<String, String> entry : extraMap.entrySet()) {
            extraWritableMap.putString(entry.getKey(),entry.getValue());
        }
        params.putMap("extras", extraWritableMap);

        params.putString("type", ALIYUN_PUSH_TYPE_NOTIFICATION);

        sendEvent("aliyunPushReceived", params);

        mCounter++;
        Log.d(TAG, "onNotificationReceivedInApp: " + mCounter);
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        if (context == null) {
            params.putString("appState", "background");
            initialMessage = params;
            FLog.d(ReactConstants.TAG, "reactContext==null");
        }else{
            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
        }
    }
    public static WritableMap initialMessage = null;

}