package github.nisrulz.scripts.rateapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The type Rate my app.
 */
public class RateMyApp {

  private final static String APP_TITLE = "YOUR-APP-NAME";
  private final static String APP_PNAME = "YOUR-PACKAGE-NAME";

  private final static int DAYS_UNTIL_PROMPT = 3;
  private final static int LAUNCHES_UNTIL_PROMPT = 7;

  /**
   * App launched.
   *
   * @param mContext the m context
   */
  public static void app_launched(Context mContext) {
    SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
    if (prefs.getBoolean("dontshowagain", false)) {
      return;
    }

    SharedPreferences.Editor editor = prefs.edit();

    // Increment launch counter
    long launch_count = prefs.getLong("launch_count", 0) + 1;
    editor.putLong("launch_count", launch_count);

    // Get date of first launch
    Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
    if (date_firstLaunch == 0) {
      date_firstLaunch = System.currentTimeMillis();
      editor.putLong("date_firstlaunch", date_firstLaunch);
    }

    // Wait at least n days before opening
    if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
      if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT
          * 24
          * 60
          * 60
          * 1000)) {
        showRateDialog(mContext, editor);
      }
    }

    editor.commit();
  }

  /**
   * Show rate dialog.
   *
   * @param mContext the m context
   * @param editor the editor
   */
  public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
    final Dialog dialog = new Dialog(mContext);
    dialog.setTitle("Rate " + APP_TITLE);

    LinearLayout ll = new LinearLayout(mContext);
    ll.setOrientation(LinearLayout.VERTICAL);

    TextView tv = new TextView(mContext);
    tv.setText("If you enjoy using "
        + APP_TITLE
        + ", please take a moment to rate it. Thanks for your support!");
    tv.setWidth(240);
    tv.setPadding(4, 0, 4, 10);
    ll.addView(tv);

    Button b1 = new Button(mContext);
    b1.setText("Rate " + APP_TITLE);
    b1.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        try {
          mContext.startActivity(
              new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
        } catch (android.content.ActivityNotFoundException anfe) {
          mContext.startActivity(new Intent(Intent.ACTION_VIEW,
              Uri.parse("http://play.google.com/store/apps/details?id=" + APP_PNAME)));
        }
        dialog.dismiss();
      }
    });
    ll.addView(b1);

    Button b2 = new Button(mContext);
    b2.setText("Remind me later");
    b2.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        dialog.dismiss();
      }
    });
    ll.addView(b2);

    Button b3 = new Button(mContext);
    b3.setText("No, thanks");
    b3.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (editor != null) {
          editor.putBoolean("dontshowagain", true);
          editor.commit();
        }
        dialog.dismiss();
      }
    });
    ll.addView(b3);

    dialog.setContentView(ll);
    dialog.show();
  }
}