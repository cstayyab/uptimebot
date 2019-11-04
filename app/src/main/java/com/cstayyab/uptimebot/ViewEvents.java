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


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class ViewEvents extends AppCompatActivity implements View.OnClickListener {
    private DBHandle dbHandle;
    private BotEvent[] events;
    private TableLayout table;
    private SimpleDateFormat formatter;
    private Button btnRemove;
    private int bid;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);
        Intent i = getIntent();
        context = this;
        bid = i.getIntExtra("bid", -1);
        if(bid == -1) {
            Toast.makeText(this, "Invalid Bot ID.", Toast.LENGTH_LONG).show();
            finish();
        }
        String title = i.getStringExtra("title");
        getSupportActionBar().setTitle("View Events - [" + title +"]");
        btnRemove = findViewById(R.id.btnDelete);
        btnRemove.setOnClickListener(this);
        formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        table = findViewById(R.id.tblEvents);
        dbHandle = new DBHandle(this);
        events = dbHandle.getEvents(bid);
        Toast.makeText(this, "Total Events: " + events.length, Toast.LENGTH_LONG).show();
        for(BotEvent event: events) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams( TableLayout.LayoutParams.MATCH_PARENT ,TableLayout.LayoutParams.WRAP_CONTENT));
            TextView lblState = new TextView(this);
            if(event.status == 200) {
                lblState.setText("Up");
                lblState.setTextColor(Color.WHITE);
                lblState.setBackgroundColor(Color.GREEN);
            } else if(event.status == -256) {
                lblState.setText("Error");
                lblState.setTextColor(Color.WHITE);
                lblState.setBackgroundColor(Color.DKGRAY);
            } else {
                lblState.setText("Down");
                lblState.setTextColor(Color.WHITE);
                lblState.setBackgroundColor(Color.RED);
            }
            lblState.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            TextView lblDT = new TextView(this);
            lblDT.setText(formatter.format(event.eventTime));
            lblDT.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            TextView lblStatus = new TextView(this);
            lblStatus.setText(event.getStatusString());
            lblStatus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            TextView lblDuration = new TextView(this);
            lblDuration.setText(event.duration);
            lblDuration.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            row.addView(lblState, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            row.addView(lblDT, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            row.addView(lblStatus, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            row.addView(lblDuration, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


        }


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnRemove.getId()) {
            new AlertDialog.Builder(this)
                    .setTitle("Remove Bot?")
                    .setMessage("Are you sure you want to remove this bot?\nThis will also remove all the monitoring data which is unrecoverable.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(dbHandle.removeBot(bid)) {
                                Toast.makeText(context, "Selected Bot Removed.", Toast.LENGTH_LONG).show();
                                setResult(MainActivity.UPDATE_LIST);
                                finish();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();

        }

    }
}
