package com.vaz.covid_19dadosdobrasil.model;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class State {
    private String state;
    private String uf;
    private String deaths;
    private String cases;
    private String refuses;
    private String suspects;
    private String datetime;
    private String image;

    public State(){}

    public State(String state, String uf, String deaths, String cases, String refuses, String suspects, String datetime, String image) {
        this.state = state;
        this.uf = uf;
        this.deaths = deaths;
        this.cases = cases;
        this.refuses = refuses;
        this.suspects = suspects;
        this.datetime = datetime;
        this.image = image;
    }

    public String formatNumber(String number){
        Locale localeBR = new Locale("pt","BR");
        int value = Integer.valueOf(number);
        NumberFormat integ = NumberFormat.getInstance();

        return integ.format(value).replace(",", ".");
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getDeaths() {
        return this.formatNumber(deaths);
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }

    public String getCases() {
        return this.formatNumber(cases);
    }

    public void setCases(String cases) {
        this.cases = cases;
    }

    public String getRefuses() {
        return this.formatNumber(refuses);
    }

    public void setRefuses(String refuses) {
        this.refuses = refuses;
    }

    public String getSuspects() {
        return this.formatNumber(suspects);
    }

    public void setSuspects(String suspects) {
        this.suspects = suspects;
    }

    public String getDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");

        Date d = null;
        try {
            d = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return output.format(d).replaceAll("-", "/");
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
