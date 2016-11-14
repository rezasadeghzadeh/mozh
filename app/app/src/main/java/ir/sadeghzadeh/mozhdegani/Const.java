package ir.sadeghzadeh.mozhdegani;

import org.apache.http.entity.mime.FormBodyPart;

/**
 * Created by reza on 11/12/16.
 */
public class Const {
    public static final String APP_CONFIG = "mozhdegani";
    public static final String VERSION = "0.1";
    public static final String SERVER_URL  = "http://192.168.177.104:7777";
    public static final String LIST_ITEMS_URL  = SERVER_URL  + "/item/list";

    public static final String LIST_CATEGORY_URL = SERVER_URL + "/category/list";
    public static final String ADD_ITEM_URL = SERVER_URL + "/item/add";

    private static final String STATIC_URL = "/static";
    public static final String THUMBNAIL_URL = STATIC_URL + "/t";
    public static final String FULL_IMAGE_URL = STATIC_URL + "/f";
    public static final String DETAIL_ITEM_URL = SERVER_URL + "/item/detail";



    public static final String TITLE  = "Title";
    public static final String DESCRIPTION  = "Description";
    public static final String DATE = "Date";
    public static final String CATEGORY = "Category";
    public static final String PROVINCE_ID = "ProvinceId";
    public static final String PROVINCE_TITLE  = "ProvinceTitle";
    public static final String CITY_ID = "CityId";
    public static final String CITY_TITLE =  "CityTitle";
    public static final String IMAGE_FILE =  "ImageFile";
    public static final String ITEM_TYPE =  "ItemType";
    public static final String UTF8 = "UTF-8";
    public static final int FOUND = 2;
    public static final int LOST = 1;
    public static final String ID = "Id";
    public static final String CATEGORY_TITLE = "CategoryTitle";
}
