/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.phiro.uiespresso.ui.activity;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.PreferenceMatchers;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uiespresso.ui.activity.utils.SettingsActivityUtils;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_SHOW_PHIRO_BRICKS;
import static org.catrobat.catroid.ui.MainMenuActivity.PHIRO_INITIALIZED;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class PhiroSettingsActivityTest {

	@Rule
	public BaseActivityInstrumentationRule<SettingsActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SettingsActivity.class, true, false);

	private List<String> allSettings = new ArrayList<>(Arrays.asList(SETTINGS_SHOW_PHIRO_BRICKS, PHIRO_INITIALIZED));
	private Map<String, Boolean> initialSettings = new HashMap<>();

	@Before
	public void setUp() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		for (String setting : allSettings) {
			initialSettings.put(setting, sharedPreferences.getBoolean(setting, false));
		}
		SettingsActivityUtils.setAllSettingsTo(allSettings, true);

		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void phiroIsFirstVisibleSettingTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_phiro_bricks))
				.atPosition(0)
				.check(matches(isDisplayed()));
	}

	@Test
	public void phiroSettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_phiro_bricks))
				.perform(click());

		SettingsActivityUtils.checkPreference(R.string.preference_title_enable_phiro_bricks, SETTINGS_SHOW_PHIRO_BRICKS);

		String phiroLink = UiTestUtils.getResourcesString(R.string.phiro_preference_link);

		Intents.init();
		Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData(phiroLink));
		intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
		onData(PreferenceMatchers.withTitle(R.string.phiro_preference_title)).perform(click());
		intended(expectedIntent);
		Intents.release();
	}

	@After
	public void tearDown() {
		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext()).edit();
		for (String setting : initialSettings.keySet()) {
			sharedPreferencesEditor.putBoolean(setting, initialSettings.get(setting));
		}
		sharedPreferencesEditor.commit();
		initialSettings.clear();
	}
}
