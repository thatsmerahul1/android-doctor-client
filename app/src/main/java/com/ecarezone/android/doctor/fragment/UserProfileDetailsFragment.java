package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.ecarezone.android.doctor.ProfileDetailsActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.DoctorProfile;
import com.ecarezone.android.doctor.model.UserProfile;
import com.ecarezone.android.doctor.model.database.ProfileDbApi;
import com.ecarezone.android.doctor.model.rest.CreateProfileRequest;
import com.ecarezone.android.doctor.model.rest.CreateProfileResponse;
import com.ecarezone.android.doctor.model.rest.UpdateProfileRequest;
import com.ecarezone.android.doctor.model.rest.UploadImageResponse;
import com.ecarezone.android.doctor.utils.EcareZoneLog;
import com.ecarezone.android.doctor.utils.ImageUtil;
import com.ecarezone.android.doctor.utils.MD5Util;
import com.ecarezone.android.doctor.utils.PermissionUtil;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;

/**
 * Created by CHAO WEI on 5/31/2015.
 */
public class UserProfileDetailsFragment extends EcareZoneBaseFragment implements
        View.OnClickListener, TextWatcher, Toolbar.OnMenuItemClickListener {

    private EditText profileNameET;
    private Activity mActivity = null;
    private Toolbar mToolBar = null;
    private UserProfile mProfile = null;
    private View view = null;
    private static final int REQUEST_SELECT_PICTURE = 1;

    private ImageView profileImageButton;
    private String mSelectedPhotoPath = null;
    private String mUploadedImageUrl = null;
    private ProgressDialog mProgressDialog;

    @Override
    protected String getCallerName() {
        return UserProfileDetailsFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_user_profle_details, container, false);
//        ((ProfileDetailsActivity) getActivity()).getSupportActionBar()
//                .setTitle(getResources().getText(R.string.profile_actionbar_title));
        mSelectedPhotoPath = null;
        mUploadedImageUrl = null;
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mToolBar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            mToolBar.setOnMenuItemClickListener(this);
        }

        EditText nameET = (EditText) view.findViewById(R.id.name);
        nameET.addTextChangedListener(this);

        ProfileDbApi profileDbApi = new ProfileDbApi(getApplicationContext());
        String profileId = mActivity.getIntent().getStringExtra(ProfileDetailsActivity.PROFILE_ID);
        mProfile = profileDbApi.getProfile(LoginInfo.userId.toString()/*, "36"*//*profileId*/);


