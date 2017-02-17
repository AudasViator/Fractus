package pro.prieran.fractus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FractusView fractusView = (FractusView) findViewById(R.id.fractus_view);
        final Button doButton = (Button) findViewById(R.id.button);

        doButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fractusView.onDoButtonClick();
            }
        });
    }
}
