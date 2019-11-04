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


import android.net.Uri;

public class BotAddress {

    public String url;
    public String title;
    public String favicon;
    public int id;
    public int status;

    public BotAddress(int id, String title, String url, int status) {
        Uri u = Uri.parse(url);
        this.url = u.toString();
        this.title = title;
        this.favicon = this.url + "/favicon.ico";
        this.id = id;
        this.status = status;
    }

}
