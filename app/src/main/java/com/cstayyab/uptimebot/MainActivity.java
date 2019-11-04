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

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private DBHandle dbHandle;
    private FloatingActionButton btnFloatingAdd;
    private ListView botsList;
    private BotAddress[] bots;
    private final int ADD_BOT_REQUEST = 1001;
    private final int VIEW_EVENTS_REQUEST = 1002;
    public static final int UPDATE_LIST = 200;
    private static boolean IS_FIRST_RUN = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(IS_FIRST_RUN) {

//            final SpannableString s = new SpannableString();
//            Linkify.addLinks(s, Linkify.ALL);


            final TextView message = new TextView(this);
            final SpannableString s =
                    new SpannableString(Html.fromHtml("Uptime Bot is brought to you by " +
                            "<a href='https://cstayyab.com'>Muhammad Tayyab Sheikh (CS Tayyab)" +
                            "</a>.<br/><a href='https://github.com/cstayyab/uptimebot'>" +
                            "View Source Code</a>"));
            message.setText(s);
            message.setMovementMethod(LinkMovementMethod.getInstance());
            message.setPadding(10,10,10,10);
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setCancelable(true)
                    .setPositiveButton("OK", null)
                    .setView(message)
                    .create()
                    .show();
            IS_FIRST_RUN = false;
        }


        dbHandle = new DBHandle(this);
        botsList = findViewById(R.id.listBots);
        btnFloatingAdd = findViewById(R.id.addBot);
        btnFloatingAdd.setOnClickListener(this);
        botsList.setOnItemClickListener(this);
        updateBotList();

        if(!EventChecker.started) {
            Intent i = new Intent(this, EventChecker.class);
            startService(i);
        }


    }


    @Override
    public void onClick(View v) {
        if(v.getId() == btnFloatingAdd.getId()) {
            Intent i = new Intent(this, AddBot.class);
            startActivityForResult(i, ADD_BOT_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == ADD_BOT_REQUEST || requestCode == VIEW_EVENTS_REQUEST) && resultCode == UPDATE_LIST) {
            updateBotList();
        }
    }

    public void updateBotList() {
        bots = dbHandle.getAllBots();
        BotListAdapter botsAdapter = new BotListAdapter(this, bots);
        botsList.setAdapter(botsAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BotAddress b = bots[position];
        Intent i = new Intent(this, ViewEvents.class);
        i.putExtra("url", b.url);
        i.putExtra("title", b.title);
        i.putExtra("bid", b.id);
        i.putExtra("status", b.status);
        startActivityForResult(i, VIEW_EVENTS_REQUEST);

    }


}
