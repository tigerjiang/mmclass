
package com.multimedia.room;

import android.os.Parcel;
import android.os.Parcelable;

public class FilesEntity implements Parcelable {
    private String mUrl;
    private String mName;
    private String mType;

    static enum FileType {
        AUDIO, VEDIO, TEXT
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    @Override
    public String toString() {
        return "FilesEntity [mUrl=" + mUrl + ", mName=" + mName + ", mType="
                + mType + "]";
    }

    public FilesEntity() {
    }

    public FilesEntity(Parcel in) {
        mUrl = in.readString();
        mName = in.readString();
        mType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUrl);
        dest.writeString(mName);
        dest.writeString(mType);
    }

    public static final Parcelable.Creator<FilesEntity> CREATOR = new Parcelable.Creator<FilesEntity>() {

        public FilesEntity createFromParcel(Parcel in) {
            return new FilesEntity(in);
        }

        public FilesEntity[] newArray(int size) {
            return new FilesEntity[size];
        }
    };
}
