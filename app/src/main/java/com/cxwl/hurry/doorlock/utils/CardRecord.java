package com.cxwl.hurry.doorlock.utils;

import java.util.Date;

public class CardRecord {
    public String card = null;
    public Date creDate = null;

    public CardRecord() {
        this.card = "";
        this.creDate = new Date();
    }

    public boolean checkLastCard(String card) {
        boolean result = false;
        if (this.card.equals(card)) {
            long offset = new Date().getTime() - this.creDate.getTime();
            if (offset > 2000) {
                this.card = card;
                this.creDate = new Date();
            } else {
                result = true;
            }
        } else {
            this.card = card;
            this.creDate = new Date();
        }
        return result;
    }

    public boolean checkLastCardNew(String name) {
        boolean result = false;
        if (this.card.equals(name)) {
            long offset = new Date().getTime() - this.creDate.getTime();
            if (offset > 3000) {
                this.card = name;
                this.creDate = new Date();
            } else {
                result = true;
            }
        } else {
            this.card = name;
            this.creDate = new Date();
        }
        return result;
    }
}
