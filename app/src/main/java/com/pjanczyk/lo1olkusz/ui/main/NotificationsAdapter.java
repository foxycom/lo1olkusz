/*
 * Copyright 2016 Piotr Janczyk
 *
 * This file is part of lo1olkusz unofficial app.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pjanczyk.lo1olkusz.ui.main;// Author: Piotr Janczyk, 04.01.2016

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.model.LuckyNumber;
import com.pjanczyk.lo1olkusz.model.Replacements;
import com.pjanczyk.lo1olkusz.utils.TimeFormatter;

import org.joda.time.LocalDate;

import java.util.List;

class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private final Context context;
    List<Entry> dataset;
    ReplacementsClickListener listener;

    public static class Entry {
        LocalDate date;
        List<Object> notifications;
    }

    public interface ReplacementsClickListener {
        void onReplacementsClick(Replacements replacements);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateText;
        public TextView dayOfWeekText;
        public ViewGroup container;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public NotificationsAdapter(Context context, List<Entry> dataset) {
        this.context = context;
        this.dataset = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.notifications_card, parent, false);

        ViewHolder vh = new ViewHolder(v);
        vh.dateText = (TextView) v.findViewById(R.id.text_date);
        vh.dayOfWeekText = (TextView) v.findViewById(R.id.text_day_of_week);
        vh.container = (ViewGroup) v.findViewById(R.id.container);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Entry entry = dataset.get(position);

        holder.dayOfWeekText.setText(TimeFormatter.dayOfWeek(entry.date));
        holder.dateText.setText(TimeFormatter.dayAndMonth(entry.date));
        holder.container.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(context);

        boolean first = true;

        final View.OnClickListener replacementsViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onReplacementsClick((Replacements) v.getTag());
                }
            }
        };

        for (Object notification : entry.notifications) {

            if (!first) {
                inflater.inflate(R.layout.notification_list_separator, holder.container, true);
            }
            first = false;

            if (notification instanceof Replacements) {
                Replacements r = (Replacements) notification;

                View view = inflater.inflate(
                        R.layout.notifications_card_replacements, holder.container, false);

                TextView replText =
                        (TextView) view.findViewById(R.id.text_replacements);
                TextView classText = (TextView) view.findViewById(R.id.text_class);

                classText.setText(r.getClassName());
                replText.setText(context.getString(R.string.notification_list_replacements, r.size()));

                view.setTag(r);
                view.setOnClickListener(replacementsViewListener);

                holder.container.addView(view);

            } else if (notification instanceof LuckyNumber) {
                LuckyNumber n = (LuckyNumber) notification;

                View view = inflater.inflate(
                        R.layout.notifications_card_lucky_number, holder.container, false);

                TextView valueText = (TextView) view.findViewById(R.id.text_lucky_number_value);

                valueText.setText(Integer.toString(n.getValue()));
                holder.container.addView(view);

            }
        }

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
