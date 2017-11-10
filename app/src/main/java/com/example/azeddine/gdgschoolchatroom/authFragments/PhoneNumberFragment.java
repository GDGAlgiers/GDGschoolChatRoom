package com.example.azeddine.gdgschoolchatroom.authFragments;

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
 * Created by azeddine on 11/10/17.
 */

public class PhoneNumberFragment extends Fragment {

    private Button mSendPinCodeButton;
    private EditText mPhoneNumberEditText;

    /**
     * an interface used to trigger an event to the Host activity when the user submit the phone number
     */
    public interface OnPhoneNumberSubmittedListener {
        void OnPhoneNumberSubmitted(String phoneNumber);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_number,container,false);
        mPhoneNumberEditText = view.findViewById(R.id.phoneNumber_editText);
        mSendPinCodeButton = view.findViewById(R.id.send_pinCode_btn);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        /**
         * 1 - listen to the send button click event
         * 2 - get the entered phone number by the user
         * 3 - pass the fetched data to the host activity that needs to implement the OnPhoneNumberSubmitted interface
         */
        super.onViewCreated(view, savedInstanceState);
        mSendPinCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = mPhoneNumberEditText.getText().toString();
                ((OnPhoneNumberSubmittedListener) getContext()).OnPhoneNumberSubmitted(phoneNumber);
            }
        });

    }
}
