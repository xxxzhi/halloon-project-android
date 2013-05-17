package com.halloon.android.util;

import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationUtil {

	public static TranslateAnimation fragmentSlideRightIn(int duration) {
		TranslateAnimation translateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
		translateAnimation.setInterpolator(new LinearInterpolator());
		translateAnimation.setDuration(duration);
		translateAnimation.setFillAfter(true);

		return translateAnimation;
	}

	public static TranslateAnimation fragmentSlideRightOut(int duration) {
		TranslateAnimation translateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
		translateAnimation.setInterpolator(new LinearInterpolator());
		translateAnimation.setDuration(duration);
		translateAnimation.setFillAfter(true);

		return translateAnimation;
	}

	public static TranslateAnimation fragmentSlideLeftIn(int duration) {
		TranslateAnimation translateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF,
				0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				0f);
		translateAnimation.setInterpolator(new LinearInterpolator());
		translateAnimation.setDuration(duration);
		translateAnimation.setFillAfter(true);

		return translateAnimation;
	}

	public static TranslateAnimation fragmentSlideLeftOut(int duration) {
		TranslateAnimation translateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-1f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f);
		translateAnimation.setInterpolator(new LinearInterpolator());
		translateAnimation.setDuration(duration);
		translateAnimation.setFillAfter(true);

		return translateAnimation;
	}

	public static TranslateAnimation fragmentSlideTopIn(int duration) {
		TranslateAnimation translateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
		translateAnimation.setInterpolator(new DecelerateInterpolator(1.3f));
		translateAnimation.setDuration(duration);
		translateAnimation.setFillAfter(true);

		return translateAnimation;
	}

	public static RotateAnimation circleLoading(int duration) {
		RotateAnimation rotateAnimation = new RotateAnimation(0, +360,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		rotateAnimation.setDuration(duration);
		rotateAnimation.setRepeatMode(RotateAnimation.RESTART);
		rotateAnimation.setRepeatCount(-1);

		return rotateAnimation;
	}

}
