package com.jasen.kimjaeseung.capstone_indoor;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

/**
 * Created by kimjaeseung on 2018. 5. 28..
 */

public class AdActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        ImageView imageView = (ImageView)findViewById(R.id.activity_ad_iv);

        String shopName = getIntent().getStringExtra("ad");

        switch (shopName) {
            case "nike":
                imageView.setImageResource(R.drawable.ad_nike);
                break;
            case "mcdonalds":
                imageView.setImageResource(R.drawable.ad_mcdonalds);
                break;
            default:
                imageView.setImageResource(R.drawable.ad_starbucks);
                break;
        }
    }
}
