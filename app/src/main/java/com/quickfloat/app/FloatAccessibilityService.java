package com.quickfloat.app;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.View;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.accessibility.AccessibilityEvent;

public class FloatAccessibilityService extends AccessibilityService {
    private static FloatAccessibilityService instance;
    private WindowManager wm;
    private View bubble, menu;
    private WindowManager.LayoutParams bubbleParams;
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    public static boolean isEnabled(Context c) {
        String enabled = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return enabled != null && enabled.toLowerCase().contains(c.getPackageName().toLowerCase());
    }
    public static void showBubble(Context c) {
        if (instance != null) instance.addBubble();
        else Toast.makeText(c, "הפעילו את שירות QuickFloat בהגדרות הנגישות ואז חזרו לאפליקציה", Toast.LENGTH_LONG).show();
    }
    @Override public void onServiceConnected() { super.onServiceConnected(); instance = this; addBubble(); }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) { }
    @Override public void onInterrupt() { }
    @Override public void onDestroy() { removeViews(); instance = null; super.onDestroy(); }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
    private TextView item(String label, Runnable action) {
        TextView t = new TextView(this); t.setText(label); t.setTextSize(16); t.setTextColor(Color.rgb(28,48,42));
        t.setPadding(dp(16),dp(12),dp(16),dp(12)); t.setBackground(shape(Color.WHITE,12));
        t.setOnClickListener(v -> { if (menu != null) { try { wm.removeView(menu); } catch(Exception ignored) {} menu=null; } action.run(); });
        return t;
    }
    private void addBubble() {
        if (bubble != null || !Settings.canDrawOverlays(this)) return;
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        TextView b = new TextView(this); b.setText("Q"); b.setTextColor(Color.WHITE); b.setTextSize(21); b.setTypeface(null, Typeface.BOLD); b.setGravity(Gravity.CENTER); b.setBackground(shape(0xFF128C7E,100)); b.setElevation(dp(8));
        bubbleParams = new WindowManager.LayoutParams(dp(56),dp(56),WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        bubbleParams.gravity = Gravity.TOP|Gravity.START; bubbleParams.x=dp(12); bubbleParams.y=dp(220);
        b.setOnTouchListener(new View.OnTouchListener() {
            float sx, sy; int ox, oy; boolean moved;
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction()==MotionEvent.ACTION_DOWN){sx=e.getRawX();sy=e.getRawY();ox=bubbleParams.x;oy=bubbleParams.y;moved=false;return true;}
                if(e.getAction()==MotionEvent.ACTION_MOVE){int dx=(int)(e.getRawX()-sx),dy=(int)(e.getRawY()-sy);if(Math.abs(dx)>dp(5)||Math.abs(dy)>dp(5))moved=true;bubbleParams.x=ox+dx;bubbleParams.y=oy+dy;try{wm.updateViewLayout(b,bubbleParams);}catch(Exception ignored){}return true;}
                if(e.getAction()==MotionEvent.ACTION_UP){if(!moved)showMenu();return true;}return true;
            }
        });
        bubble=b; try { wm.addView(bubble,bubbleParams); } catch(Exception e){ bubble=null; }
    }
    private void showMenu() {
        if(menu!=null){try{wm.removeView(menu);}catch(Exception ignored){}menu=null;return;}
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(8),dp(8),dp(8),dp(8)); box.setBackground(shape(0xFFF0F4F2,18)); box.setElevation(dp(12));
        TextView title=new TextView(this);title.setText("QuickFloat · פעולות");title.setTextSize(17);title.setTypeface(null,Typeface.BOLD);title.setTextColor(0xFF075E54);title.setPadding(dp(12),dp(10),dp(12),dp(10));box.addView(title);
        box.addView(item("↩  חזרה",()->performGlobalAction(GLOBAL_ACTION_BACK)));
        box.addView(item("⌂  מסך הבית",()->performGlobalAction(GLOBAL_ACTION_HOME)));
        box.addView(item("▢  אפליקציות אחרונות",()->performGlobalAction(GLOBAL_ACTION_RECENTS)));
        box.addView(item("▤  התראות",()->performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)));
        box.addView(item("☷  הגדרות מהירות",()->performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)));
        box.addView(item("⏻  תפריט הפעלה",()->performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)));
        WindowManager.LayoutParams p=new WindowManager.LayoutParams(dp(250),WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT);
        p.gravity=Gravity.TOP|Gravity.START;p.x=Math.max(dp(4),bubbleParams.x-dp(90));p.y=Math.max(dp(40),bubbleParams.y-dp(20));
        menu=box;try{wm.addView(menu,p);}catch(Exception e){menu=null;}
    }
    private void removeViews(){if(wm!=null){try{if(menu!=null)wm.removeView(menu);}catch(Exception ignored){}try{if(bubble!=null)wm.removeView(bubble);}catch(Exception ignored){}}menu=null;bubble=null;}
}
