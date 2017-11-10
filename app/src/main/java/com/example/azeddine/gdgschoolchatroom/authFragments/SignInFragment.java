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

public class SignInFragment extends Fragment {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mSignInButton;
    private TextView mNotRegisteredLink;
    private Button mPhoneSignInButton;
    private Context mContext;

    /**
     * an interface used to trigger an event to the Host activity when the user submit the sign in form
     */
    public interface OnSignInListener{
        void onSignIn(String email,String password);
    }
    /**
     * an interface used to trigger an event to the Host activity when the user click on the registered link
     */
    public interface OnRegisterLinkClickListener {
        void onRegisterLinkClick();
    }
    /**
     * an interface used to trigger an event to the Host activity when the user click on the sign in with phone number button
     */
    public interface OnPhoneSignInLinkClickListener {
         void onPhoneSignInLinkClick();
    }

    public SignInFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        View view = inflater.inflate(R.layout.fragment_sign_in,container,false);
        mEmailEditText =  view.findViewById(R.id.email_editText);
        mPasswordEditText = view.findViewById(R.id.password_editText);
        mSignInButton = view.findViewById(R.id.signIn_btn);
        mNotRegisteredLink = view.findViewById(R.id.not_registered_link);
        mPhoneSignInButton = view.findViewById(R.id.phoneNumber_signIn);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /**
         * 1 - listen to the sign in button click event
         * 2 - get the entered email and password
         * 3 - pass the fetched data to the host activity that needs to implement the OnSignInListener interface
         */
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                ((OnSignInListener) mContext).onSignIn(email,password);
            }
        });
        /**
         * 1 - listen to the click on the go to register link
         * 3 - pass the event to the host activity
         */
        mNotRegisteredLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnRegisterLinkClickListener) mContext).onRegisterLinkClick();
            }
        });

        /**
         * 1 - listen to the sign in with phone number button click event
         * 2 - pass the event to the host activity
         */
        mPhoneSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnPhoneSignInLinkClickListener) mContext).onPhoneSignInLinkClick();
            }
        });
    }
}
