package exercise.find.roots;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  private BroadcastReceiver broadcastReceiverForSuccess = null;
  private BroadcastReceiver broadcastReceiverForFailure = null;
  boolean calc_in_bg = false;
  EditText editTextUserInput;
  ProgressBar progressBar;
  Button buttonCalculateRoots;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    progressBar = findViewById(R.id.progressBar);
    editTextUserInput = findViewById(R.id.editTextInputNumber);
    buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

    // set initial UI:
    progressBar.setVisibility(View.GONE); // hide progress
    editTextUserInput.setText(""); // cleanup text in edit-text
    editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
    buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)

    // set listener on the input written by the keyboard to the edit-text
    editTextUserInput.addTextChangedListener(new TextWatcher() {
      public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
      public void onTextChanged(CharSequence s, int start, int before, int count) { }
      public void afterTextChanged(Editable s) {
        // text did change
        String newText = editTextUserInput.getText().toString();
        try {
          long userInputLong = Long.parseLong(newText);
          buttonCalculateRoots.setEnabled(userInputLong > 0);
        } catch (NumberFormatException e){
          buttonCalculateRoots.setEnabled(false);
        }
      }
    });

    // set click-listener to the button
    buttonCalculateRoots.setOnClickListener(v -> {
      long userInputLong;
      calc_in_bg = true;
      Intent intentToOpenService = new Intent(MainActivity.this, CalculateRootsService.class);
      String userInputString = editTextUserInput.getText().toString();
      userInputLong = Long.parseLong(userInputString);
      intentToOpenService.putExtra("number_for_service", userInputLong);
      startService(intentToOpenService);
      buttonCalculateRoots.setEnabled(false);
      editTextUserInput.setEnabled(false);
      progressBar.setVisibility(View.VISIBLE);

    });

    // register a broadcast-receiver to handle action "found_roots"
    broadcastReceiverForSuccess = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals("found_roots")) return;
        // success finding roots!
        calc_in_bg = false;
        long origin_num = incomingIntent.getLongExtra("original_number", 0L);
        long first_root = incomingIntent.getLongExtra("root1", 0L);
        long second_root = incomingIntent.getLongExtra("root2", 0L);
        long time = incomingIntent.getLongExtra("time_until_give_up_seconds", 0L);
        Intent intentToOpenNewActivity = new Intent(MainActivity.this, ResultActivity.class);
        intentToOpenNewActivity.putExtra("original_number", origin_num);
        intentToOpenNewActivity.putExtra("root1", first_root);
        intentToOpenNewActivity.putExtra("root2", second_root);
        intentToOpenNewActivity.putExtra("calculation_time", time);
        reset_mode();
        startActivity(intentToOpenNewActivity);
      }
    };
    registerReceiver(broadcastReceiverForSuccess, new IntentFilter("found_roots"));



    broadcastReceiverForFailure = new BroadcastReceiver(){
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals("stopped_calculations")) return;
        progressBar.setVisibility(View.GONE);
        editTextUserInput.setEnabled(true);
        buttonCalculateRoots.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        editTextUserInput.setText("");
        long time_out = incomingIntent.getLongExtra("time_until_give_up_seconds", 0L);
        Toast.makeText(MainActivity.this,
                "calculation aborted after "+time_out+" seconds", Toast.LENGTH_SHORT).show();
      }
    };
    registerReceiver(broadcastReceiverForFailure, new IntentFilter("stopped_calculations"));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.unregisterReceiver(broadcastReceiverForFailure);
    this.unregisterReceiver(broadcastReceiverForSuccess);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString("edited_user_input", editTextUserInput.getText().toString());
    outState.putBoolean("edit_text_enable", editTextUserInput.isEnabled());
    outState.putInt("progress_bar_visibility", progressBar.getVisibility());
    outState.putBoolean("button_enable", buttonCalculateRoots.isEnabled());
    outState.putBoolean("calc_in_bg", calc_in_bg);
  }

  @Override
  protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    editTextUserInput.setText(savedInstanceState.getString("edited_user_input"));
    editTextUserInput.setEnabled(savedInstanceState.getBoolean("edit_text_enable"));
    progressBar.setVisibility(savedInstanceState.getInt("progress_bar_visibility"));
    buttonCalculateRoots.setEnabled(savedInstanceState.getBoolean("button_enable"));
    calc_in_bg = savedInstanceState.getBoolean("calc_in_bg");

  }

  private void reset_mode(){
    progressBar.setVisibility(View.GONE);
    editTextUserInput.setText("");
    editTextUserInput.setEnabled(true);
    buttonCalculateRoots.setEnabled(false);
    calc_in_bg = false;

  }
}


/*

TODO:
the spec is:

upon launch, Activity starts out "clean":
* progress-bar is hidden
* "input" edit-text has no input and it is enabled
* "calculate roots" button is disabled

the button behavior is:
* when there is no valid-number as an input in the edit-text, button is disabled
* when we triggered a calculation and still didn't get any result, button is disabled
* otherwise (valid number && not calculating anything in the BG), button is enabled

the edit-text behavior is:
* when there is a calculation in the BG, edit-text is disabled (user can't input anything)
* otherwise (not calculating anything in the BG), edit-text is enabled (user can tap to open the keyboard and add input)

the progress behavior is:
* when there is a calculation in the BG, progress is showing
* otherwise (not calculating anything in the BG), progress is hidden

when "calculate roots" button is clicked:
* change states for the progress, edit-text and button as needed, so user can't interact with the screen

when calculation is complete successfully:
* change states for the progress, edit-text and button as needed, so the screen can accept new input
* open a new "success" screen showing the following data:
  - the original input number
  - 2 roots combining this number (e.g. if the input was 99 then you can show "99=9*11" or "99=3*33"
  - calculation time in seconds

when calculation is aborted as it took too much time:
* change states for the progress, edit-text and button as needed, so the screen can accept new input
* show a toast "calculation aborted after X seconds"


upon screen rotation (saveState && loadState) the new screen should show exactly the same state as the old screen. this means:
* edit-text shows the same input
* edit-text is disabled/enabled based on current "is waiting for calculation?" state
* progress is showing/hidden based on current "is waiting for calculation?" state
* button is enabled/disabled based on current "is waiting for calculation?" state && there is a valid number in the edit-text input


 */