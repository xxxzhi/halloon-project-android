package com.halloon.android.ui.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.halloon.android.R;
import com.halloon.android.ui.activity.BaseMultiFragmentActivity;
import com.halloon.android.util.NumberUtil;

public class FileBrowserFragment extends Fragment {
	private static final String ROOT_PATH = "/";

	private Context context;

	private ListView parentView;
	private ListView fileView;

	private ParentAdapter parentAdapter;
	private FileAdapter fileAdapter;

	private ArrayList<HashMap<String, String>> fileLists;
	private ArrayList<HashMap<String, String>> parentNames;

	@Override
	public View onCreateView(LayoutInflater layoutInflater,
			ViewGroup container, Bundle savedInstanceState) {
		View root = layoutInflater.inflate(R.layout.file_browser, null, false);

		context = getActivity();

		parentView = (ListView) root.findViewById(R.id.parent_view);
		fileView = (ListView) root.findViewById(R.id.file_view);

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			fileLists = listFile(Environment.getExternalStorageDirectory()
					.toString());
		} else {
			((BaseMultiFragmentActivity) context).backStackAction();
			Toast.makeText(context, context.getString(R.string.can_not_find_sd_card), Toast.LENGTH_LONG).show();
		}
		parentNames = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> tmpHash = new HashMap<String, String>();
		tmpHash.put("name", "SD_CARD");
		tmpHash.put("dir", Environment.getExternalStorageDirectory().toString());
		parentNames.add(tmpHash);

		parentAdapter = new ParentAdapter(parentNames);
		fileAdapter = new FileAdapter(fileLists);

		parentView.setAdapter(parentAdapter);
		fileView.setAdapter(fileAdapter);

		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		parentView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < parentNames.size()) {
					String path = parentNames.get(position).get("dir");
					ArrayList<HashMap<String, String>> tmp_list = listFile(path);

					fileLists.clear();
					fileLists.addAll(tmp_list);
					fileAdapter.notifyDataSetChanged();

					int size = parentNames.size() - position;
					for (int i = 1; i < size; i++) {
						parentNames.remove(parentNames.size() - 1);
					}

					parentAdapter.notifyDataSetChanged();
				}
			}
		});

		fileView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String path = fileLists.get(position).get("path");
				File file = new File(path);
				if (file.exists() && file.canRead()) {
					if (fileLists.get(position).get("type").equals("dir")) {
						ArrayList<HashMap<String, String>> tmp_list = listFile(path);

						fileLists.clear();
						fileLists.addAll(tmp_list);
						fileAdapter.notifyDataSetChanged();
						HashMap<String, String> tmp_hash = new HashMap<String, String>();
						tmp_hash.put("name", path.substring(path.lastIndexOf("/") + 1));
						tmp_hash.put("dir", path);
						parentNames.add(tmp_hash);
						parentAdapter.notifyDataSetChanged();
					} else {
						Toast.makeText(context, "this is a file", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}

	private ArrayList<HashMap<String, String>> listFile(String path) {
		File file = new File(path);
		File[] files = file.listFiles();
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> item;
		for (File currentFile : files) {
			item = new HashMap<String, String>();
			if (currentFile.isDirectory()) {
				item.put("type", "dir");
			} else {
				item.put("type", "file");
			}
			item.put("name", currentFile.getName());
			item.put("path", currentFile.getPath());
			item.put("size", (currentFile.length() < 1) ? "" : NumberUtil.formatBytesSize(context, currentFile.length()));

			items.add(item);
		}

		return items;
	}

	private class ParentAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, String>> parentNames;
		private LayoutInflater inflater;

		public ParentAdapter(ArrayList<HashMap<String, String>> parentNames) {
			this.parentNames = parentNames;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return parentNames.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			return parentNames.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ContentHolder contentHolder;
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.halloon_simple_icon_item_1, null);
				contentHolder = new ContentHolder();
				contentHolder.content = (TextView) convertView
						.findViewById(R.id.content);

				convertView.setTag(contentHolder);
			} else {
				contentHolder = (ContentHolder) convertView.getTag();
			}

			contentHolder.content.setText(getItem(position).get("name"));

			return convertView;
		}

		private class ContentHolder {
			TextView content;
		}

	}

	private class FileAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, String>> items;
		private LayoutInflater inflater;

		public FileAdapter(ArrayList<HashMap<String, String>> items) {
			this.items = items;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ContentHolder contentHolder;

			HashMap<String, String> item = getItem(position);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.halloon_simple_item_1,
						null);

				contentHolder = new ContentHolder();
				contentHolder.icon = (ImageView) convertView
						.findViewById(R.id.icon);
				contentHolder.content = (TextView) convertView
						.findViewById(R.id.content);
				contentHolder.extra = (TextView) convertView
						.findViewById(R.id.extra);

				convertView.setTag(contentHolder);
			} else {
				contentHolder = (ContentHolder) convertView.getTag();
			}

			contentHolder.icon.setImageResource(R.drawable.ic_launcher);
			if (item.get("type") == "file") {
				contentHolder.icon
						.setImageResource(R.drawable.file_icon_default);
				contentHolder.extra.setVisibility(View.VISIBLE);
				String name = item.get("name");
				int dot = name.lastIndexOf(".");
				if (dot > 0) {
					contentHolder.content.setText(name.substring(0, dot));
					contentHolder.extra.setText(name.substring(dot + 1) + " " + item.get("size"));
				} else {
					contentHolder.content.setText(name);
					contentHolder.extra.setText(item.get("size"));
				}
			} else {
				contentHolder.icon.setImageResource(R.drawable.folder);
				contentHolder.content.setText(item.get("name"));
				contentHolder.extra.setVisibility(View.GONE);
			}

			return convertView;
		}

		private class ContentHolder {
			ImageView icon;
			TextView content;
			TextView extra;
		}

	}
}
