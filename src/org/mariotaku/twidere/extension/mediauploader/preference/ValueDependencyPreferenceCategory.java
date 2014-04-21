/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.extension.mediauploader.preference;

import java.util.Map;

import org.mariotaku.twidere.extension.mediauploader.R;
import org.mariotaku.twidere.extension.mediauploader.util.ParseUtils;
import org.mariotaku.twidere.internal.ArrayUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.TypedValue;

public class ValueDependencyPreferenceCategory extends PreferenceCategory implements OnSharedPreferenceChangeListener {

	private final String mDependencyKey, mDependencyValueDefault;
	private final String[] mDependencyValues;

	public ValueDependencyPreferenceCategory(final Context context) {
		this(context, null);
	}

	public ValueDependencyPreferenceCategory(final Context context, final AttributeSet attrs) {
		this(context, attrs, android.R.attr.preferenceCategoryStyle);
	}

	public ValueDependencyPreferenceCategory(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		final Resources res = context.getResources();
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ValueDependencyPreference, defStyle, 0);
		mDependencyKey = a.getString(R.styleable.ValueDependencyPreference_dependencyKey);
		final TypedValue outValue = new TypedValue();
		a.getValue(R.styleable.ValueDependencyPreference_dependencyValues, outValue);
		if (outValue.type == TypedValue.TYPE_REFERENCE) {
			final String dependencyValueType = res.getResourceTypeName(outValue.resourceId);
			if ("string".equals(dependencyValueType)) {
				mDependencyValues = new String[] { res.getString(outValue.resourceId) };
			} else {
				mDependencyValues = res.getStringArray(outValue.resourceId);
			}
		} else {
			mDependencyValues = new String[] { ParseUtils.parseString(outValue.coerceToString()) };
		}
		mDependencyValueDefault = a.getString(R.styleable.ValueDependencyPreference_dependencyValueDefault);
		a.recycle();
	}

	@Override
	public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
		if (key.equals(mDependencyKey)) {
			updateEnableState();
		}
	}

	@Override
	protected void notifyHierarchyChanged() {
		super.notifyHierarchyChanged();
		updateEnableState();
	}

	@Override
	protected void onAttachedToHierarchy(final PreferenceManager preferenceManager) {
		super.onAttachedToHierarchy(preferenceManager);
		final SharedPreferences prefs = getSharedPreferences();
		if (prefs != null) {
			prefs.registerOnSharedPreferenceChangeListener(this);
		}
		updateEnableState();
	}

	private void updateEnableState() {
		final SharedPreferences prefs = getSharedPreferences();
		if (prefs == null || mDependencyKey == null || mDependencyValues == null) return;
		final Map<String, ?> all = prefs.getAll();
		final String valueString = ParseUtils.parseString(all.get(mDependencyKey), mDependencyValueDefault);
		setEnabled(ArrayUtils.contains(mDependencyValues, valueString));
	}

}
