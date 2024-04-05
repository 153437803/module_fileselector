package lib.kalu.fileselector.filter;

import android.content.Context;
import androidx.annotation.IntDef;
import androidx.fragment.app.FragmentActivity;
import android.widget.Toast;

import lib.kalu.fileselector.widget.IncapableDialog;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class FilterFailCause {
    public static final int TOAST = 0x00;
    public static final int DIALOG = 0x01;
    public static final int NONE = 0x02;

    @Retention(SOURCE)
    @IntDef({TOAST, DIALOG, NONE})
    public @interface Form {
    }

    private int mForm = TOAST;
    private String mTitle;
    private String mMessage;

    public FilterFailCause(String message) {
        mMessage = message;
    }

    public FilterFailCause(String title, String message) {
        mTitle = title;
        mMessage = message;
    }

    public FilterFailCause(@Form int form, String message) {
        mForm = form;
        mMessage = message;
    }

    public FilterFailCause(@Form int form, String title, String message) {
        mForm = form;
        mTitle = title;
        mMessage = message;
    }

    public static void handleCause(Context context, FilterFailCause cause) {
        if (cause == null)
            return;

        switch (cause.mForm) {
            case NONE:
                // do nothing.
                break;
            case DIALOG:
                IncapableDialog incapableDialog = IncapableDialog.newInstance(cause.mTitle, cause.mMessage);
                incapableDialog.show(((FragmentActivity) context).getSupportFragmentManager(),
                        IncapableDialog.class.getName());
                break;
            case TOAST:
            default:
                Toast.makeText(context, cause.mMessage, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
