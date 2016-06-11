package myapp.tae.ac.uk.autosuggester.UI;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

/**
 * Created by Karma on 09/06/16.
 */
public class CustomAutoComplete extends AutoCompleteTextView {
    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 500;

    private int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;
    private ProgressBar mLoadingIndicator;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CustomAutoComplete.super.performFiltering((CharSequence) msg.obj, msg.arg1);
        }
    };

    public CustomAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAutoCompleteDelay(int mAutoCompleteDelay) {
        this.mAutoCompleteDelay = mAutoCompleteDelay;
    }

    public void setLoadingIndicator(ProgressBar mLoadingIndicator) {
        this.mLoadingIndicator = mLoadingIndicator;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text), mAutoCompleteDelay);
    }

    @Override
    public void onFilterComplete(int count) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.GONE);
        }
        super.onFilterComplete(count);
    }
}
