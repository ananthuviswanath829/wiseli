package uk.ac.tees.mad.w9501736.ui.fragment.registerPage;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.iceteck.silicompressorr.SiliCompressor;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import uk.ac.tees.mad.w9501736.Database.DatabaseFactory;
import uk.ac.tees.mad.w9501736.R;
import uk.ac.tees.mad.w9501736.data.WiseLiRepository;
import uk.ac.tees.mad.w9501736.data.model.Resource;
import uk.ac.tees.mad.w9501736.data.model.WiseLiUser;
import uk.ac.tees.mad.w9501736.data.remote.WiseLiApiClient;
import uk.ac.tees.mad.w9501736.data.remote.WiseLiApiService;
import uk.ac.tees.mad.w9501736.models.LoginModel;
import uk.ac.tees.mad.w9501736.ui.BaseFragment;
import uk.ac.tees.mad.w9501736.ui.activity.LandingActivity;
import uk.ac.tees.mad.w9501736.ui.viewModel.RegisterPage.RegisterFragmentViewModel;
import uk.ac.tees.mad.w9501736.utils.AppPreferences;
import uk.ac.tees.mad.w9501736.utils.UtilHelper;

import static android.os.Environment.getExternalStoragePublicDirectory;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends BaseFragment implements BottomSheetImagePicker.OnImagesSelectedListener {

    private static final String TAG = "RegisterFragment";
    public MultipartBody.Part image;
    @BindView(R.id.btnSignUp)
    AppCompatButton btnSignUp;
    @BindView(R.id.imgProfileImage)
    AppCompatImageView imgProfileImage;
    @BindView(R.id.edtFirstName)
    TextInputLayout edtFirstName;
    @BindView(R.id.edtLastName)
    TextInputLayout edtLastName;
    @BindView(R.id.etUserName)
    TextInputLayout etUserName;
    @BindView(R.id.edtPassword)
    TextInputLayout edtPassword;
    @BindView(R.id.rlUploadImage)
    RelativeLayout rlUploadImage;
    @BindView(R.id.etPhoneNumber)
    TextInputLayout etPhoneNumber;
    @BindView(R.id.btnTG)
    MaterialButtonToggleGroup btnTG;
    WiseLiUser wiseLiUser;
    @BindView(R.id.edtEmailID)
    TextInputLayout edtEmailID;
    Disposable dMainListObservable;
    AppPreferences mAppPreferences;
    private WiseLiRepository apiRepo;
    private RegisterFragmentViewModel registerFragmentViewModel;
    private FusedLocationProviderClient mFusedLocationClient;
    private  static  String  USERNAME ="username";
    private  static  String  PASSWORD ="password";
    private  static  String  FIRSTNAME ="firstname";
    private  static  String  LASTNAME ="lastname";
    private  static  String  PHONENO ="phone";
    private  static  String  EMAIL ="email";
    private  static  String  PROFILE_PIC ="pic";
    private  static  String  GENDER ="gender";
    public File imageFile;
    Uri compressUri = null;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USERNAME,etUserName.getEditText().getText().toString());
        outState.putString(PASSWORD,edtPassword.getEditText().getText().toString());
        outState.putString(FIRSTNAME,edtFirstName.getEditText().getText().toString());
        outState.putString(LASTNAME,edtLastName.getEditText().getText().toString());
        outState.putString(PHONENO,etPhoneNumber.getEditText().getText().toString());
        outState.putString(EMAIL,edtEmailID.getEditText().getText().toString());
        outState.putString(PROFILE_PIC,wiseLiUser.getProfilePic());
        outState.putString(GENDER,wiseLiUser.getGender());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState !=null) {
            edtFirstName .getEditText().setText(savedInstanceState.getString(FIRSTNAME));
            edtLastName .getEditText().setText(savedInstanceState.getString(LASTNAME));
            etUserName .getEditText().setText(savedInstanceState.getString(USERNAME));
            etPhoneNumber .getEditText().setText(savedInstanceState.getString(PHONENO));
            edtPassword .getEditText().setText(savedInstanceState.getString(PASSWORD));
            edtEmailID .getEditText().setText(savedInstanceState.getString(EMAIL));
            Glide.with(getActivity()).load(savedInstanceState.getString(PROFILE_PIC)).placeholder(R.drawable.image1).error(R.drawable.image1).into(imgProfileImage);
            if (GENDER.equals("Male")) {
                btnTG.check(R.id.btnMale);
            }
            if (GENDER.equals("Female")) {
                btnTG.check(R.id.btnFemale);
            }
                wiseLiUser.setProfilePic(savedInstanceState.getString(PROFILE_PIC));
            Uri uri =Uri.fromFile( new File(wiseLiUser.getProfilePic()));
            image =getImageFile(uri);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //WiseLiComponent wiseLiComponent = ((WiseLiComponentProvider) getActivity().getApplication()).getWiseLiComponent();
        //wiseLiComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_register;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        getTextChangeListener();
        btnClicks();
    }

    private void init(View view) {

        wiseLiUser = new WiseLiUser();

        DatabaseFactory.setupObject(getContext());

        mAppPreferences = AppPreferences.getInstance(getContext());

        Glide.with(getActivity()).load(R.drawable.image1).into(imgProfileImage);

        imageFile = new File("");

        MaterialButton materialButton = view.findViewById(btnTG.getCheckedButtonId());
        wiseLiUser.setGender(materialButton.getText().toString());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        getCurrentLoc();

        getDeviceDetails();

        btnTG.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnMale) {
                    wiseLiUser.setGender("Male");
                } else {
                    wiseLiUser.setGender("Female");
                }
            }
        });


    }

    private void btnClicks() {
        getImageBtnClick();
        getGenderOnBtnClick();
        registerBtnClick();
    }

    public void getCurrentLoc() {
        Dexter.withContext(getActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    getLastLocation();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void getGenderOnBtnClick() {
        btnTG.setOnClickListener(v -> {
            MaterialButton materialButton = v.findViewById(btnTG.getCheckedButtonId());
            wiseLiUser.setGender(materialButton.getText().toString());
        });
    }

    private void getImageBtnClick() {
        rlUploadImage.setOnClickListener(v -> {
            Dexter.withContext(getActivity())
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        new BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                                .cameraButton(ButtonType.Button)
                                .galleryButton(ButtonType.Button)
                                .singleSelectTitle(R.string.pick_single)
                                .peekHeight(R.dimen._260)
                                .columnSize(R.dimen._90)
                                .requestTag("single")
                                .show(getChildFragmentManager(), null);
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
        });
    }

    private void registerBtnClick() {
        btnSignUp.setOnClickListener(v -> {
            getRegisterData();
        });

    }

    private void getTextChangeListener() {
        validateFirstName();
        validateLastName();
        validateUserName();
        validatePassword();
        validatePhoneNumber();
        validateEmailId();
    }

    private void validateFirstName() {
        edtFirstName.setHelperText(getString(R.string.empty_string));
        edtFirstName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    edtFirstName.setHelperText(getString(R.string.empty_string));
                    wiseLiUser.setFirstName(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    edtFirstName.setHelperText(getString(R.string.field_empty_error));
                }
            }
        });
    }

    private void validateLastName() {
        edtLastName.setHelperText(getString(R.string.empty_string));
        edtLastName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    edtLastName.setHelperText(getString(R.string.empty_string));
                    wiseLiUser.setLastName(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    edtLastName.setHelperText(getString(R.string.field_empty_error));
                }
            }
        });
    }

    private void validateUserName() {
        etUserName.setHelperText(getString(R.string.empty_string));
        etUserName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    etUserName.setHelperText(getString(R.string.empty_string));
                    wiseLiUser.setUsername(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    etUserName.setHelperText(getString(R.string.field_empty_error));
                }
            }
        });
    }

    private void validatePassword() {
        edtPassword.setHelperText(getString(R.string.empty_string));
        edtPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    edtPassword.setHelperText(getString(R.string.empty_string));
                    wiseLiUser.setPassword(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    edtPassword.setHelperText(getString(R.string.field_empty_error));
                }
            }
        });
    }

    private void validatePhoneNumber() {
        etPhoneNumber.setHelperText(getString(R.string.empty_string));
        etPhoneNumber.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    etPhoneNumber.setHelperText(getString(R.string.empty_string));
                    wiseLiUser.setPhoneNumber(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    etPhoneNumber.setHelperText(getString(R.string.field_empty_error));
                }
            }
        });
    }

    private void validateEmailId() {
        edtEmailID.setHelperText(getString(R.string.empty_string));
        edtEmailID.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    edtEmailID.setHelperText(getString(R.string.empty_string));
                    wiseLiUser.setEmail(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    edtEmailID.setHelperText(getString(R.string.field_empty_error));
                }
            }
        });
    }

    private void getRegisterData() {
        if (edtPassword.getEditText() != null &&
                edtEmailID.getEditText() != null &&
                etUserName.getEditText() != null &&
                etPhoneNumber.getEditText() != null &&
                edtFirstName.getEditText() != null &&
                edtLastName.getEditText() != null
        ) {
            //subscribeObservers(wiseLiUser);
            getRegisterApiCall();
        }

        //navigateToLanding();
    }

    private void navigateToLanding() {
        showProgressBar(false);
        startActivity(new Intent(getActivity(), LandingActivity.class));
        getActivity().finish();
    }

    private MultipartBody.Part getImageFile(Uri uri) {
        MultipartBody.Part body = MultipartBody.Part.createFormData("", "");
        try {
            //pass it like this
            Log.d(TAG, "getImageFile" + "uri" + uri.toString());
            File file = new File(UtilHelper.getRealPathFromURI_API19(getContext(), uri));
            Log.d(TAG, "getImageFile" + "File Path:" + file.getPath());
            wiseLiUser.setProfilePic(file.getPath());

            // create upload service client
            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

            // MultipartBody.Part is used to send also the actual file name
            body = MultipartBody.Part.createFormData("profile_pic","abc.jpeg", requestFile);
            System.out.println("getImageFile: file.getName()" + file.getName());


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception getImageFile: Error" + e.getMessage());
        }
        System.out.println("getImageFile: Error");
        return body;

    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getBaseActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public void onImagesSelected(@NotNull List<? extends Uri> list, @org.jetbrains.annotations.Nullable String s) {
        for (Uri uri : list) {
            //Glide.with(getActivity()).load(uri).into(imgProfileImage);
            new ImageCompressionAsyncTask(getActivity()).execute(uri.toString(),
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/WiseLi/images");
        }
    }

    public void getRegisterApiCall() {
        showProgressBar(true);
        Retrofit retrofit = new WiseLiApiClient().getRetrofitClient();
        final WiseLiApiService webServices = retrofit.create(WiseLiApiService.class);
        Log.d(TAG, "getRegisterApiCall: wiseLiUser.getLatitude()" + wiseLiUser.getLatitude());
        Log.d(TAG, "getRegisterApiCall: wiseLiUser.getLongitude()" + wiseLiUser.getLongitude());
        Log.d(TAG, "getRegisterApiCall: wiseLiUser.getFirstName()" + wiseLiUser.getFirstName());
        Log.d(TAG, "getRegisterApiCall: wiseLiUser.getLastName()" + wiseLiUser.getLastName());
        Log.d(TAG, "getRegisterApiCall: wiseLiUser.getEmail()" + wiseLiUser.getEmail());
        Log.d(TAG, "getRegisterApiCall: wiseLiUser.getPhoneNumber()" + wiseLiUser.getDeviceType());
        Log.d(TAG, "getRegisterApiCall: wiseLiUser.getPhoneNumber()" + wiseLiUser.getDeviceId());

        RequestBody first_name = createPartFromString(wiseLiUser.getFirstName());
        RequestBody last_name = createPartFromString(wiseLiUser.getLastName());
        RequestBody username = createPartFromString(wiseLiUser.getUsername());
        RequestBody email = createPartFromString(wiseLiUser.getEmail());
        RequestBody phone_number = createPartFromString(wiseLiUser.getPhoneNumber());
        RequestBody password = createPartFromString(wiseLiUser.getPassword());
        RequestBody device_id = createPartFromString(wiseLiUser.getDeviceId());
        RequestBody device_type = createPartFromString(wiseLiUser.getDeviceType());
        RequestBody latitude = createPartFromString(wiseLiUser.getLatitude());
        RequestBody longitude = createPartFromString(wiseLiUser.getLongitude());
        RequestBody gender = createPartFromString(wiseLiUser.getGender());

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("first_name", first_name);
        map.put("last_name", last_name);
        map.put("email", email);
        map.put("gender", gender);
        map.put("phone_number", phone_number);
        map.put("username", username);
        map.put("password", password);
        map.put("device_id", device_id);
        map.put("device_type", device_type);
        map.put("latitude", latitude);
        map.put("longitude", longitude);

        Observable<Resource<WiseLiUser>> likedObservable = webServices.registerUser(map,
                image
        );
        likedObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(registerUser());
    }

    private RequestBody createPartFromString(String partString) {
        return RequestBody.create(MultipartBody.FORM, partString);
    }

    private Observer<Resource<WiseLiUser>> registerUser() {
        return new Observer<Resource<WiseLiUser>>() {

            @Override
            public void onSubscribe(Disposable d) {
                dMainListObservable = d;
                Log.d("registerUser", " onSubscribe : " + d.isDisposed());
            }

            @Override
            public void onNext(Resource<WiseLiUser> value) {
                if (value.result) {
                    LoginModel.LoginModelDB loginModel = new LoginModel().new LoginModelDB();
                    loginModel.setUser_id(String.valueOf(value.getData().getUserId()));
                    loginModel.setFirst_name(value.getData().getFirstName());
                    loginModel.setLast_name(value.getData().getLastName());
                    loginModel.setToken(value.getData().getToken());
                    loginModel.setEmail(value.getData().getEmail());
                    loginModel.setUsername(value.getData().getUsername());
                    loginModel.setGender(value.getData().getGender());
                    loginModel.setProfile_pic(value.getData().getProfilePic());
                    wiseLiUser.setToken(value.data.getToken());
                    wiseLiUser.setProfilePic(value.data.getProfilePic());
                    mAppPreferences.setToken(value.data.getToken());
                    mAppPreferences.setUserDetails(loginModel);
                    mAppPreferences.setUserCashedInfo(wiseLiUser);
                    Log.d("registerUser", " onNext : value : " + value);
                    navigateToLanding();
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), value.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                showProgressBar(false);
                Snackbar.make(getActivity().findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
                Log.d("registerUser", " onError : value : " + e.getMessage());
            }

            @Override
            public void onComplete() {
                showProgressBar(false);
                Log.d("registerUser", " onComplete");
            }
        };
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        //iLandingPresenter.getWeatherForecastWebService(String.valueOf(latitude), String.valueOf(longitude));
        System.out.println("LandingActivity getLastLocation");
        wiseLiUser.setLongitude("0.000");
        wiseLiUser.setLatitude("0.000");
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location mLastLocation = task.getResult();
                        System.out.println("LandingActivity getLatitude : " + mLastLocation.getLatitude() + ", getLongitude : " + mLastLocation.getLongitude());
                        wiseLiUser.setLatitude(Double.toString(mLastLocation.getLatitude()));
                        wiseLiUser.setLongitude(Double.toString(mLastLocation.getLongitude()));
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.snack_error_location_null), Snackbar.LENGTH_LONG).show();

                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dMainListObservable != null)
            dMainListObservable.dispose();
    }

    public void getDeviceDetails() {
        Dexter.withContext(getActivity())
                .withPermissions(
                        Manifest.permission.READ_PHONE_STATE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    EasyDeviceMod easyDeviceMod = new EasyDeviceMod(getActivity());
                    String deviceName = easyDeviceMod.getManufacturer() + " " + easyDeviceMod.getDevice() + " " + easyDeviceMod.getModel();
                    wiseLiUser.setDeviceType(deviceName);
                    wiseLiUser.setDeviceId(easyDeviceMod.getBuildID());
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {

        Context mContext;

        public ImageCompressionAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {

            return SiliCompressor.with(mContext).compress(params[0], new File(params[1]));

        }

        @Override
        protected void onPostExecute(String s) {

            float length = 0;
            String name;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                compressUri = Uri.parse(s);
                Cursor c = getContext().getContentResolver().query(compressUri, null, null, null, null);
                c.moveToFirst();
                name = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                length = c.getLong(c.getColumnIndex(OpenableColumns.SIZE)) / 1024;
            } else {
                imageFile = new File(s);
                compressUri = Uri.fromFile(imageFile);
                name = imageFile.getName();
                length = imageFile.length() / 1024f; // Size in KB
            }

            try {
                wiseLiUser.setImgBody(getImageFile(compressUri));
                image = getImageFile(compressUri);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), compressUri);
                Glide.with(getActivity()).load(bitmap).into(imgProfileImage);
                int compressWidth = bitmap.getWidth();
                int compressHieght = bitmap.getHeight();
                String text = String.format(Locale.US, "Name: %s\nSize: %fKB\nWidth: %d\nHeight: %d", name, length, compressWidth, compressHieght);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
