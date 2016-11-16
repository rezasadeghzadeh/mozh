package ir.sadeghzadeh.mozhdegani.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ir.sadeghzadeh.mozhdegani.ApplicationController;
import ir.sadeghzadeh.mozhdegani.Const;
import ir.sadeghzadeh.mozhdegani.MainActivity;
import ir.sadeghzadeh.mozhdegani.R;
import ir.sadeghzadeh.mozhdegani.dialog.ChooseOneItemDialog;
import ir.sadeghzadeh.mozhdegani.dialog.OnOneItemSelectedInDialog;
import ir.sadeghzadeh.mozhdegani.entity.Category;
import ir.sadeghzadeh.mozhdegani.entity.City;
import ir.sadeghzadeh.mozhdegani.entity.KeyValuePair;
import ir.sadeghzadeh.mozhdegani.entity.Province;
import ir.sadeghzadeh.mozhdegani.utils.Util;
import ir.sadeghzadeh.mozhdegani.volley.CustomMultipartVolleyRequest;
import ir.sadeghzadeh.mozhdegani.volley.GsonRequest;

/**
 * Created by reza on 11/2/16.
 */
public class NewFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {
    public static final String TAG="NewFragment";
    private static final int TAKE_IMAGE = 0;
    private static final int PICK_IMAGE = 1;
    String currentCategoryId;
    MainActivity activity;
    Button openCategoryPopup;
    Button submit;
    EditText  title;
    EditText description;
    EditText mobile;
    Button uploadImage;
    Button pickDate;
    Button selectProvince;
    Button selectCity;
    String occurredDate;
    PersianCalendar persianCalendar = new PersianCalendar();
    ImageView imageView;
    String imageUrl;
    File photo;
    Uri mImageUri;
    RadioGroup radioGroup;
    String selectedCityId;
    String selectedCityTitle;
    String selectedProvinceId;
    String  selectedProvideTitle;
    String currentCategoryTitle;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activity  = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        activity.highlightNewIcon();
        View view = layoutInflater.inflate(R.layout.new_fragment, container, false);
        initOpenCategoryPopup(view);
        initSelectProvice(view);
        initSelectCity(view);
        initTitle(view);
        initDescription(view);
        initUploadImage(view);
        initChooseDate(view);
        initSubmit(view);
        initRadioGroup(view);
        initMobile(view);
        return view;
    }

    private void initMobile(View view) {
        mobile = (EditText) view.findViewById(R.id.mobile);
    }

    private void initSelectCity(View view) {
        selectCity = (Button) view.findViewById(R.id.select_city);
        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseOneItemDialog dialog = new ChooseOneItemDialog();
                List<City> cities = activity.databaseHandler.getCities(selectedProvinceId);
                List<KeyValuePair> citiesKeyValuePair  =  new ArrayList<KeyValuePair>();
                for(City c: cities){
                    KeyValuePair keyValuePair = new KeyValuePair(c.id,c.name);
                    citiesKeyValuePair.add(keyValuePair);
                }
                dialog.setArguments(citiesKeyValuePair, new OnOneItemSelectedInDialog() {
                    @Override
                    public void onItemSelected(String selectedId, String selectedTitle) {
                        selectedCityId = selectedId;
                        selectedCityTitle = selectedTitle;
                        selectCity.setText(selectedCityTitle);
                    }
                });
                dialog.show(activity.getSupportFragmentManager().beginTransaction(),TAG);
            }
        });
    }

    private void initSelectProvice(View view) {
        selectProvince = (Button) view.findViewById(R.id.select_province);
        selectProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseOneItemDialog dialog = new ChooseOneItemDialog();
                List<Province> provinceList = activity.databaseHandler.getProvinces();
                List<KeyValuePair> items = new ArrayList<KeyValuePair>();
                for(Province p: provinceList){
                    KeyValuePair keyValuePair = new KeyValuePair(String.valueOf(p.id),p.name);
                    items.add(keyValuePair);
                }
                dialog.setArguments(items, new OnOneItemSelectedInDialog() {
                    @Override
                    public void onItemSelected(String selectedId, String selectedTitle) {
                        selectedProvinceId = selectedId;
                        selectedProvideTitle = selectedTitle;
                        selectProvince.setText(selectedProvideTitle);
                        selectCity.setEnabled(true);
                        //if already selected a city
                        if(selectedCityId != null){
                            selectedCityTitle = getString(R.string.select_city);
                            selectedCityId = "";
                            selectCity.setText(selectedCityTitle);
                        }
                    }
                });
                dialog.show(activity.getSupportFragmentManager().beginTransaction(),TAG);
            }
        });
    }

    private void initRadioGroup(View view) {
        radioGroup  = (RadioGroup) view.findViewById(R.id.type_radio_group);
    }

    private void initSubmit(View view) {
        submit = (Button) view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateForm()){
                    Toast.makeText(getContext(),getString(R.string.fill_requirement),Toast.LENGTH_LONG).show();
                    return;
                }
                int itemType  = Const.LOST;
                int selectedTypeId  =  radioGroup.getCheckedRadioButtonId();
                if(selectedTypeId  ==  R.id.found){
                    itemType = Const.FOUND;
                }
                Charset chars = Charset.forName(Const.UTF8); // Setting up the encoding
                MultipartEntity multipartEntity = new MultipartEntity();
                try {
                    multipartEntity.addPart(Const.TITLE, new StringBody(title.getText().toString(),chars));
                    multipartEntity.addPart(Const.DESCRIPTION, new StringBody(description.getText().toString(),chars));
                    multipartEntity.addPart(Const.DATE, new StringBody(occurredDate,chars));
                    multipartEntity.addPart(Const.CATEGORY, new StringBody(currentCategoryId));
                    multipartEntity.addPart(Const.CATEGORY_TITLE, new StringBody(currentCategoryTitle,chars));
                    multipartEntity.addPart(Const.PROVINCE_ID, new StringBody(selectedProvinceId));
                    multipartEntity.addPart(Const.PROVINCE_TITLE, new StringBody(selectedProvideTitle,chars));
                    multipartEntity.addPart(Const.CITY_ID, new StringBody(selectedCityId));
                    multipartEntity.addPart(Const.CITY_TITLE, new StringBody(selectedCityTitle, chars));
                    multipartEntity.addPart(Const.MOBILE, new StringBody(mobile.getText().toString(), chars));

                    if(photo != null){
                        multipartEntity.addPart(Const.IMAGE_FILE, new FileBody(photo));
                    }
                    multipartEntity.addPart(Const.ITEM_TYPE, new StringBody(String.valueOf(itemType)));
                    activity.showProgress();
                    CustomMultipartVolleyRequest request = new CustomMultipartVolleyRequest(Const.ADD_ITEM_URL, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG,error.toString());
                        }
                    }, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            activity.hideProgress();
                            Log.e(TAG,response.toString());
                            activity.addFragmentToContainer(new BrowseFragment(),BrowseFragment.TAG);
                            Toast.makeText(getContext(),getString(R.string.new_item_added_successfully),Toast.LENGTH_LONG).show();
                        }
                    }, multipartEntity, Long.valueOf(0), null);
                    ApplicationController.getInstance().addToRequestQueue(request,30000);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (AuthFailureError authFailureError) {
                    authFailureError.printStackTrace();
                }finally {
                    //photo.delete();
                }
            }
        });
    }

    private boolean validateForm() {
        if(currentCategoryId== null || currentCategoryId.length() == 0){
            openCategoryPopup.setError(getString(R.string.category_is_required));
            openCategoryPopup.requestFocus();
            return false;
        }else {
            openCategoryPopup.setError(null);
        }

        if(title.getText().toString().trim().length() == 0){
            title.setError(getString(R.string.title_is_required));
            title.requestFocus();
            return false;
        }else {
            title.setError(null);
        }

        if(description.getText().toString().trim().length() == 0){
            description.setError(getString(R.string.description_is_required));
            description.requestFocus();
            return false;
        }else {
            description.setError(null);
        }

        if(pickDate.getText().toString().length() > 10){
            pickDate.setError(getString(R.string.occurred_date_is_required));
            pickDate.requestFocus();
            return false;
        }else{
            pickDate.setError(null);
        }

        return true;
    }

    private void initChooseDate(View view) {
        pickDate = (Button) view.findViewById(R.id.pick_date);
        pickDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        NewFragment.this,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
                android.app.FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
                datePickerDialog.show(fragmentTransaction,"Datepickerdialog");
            }
        });
    }

    class ImageUploadClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.choose_take_picture_type);
            TextView fromCamera  = (TextView) dialog.findViewById(R.id.take_image_from_camera);
            //take an image
            fromCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    // place where to store camera taken picture
                    try {
                        photo = createTemporaryFile("picture"+ System.currentTimeMillis(), ".jpg");
                        mImageUri = Uri.fromFile(photo);
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                        startActivityForResult(intent, TAKE_IMAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            //choose image from gallery
            TextView fromGallery  = (TextView) dialog.findViewById(R.id.take_image_from_gallery);
            fromGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                    startActivityForResult(chooserIntent, PICK_IMAGE);
                }
            });
            dialog.setTitle(getString(R.string.select_image_from));
            dialog.show();
        }
    }

    private void initUploadImage(View view) {
        uploadImage= (Button) view.findViewById(R.id.upload_mage);
        imageView = (ImageView) view.findViewById(R.id.image_item);
        ImageUploadClickListener listener = new ImageUploadClickListener();
        imageView.setOnClickListener(listener);
        uploadImage.setOnClickListener(listener);
    }

    //call after take image of choose image from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAKE_IMAGE && resultCode == Activity.RESULT_OK )
        {
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            //photo = Bitmap.createScaledBitmap(photo, 80, 80, false);
            //imageView.setImageBitmap(photo);
            grabImage(imageView,mImageUri);

        }else if (requestCode  == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(bitmap);
            imageUrl = picturePath;
            photo =  new File(picturePath);
        }
    }

    private void initDescription(View view) {
        description  = (EditText) view.findViewById(R.id.description);
        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(description.getText().toString().trim().equals("")){
                        description.setError(getString(R.string.description_is_required));
                    }else {
                        description.setError(null);
                    }
                }
            }
        });
    }

    private void initTitle(View view) {
        title  = (EditText) view.findViewById(R.id.title);
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(title.getText().toString().trim().equals("")){
                        title.setError(getString(R.string.title_is_required));
                    }else {
                        title.setError(null);
                    }
                }
            }
        });
    }

    private void initOpenCategoryPopup(View view) {
        openCategoryPopup  = (Button) view.findViewById(R.id.openCategoryPopup);
        openCategoryPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationController.getInstance().addToRequestQueue(
                        new GsonRequest(Const.LIST_CATEGORY_URL, Category[].class, null, null, new Response.Listener<Category[]>() {
                            @Override
                            public void onResponse(Category[] categories) {
                                ChooseOneItemDialog dialog = new ChooseOneItemDialog();
                                List<KeyValuePair> keyValuePairsCategories  = new ArrayList<KeyValuePair>();
                                for(Category c: categories){
                                    KeyValuePair keyValuePair = new KeyValuePair(c.Id,c.Title);
                                    keyValuePairsCategories.add(keyValuePair);
                                }

                                dialog.setArguments(keyValuePairsCategories, new OnOneItemSelectedInDialog() {
                                    @Override
                                    public void onItemSelected(String selectedId, String selectedTitle) {
                                        currentCategoryId = selectedId;
                                        openCategoryPopup.setText(selectedTitle);
                                        currentCategoryTitle = selectedTitle;

                                    }
                                });
                                dialog.show(activity.getSupportFragmentManager().beginTransaction(),TAG);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(),getString(R.string.connection_error),Toast.LENGTH_LONG).show();
                            }
                        }));
            }
        });
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        occurredDate = String.format("%d/%d/%d",year,monthOfYear,dayOfMonth);
        pickDate.setText(occurredDate);
    }


    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir=new File(Util.getDownloadDirectoryPath() + "/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    public void grabImage(ImageView imageView, Uri mImageUri)
    {
        getContext().getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = getContext().getContentResolver();
        Bitmap bitmap;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            imageView.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }
    }


}
