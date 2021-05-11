package exercise.find.roots;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class CalculateRootsService extends IntentService {
  private static final long MAX_TIME_TO_CALC = 20;


  public CalculateRootsService() {
    super("CalculateRootsService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent == null) return;
    boolean time_out = false;
    Intent broadCastIntent;
    long root1, root2, seconds = 0;
    long timeStartMs = System.currentTimeMillis();
    long numberToCalculateRootsFor = intent.getLongExtra("number_for_service", 0);
    if (numberToCalculateRootsFor <= 0) {
      Log.e("CalculateRootsService", "can't calculate roots for non-positive input" + numberToCalculateRootsFor);
      return;
    }
    root1 = numberToCalculateRootsFor;
    root2 = 1;
    if (numberToCalculateRootsFor % 2 == 0){
      root1 = 2;
      root2 = numberToCalculateRootsFor/2;
      seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeStartMs);
    }
    else {
      double sqrt = Math.ceil(Math.sqrt(numberToCalculateRootsFor));
      for (int i = 3; i < sqrt; i = i + 2) {
        if (numberToCalculateRootsFor % i == 0) {
          root1 = i;
          root2 = numberToCalculateRootsFor / root1;
        }
        seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeStartMs);
        if (seconds > MAX_TIME_TO_CALC) {
          time_out = true;
          break;
        }
      }
    }
    if (time_out) {
      broadCastIntent = new Intent("stopped_calculations");
      broadCastIntent.putExtra("original_number", numberToCalculateRootsFor);
    }
    else {
      broadCastIntent = new Intent("found_roots");
      broadCastIntent.putExtra("original_number", numberToCalculateRootsFor);
      broadCastIntent.putExtra("root1", root1);
      broadCastIntent.putExtra("root2", root2);
    }
    broadCastIntent.putExtra("time_until_give_up_seconds", seconds);
    sendBroadcast(broadCastIntent);

    /*

      examples:
       for input "33", roots are (3, 11)
       for input "30", roots can be (3, 10) or (2, 15) or other options
       for input "17", roots are (17, 1)
       for input "829851628752296034247307144300617649465159", after 20 seconds give up

     */
  }
}
