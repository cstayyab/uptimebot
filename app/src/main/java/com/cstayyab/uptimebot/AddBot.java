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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddBot extends AppCompatActivity implements View.OnClickListener {
    Button btnAdd;
    EditText txtTitle, txtURL;
    DBHandle dbHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bot);

        btnAdd = findViewById(R.id.btnAdd);
        txtTitle = findViewById(R.id.txtTitle);
        txtURL = findViewById(R.id.txtURL);

        dbHandle = new DBHandle(this);
        btnAdd.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnAdd.getId()) {
            String title = txtTitle.getText() + "".trim();
            String url = txtURL.getText() + "".trim();
            if(url.equals("") || !url.matches("^(([^:/?#]+):)?(//([^/?#]*))?$")) {
                new AlertDialog.Builder(this)
                        .setTitle("Invalid URL")
                        .setMessage("URL you speicified is invalid. Please specify host part of URL only. e.g. https://www.google.com")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return;
            }

            String res = dbHandle.addBot(title, url);
            if(res.equals("")) {
                Toast.makeText(this, "Added: " + title + "(" + url + ")" , Toast.LENGTH_LONG).show();
                Intent i = getIntent();
                setResult(MainActivity.UPDATE_LIST, i);
                finish();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Cannot Add!")
                        .setMessage("Cannot add new bot.\n" + res)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        }
    }
}
