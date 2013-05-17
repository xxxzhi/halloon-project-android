/*
 * modified by 7heaven 19:41  11-25-2012
 * 
 * Licensed under the Apache license Version 2
 * 
 * see detail at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 
 * 
 * 
 * 
 * Copyright (C) 2010 The Android Open Source Project
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
 * 
 */

package com.halloon.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class MultiPinnedHeaderListView extends PinnedHeaderListView {

	// 分组头的view容器
	private View[] headerViews;
	// 分组容器 数组的每个数值为每个分组包括分组头的数量 按次序排列
	private int[] group;
	private View headerView;
	private boolean headerViewVisible;
	private MultiPinnedHeaderAdapter pinnedAdapter;
	private int headerWidth;
	private int headerHeight;

	// private int lastMotionY;

	/**
	 * Adapter interface. The list adapter must implement this interface.
	 */
	public interface MultiPinnedHeaderAdapter {

		/**
		 * Pinned header state: don't show the header.
		 */
		public static final int PINNED_HEADER_GONE = 0;

		/**
		 * Pinned header state: show the header at the top of the list.
		 */
		public static final int PINNED_HEADER_VISIBLE = 1;

		/**
		 * Pinned header state: show the header. If the header extends beyond
		 * the bottom of the first shown element, push it up and clip.
		 */
		public static final int PINNED_HEADER_PUSHED_UP = 2;

		/**
		 * Computes the desired state of the pinned header for the given
		 * position of the first visible list item. Allowed return values are
		 * {@link #PINNED_HEADER_GONE}, {@link #PINNED_HEADER_VISIBLE} or
		 * {@link #PINNED_HEADER_PUSHED_UP}.
		 */
		int getPinnedHeaderState(int position);

		/**
		 * Configures the pinned header view to match the first visible list
		 * item.
		 * 
		 * @param header
		 *            pinned header view.
		 * @param position
		 *            position of the first visible list item.
		 */
		void configurePinnedHeader(View header, int position, int section);
	}

	/**
	 * 
	 * @param context
	 * @param headerViews
	 *            分组头容器
	 * @param group
	 *            各个分组的数量容器
	 */
	public MultiPinnedHeaderListView(Context context, View[] headerViews,
			int[] group) {
		super(context);
		this.group = group;
		this.headerViews = headerViews;
		this.headerView = headerViews[0];
	}

	public MultiPinnedHeaderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MultiPinnedHeaderListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setMultiPinnedHeaderListView(View[] headerViews, int[] group) {
		this.headerViews = headerViews;
		this.headerView = headerViews[0];
		this.group = group;
	}

	public void setPinnedHeaderFromPosition(int position) {
		headerView = headerViews[position];

		if (headerView != null) {
			setFadingEdgeLength(0);
		}
		requestLayout();
	}

	/**
	 * 根据position得到所在分组编号
	 * 
	 * @param group
	 * @param position
	 * @return
	 */
	public static int getSectionFromPosition(int[] group, int position) {
		int section = 0;
		int count = position;
		for (int i = 0; i < group.length; i++) {
			if (i > 0) {
				count = count - group[i - 1];
			}
			if (group[i] - count > 0) {
				section = i;
				break;
			}
		}

		return section;
	}

	/**
	 * 根据分组编号获取分组头的position
	 * 
	 * @param group
	 * @param section
	 *            分组编号
	 * @return
	 */
	public static int getPositionFromSection(int[] group, int section) {
		int count = 0;
		for (int i = 0; i < group.length; i++) {
			if (i == section)
				break;
			count = count + group[i];
		}

		return count;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (headerView != null) {
			headerView.layout(0, 0, headerWidth, headerHeight);
			configureHeaderView(getFirstVisiblePosition());
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (headerView != null) {
			measureChild(headerView, widthMeasureSpec, heightMeasureSpec);
			headerWidth = headerView.getMeasuredWidth();
			headerHeight = headerView.getMeasuredHeight();
		}
	}

	@Override
	public void configureHeaderView(int position) {
		if (headerView == null)
			return;

		switch (pinnedAdapter.getPinnedHeaderState(position)) {
		case MultiPinnedHeaderAdapter.PINNED_HEADER_GONE:
			headerViewVisible = false;
			break;
		case MultiPinnedHeaderAdapter.PINNED_HEADER_VISIBLE:
			pinnedAdapter.configurePinnedHeader(headerView, position,
					getSectionFromPosition(group, position));
			if (headerView.getTop() != 0) {
				headerView.layout(0, 0, headerWidth, headerHeight);
			}
			headerViewVisible = true;
			break;
		case MultiPinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP:
			View firstView = getChildAt(0);
			int bottom = firstView.getBottom();
			// int itemHeight = firstView.getHeight();
			int headerHeight = headerView.getHeight();
			int y;
			if (bottom < headerHeight) {
				y = bottom - headerHeight;
			} else {
				y = 0;
			}
			pinnedAdapter.configurePinnedHeader(headerView, position,
					getSectionFromPosition(group, position));
			if (headerView.getTop() != y) {
				headerView.layout(0, y, headerWidth, headerHeight + y);
			}
			headerViewVisible = true;
			break;
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (headerViewVisible) {
			drawChild(canvas, headerView, getDrawingTime());
		}
	}

	/*
	 * @Override public boolean onTouchEvent(MotionEvent mev){ final int y =
	 * (int) mev.getY(); switch(mev.getAction()){ case MotionEvent.ACTION_UP:
	 * lastMotionY = y; break; case MotionEvent.ACTION_DOWN: lastMotionY = y;
	 * break; case MotionEvent.ACTION_MOVE: applyPadding(mev); break; }
	 * 
	 * return super.onTouchEvent(mev); }
	 * 
	 * private void applyPadding(MotionEvent e){ int pointCount =
	 * e.getHistorySize();
	 * 
	 * for(int i = 0; i < pointCount; i++){ int historicalY = (int)
	 * e.getHistoricalY(i);
	 * 
	 * int topPadding = (int) (((historicalY - lastMotionY) - headerHeight) /
	 * 1.7);
	 * 
	 * headerViews[0].setPadding(headerViews[0].getPaddingLeft(), topPadding,
	 * headerViews[0].getPaddingRight(), headerViews[0].getPaddingBottom()); } }
	 */
}
