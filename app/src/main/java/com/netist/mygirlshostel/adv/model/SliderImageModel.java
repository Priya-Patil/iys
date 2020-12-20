package com.netist.mygirlshostel.adv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SliderImageModel implements Parcelable {
    int id;
    String title;
    String details;
    String images;
    int userid;
    String expirydate;
    int isapprove;
    double amount;
    int isactive;
    String created;
    int createdby;
    String modified;
    int modifiedby;
    String name;
    String mobile;
    String address;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "SliderImageModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", details='" + details + '\'' +
                ", images='" + images + '\'' +
                ", userid=" + userid +
                ", expirydate='" + expirydate + '\'' +
                ", isapprove=" + isapprove +
                ", amount=" + amount +
                ", isactive=" + isactive +
                ", created='" + created + '\'' +
                ", createdby=" + createdby +
                ", modified='" + modified + '\'' +
                ", modifiedby=" + modifiedby +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getExpirydate() {
        return expirydate;
    }

    public void setExpirydate(String expirydate) {
        this.expirydate = expirydate;
    }

    public int getIsapprove() {
        return isapprove;
    }

    public void setIsapprove(int isapprove) {
        this.isapprove = isapprove;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getIsactive() {
        return isactive;
    }

    public void setIsactive(int isactive) {
        this.isactive = isactive;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getCreatedby() {
        return createdby;
    }

    public void setCreatedby(int createdby) {
        this.createdby = createdby;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public int getModifiedby() {
        return modifiedby;
    }

    public void setModifiedby(int modifiedby) {
        this.modifiedby = modifiedby;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static Creator<SliderImageModel> getCREATOR() {
        return CREATOR;
    }

    protected SliderImageModel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        details = in.readString();
        images = in.readString();
        userid = in.readInt();
        expirydate = in.readString();
        isapprove = in.readInt();
        amount = in.readDouble();
        isactive = in.readInt();
        created = in.readString();
        createdby = in.readInt();
        modified = in.readString();
        modifiedby = in.readInt();
        name = in.readString();
        mobile = in.readString();
        address = in.readString();
    }

    public static final Creator<SliderImageModel> CREATOR = new Creator<SliderImageModel>() {
        @Override
        public SliderImageModel createFromParcel(Parcel in) {
            return new SliderImageModel(in);
        }

        @Override
        public SliderImageModel[] newArray(int size) {
            return new SliderImageModel[size];
        }
    };

    public SliderImageModel(int id, String title, String details, String images, int userid, String expirydate, int isapprove, double amount, int isactive, String created, int createdby, String modified, int modifiedby, String name, String mobile, String address) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.images = images;
        this.userid = userid;
        this.expirydate = expirydate;
        this.isapprove = isapprove;
        this.amount = amount;
        this.isactive = isactive;
        this.created = created;
        this.createdby = createdby;
        this.modified = modified;
        this.modifiedby = modifiedby;
        this.name = name;
        this.mobile = mobile;
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(details);
        parcel.writeString(images);
        parcel.writeInt(userid);
        parcel.writeString(expirydate);
        parcel.writeInt(isapprove);
        parcel.writeDouble(amount);
        parcel.writeInt(isactive);
        parcel.writeString(created);
        parcel.writeInt(createdby);
        parcel.writeString(modified);
        parcel.writeInt(modifiedby);
        parcel.writeString(name);
        parcel.writeString(mobile);
        parcel.writeString(address);
    }
}