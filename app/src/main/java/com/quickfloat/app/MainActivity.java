package com.quickfloat.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private TextView text(String value, int size) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size);
        t.setTextColor(0xFF172B25); t.setPadding(dp(8), dp(10), dp(8), dp(10)); return t;
    }
    private Button button(String value, Runnable action) {
        Button b = new Button(this); b.setText(value); b.setAllCaps(false);
        b.setTextColor(0xFFFFFFFF); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF128C7E));
        b.setOnClickListener(v -> action.run()); return b;
    }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(0xFF075E54); getWindow().setNavigationBarColor(0xFF075E54);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(18),dp(12),dp(18),dp(18)); root.setBackgroundColor(0xFFF5F7F6);
        TextView header = text("QuickFloat", 28); header.setTextColor(0xFFFFFFFF); header.setBackgroundColor(0xFF075E54); header.setGravity(Gravity.CENTER_VERTICAL); root.addView(header);
        ScrollView scroll = new ScrollView(this); LinearLayout content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(0,dp(12),0,0);
        content.addView(text("קיצורי נגישות בכפתור צף", 22));
        content.addView(text("הפעלת השירות דורשת אישור מפורש בהגדרות הנגישות של Android. ההרשאה מאפשרת לבצע רק את פעולות המערכת שתבחרו.", 16));
        content.addView(text("1. הרשאת חלון צף", 18));
        content.addView(button("מתן הרשאת הצגה מעל אפליקציות", () -> { if (!Settings.canDrawOverlays(this)) startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()))); else FloatAccessibilityService.showBubble(this); }));
        content.addView(text("2. שירות נגישות", 18));
        content.addView(button("פתיחת הגדרות נגישות", () -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))));
        content.addView(text("לאחר הפעלת השירות, הכפתור הצף יופיע. לחיצה עליו תציג פעולות מערכת זמינות.", 15));
        content.addView(text("פעולות זמינות (בהתאם לגרסת Android):", 18));
        content.addView(text("• חזרה\n• מסך הבית\n• אפליקציות אחרונות\n• לוח התראות\n• הגדרות מהירות\n• תפריט הפעלה", 16));
        content.addView(text("הערה: Android והיצרן עשויים להגביל פעולות מסוימות. האפליקציה אינה קוראת תוכן מסך ואינה שולחת מידע.", 14));
        scroll.addView(content); root.addView(scroll, new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }
    @Override protected void onResume() { super.onResume(); if (Settings.canDrawOverlays(this) && FloatAccessibilityService.isEnabled(this)) FloatAccessibilityService.showBubble(this); }
}
