package com.example.gaston.util;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private List<Notification> notifications;

    @Override
    public void onCreate() {
        super.onCreate();
        // Força o uso do tema claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Inicializa a lista de notificações
        notifications = new ArrayList<>();
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }
}
