package com.example.gonmator.lalista_draft.presentation;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.example.gonmator.lalista_draft.R;

/**
 * Created by gonmator on 13.08.17.
 */

public class ConfigmDialogFragment extends AppCompatDialogFragment {
    interface ConfirmDialogListener {
        void onConfirmedClick(String tag, Bundle context);
    }

    ConfirmDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ConfirmDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + "must implement ConfirmDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        String title = arguments.getString("title");
        String message = arguments.getString("message");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onConfirmedClick(getTag(), arguments.getBundle("context"));
                dismiss();
            }
        });

        return builder.create();
    }
}