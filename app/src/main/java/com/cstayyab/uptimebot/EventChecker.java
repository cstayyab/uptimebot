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

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class EventChecker extends Service {
    public static  boolean started = false;
    private DBHandle dbHandle;

    public EventChecker() {
        dbHandle = new DBHandle(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(started) {
                    return;
                }
                started = true;
                while(true) {
                    BotAddress[] bots = dbHandle.getAllBots();
                    for(BotAddress b:bots) {
                        if(!isInternetAvailable()) {
                            int statusCode = -256; //Connection Timeout
                            if(statusCode != dbHandle.getStatus(b.id)) {
                                dbHandle.addEvent(b.id, statusCode);
                            }
                            continue;
                        }
                        new UpdateStatus().execute(b);
                        SystemClock.sleep(5000);

                    }
                }
            }
        }).start();
        return START_STICKY;

    }

    private boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    class UpdateStatus extends AsyncTask<BotAddress, Void, Void> {

        @Override
        protected Void doInBackground(BotAddress... bots) {
            BotAddress b = bots[0];
            try {
                HttpGet httpRequest = new HttpGet(new URI(b.url));
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpRequest);
                int statusCode = response.getStatusLine().getStatusCode();
                if(statusCode != dbHandle.getStatus(b.id)) {
                    dbHandle.addEvent(b.id, statusCode);
                }

            } catch(URISyntaxException e) {
                e.printStackTrace();
            } catch(ClientProtocolException e) {
                e.printStackTrace();
            } catch(org.apache.http.conn.HttpHostConnectException e) {
                int statusCode = -1; //Connection Timeout
                if(statusCode != dbHandle.getStatus(b.id)) {
                    dbHandle.addEvent(b.id, statusCode);
                }

            } catch (IOException e) {

                e.printStackTrace();
            }
//
//            HttpURLConnection urlConnection = null;
//            System.setProperty("http.keepAlive", "false");
//            try {
//                URL url = new URL(b.url);
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("HEAD");
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (urlConnection != null) {
//                    try {
//                        int statusCode = urlConnection.getResponseCode();
//                        if(statusCode != dbHandle.getStatus(b.id)) {
//                            dbHandle.addEvent(b.id, statusCode);
//                        }
//                        urlConnection.getInputStream().close();
//
//                    } catch(IOException ex) {
//                        ex.printStackTrace();
//                    }
//                    urlConnection.disconnect();
//                }
//            }
            return null;
        }


    }

}