//        if (!mActivity.getIntent().getBooleanExtra(ProfileDetailsActivity.IS_NEW_PROFILE, false)) {
//            // Profile exists. Retrieve from DB and display the profile details
//            String profileId = mActivity.getIntent().getStringExtra(ProfileDetailsActivity.PROFILE_ID);
//            if (profileId != null) {
//                mProfile = profileDbApi.getProfile(LoginInfo.userId.toString(), profileId);
//            }
//        } else if (!profileDbApi.hasProfile(LoginInfo.userId.toString())) {
//            // No profiles found. make this as "My profile"
//         }

        if (mProfile != null) {
            setProfileValuesToFormFields(mProfile);
        } else {
            // This is creating new profile. So, disabling the delete profile button
        }

        view.findViewById(R.id.dob).setOnClickListener(this);
        view.findViewById(R.id.gender).setOnClickListener(this);

        profileImageButton = (ImageView) view.findViewById(R.id.imageButton);
        profileImageButton.setOnClickListener(this);

        // Get the image url. Check if profile has an url else get a Gravatar
        String imageUrl = null;
        if (mProfile != null) {
            if (mProfile.avatarUrl == null || mProfile.avatarUrl.equals("")) {
                //size of the profile picture to download from Gravatar. Giving the dimensions of the image container(imageView)
                int imageSize = getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);
                if (mProfile.name != null) {
                    // convert the email string to md5hex and pass it in the Gravatarl url.
                    String hashEmail = MD5Util.md5Hex(mProfile.name);
                    imageUrl = Constants.GRAVATOR_URL + hashEmail + "?d=" + Constants.DEFAULT_GRAVATOR_IMAGE_URL + "?s=" + imageSize;
                } else {
                    imageUrl = null;
                }
            } else {
                imageUrl = mProfile.avatarUrl;
            }
        }
        // If imageUrl is available load it via Picasso
        if (imageUrl != null && imageUrl.trim().length() > 8) {
            Picasso.with(getApplicationContext())
                    .load(imageUrl)
                    .fit()
                    .placeholder(R.drawable.news_other)
                    .error(R.drawable.news_other)
                    .into(profileImageButton);
        } else {
            // When no avatarUrl found, use local default image.
            profileImageButton.setImageResource(R.drawable.news_other);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_check) {
            saveProfile();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageUtil.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // sets to the image view
            setPic(mSelectedPhotoPath);
        } else if (requestCode == REQUEST_SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // copy the selected image to temporary file and get the image path
                mSelectedPhotoPath = ImageUtil.saveUriPhotoToFile(data.getData(), getActivity());
                setPic(mSelectedPhotoPath);
            }
        }
    }

    /* scales & sets the image thumbnail to the profile image button*/
    private void setPic(String imagePath) {
        Bitmap bitmap = ImageUtil.createScaledBitmap(imagePath, profileImageButton.getWidth(), profileImageButton.getHeight());
        profileImageButton.setImageBitmap(bitmap);
    }

    /* sets the provide profile details in the UI fields */
    private void setProfileValuesToFormFields(UserProfile profile) {
        ((EditText) view.findViewById(R.id.name)).setText(profile.name);
        ((EditText) view.findViewById(R.id.gender)).setText(profile.gender);
        ((EditText) view.findViewById(R.id.specializedArea)).setText(profile.category);
        ((EditText) view.findViewById(R.id.dob)).setText(profile.birthDate);
        ((EditText) view.findViewById(R.id.registrationID)).setText(profile.registrationId);
        ((EditText) view.findViewById(R.id.myBio)).setText(profile.doctorDescription);
    }

    private void setDateToDobField(String date) {
        ((EditText) view.findViewById(R.id.dob)).setText(date);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mProfile != null) {
            // Profile has already information. enabling the save button in action bar menu
            MenuItem saveMenuItem = mToolBar.getMenu().findItem(R.id.action_check);
            saveMenuItem.setEnabled(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    // read all the fields in the UI and save the profile
    private void saveProfile() {
        final DoctorProfile userProfile = new DoctorProfile();
        userProfile.name = ((EditText) view.findViewById(R.id.name)).getText().toString();
        userProfile.gender = ((EditText) view.findViewById(R.id.gender)).getText().toString();
        userProfile.birthDate = ((EditText) view.findViewById(R.id.dob)).getText().toString();
//        userProfile.email = LoginInfo.userName;
        userProfile.category = ((EditText) view.findViewById(R.id.specializedArea)).getText().toString();
        userProfile.registrationId = ((EditText) view.findViewById(R.id.registrationID)).getText().toString();
        userProfile.doctorDescription = ((EditText) view.findViewById(R.id.myBio)).getText().toString();

        SaveProfileAsyncTask saveProfileAsyncTask = new SaveProfileAsyncTask();
        saveProfileAsyncTask.execute(userProfile);
    }

    class SaveProfileAsyncTask extends AsyncTask<DoctorProfile, Void, Void> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                    getResources().getString(R.string.progress_dialog_save));
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(DoctorProfile... params) {
            DoctorProfile userProfile = params[0];
            // if image is selected, upload it and receive the image URL.
            UploadImageResponse uploadImageResponse = null;
            if (mSelectedPhotoPath != null) {
                uploadImageResponse = ImageUtil.uploadProfilePicture(mSelectedPhotoPath);
            }
            if (uploadImageResponse != null) {
                mUploadedImageUrl = uploadImageResponse.data.avatarUrl;
            }

            if (mUploadedImageUrl != null) {
                // user changed the current image
                userProfile.avatarUrl = mUploadedImageUrl;
            } else if (mProfile != null && mProfile.avatarUrl != null) {
                // user not changed the current image
                userProfile.avatarUrl = mProfile.avatarUrl;
            } else {
                userProfile.avatarUrl = null;
            }

            if (mProfile == null) {
                // Create new profile & save in local DB
                saveProfileInServer(userProfile);
            } else {
                // Update the current profile in server and in local DB

                SharedPreferences perPreferences =
                        getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
                 String userId = perPreferences.getString(Constants.PROFILE_ID, null);
                long profileId = Long.parseLong(userId);
                updateProfileInServer(profileId, userProfile);
            }
            return null;
        }
    }

    /* creates a new profile in server by calling web service api */
    private void saveProfileInServer(DoctorProfile userProfile) {
        CreateProfileRequest request = new CreateProfileRequest();
        request.doctorProfile = userProfile;
        getSpiceManager().execute(request, "profile_create", DurationInMillis.ALWAYS_EXPIRED, new CreateProfileResponseListener());
        Log.d("","Request::" + request);
    }

    /* updates the current profile in server */
    private void updateProfileInServer(Long profileId, final DoctorProfile userProfile) {
        UpdateProfileRequest request = new UpdateProfileRequest(profileId);
        request.userProfile = userProfile;
        getSpiceManager().execute(request, "profile_update", DurationInMillis.ALWAYS_EXPIRED, new UpdateProfileResponseListener());
    }

    /* creates and fires an intent that opens gallery to pick a photo */
    private void dispatchSelectFromGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.gallery_chooser_select_picture))
                , REQUEST_SELECT_PICTURE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton:
                showPhotoSelectOptionsDialog();
                break;
            case R.id.dob:
                DialogFragment newFragment = new DatePickerFragment();
                String dateStr = ((EditText) view.findViewById(R.id.dob)).getText().toString();
                ((DatePickerFragment) newFragment).setDate(dateStr);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                break;
            case R.id.gender:
                showGenderSelectorDialog();
                break;
        }
    }

    private void showPhotoSelectOptionsDialog() {
        new AlertDialog.Builder(getActivity())
                .setItems(R.array.photo_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 1) {
                            if (PermissionUtil.isPermissionRequired()
                                    && PermissionUtil.getAllpermissionRequired(getActivity(),
                                    PermissionUtil.CAPTURE_PHOTO_FROM_CAMERA_PERMISSIONS).length > 0) {

                                PermissionUtil.setAllPermission(getActivity(),
                                        PermissionUtil.REQUEST_CODE_ASK_CAPTURE_PHOTO_PERMISSIONS,
                                        PermissionUtil.CAPTURE_PHOTO_FROM_CAMERA_PERMISSIONS);
                            } else {
                                // already have all permissions
                                mSelectedPhotoPath = ImageUtil.dispatchTakePictureIntent(getActivity());
                            }
                        } else {
                            if (PermissionUtil.isPermissionRequired()
                                    && PermissionUtil.getAllpermissionRequired(getActivity(),
                                    PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSIONS).length > 0) {

                                PermissionUtil.setAllPermission(getActivity(),
                                        PermissionUtil.REQUEST_CODE_ASK_WRITE_EXTERNAL_STORAGE_PERMISSIONS,
                                        PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSIONS);
                            } else {
                                // already have all permissions
                                dispatchSelectFromGalleryIntent();
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showGenderSelectorDialog() {
        new AlertDialog.Builder(getActivity())
                .setItems(R.array.gender, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String gender;
                        if (which == 1) {
                            gender = getResources().getStringArray(R.array.gender)[1];
                        } else {
                            gender = getResources().getStringArray(R.array.gender)[0];
                        }
                        ((EditText) view.findViewById(R.id.gender)).setText(gender);
                    }
                })
                .show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            if (s.length() > 2) {
                MenuItem saveMenuItem = (MenuItem) mToolBar.getMenu().findItem(R.id.action_check);
                if (saveMenuItem != null) saveMenuItem.setEnabled(true);
            }
        } catch (Exception e) {
            EcareZoneLog.e(getCallerName(), e);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    // Robospice response listeners
    private class CreateProfileResponseListener implements RequestListener<CreateProfileResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            dismissDialog();
            spiceException.printStackTrace();
        }

        @Override
        public void onRequestSuccess(CreateProfileResponse response) {
            if (response != null && response.id != null && Integer.parseInt(response.id) > 0) {

                UserProfile profile = createUserProfileFromResponse(response);
                SharedPreferences perPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = perPreferences.edit();
                editor.putString(Constants.PROFILE_ID, response.id);
                editor.commit();

                ProfileDbApi profileDbApi = new ProfileDbApi(getApplicationContext());
                profileDbApi.saveProfile(LoginInfo.userId.toString(), profile, response.id);
//                profileDbApi.updateProfile(LoginInfo.userId.toString(), profile, response.id);

                getActivity().setResult(getActivity().RESULT_OK, null);
                getActivity().finish();
            }
            dismissDialog();
        }
    }

    private class UpdateProfileResponseListener implements RequestListener<CreateProfileResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            dismissDialog();
            spiceException.printStackTrace();
        }

        @Override
        public void onRequestSuccess(CreateProfileResponse response) {
            if (response != null && response.id != null &&  Integer.parseInt(response.id) > 0) {
                UserProfile profile = createUserProfileFromResponse(response);

                ProfileDbApi profileDbApi = new ProfileDbApi(getApplicationContext());
                profileDbApi.updateProfile(LoginInfo.userId.toString(), profile, mProfile.id);
                getActivity().setResult(getActivity().RESULT_OK, null);
                getActivity().finish();
            }
            dismissDialog();
        }
    }

    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private UserProfile createUserProfileFromResponse(CreateProfileResponse response) {
        if (response != null) {
            UserProfile profile = new UserProfile();
            profile.id = response.id;
            profile.name = response.name;
            profile.doctorDescription = response.doctorDescription;
            profile.avatarUrl = response.avatarUrl;
            profile.registrationId = response.registrationId;
            profile.birthDate = response.birthDate;
            profile.gender = response.gender;
            profile.category = response.category;
            return profile;
        } else {
            return null;
        }
    }

    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        int[] date = new int[3];

        // instance block to initialize the date value
        {
            date[0] = -1;
        }

        public void setDate(String strDate) {
            try {
                String[] splitDate = strDate.split("-");
                date[0] = Integer.parseInt(splitDate[0]);
                date[1] = Integer.parseInt(splitDate[1]);
                date[2] = Integer.parseInt(splitDate[2]);
            } catch (NumberFormatException nfe) {
                date[0] = -1;
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                date[0] = -1;
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year, month, day;
            if (date[0] >= 0) {
                year = date[0];
                // month uses 0-11 values for calendar, so decrement by one to the month value which is visible to user.
                month = date[1] - 1;
                day = date[2];
            } else {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            StringBuilder dateSb = new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day);
            setDateToDobField(dateSb.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // if any of the permission is not granted. do not process anything. just return.
        for (int isGranted : grantResults) {
            if (isGranted == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }

        switch (requestCode) {
            case PermissionUtil.REQUEST_CODE_ASK_WRITE_EXTERNAL_STORAGE_PERMISSIONS:
                dispatchSelectFromGalleryIntent();
                break;
            case PermissionUtil.REQUEST_CODE_ASK_CAPTURE_PHOTO_PERMISSIONS:
                mSelectedPhotoPath = ImageUtil.dispatchTakePictureIntent(getActivity());
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
