package com.daemin.lib;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "timedao");
        Entity MyTime = schema.addEntity("MyTime");
        MyTime.addIdProperty().autoincrement();
        MyTime.addStringProperty("timecode").notNull();
        MyTime.addIntProperty("timetype").notNull();
        MyTime.addStringProperty("name");
        MyTime.addIntProperty("year");
        MyTime.addIntProperty("monthofyear");
        MyTime.addIntProperty("dayofmonth");
        MyTime.addIntProperty("dayofweek").notNull();
        MyTime.addIntProperty("starthour").notNull();
        MyTime.addIntProperty("startmin").notNull();
        MyTime.addIntProperty("endhour").notNull();
        MyTime.addIntProperty("endmin").notNull();
        MyTime.addLongProperty("startmillis");
        MyTime.addLongProperty("endmillis");
        MyTime.addStringProperty("memo");
        MyTime.addStringProperty("place");
        MyTime.addDoubleProperty("lat");
        MyTime.addDoubleProperty("lng");
        MyTime.addIntProperty("share");
        MyTime.addLongProperty("alarm");
        MyTime.addStringProperty("repeat");
        MyTime.addStringProperty("color");
        Entity WidgetID = schema.addEntity("WidgetID");
        WidgetID.addIdProperty().autoincrement();
        WidgetID.addIntProperty("tvID").notNull();
        new DaoGenerator().generateAll(schema, args[0]);
    }
}