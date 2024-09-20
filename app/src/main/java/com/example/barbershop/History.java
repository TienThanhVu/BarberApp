package com.example.barbershop;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class History {
    private String action;
    private String details;
    private Timestamp timestamp;

    // Constructor không đối số
    public History() {
    }

    public History(String action, String details, Timestamp timestamp) {
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedTimestamp() {
        if (timestamp != null) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(date);
        }
        return "Không xác định"; // Hoặc bất kỳ giá trị mặc định nào
    }

    @Override
    public String toString() {
        return "Dịch vụ: " +
                "action='" + action + '\'' +
                ", details='" + details + '\'' +
                ", timestamp='" + getFormattedTimestamp() + '\'';
    }
}