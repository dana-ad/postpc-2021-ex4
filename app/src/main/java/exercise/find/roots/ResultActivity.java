package exercise.find.roots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        if (intent == null)
            return;
        long origin_num = intent.getLongExtra("original_number", 0L);
        long calc_time = intent.getLongExtra("calculation_time", 0L);
        long root1 = intent.getLongExtra("root1", 0L);
        long root2 = intent.getLongExtra("root2", 0L);
        TextView chosen_number = findViewById(R.id.origin_number);
        TextView roots_result = findViewById(R.id.roots);
        TextView calculation_time = findViewById(R.id.calc_time);
        chosen_number.setText(String.format(getString(R.string.origin_number), origin_num));
        calculation_time.setText(String.format(getString(R.string.calc_time), calc_time));
        roots_result.setText(String.format(getString(R.string.roots), root1, root2, origin_num));
        }
    }
