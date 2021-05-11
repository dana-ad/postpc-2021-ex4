package exercise.find.roots;

import android.content.Intent;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MainActivityTest extends TestCase {

  @Test
  public void when_activityIsLaunching_then_theButtonShouldStartDisabled(){
    // create a MainActivity and let it think it's currently displayed on the screen
    MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

    // test: make sure that the "calculate" button is disabled
    Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);
    assertFalse(button.isEnabled());
  }

  @Test
  public void when_activityIsLaunching_then_theEditTextShouldStartEmpty(){
    // create a MainActivity and let it think it's currently displayed on the screen
    MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

    // test: make sure that the "input" edit-text has no text
    EditText inputEditText = mainActivity.findViewById(R.id.editTextInputNumber);
    String input = inputEditText.getText().toString();
    assertTrue(input.isEmpty());
  }

  @Test
  public void when_userIsEnteringNumberInput_and_noCalculationAlreadyHappned_then_theButtonShouldBeEnabled(){
    // create a MainActivity and let it think it's currently displayed on the screen
    MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

    // find the edit-text and the button
    EditText inputEditText = mainActivity.findViewById(R.id.editTextInputNumber);
    Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);

    // test: insert input to the edit text and verify that the button is enabled
    inputEditText.setText("5678764");
    assertTrue(button.isEnabled());
  }

  @Test
  public void when_activity_launches_progress_starts_hidden(){
    // create a MainActivity and let it think it's currently displayed on the screen
    MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

    // find the edit-text and the button
    ProgressBar progress = mainActivity.findViewById(R.id.progressBar);

    // test: insert input to the edit text and verify that the button is enabled
    assertEquals(progress.getVisibility(), View.GONE);
  }

  @Test
  public void when_starting_a_calculation_button_is_disabled(){
    // create a MainActivity and let it think it's currently displayed on the screen
    MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

    // find the edit-text and the button
    EditText inputEditText = mainActivity.findViewById(R.id.editTextInputNumber);
    Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);

    // test: insert input to the edit text and verify that the button is enabled
    inputEditText.setText("5678764");
    button.performClick();
    assertFalse(button.isEnabled());
  }

  @Test
  public void check_broadcast_success_and_failure_cases(){
    // create a MainActivity and let it think it's currently displayed on the screen
    MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

    // find the button
    Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);
    ProgressBar progress = mainActivity.findViewById(R.id.progressBar);
    Intent broadcast = new Intent("stopped_calculations");
    RuntimeEnvironment.application.sendBroadcast(broadcast);
    Shadows.shadowOf(Looper.getMainLooper()).idle();

    // test: insert input to the edit text and verify that the button is enabled
    assertTrue(button.isEnabled());
    assertEquals(progress.getVisibility(), View.GONE);
  }
}