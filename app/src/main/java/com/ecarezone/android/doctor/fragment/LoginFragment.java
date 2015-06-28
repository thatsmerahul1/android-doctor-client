package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.R;

public class LoginFragment extends EcareZoneBaseFragment implements View.OnClickListener {

    public static LoginFragment newInstance() {
        return  new LoginFragment();
    }

    private EditText mEditTextUsername = null;
    private EditText mEditTextPassword = null;
    private View mButtonLogin = null;

    @Override
    protected String getCallerName() {
        return LoginFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_login, container, false);
        mEditTextUsername = (EditText)view.findViewById(R.id.edit_text_login_username);
        mEditTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    checkLoginButtonStatus(String.valueOf(s),
                            mEditTextPassword.getEditableText().toString());
                } catch (Exception e) {
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEditTextPassword = (EditText)view.findViewById(R.id.edit_text_login_password);
        mEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    checkLoginButtonStatus(String.valueOf(s),
                            mEditTextUsername.getEditableText().toString());
                } catch (Exception e) {
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mButtonLogin = view.findViewById(R.id.button_login);
        mButtonLogin.setOnClickListener(this);
        mButtonLogin.setEnabled(false);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == null) return;

        final int viewId = v.getId();
        if(viewId == R.id.button_login) {
            final String username = mEditTextUsername.getEditableText().toString();
            final String password = mEditTextUsername.getEditableText().toString();
            if(TextUtils.isEmpty(username)
                    || (!android.util.Patterns.EMAIL_ADDRESS.matcher(username.trim()).matches())
                    || (TextUtils.isEmpty(username) || (password.trim().length() < 8))) {
                Toast.makeText(v.getContext(), R.string.error_user_login, Toast.LENGTH_LONG).show();
            } else {
                doLogin(username, password);
            }
        }
    }

    private void doLogin(final String username, final String pwd) {
        final Activity activity = getActivity();
        if(activity != null) {
            activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
            activity.finish();
        }
    }

    private void checkLoginButtonStatus(final String username, final String password) {
        boolean enable = false;
        enable = ((!TextUtils.isEmpty(username))
                && (!TextUtils.isEmpty(password)));
        if(mButtonLogin != null) {
            mButtonLogin.setEnabled(enable);
        }
    }

}
