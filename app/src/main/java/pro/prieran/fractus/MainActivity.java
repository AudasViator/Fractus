package pro.prieran.fractus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PatternView patternView = (PatternView) findViewById(R.id.pattern_view);
        final FractusView fractusView = (FractusView) findViewById(R.id.fractus_view);
        final Button doButton = (Button) findViewById(R.id.button_do);
        final Button clearButton = (Button) findViewById(R.id.button_clear);

        doButton.setOnClickListener(v -> fractusView.onDoButtonClick(patternView.getPatternPoints()));
        clearButton.setOnClickListener(v -> {
            fractusView.onClearButtonClick();
            patternView.onClearButtonClick();
        });
    }
}
