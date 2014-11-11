package com.zale.data;

import java.util.Locale;

import com.zale.uti.PinyinUtil;
import com.zale.view.AlphaWrapper;


public class News implements AlphaWrapper {

	private String title;
	private String alpha;
	
	public News() {
		super();
	}

	public News(String title) {
		setTitle(title);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		alpha = PinyinUtil.getPinyin(title).toUpperCase(Locale.getDefault());
	}

	@Override
	public String getAlpha() {
		return alpha;
	}
	
}
