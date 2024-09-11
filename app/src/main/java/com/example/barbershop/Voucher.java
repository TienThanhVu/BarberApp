package com.example.barbershop;

import java.util.Date;

public class Voucher {
    private  String id;
    private String code;
    private int percent;
    private String note;
    private Date exDate;

    public Voucher(){}

    public Voucher(String id, String code, int percent, String note, Date exDate) {
        this.id = id;
        this.code = code;
        this.percent = percent;
        this.note = note;
        this.exDate = exDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getExDate() {
        return exDate;
    }

    public void setExDate(Date exDate) {
        this.exDate = exDate;
    }

    // Phương thức kiểm tra tính hợp lệ của voucher
    public boolean isValid(Date currentDate) {
        if (exDate != null && currentDate.after(exDate)) {
            return false; // Voucher đã hết hạn
        }
        return true;
    }

    @Override
    public String toString() {
        return percent + " %" + "( " + note + " )";
    }
}
