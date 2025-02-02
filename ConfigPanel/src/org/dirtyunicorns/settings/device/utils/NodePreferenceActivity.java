/*
 * Copyright (C) 2015 The CyanogenMod Project
 * Copyright (C) 2019 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dirtyunicorns.settings.device.utils;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.view.MenuItem;
import java.io.File;

public class NodePreferenceActivity extends PreferenceActivity
    implements OnPreferenceChangeListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  protected void onResume() {
    super.onResume();
    // If running on a phone, remove padding around the listview
    getListView().setPadding(0, 0, 0, 0);
  }

  @Override
  public boolean onPreferenceChange(Preference preference, Object newValue) {
    String node = Constants.sBooleanNodePreferenceMap.get(preference.getKey());
    if (!TextUtils.isEmpty(node)) {
      Boolean value = (Boolean) newValue;
      if (node.equals(Constants.TOUCHSCREEN_DOUBLE_SWIPE_NODE)) {
        for (String music_nodes : Constants.TOUCHSCREEN_MUSIC_GESTURES_ARRAY) {
          FileUtils.writeLine(music_nodes, value ? "1" : "0");
        }
      } else FileUtils.writeLine(node, value ? "1" : "0");
      return true;
    }
    node = Constants.sStringNodePreferenceMap.get(preference.getKey());
    if (!TextUtils.isEmpty(node)) {
      FileUtils.writeLine(node, (String) newValue);
      return true;
    }
    return false;
  }

  @Override
  public void addPreferencesFromResource(int preferencesResId) {
    super.addPreferencesFromResource(preferencesResId);
    // Initialize node preferences
    for (String pref : Constants.sBooleanNodePreferenceMap.keySet()) {
      SwitchPreference b = (SwitchPreference) findPreference(pref);
      if (b == null) continue;
      b.setOnPreferenceChangeListener(this);
      String node = Constants.sBooleanNodePreferenceMap.get(pref);
      if (new File(node).exists()) {
        String curNodeValue = FileUtils.readOneLine(node);
        b.setChecked(curNodeValue.equals("1"));
      } else {
        b.setEnabled(false);
      }
    }
    for (String pref : Constants.sStringNodePreferenceMap.keySet()) {
      ListPreference l = (ListPreference) findPreference(pref);
      if (l == null) continue;
      l.setOnPreferenceChangeListener(this);
      String node = Constants.sStringNodePreferenceMap.get(pref);
      if (new File(node).exists()) {
        l.setValue(FileUtils.readOneLine(node));
      } else {
        l.setEnabled(false);
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}