package feipeng.andzop.Main;

import java.util.ArrayList;
import java.util.List;

import feipeng.andzop.render.BasicZoomControl;
import feipeng.andzop.render.PinchZoomListener;
import feipeng.andzop.render.RenderView;
import feipeng.andzop.render.SimpleZoomListener;
import feipeng.andzop.render.ZoomState;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * @author roman10
 * 	naming convention:
 *  class private fields start with pr, public fields start with pu
 *  method local fields start with l, input parameters start with _
 *  final fields start with c (as in constant)
 */

public class VideoPlayer extends Activity {
//	private SimpleZoomListener prZoomListener;
	private PinchZoomListener prPinchZoomListener;
	private BasicZoomControl prZoomControl;
	private RenderView prRenderView;
	private Context mContext;
	private PowerManager.WakeLock mWl;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this.getApplicationContext();
        /*force 32-bit display for better display quality*/
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    	lp.copyFrom(getWindow().getAttributes());
    	lp.format = PixelFormat.RGBA_8888;
        getWindow().setBackgroundDrawable(new ColorDrawable(0xff000000));
        getWindow().setAttributes(lp);
        /*get the screen size and initialize the render window size*/
        int lScreenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        int lScreenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        Intent lIntent = this.getIntent();
        //String lVideoFileName = lIntent.getStringExtra(VideoBrowser.pucVideoFileNameList);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, 
				"generateStreamletTask");
		mWl.acquire();
        ArrayList<String> lVideoFileNameList = lIntent.getStringArrayListExtra(VideoBrowser2.pucVideoFileNameList);
        prRenderView = new RenderView(this, lVideoFileNameList, lScreenWidth, lScreenHeight);
        //initilization of zoom listener and zoom control
        prZoomControl = new BasicZoomControl();
        prPinchZoomListener = new PinchZoomListener(mContext);
//        prZoomListener = new SimpleZoomListener();
//        prZoomListener.setZoomControl(prZoomControl);
        prPinchZoomListener.setZoomControl(prZoomControl);
        
        prRenderView.setZoomState(prZoomControl.getZoomState());
//        prRenderView.setOnTouchListener(prZoomListener);
        prRenderView.setOnTouchListener(prPinchZoomListener);
        prZoomControl.setAspectQuotient(prRenderView.getAspectQuotient());
        //width and height are reversed as it's horizontal view
        //RenderView lRenderView = new RenderView(this, lScreenHeight, lScreenWidth);
        setContentView(prRenderView);
    }
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		if (mWl != null) {
			mWl.release();
		}
	}
    
    final CharSequence[] view_options = {
			"Full View",
			"Auto"};
    private static final int VIEW_OPTION_FULL = 0;
    private static final int VIEW_OPTION_AUTO = 1;
    private void showViewOptions() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose an Action");
		builder.setItems(view_options, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	switch (item) {
//		    	case VIEW_OPTION_FULL:
//		    		ROISettingsStatic.setViewMode(mContext, 0);
//		    		prZoomState.setMode(MODE.FULL);
//		    		break;
//		    	case VIEW_OPTION_AUTO:
//		    		ROISettingsStatic.setViewMode(mContext, 1);
//		    		prZoomState.setMode(MODE.AUTO);
//		    		break;
//		    	default:
//		    		return;
		    	}
		    }
		});
		AlertDialog contextDialog = builder.create();
		contextDialog.show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {
    	_menu.add(1, Menu.FIRST, Menu.FIRST, "Update ROI Size");
    	_menu.add(1, Menu.FIRST + 1, Menu.FIRST + 1, "Change Mode");
    	_menu.add(1, Menu.FIRST + 2, Menu.FIRST + 2, "Zoom Level +");
    	_menu.add(1, Menu.FIRST + 3, Menu.FIRST + 3, "Zoom Level -");
        return super.onCreateOptionsMenu(_menu);	
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1:
        	Intent lIntent = new Intent();
			lIntent.setClass(mContext, ROIOptionsDialog.class);
			startActivityForResult(lIntent, REQUEST_UPDATE_ROI_OPTIONS);
            return true;
        case 2:
        	showViewOptions();
        	return true;
//        case 3:
//        	prRenderView.prZoomLevelUpdate = 1;
//        	return true;
//        case 4:
//        	prRenderView.prZoomLevelUpdate = -1;
//        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private static final int REQUEST_UPDATE_ROI_OPTIONS = 0;
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	switch (requestCode) {
    	case REQUEST_UPDATE_ROI_OPTIONS:
    		if (resultCode == RESULT_OK) {
    			//update the roi size
//    			prRenderView.updateROI();
    		}
    		break;
    	}
	}
    
//    //load the native library
//    static {
//    	System.loadLibrary("ffmpeg");
//    	System.loadLibrary("andzop");}
}
