package com.example.gonmator.lalista_draft.presentation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gonmator.lalista_draft.R;

/**
 * Created by gonmator on 11.08.17.
 */

public class EditListDialogFragment extends AppCompatDialogFragment {
    public interface AddItemDialogListener {
        public void onDialogTextDone(String text);
    }

    AddItemDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (AddItemDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + " must implement AddItemDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        EditText editText = null;
        ViewGroup layout = (ViewGroup)getActivity().getLayoutInflater().inflate(
                R.layout.add_item, null);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view.getId() == R.id.editText) {
                ((EditText)view).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        mListener.onDialogTextDone(textView.getText().toString());
                        dismiss();
                        return true;
                    }
                });
                break;
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.new_list);
        builder.setView(layout);

        return builder.create();
    }
}
