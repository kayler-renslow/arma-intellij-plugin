package com.kaylerrenslow.a3plugin.components.versionChecker;

import com.intellij.notification.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * Created on 05/10/2016.
 */
class PluginVersionNotification extends Notification {
	private static final String GROUP_ID = "Arma Plugin Version Notification";
	private static final NotificationGroup MY_GROUP = new NotificationGroup(GROUP_ID, NotificationDisplayType.STICKY_BALLOON, false);

	private PluginVersionNotification(@NotNull String title, @NotNull String content, @NotNull NotificationType type, @Nullable NotificationListener listener) {
		super(MY_GROUP.getDisplayId(), title, content, type, listener);
	}

	static void showNewNotification(@NotNull String title, @NotNull String content, @NotNull NotificationType type, @Nullable NotificationListener listener){
		PluginVersionNotification notification = new PluginVersionNotification(title, content, type, listener);
		Notifications.Bus.notify(notification);
	}
}
