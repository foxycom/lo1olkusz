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

package com.pjanczyk.lo1olkusz.ui.main;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.model.LuckyNumber;
import com.pjanczyk.lo1olkusz.model.Replacements;
import com.pjanczyk.lo1olkusz.storage.SavedLuckyNumbers;
import com.pjanczyk.lo1olkusz.storage.SavedReplacements;
import com.pjanczyk.lo1olkusz.ui.main.NotificationsAdapter.Entry;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends FragmentBase {

    private NotificationsAdapter adapter;
    private RecyclerView recyclerView;
    private TextView textEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.notifications, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        textEmpty = (TextView) root.findViewById(R.id.text_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NotificationsAdapter(getContext(), Collections.<Entry>emptyList());
        adapter.listener = new NotificationsAdapter.ReplacementsClickListener() {
            @Override
            public void onReplacementsClick(Replacements replacements) {
                activity.showDaily(replacements.getDate(), replacements.getClassName());
            }
        };

        recyclerView.setAdapter(adapter);

        updateContent();

        return root;
    }

    @Override
    public void onSyncCompleted() {
        if (getView() != null) { //only if onCreateView has already been called
            updateContent();
        }
    }

    private void updateContent() {
        SavedReplacements replacements = new SavedReplacements(getContext());
        SavedLuckyNumbers numbers = new SavedLuckyNumbers(getContext());

        Map<LocalDate, Entry> entries = new HashMap<>();

        LocalDate since = LocalDate.now();

        for (LuckyNumber n : numbers.loadAll(since)) {
            if (!entries.containsKey(n.getDate())) {
                Entry entry = new Entry();
                entry.date = n.getDate();
                entry.notifications = new ArrayList<>();
                entries.put(entry.date, entry);
            }
            entries.get(n.getDate()).notifications.add(n);
        }

        for (Replacements r : replacements.loadAll(since)) {
            if (!entries.containsKey(r.getDate())) {
                Entry entry = new Entry();
                entry.date = r.getDate();
                entry.notifications = new ArrayList<>();
                entries.put(entry.date, entry);
            }
            entries.get(r.getDate()).notifications.add(r);
        }

        ArrayList<Entry> dataset = new ArrayList<>(entries.values());
        Collections.sort(dataset, new Comparator<Entry>() {
            @Override
            public int compare(Entry lhs, Entry rhs) {
                return lhs.date.compareTo(rhs.date);
            }
        });
        adapter.dataset = dataset;
        adapter.notifyDataSetChanged();

        boolean empty = dataset.isEmpty();
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }



}
