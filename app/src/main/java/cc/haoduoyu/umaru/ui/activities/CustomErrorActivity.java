package cc.haoduoyu.umaru.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cc.haoduoyu.umaru.R;

public class CustomErrorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_error);

        TextView errorDetailsText = (TextView) findViewById(R.id.error_details);
        errorDetailsText.setText(CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, getIntent()));

        ImageView restartButton = (ImageView) findViewById(R.id.restart_button);

        final Class<? extends Activity> restartActivityClass = CustomActivityOnCrash.getRestartActivityClassFromIntent(getIntent());
        final CustomActivityOnCrash.EventListener eventListener = CustomActivityOnCrash.getEventListenerFromIntent(getIntent());

        if (restartActivityClass != null) {
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CustomErrorActivity.this, restartActivityClass);
                    CustomActivityOnCrash.restartApplicationWithIntent(CustomErrorActivity.this, intent, eventListener);
                }
            });
        } else {
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomActivityOnCrash.closeApplication(CustomErrorActivity.this, eventListener);
                }
            });
        }
    }
}
