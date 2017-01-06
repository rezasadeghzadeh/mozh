package ir.sadeghzadeh.mozhdeh.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import ir.sadeghzadeh.mozhdeh.ApplicationController;
import ir.sadeghzadeh.mozhdeh.Const;
import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.dialog.ChooseLocationOnMapDialog;
import ir.sadeghzadeh.mozhdeh.dialog.ChooseItemsDialog;
import ir.sadeghzadeh.mozhdeh.dialog.FetchAddressIntentService;
import ir.sadeghzadeh.mozhdeh.dialog.OnOneItemSelectedInDialog;
import ir.sadeghzadeh.mozhdeh.entity.Category;
import ir.sadeghzadeh.mozhdeh.entity.City;
import ir.sadeghzadeh.mozhdeh.entity.Item;
import ir.sadeghzadeh.mozhdeh.entity.KeyValuePair;
import ir.sadeghzadeh.mozhdeh.entity.Province;
import ir.sadeghzadeh.mozhdeh.utils.LoadImageTask;
import ir.sadeghzadeh.mozhdeh.utils.Util;
import ir.sadeghzadeh.mozhdeh.volley.CustomMultipartVolleyRequest;
import ir.sadeghzadeh.mozhdeh.volley.GsonRequest;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by reza on 11/2/16.
 */
@RuntimePermissions
public class NewFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener, ChooseLocationOnMapDialog.OnLocationChoosed {
    public static final String TAG = "NewFragment";
    private static final int TAKE_IMAGE = 0;
    private static final int PICK_IMAGE = 1;
    private static final int DECODE_LOCATION_ADDRESS = 2;

