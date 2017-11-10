package com.example.azeddine.gdgschoolchatroom.authFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.azeddine.gdgschoolchatroom.R;

/**
 * Created by azeddine on 11/8/17.
 */

public class PinCodeFragment extends Fragment {


    private EditText mPinCodeEditText;
    private Button mSubmissionButton;
    private Context mContext;

    /**
     * an interface used to trigger an event to the Host activity when the user submit the pin code
     */
    public interface OnPinCodeSubmittedListener{
        void onPinCodeSubmitted(String codePin);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        View view = inflater.inflate(R.layout.fragment_pincode,container,false);
        mPinCodeEditText = view.findViewById(R.id.pinCode_editText);
        mSubmissionButton = view.findViewById(R.id.start_btn);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        /**
         * 1 - listen to the submit button click event
         * 2 - get the entered pin code by the user
         * 3 - pass the fetched data to the host activity that needs to implement the OnPinCodeSubmitted interface
         */
        super.onViewCreated(view, savedInstanceState);
        mSubmissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pinCode = mPinCodeEditText.getText().toString();
                ((OnPinCodeSubmittedListener) mContext).onPinCodeSubmitted(pinCode);
            }
        });
    }
}
