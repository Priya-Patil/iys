package com.netist.mygirlshostel.advertisement;

import android.os.Parcel;
import android.os.Parcelable;

public class DeleteModel implements Parcelable {

    int TransactionId;

    public DeleteModel(int transactionId) {
        TransactionId = transactionId;
    }

    protected DeleteModel(Parcel in) {
        TransactionId = in.readInt();
    }

    public static final Creator<DeleteModel> CREATOR = new Creator<DeleteModel>() {
        @Override
        public DeleteModel createFromParcel(Parcel in) {
            return new DeleteModel(in);
        }

        @Override
        public DeleteModel[] newArray(int size) {
            return new DeleteModel[size];
        }
    };

    public int getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(int transactionId) {
        TransactionId = transactionId;
    }

    @Override
    public String toString() {
        return "DeleteModel{" +
                "TransactionId=" + TransactionId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(TransactionId);
    }
}
