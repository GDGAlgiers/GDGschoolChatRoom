package com.example.azeddine.gdgschoolchatroom;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.azeddine.gdgschoolchatroom.authFragments.PhoneNumberFragment;
import com.example.azeddine.gdgschoolchatroom.authFragments.PinCodeFragment;
import com.example.azeddine.gdgschoolchatroom.authFragments.SignInFragment;
import com.example.azeddine.gdgschoolchatroom.authFragments.SignUpFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;


import java.util.concurrent.TimeUnit;


public class AuthActivity extends AppCompatActivity implements
        SignInFragment.OnSignInListener,
        SignInFragment.OnRegisterLinkClickListener,
        SignInFragment.OnPhoneSignInLinkClickListener,
        SignUpFragment.OnSignUpListener,
        PinCodeFragment.OnPinCodeSubmittedListener,
        PhoneNumberFragment.OnPhoneNumberSubmittedListener {

    private static final String TAG = "AuthActivity";
    private FirebaseAuth mAuth;
    private String mPhoneVerificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //get the firebase authentication instance
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * a method used to start the authentication activity
     */
    void startAuthFragment(){
        SignInFragment signInFragment = new SignInFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.auth_fragments_container,signInFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    /**
     * a method used to start the chat room activity, Besides it deletes the Auth activity
     * from the back stack
     */
    void startChatActivity(){
        Intent intent =new Intent(AuthActivity.this,ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // start the authentication fragment
        startAuthFragment();
    }

    @Override
    public void onSignIn(String email, String password) {
        /**
         * 1- validate the credentials
         * 2- sign in the user with email and password
         * 3- listen for the server response, if is it successful, start the chat activity, if it isn't display an error message
         */
        if(validateCredentials(email,password)){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startChatActivity();
                        }else {
                            Toast.makeText(AuthActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
       }else {
           Toast.makeText(AuthActivity.this, R.string.invalid_input, Toast.LENGTH_SHORT).show();
       }
    }


    @Override
    public void onRegisterLinkClick() {
        SignUpFragment signUpFragment = new SignUpFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.auth_fragments_container,signUpFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();

    }


    @Override
    public void onPhoneSignInLinkClick() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.auth_fragments_container,new PhoneNumberFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

    }

    @Override
    public void onSignUp(String email,String password) {
        /**
         * 1- validate the credentials
         * 2- create the user with mail and password
         * 3- listen for the server response, if is it successful, start the chat activity, if it isn't display an error message
         */
        if(validateCredentials(email,password)){
            //signing it the user with email and password
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification();
                                startChatActivity();
                                finish();
                            }else {
                                Toast.makeText(AuthActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        }else{
            Toast.makeText(AuthActivity.this, R.string.invalid_input, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * a method used to validate the credentials,
     * @param credentials
     * @return true if all the the fields have a length > 0
     */
    boolean validateCredentials(@NonNull  String ...credentials){
        boolean validText ;
        int i = credentials.length-1;
        do{
            validText = (credentials[i] != null)&&(credentials[i].length()>0) ;
            i--;
        }while (validText && i != -1);

        return validText;
    }

    /**
     * a method to sign in the user with the pin Code entered by the user
     * it listens for the server response, if is it successful, start the chat activity, if it isn't display an error message
     * @param credential the phone number to verify
     */

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            startChatActivity();
                            finish();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(AuthActivity.this, "Invalid verification number", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onPinCodeSubmitted(String codePin) {
      if(validateCredentials(codePin)){
          PhoneAuthCredential credential= PhoneAuthProvider.getCredential(codePin, mPhoneVerificationId);
          signInWithPhoneAuthCredential(credential);
      }
    }

    @Override
    public void OnPhoneNumberSubmitted(String phoneNumber) {
        //getting firebase phone auth provider instance
        PhoneAuthProvider phoneAuthProvider = PhoneAuthProvider.getInstance();
        //verifying phone number, using my personnel phone number, setting 1 minute as a time out for the second attempt
        phoneAuthProvider.verifyPhoneNumber(
                phoneNumber, // the phone number of the user
                60,  // the time out
                TimeUnit.SECONDS, // the time out unit
                this,   // maneging the activity life cycle change
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        //some devices, can detect automatically the sent pinCode
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        //something went wrong while submitting the pinCode
                        Toast.makeText(AuthActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();;
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        //the user received the pinCode
                        super.onCodeSent(verificationId, forceResendingToken);
                        //saving the verification Id to use later
                        mPhoneVerificationId = verificationId;
                        //stating the pinCode fragment
                        PinCodeFragment pinCodeFragment = new PinCodeFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.auth_fragments_container,pinCodeFragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .addToBackStack(null)
                                .commit();
                    }
                }
        );
        Toast.makeText(this, R.string.pinCode_hint_text, Toast.LENGTH_SHORT).show();

    }
}
