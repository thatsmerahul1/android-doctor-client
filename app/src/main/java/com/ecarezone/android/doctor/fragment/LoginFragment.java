package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.doctor.DoctorApplication;
import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.NetworkCheck;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.database.ProfileDbApi;
import com.ecarezone.android.doctor.model.database.UserTable;
import com.ecarezone.android.doctor.model.rest.Data;
import com.ecarezone.android.doctor.model.rest.LoginRequest;
import com.ecarezone.android.doctor.model.rest.LoginResponse;
import com.ecarezone.android.doctor.service.LocationFinder;
import com.ecarezone.android.doctor.service.SinchService;
import com.ecarezone.android.doctor.utils.EcareZoneLog;
import com.ecarezone.android.doctor.utils.PasswordUtil;
import com.ecarezone.android.doctor.utils.PermissionUtil;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.ecarezone.android.doctor.utils.SinchUtil;
import com.ecarezone.android.doctor.utils.Util;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.sinch.android.rtc.SinchError;

/**
 * Created by L&T Technology Services  on 2/19/2016.
 */
public class LoginFragment extends EcareZoneBaseFragment implements View.OnClickListener,
        SinchService.StartFailedListener {
    public ProgressDialog progressDialog;
    private static String TAG = LoginFragment.class.getSimpleName();



    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private EditText mEditTextUsername = null;
    private EditText mEditTextPassword = null;
    private TextView mTextViewForgotPwd = null;
    private View mButtonLogin = null;
    private UserTable userTable;
    public LocationFinder locationFinder;
    public String hashedPassword;
    private TextView textView_error;

    @Override
    protected String getCallerName() {
        return LoginFragment.class.getSimpleName();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_login, container, false);

        if (PermissionUtil.isPermissionRequired()
                && PermissionUtil.getAllpermissionRequired(getActivity(),
                PermissionUtil.LOCATION_PERMISSIONS).length > 0) {

            PermissionUtil.setAllPermission(getActivity(),
                    PermissionUtil.REQUEST_CODE_ASK_LOCATION_PERMISSIONS,
                    PermissionUtil.LOCATION_PERMISSIONS);
        } else {
            // already have all permissions
            locationFinder = new LocationFinder(getActivity());
        }

        mEditTextUsername = (EditText) view.findViewById(R.id.edit_text_login_username);
        mEditTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s != null) {
                        checkLoginButtonStatus(String.valueOf(s),
                                mEditTextPassword.getEditableText().toString());
                    }
                } catch (Exception e) {
                    EcareZoneLog.e(getCallerName(), e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        mEditTextPassword = (EditText) view.findViewById(R.id.edit_text_login_password);
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
                    EcareZoneLog.e(getCallerName(), e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //if User already exists
        mEditTextUsername.setText(LoginInfo.userName);

        // add editor action listener for password EditText to accept input from soft keyboard
        // Then the user need not to click login button
        mEditTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_SEND) {

                    onClick(mButtonLogin);
                    return true;
                }
                return false;
            }
        });
        mTextViewForgotPwd = (TextView) view.findViewById(R.id.textview_forgotpwd);
        if (LoginInfo.userName != null) {
            mEditTextUsername.setText(LoginInfo.userName);
            mTextViewForgotPwd.setOnClickListener(this);
            mTextViewForgotPwd.setEnabled(true);
        }

        mButtonLogin = view.findViewById(R.id.button_login);
        mButtonLogin.setOnClickListener(this);
        view.findViewById(R.id.button_create_account).setOnClickListener(this);
        textView_error = (TextView) view.findViewById(R.id.textview_error);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;
        Util.hideKeyboard(getActivity());
        final int viewId = v.getId();
        if (viewId == R.id.button_login) {
            textView_error.setVisibility(View.INVISIBLE);
            final String username = mEditTextUsername.getEditableText().toString();
            final String password = mEditTextPassword.getEditableText().toString();
            /*
                Checking client side validation during login
            */
            if (TextUtils.isEmpty(username)
                    || (!android.util.Patterns.EMAIL_ADDRESS.matcher(username.trim()).matches())
                    || (TextUtils.isEmpty(password) || (password.trim().length() < 8))) {
                textView_error.setText(R.string.error_user_login);
                textView_error.setVisibility(View.VISIBLE);
                mEditTextPassword.setText("");
                Toast.makeText(v.getContext(), R.string.error_user_login, Toast.LENGTH_LONG).show();
                return;
            } else {
                if(NetworkCheck.isNetworkAvailable(getActivity())) {
                    doLogin(username, password);
                } else {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
        } else if (viewId == R.id.button_create_account) {
            // change to account creation
            invokeNavigationChanged(R.layout.frag_registration, null);
        } else if (viewId == R.id.textview_forgotpwd) {
            if (LoginInfo.userName == null) {
                textView_error.setText(R.string.error_user_login);
                textView_error.setVisibility(View.VISIBLE);
            } else {
                invokeNavigationChanged(R.layout.act_forgotpassword, null);
            }
        }
    }

    //Login Request
    public void doLogin(String username, String password) {
        // Changing password to hashed password for security
        hashedPassword = PasswordUtil.getHashedPassword(password);
        LoginRequest request =
                new LoginRequest(username, hashedPassword, 0, Constants.API_KEY, Constants.deviceUnique, locationFinder.getLatitude(), locationFinder.getLongitude());
        final LoginResponse response = new LoginResponse();
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), "Logging ........");
        getSpiceManager().execute(request, new LoginRequestListener());
    }

    /*
         Login Button is enable only all fields entered
     */
    private void checkLoginButtonStatus(final String username, final String password) {
        boolean enable = false;
        enable = ((!TextUtils.isEmpty(username))
                && (!TextUtils.isEmpty(password)));
        if (mButtonLogin != null) {
            mButtonLogin.setEnabled(enable);
        }
    }

    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {

    }



    //Login response listner
    public final class LoginRequestListener implements RequestListener<LoginResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.d(TAG, "NetWork Failure");
            Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
            mEditTextPassword.setText("");

        }

        @Override
        public void onRequestSuccess(final LoginResponse response) {
            mEditTextPassword.setText("");
            Log.d(TAG, "Status Code " + response.status.code);
            if (response.status.code == 200) {
                final Activity activity = getActivity();
                Data data = response.data;
                LoginInfo.userName = data.settings.email;
                LoginInfo.userId = data.userId;
                LoginInfo.hashedPassword = hashedPassword;
                LoginInfo.role = String.valueOf(0);

                if (activity != null) {
                    // record the app stauts as "is_login" then the next launch will go to main page directly instead of go to registration page

                    // Make server call & get the user information & save it internally in db.
                    if (data.doctorProfile != null) {
                        ProfileDbApi profileDbApi = new ProfileDbApi(getApplicationContext());
                        profileDbApi.deleteProfiles(LoginInfo.userId.toString());
                        profileDbApi.saveMultipleProfiles(LoginInfo.userId.toString(), response.data.doctorProfile);
                    }
                    userTable = new UserTable(getActivity());
                    /*
                         If user already exists , Updating the data into database.
                         If user doesn't exist , Inserting data in database
                     */
                    if (userTable.userExists(Long.toString(data.userId))) {
                        userTable.updateUserData(Long.toString(data.userId), data.settings.email, hashedPassword, data.settings.language
                                , Integer.toString(0), data.settings.country);
                    } else {
                        userTable.saveUserData(Long.toString(data.userId), data.settings.email, hashedPassword, data.settings.language
                                , Integer.toString(0), data.settings.country);
                    }
                    /*
                       Saving UserId and Login status into shared preference
                     */
                    SharedPreferences perPreferences = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = perPreferences.edit();
                    editor.putBoolean(Constants.IS_LOGIN, true);
                    editor.putString(Constants.USER_ID, String.valueOf(LoginInfo.userId));
                    editor.commit();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!SinchUtil.getSinchServiceInterface().isStarted()) {
                                SinchUtil.getSinchServiceInterface().startClient(LoginInfo.userName);
                            }
                            nextScreen(activity);
                        }
                    });

                }
            } else {
                textView_error.setText(response.status.message);
                textView_error.setVisibility(View.VISIBLE);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
            Log.d(TAG, "Login Success");
        }
    }

    private void nextScreen(Activity activity) {
        activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
        activity.finish();
    }

}
