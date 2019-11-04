/*
 * Copyright (C) 2019 Muhammad Tayyab Sheikh (CS Tayyab) <cstayyab@gmail.com>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
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

import android.content.Context;

import java.util.Date;

public class BotEvent {
    private DBHandle dbHandle;
    public final BotAddress bot;
    public final int status;
    public final Date eventTime;
    public final Date endTime;
    public final String duration;
    public final int eventID;

    public BotEvent(Context context, int eid, int bid, int code, long timestamp, long nextTimestamp) {
        dbHandle = new DBHandle(context);
        this.bot = dbHandle.getBot(bid);
        this.status = code;
        this.eventTime = new Date((long)timestamp*1000);
        this.endTime = new Date((long) nextTimestamp*1000);
        this.duration = getDifference(this.eventTime, this.endTime);
        this.eventID = eid;
    }

    public String getStatusString() {
        if(status == -1) {
            return "Timeout";
        } else if( status == 200) {
            return "OK (200)";
        } else if(status == -256) {
            return "Disconnected";
        }
        return status + "";
    }

    private String getDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        String result = "";
        if(elapsedSeconds > 0) {
            result = elapsedSeconds +" seconds" + result;
        }
        if(elapsedMinutes > 0) {
            result = elapsedMinutes +" minutes " + result;
        }

        if(elapsedHours > 0) {
            result = elapsedHours + " hours " + result;
        }

        if(elapsedDays > 0) {
            result = elapsedDays + " days " + result;
        }

        if(result.equals("")) {
            result = "0 second(s)";
        }
        return result;

    }
}
