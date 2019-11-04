/*
 * Copyright (C) 2019 Muhammad Tayyab Sheikh (CS Tayyab) <cstayyab@gmail.com>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3
 * of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */


package com.cstayyab.uptimebot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;


import java.util.ArrayList;

public class DBHandle extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "botstore.db";
    private final Context context;

    public DBHandle(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `bots` (" +
                "`bid` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "`name` TEXT NOT NULL, " +
                "`url` TEXT NOT NULL UNIQUE, " +
                "`status` INTEGER NOT NULL " +
                ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS  `events` (" +
                "`eid` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "`bid` INTEGER NOT NULL, " +
                "`code` INTEGER NOT NULL, " +
                "`timestamp` INTEGER NOT NULL, " +
                "FOREIGN KEY(`bid`) REFERENCES `bots`(`bid`) " +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS bots");
        onCreate(db);
    }

    public BotAddress[] getAllBots() {
        ArrayList<BotAddress>  bots = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM bots", null);
        res.moveToFirst();
        while(res.isAfterLast() == false) {
            BotAddress b = new BotAddress(res.getInt(res.getColumnIndex("bid")), res.getString(res.getColumnIndex("name")), res.getString(res.getColumnIndex("url")), res.getInt(res.getColumnIndex("status")));
            bots.add(b);
            res.moveToNext();
        }
        BotAddress[] BotList = new BotAddress[bots.size()];
        BotList = bots.toArray(BotList);
        res.close();
        db.close();
        return BotList;
    }

    public String addBot(String name, String url) {
        if(botExists(url)) {
            return "Bot with same host already exists.";
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("url", Uri.parse(url).getScheme() + "://" + Uri.parse(url).getHost());
        contentValues.put("status", 0);
        db.insert("bots", null, contentValues);
        db.close();
        return ""; //No error
    }

    public boolean botExists(String url) {
        Uri u = Uri.parse(url);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM bots WHERE url LIKE '%://" + u.getHost() + "%'", null);
        res.moveToFirst();
        boolean exists = res.getCount() > 0;
        res.close();
        db.close();
        return exists;
    }

    public boolean addEvent(int bid, int statusCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("bid", bid);
        contentValues.put("code", statusCode);
        long unixTime = System.currentTimeMillis() / 1000L;
        contentValues.put("timestamp", unixTime);
        db.insert("events", null, contentValues);
        contentValues = new ContentValues();
        contentValues.put("status", statusCode);
        db.update("bots", contentValues, " bid = ? ", new String[] { Integer.toString(bid)} );
        db.close();
        return true;
    }
    public int getStatus(int bid) {
        BotAddress b = getBot(bid);
        if(b != null) {
            return b.status;
        } else {
            return -1;
        }
    }
    public BotAddress getBot(int bid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM bots WHERE bid =?", new String[] {Integer.toString(bid)});
        if(res.getCount() == 1) {
            res.moveToFirst();
            String name = res.getString(res.getColumnIndex("name"));
            String url =res.getString(res.getColumnIndex("url"));
            int status = res.getInt(res.getColumnIndex("status"));
            BotAddress b  = new BotAddress(bid, name, url, status);
            res.close();
            db.close();
            return b;
        } else {
            res.close();
            db.close();
            return null;
        }
    }

    public BotEvent[] getEvents(int bid) {
        ArrayList<BotEvent> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM events WHERE bid = ? ORDER BY timestamp ASC", new String[] {Integer.toString(bid)});
        res.moveToFirst();
        while(!res.isAfterLast()) {
            int eid = res.getInt(res.getColumnIndex("eid"));
            int code = res.getInt(res.getColumnIndex("code"));
            long timestamp = res.getLong(res.getColumnIndex("timestamp"));
            long nextTimestamp = System.currentTimeMillis() / 1000L;
            res.moveToNext();
            if(res.isAfterLast()) {
                events.add(new BotEvent(context, eid, bid, code, timestamp, nextTimestamp));
            } else {
                nextTimestamp = res.getLong(res.getColumnIndex("timestamp"));
                events.add(new BotEvent(context, eid, bid, code, timestamp, nextTimestamp));
            }
        }
        BotEvent[] botEvents = new BotEvent[events.size()];
        botEvents = events.toArray(botEvents);
        res.close();
        db.close();
        return  botEvents;
    }

    public boolean removeBot(int bid) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM bots WHERE bid = ? ", new String[] { Integer.toString(bid) });
        db.execSQL("DELETE FROM events WHERE bid = ? ", new String[] { Integer.toString(bid) });
        return true;
    }


}
