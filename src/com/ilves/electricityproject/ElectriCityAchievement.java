package com.ilves.electricityproject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.common.images.ImageManager.OnImageLoadedListener;
import com.google.android.gms.games.achievement.Achievement;

public class ElectriCityAchievement implements OnImageLoadedListener {

	private Achievement	mAch;
	private Drawable	mDrawable;

	public ElectriCityAchievement(Context context, Achievement ach) {
		mAch = ach;

		ImageManager iManager = ImageManager.create(context);
		iManager.loadImage(this, ach.getRevealedImageUri());
	}

	@Override
	public void onImageLoaded(Uri uri, Drawable drawable, boolean isRequestedDrawable) {
		// TODO Auto-generated method stub
		mDrawable = drawable;
	}

	public Achievement getAch() {
		return mAch;
	}

	public Drawable getDrawable() {
		return mDrawable;
	}
}
