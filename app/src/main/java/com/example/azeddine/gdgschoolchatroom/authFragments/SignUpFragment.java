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
import android.widget.TextView;

import com.example.azeddine.gdgschoolchatroom.R;

/**
 * Created by azeddine on 11/8/17.
 */

public class SignUpFragment extends Fragment {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private Button mSignUpButton;

    /**
     * an interface used to trigger an event to the Host activity when the user submit the sign up form
     */
    public interface OnSignUpListener{
        void onSignUp(String email,String password);
    }
    public SignUpFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up,container,false);
        mEmailEditText =  view.findViewById(R.id.email_editText);
        mPasswordEditText = view.findViewById(R.id.password_editText);
        mSignUpButton = view.findViewById(R.id.signUp_btn);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        /**
         * 1 - listen to the sign up button click event
         * 2 - get the entered email and password
         * 3 - pass the fetched data to the host activity that needs to implement the OnSignUpListener interface
         */
        super.onViewCreated(view, savedInstanceState);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                ((OnSignUpListener) getContext()).onSignUp(email,password);
            }
        });
    }
}
