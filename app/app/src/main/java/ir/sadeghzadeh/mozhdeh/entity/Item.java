package ir.sadeghzadeh.mozhdeh.entity;

import java.util.List;

/**
 * Created by reza on 11/3/16.
 */
public class Item{
    public String id;
    public String Title;
    public String Description;
    public String ItemType;
    public long RegisterDate;
    public String Date;
    public String CategoryId;
    public String CategoryTitle;
    public String CityId;
    public String CityTitle;
    public String ProvinceId;
    public String ProvinceTitle;
    public String ImageExt;
    public String Mobile;
    public String Latitude;
    public String Longitude;
    public String Address;
    public String Email;
    public String TelegramId;
    public List<ItemMessage> Messages;

}
