package jp.thotta.android.recommendapps;

import android.content.Context;
import android.text.Layout;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * Created by thotta on 15/01/05.
 */
public class Utility {
    public static AdView appendAdView(Context context, FrameLayout adFrameLayout) {
        AdView adView = new AdView(context);
        adView.setAdUnitId(context.getString(R.string.ad_unit_id));
        adView.setAdSize(AdSize.BANNER);
        adFrameLayout.addView(adView);
        com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder()
                .addTestDevice(context.getString(R.string.test_device_id))
                .build();
        adView.loadAd(adRequest);
        return adView;
    }
}
