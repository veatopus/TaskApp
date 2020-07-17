package com.example.taskapp.ui.auth;


import android.annotation.SuppressLint;
import android.content.Context;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.taskapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class PhoneFragment extends Fragment {

    private EditText editTextPhone;
    private CountryCodePicker countryCodePicker;
    private TextView textView;
    private Button buttonNext;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String smsCode;
    private boolean isNext = false;
    private NavController navController;

    private TextView timerTextView;
    private long startTime = 60;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            long millis = (60 * 1000) - (System.currentTimeMillis() - startTime);
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timerTextView.setText(String.format("%d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_phone, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialisation(view);
        setOnClickListeners();

        countryCodePicker.registerCarrierNumberEditText(editTextPhone);
    }

    private void initialisation(View view) {
        editTextPhone = view.findViewById(R.id.edit_text_phone);
        textView = view.findViewById(R.id.text_view);
        buttonNext = view.findViewById(R.id.btn_next);
        countryCodePicker = view.findViewById(R.id.countryCodePicker);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        timerTextView = view.findViewById(R.id.timerTextView);
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                smsCode = phoneAuthCredential.getSmsCode();
                editTextPhone.setText(smsCode);
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("TAG", "onVerificationFailed: " + e.getMessage());

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                isNext = false;
                countryCodePicker.setVisibility(View.VISIBLE);
                countryCodePicker.registerCarrierNumberEditText(editTextPhone);
                timerHandler.removeCallbacks(timerRunnable);
                textView.setText("введите номер");
                timerTextView.setVisibility(View.GONE);
            }
        };
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            navController.popBackStack();
                        } else {
                            Log.e("TAG", "onComplete: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }

    private void setOnClickListeners() {
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (!isNext) onFirstButtonClick();
                else onSecondButtonClick();
            }
        });
    }

    private void verify() {
        String phone = countryCodePicker.getFullNumberWithPlus();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                requireActivity(),
                callbacks);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputManager != null;
        inputManager.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void onFirstButtonClick() {
        if (editTextPhone.getText().toString().trim().equals(""))
            editTextPhone.setError("введите номер");
        else {
            verify();
            hideKeyboard();
            countryCodePicker.deregisterCarrierNumberEditText();
            countryCodePicker.setVisibility(View.GONE);
            editTextPhone.setText("");
            editTextPhone.setHint("");
            textView.setText("ведите код");
            isNext = true;
            timerTextView.setVisibility(View.VISIBLE);
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void onSecondButtonClick() {

        if (editTextPhone.getText().toString().equals(smsCode)) {
            hideKeyboard();
            navController.popBackStack();
        } else {
            editTextPhone.setError("не правильный код");
        }
    }
}