    List<String> selectedCategoryIds= new ArrayList<>();
    Button openCategoryPopup;
    Button submit;
    EditText title;
    EditText description;
    EditText mobile;
    EditText email;
    EditText telegramId;
    Button uploadImage;
    Button pickDate;
    TextView dateTitle;
    Button selectProvince;
    Button selectCity;
    String occurredDate;
    PersianCalendar persianCalendar = new PersianCalendar();
    ImageView imageView;
    String imageUrl;
    File photo;
    File compressedPhoto;
    Uri mImageUri;
    RadioGroup radioGroup;
    String selectedCityId;
    String selectedCityTitle;
    String selectedProvinceId;
    String selectedProvideTitle;
    List<String> selectedCategoryTitles = new ArrayList<>();
    boolean takeImageFromCamera = false;
    Button showMap;
    private String selectedAddress;
    private String latitude;
    private String longitude;
    String id;
    private boolean edit;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        activity.highlightNewIcon();
        View view = layoutInflater.inflate(R.layout.new_fragment, container, false);
        initOpenCategoryPopup(view);
        initSelectProvice(view);
        initSelectCity(view);
        initShowMap(view);
        initTitle(view);
        initDescription(view);
        NewFragmentPermissionsDispatcher.initUploadImageWithCheck(this, view);
        initChooseDate(view);
        initSubmit(view);
        initDate(view);
        initRadioGroup(view);
        initMobile(view);
        initEmail(view);
        initTelegramId(view);
        initBackButton();
        Bundle args = getArguments();
        if(args != null  && !args.getString(Const.ID).isEmpty()){
            edit= true;
            id  =  args.getString(Const.ID);
            initValues();
        }
        return view;
    }

    private void initValues() {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ID,id);
        GsonRequest<Item> request = new GsonRequest<>(Const.DETAIL_ITEM_URL, Item.class, params, null, new Response.Listener<Item>() {
            @Override
            public void onResponse(final Item item) {
                title.setText(item.Title);
                description.setText(item.Description);
                pickDate.setText(item.Date);
                occurredDate = item.Date;
                selectCity.setText(item.CityTitle);
                selectedCityId =  item.CityId;
                selectedCityTitle = item.CityTitle;
                selectProvince.setText(item.ProvinceTitle);
                selectedProvinceId =  item.ProvinceId;
                selectedProvideTitle = item.ProvinceTitle;
                String title="";
                for(String t : item.CategoryTitles){
                    title += t;
                }
                openCategoryPopup.setText(title);
                selectedCategoryIds = item.CategoryIds;
                selectedCategoryTitles = item.CategoryTitles;
                if (item.ItemType == Const.FOUND){
                    radioGroup.check(R.id.found);
                }else {
                    radioGroup.check(R.id.lost);
                }
                String  url  = Util.imageUrlMaker(true,item);
                LoadImageTask imageTask = new LoadImageTask(new LoadImageTask.Listener() {
                    @Override
                    public void onImageLoaded(Bitmap bitmap) {
                        FileOutputStream photoOutputStream = null;
                        try {
                            compressedPhoto  =  createTemporaryFile("picture" + System.currentTimeMillis(), ".jpg");
                            photoOutputStream = new FileOutputStream(compressedPhoto);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, photoOutputStream);
                            imageView.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
                imageTask.execute(url);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),getString(R.string.connection_error),Toast.LENGTH_LONG).show();
            }
        });
        ApplicationController.getInstance().addToRequestQueue(request);
    }

    private void initTelegramId(View view) {
        telegramId = (EditText) view.findViewById(R.id.telegram_id);
    }

    private void initEmail(View view) {
        email = (EditText) view.findViewById(R.id.email);
    }

    private void initBackButton() {
        activity.backButton.setVisibility(View.GONE);
    }

    private void initShowMap(View view) {
        showMap  = (Button) view.findViewById(R.id.showMap);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showProgress();
                NewFragmentPermissionsDispatcher.showMap2WithCheck(NewFragment.this);
            }
        });
    }


    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void showMap2() {
        ChooseLocationOnMapDialog dialog = new ChooseLocationOnMapDialog();
        dialog.mListener = this;
        dialog.show(activity.getSupportFragmentManager(),TAG);
    }

    private void initDate(View view) {
        dateTitle = (TextView) view.findViewById(R.id.date_title);
    }

    private void initMobile(View view) {
        mobile = (EditText) view.findViewById(R.id.mobile);
    }

    private void initSelectCity(View view) {
        selectCity = (Button) view.findViewById(R.id.select_city);
        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseItemsDialog dialog = new ChooseItemsDialog();
                List<City> cities = activity.databaseHandler.getCities(selectedProvinceId);
                List<KeyValuePair> citiesKeyValuePair = new ArrayList<KeyValuePair>();
                for (City c : cities) {
                    KeyValuePair keyValuePair = new KeyValuePair(c.id, c.name);
                    citiesKeyValuePair.add(keyValuePair);
                }
              /*  dialog.setArguments(citiesKeyValuePair, new OnOneItemSelectedInDialog() {
                    @Override
                    public void onItemSelected(String selectedId, String selectedTitle) {
                        selectedCityId = selectedId;
                        selectedCityTitle = selectedTitle;
                        selectCity.setText(selectedCityTitle);
                    }
                });
                dialog.show(activity.getSupportFragmentManager().beginTransaction(), TAG);*/
            }
        });
    }

    private void initSelectProvice(View view) {
        selectProvince = (Button) view.findViewById(R.id.select_province);
        selectProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseItemsDialog dialog = new ChooseItemsDialog();
                List<Province> provinceList = activity.databaseHandler.getProvinces();
                List<KeyValuePair> items = new ArrayList<KeyValuePair>();
                for (Province p : provinceList) {
                    KeyValuePair keyValuePair = new KeyValuePair(String.valueOf(p.id), p.name);
                    items.add(keyValuePair);
                }
              /*  dialog.setArguments(items, new OnOneItemSelectedInDialog() {
                    @Override
                    public void onItemSelected(String selectedId, String selectedTitle) {
                        selectedProvinceId = selectedId;
                        selectedProvideTitle = selectedTitle;
                        selectProvince.setText(selectedProvideTitle);
                        selectCity.setEnabled(true);
                        //if already selected a city
                        if (selectedCityId != null) {
                            selectedCityTitle = getString(R.string.select_city);
                            selectedCityId = "";
                            selectCity.setText(selectedCityTitle);
                        }
                    }
                });
                dialog.show(activity.getSupportFragmentManager().beginTransaction(), TAG);*/
            }
        });
    }

    private void initRadioGroup(View view) {
        radioGroup = (RadioGroup) view.findViewById(R.id.type_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int itemType = getSelectedItemType();
                if (itemType == Const.FOUND) {
                    dateTitle.setText(getString(R.string.founded_date));
                } else {
                    dateTitle.setText(getString(R.string.lost_date));
                }

            }
        });
    }

    private int getSelectedItemType() {
        int selectedTypeId = radioGroup.getCheckedRadioButtonId();
        if (selectedTypeId == R.id.found) {
            return Const.FOUND;
        }

        return Const.LOST;
    }


    private void initSubmit(View view) {
        submit = (Button) view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateForm()) {
                    Toast.makeText(getContext(), getString(R.string.fill_requirement), Toast.LENGTH_LONG).show();
                    return;
                }
                int itemType = getSelectedItemType();

                Charset chars = Charset.forName(Const.UTF8); // Setting up the encoding
                MultipartEntity multipartEntity = new MultipartEntity();
                try {
                    if(edit){
                        multipartEntity.addPart(Const.ID, new StringBody(id, chars));
                    }
                    multipartEntity.addPart(Const.TITLE, new StringBody(title.getText().toString(), chars));
                    multipartEntity.addPart(Const.DESCRIPTION, new StringBody(description.getText().toString(), chars));
                    multipartEntity.addPart(Const.DATE, new StringBody(occurredDate, chars));
                    multipartEntity.addPart(Const.CATEGORIES, new StringBody(Util.buildCommaSeperate(selectedCategoryIds)));
                    multipartEntity.addPart(Const.CATEGORY_TITLES, new StringBody(Util.buildCommaSeperate(selectedCategoryTitles), chars));
                    if(selectedProvinceId != null && !selectedProvinceId.isEmpty()){
                        multipartEntity.addPart(Const.PROVINCE_ID, new StringBody(selectedProvinceId));
                        multipartEntity.addPart(Const.PROVINCE_TITLE, new StringBody(selectedProvideTitle, chars));
                    }

                    if(selectedCityId != null && !selectedCityId.isEmpty()){
                        multipartEntity.addPart(Const.CITY_ID, new StringBody(selectedCityId));
                        multipartEntity.addPart(Const.CITY_TITLE, new StringBody(selectedCityTitle, chars));
                    }
                    multipartEntity.addPart(Const.MOBILE, new StringBody(mobile.getText().toString(), chars));
                    multipartEntity.addPart(Const.EMAIL, new StringBody(email.getText().toString(), chars));
                    multipartEntity.addPart(Const.TELEGRAM_ID, new StringBody(telegramId.getText().toString(), chars));
                    if(latitude != null &&  !latitude.isEmpty()){
                        multipartEntity.addPart(Const.LATITUDE, new StringBody(latitude, chars));
                        multipartEntity.addPart(Const.LONGITUDE, new StringBody(longitude, chars));
                    }

                    if(selectedAddress== null  || selectedAddress.isEmpty()){
                        selectedAddress = selectedProvideTitle + " " + selectedCityTitle;
                    }
                    multipartEntity.addPart(Const.ADDRESS, new StringBody(String.valueOf(selectedAddress), chars));

                    if (compressedPhoto != null) {
                        multipartEntity.addPart(Const.IMAGE_FILE, new FileBody(compressedPhoto));
                    }
                    multipartEntity.addPart(Const.ITEM_TYPE, new StringBody(String.valueOf(itemType)));
                    activity.showProgress();
                    CustomMultipartVolleyRequest request = new CustomMultipartVolleyRequest(Const.ADD_ITEM_URL, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, error.toString());
                        }
                    }, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            activity.hideProgress();
                            Log.e(TAG, response.toString());
                            if (takeImageFromCamera) {
                                photo.delete();
                            }

                            if (compressedPhoto != null) {
                                compressedPhoto.delete();
                            }

                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
                            dlgAlert.setPositiveButton(getString(R.string.bashe), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            if(edit){
                                dlgAlert.setMessage(getString(R.string.updated_successfully));
                            }else {
                                dlgAlert.setMessage(getString(R.string.new_item_added_successfully));
                            }

                            dlgAlert.show()
                            ;
                            if(edit){
                                activity.addFragmentToContainer(new MyItemsFragment(), MyItemsFragment.TAG);
                            }else {
                                activity.addFragmentToContainer(new BrowseFragment(), BrowseFragment.TAG);
                            }

                        }
                    }, multipartEntity, Long.valueOf(0), null);
                    ApplicationController.getInstance().addToRequestQueue(request, 60000);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (AuthFailureError authFailureError) {
                    authFailureError.printStackTrace();
                }
            }
        });
    }

    private boolean validateForm() {
        if (selectedCategoryIds == null || selectedCategoryIds.size() == 0) {
            openCategoryPopup.setError(getString(R.string.category_is_required));
            openCategoryPopup.requestFocus();
            return false;
        } else {
            openCategoryPopup.setError(null);
        }

        if (title.getText().toString().trim().length() == 0) {
            title.setError(getString(R.string.title_is_required));
            title.requestFocus();
            return false;
        } else {
            title.setError(null);
        }

        if (description.getText().toString().trim().length() == 0) {
            description.setError(getString(R.string.description_is_required));
            description.requestFocus();
            return false;
        } else {
            description.setError(null);
        }

        if (pickDate.getText().toString().length() > 10) {
            pickDate.setError(getString(R.string.occurred_date_is_required));
            pickDate.requestFocus();
            return false;
        } else {
            pickDate.setError(null);
        }

        return true;
    }

    private void initChooseDate(View view) {
        pickDate = (Button) view.findViewById(R.id.pick_date);
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        NewFragment.this,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
                android.app.FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
                datePickerDialog.show(fragmentTransaction, "Datepickerdialog");
            }
        });
    }

    public void onSelectedLocation(Location location, String address) {
        showMap.setText(address);

    }

    @Override
    public void onLocationChoosed(Location location, String address) {
        this.latitude = String.valueOf(location.getLatitude());
        this.longitude = String.valueOf(location.getLongitude());
        this.selectedAddress = address;
        showMap.setText(address);
    }

    class ImageUploadClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.choose_take_picture_type);
            TextView fromCamera = (TextView) dialog.findViewById(R.id.take_image_from_camera);
            //take an image
            fromCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    // place where to store camera taken picture
                    try {
                        photo = createTemporaryFile("picture" + System.currentTimeMillis(), ".jpg");
                        mImageUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", photo);
                        //mImageUri = Uri.fromFile(photo);
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                        startActivityForResult(intent, TAKE_IMAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            //choose image from gallery
            TextView fromGallery = (TextView) dialog.findViewById(R.id.take_image_from_gallery);
            fromGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                    startActivityForResult(chooserIntent, PICK_IMAGE);
                }
            });
            dialog.setTitle(getString(R.string.select_image_from));
            dialog.show();
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void initUploadImage(View view) {
        uploadImage = (Button) view.findViewById(R.id.upload_mage);
        imageView = (ImageView) view.findViewById(R.id.image_item);
        ImageUploadClickListener listener = new ImageUploadClickListener();
        imageView.setOnClickListener(listener);
        uploadImage.setOnClickListener(listener);
    }


    //call after take image of choose image from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_IMAGE && resultCode == Activity.RESULT_OK) {
            takeImageFromCamera = true;
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            //photo = Bitmap.createScaledBitmap(photo, 80, 80, false);
            //imageView.setImageBitmap(photo);
            grabImage(imageView, mImageUri);
            compressedPhoto = Compressor.getDefault(getContext()).compressToFile(photo);

        } else if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            takeImageFromCamera = false;
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(bitmap);
            imageUrl = picturePath;
            photo = new File(picturePath);
            compressedPhoto = Compressor.getDefault(getContext()).compressToFile(photo);
        }else  if(requestCode == DECODE_LOCATION_ADDRESS ){
            if(resultCode == FetchAddressIntentService.SUCCESS_RESULT){
                Location location = data.getExtras().getParcelable(FetchAddressIntentService.LOCATION_DATA_EXTRA);
                String address = data.getExtras().getString(FetchAddressIntentService.RESULT_DATA_KEY);
                onSelectedLocation(location, address);
            }else{
                Toast.makeText(getContext(),getString(R.string.connection_error),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initDescription(View view) {
        description = (EditText) view.findViewById(R.id.description);
        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (description.getText().toString().trim().equals("")) {
                        description.setError(getString(R.string.description_is_required));
                    } else {
                        description.setError(null);
                    }
                }
            }
        });
    }

    private void initTitle(View view) {
        title = (EditText) view.findViewById(R.id.title);
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (title.getText().toString().trim().equals("")) {
                        title.setError(getString(R.string.title_is_required));
                    } else {
                        title.setError(null);
                    }
                }
            }
        });
    }

    private void initOpenCategoryPopup(View view) {
        openCategoryPopup = (Button) view.findViewById(R.id.openCategoryPopup);
        openCategoryPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationController.getInstance().addToRequestQueue(
                        new GsonRequest(Const.LIST_CATEGORY_URL, Category[].class, null, null, new Response.Listener<Category[]>() {
                            @Override
                            public void onResponse(Category[] categories) {
                                ChooseItemsDialog dialog = new ChooseItemsDialog();
                                List<KeyValuePair> keyValuePairsCategories = new ArrayList<KeyValuePair>();
                                for (Category c : categories) {
                                    KeyValuePair keyValuePair = new KeyValuePair(c.Id, c.Title);
                                    keyValuePairsCategories.add(keyValuePair);
                                }

                                dialog.setArguments(keyValuePairsCategories, true, new OnOneItemSelectedInDialog() {
                                    @Override
                                    public void onItemSelected(List<KeyValuePair> selected) {
                                        selectedCategoryIds.clear();
                                        selectedCategoryTitles.clear();
                                        for( KeyValuePair pair : selected){
                                            selectedCategoryIds.add(pair.key);
                                            selectedCategoryTitles.add(pair.value);
                                        }
                                        if(selected.size() > 0){
                                            openCategoryPopup.setText(Util.buildCommaSeperate(selectedCategoryTitles));
                                        }else {
                                            openCategoryPopup.setText(getString(R.string.select));
                                        }

                                    }
                                });
                                dialog.show(activity.getSupportFragmentManager().beginTransaction(), TAG);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                            }
                        }));
            }
        });
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        occurredDate = String.format("%d/%d/%d", year, monthOfYear, dayOfMonth);
        pickDate.setText(occurredDate);
    }


    private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir = new File(Util.getDownloadDirectoryPath() + "/.temp/");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    public void grabImage(ImageView imageView, Uri mImageUri) {
        getContext().getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = getContext().getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }
    }

}
