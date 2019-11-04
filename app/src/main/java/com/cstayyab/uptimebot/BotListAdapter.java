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

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BotListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final BotAddress[] bots;

    public BotListAdapter(Activity context, BotAddress[] bots) {
        super(context, R.layout.onebotitem, new String[bots.length]);
        this.context = context;
        this.bots = bots;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Bitmap favicon = null;
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.onebotitem, null,  true);
        TextView lblName =rowView.findViewById(R.id.lblName);
        TextView lblURL =  rowView.findViewById(R.id.lblURL);
        ImageView imgFavicon =  rowView.findViewById(R.id.favicon);


        Picasso.get()
                .load(this.bots[position].favicon)
                //.resize(512, 512) // here you resize your image to whatever width and height you like
                .fit()
                .centerCrop()
                .into(imgFavicon);
        lblName.setText(this.bots[position].title);
        lblURL.setText(this.bots[position].url);
        return rowView;
    }


}
