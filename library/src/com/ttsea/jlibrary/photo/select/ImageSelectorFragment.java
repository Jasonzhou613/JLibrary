package com.ttsea.jlibrary.photo.select;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.common.JToast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageSelectorFragment extends Fragment {
    private final String TAG = "ImageSelectorFragment";

    private final int LOADER_TYPE_ALL = 0;
    private final int LOADER_TYPE_CATEGORY = 1;
    private final int REQUEST_CODE_TAKE_CAMERA = 0x111;

    private Activity mActivity;

    private PopupWindow folderPopupWindow;
    private TextView tvDate;
    private TextView btnCategory;
    private GridView gvImages;
    private View popupAnchorView;

    private ArrayList<String> resultList;
    private List<Folder> folderList;
    private List<ImageItem> imageList;

    private OnImageSelectListener onImageSelectListener;
    private ImageAdapter imageAdapter;
    private FolderAdapter folderAdapter;
    private ImageConfig imageConfig;
    private File tempFile;

    private int gridWidth, gridHeight;

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
        try {
            onImageSelectListener = (OnImageSelectListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("The Activity must implement ImageSelectorFragment.Callback interface...");
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(LOADER_TYPE_ALL, null, mLoaderCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.imageselector_main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDate = (TextView) view.findViewById(R.id.tvDate);
        btnCategory = (TextView) view.findViewById(R.id.btnCategory);
        gvImages = (GridView) view.findViewById(R.id.gvImages);
        popupAnchorView = view.findViewById(R.id.rlyBottomView);

        tvDate.setVisibility(View.GONE);

        init();
    }

    private void init() {
        resultList = new ArrayList<>();
        folderList = new ArrayList<>();
        imageList = new ArrayList<>();

        imageConfig = ImageSelector.getImageConfig();
        folderAdapter = new FolderAdapter(mActivity, imageConfig);
        imageAdapter = new ImageAdapter(mActivity, imageList);
        imageAdapter.setShowCamera(imageConfig.isShowCamera());
        imageAdapter.setShowSelectIndicator(imageConfig.isMutiSelect());

        gvImages.setAdapter(imageAdapter);
        resultList = imageConfig.getPathList();

        btnCategory.setText(R.string.image_all_folder);
        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folderPopupWindow == null) {
                    createPopupFolderList(gridWidth, gridHeight);
                }

                if (folderPopupWindow.isShowing()) {
                    folderPopupWindow.dismiss();
                } else {
                    folderPopupWindow.showAsDropDown(popupAnchorView, 0, 0);
                    int index = folderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    ListView lvFolder = (ListView) folderPopupWindow.getContentView().findViewById(R.id.lvFolder);
                    if (lvFolder != null) {
                        lvFolder.setSelection(index);
                    }
                }
            }
        });

        gvImages.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    tvDate.setVisibility(View.GONE);
                } else if (scrollState == SCROLL_STATE_FLING
                        || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    tvDate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (tvDate.getVisibility() == View.VISIBLE) {
                    int index = firstVisibleItem + 1 == view.getAdapter().getCount() ? view.getAdapter().getCount() - 1 : firstVisibleItem + 1;
                    ImageItem image = (ImageItem) view.getAdapter().getItem(index);
                    if (image != null) {
                        tvDate.setText(ImageUtils.formatPhotoDate(image.getPath()));
                    }
                }
            }
        });

        gvImages.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {

                final int width = gvImages.getWidth();
                final int height = gvImages.getHeight();

                gridWidth = width;
                gridHeight = height;

                final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
                final int numCount = width / desireSize;
                final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
                int columnWidth = (width - columnSpace * (numCount - 1)) / numCount;
                JLog.d(TAG, "columnWidth:" + columnWidth + ", columnSpace:" + columnSpace);
                gvImages.setVerticalSpacing(columnSpace);
                imageAdapter.setItemSize(columnWidth);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    gvImages.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    gvImages.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        gvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (imageAdapter.isShowCamera()) {
                    if (i == 0) {
                        showCameraAction();
                    } else {
                        ImageItem image = (ImageItem) adapterView.getAdapter().getItem(i);
                        selectImageFromGrid(image, imageConfig.isMutiSelect());
                    }
                } else {
                    // 正常操作
                    ImageItem image = (ImageItem) adapterView.getAdapter().getItem(i);
                    selectImageFromGrid(image, imageConfig.isMutiSelect());
                }
            }
        });
    }

    /*** 创建弹出的ListView */
    private void createPopupFolderList(int width, int height) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View contentView = inflater.inflate(R.layout.imageselector_pop_item, null);
        ListView listView = (ListView) contentView.findViewById(R.id.lvFolder);
        View extraView = contentView.findViewById(R.id.extraView);

        folderPopupWindow = new PopupWindow(mActivity);
        folderPopupWindow.setContentView(contentView);
        folderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        folderPopupWindow.setWidth(width);
        folderPopupWindow.setHeight(height);
        folderPopupWindow.setFocusable(true);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (params != null && params instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) params).topMargin = height / 3;
            listView.setLayoutParams(params);
        }

        extraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folderPopupWindow.isShowing()) {
                    folderPopupWindow.dismiss();
                }
            }
        });

        listView.setAdapter(folderAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (folderAdapter.getSelectIndex() == i) {
                    return;
                }

                folderAdapter.setSelectIndex(i);

                final int index = i;
                final AdapterView v = adapterView;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        folderPopupWindow.dismiss();
                        if (index == 0) {
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_TYPE_ALL, null, mLoaderCallback);
                            btnCategory.setText(R.string.image_all_folder);
                            if (imageConfig.isShowCamera()) {
                                imageAdapter.setShowCamera(true);
                            } else {
                                imageAdapter.setShowCamera(false);
                            }
                        } else {
                            Folder folder = (Folder) v.getAdapter().getItem(index);
                            if (null != folder) {
                                imageList.clear();
                                imageList.addAll(folder.getImages());
                                imageAdapter.notifyDataSetChanged();
                                btnCategory.setText(folder.getName());
                                // 设定默认选择
                                if (resultList != null && resultList.size() > 0) {
                                    imageAdapter.setDefaultSelected(resultList);
                                }
                            }
                            imageAdapter.setShowCamera(false);
                        }
                        // 滑动到最初始位置
                        gvImages.smoothScrollToPosition(0);
                    }
                }, 100);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (folderPopupWindow != null) {
            if (folderPopupWindow.isShowing()) {
                folderPopupWindow.dismiss();
            }
        }

        btnCategory.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {

                final int height = btnCategory.getHeight();

                final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
                final int numCount = btnCategory.getWidth() / desireSize;
                final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
                int columnWidth = (btnCategory.getWidth() - columnSpace * (numCount - 1)) / numCount;
                imageAdapter.setItemSize(columnWidth);

                if (folderPopupWindow != null) {
                    folderPopupWindow.setHeight(height);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btnCategory.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    btnCategory.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        super.onConfigurationChanged(newConfig);
    }

    /** 选择相机 */
    private void showCameraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            tempFile = ImageUtils.createTmpFile(imageConfig.getOutPutPath(), imageConfig.getImageSuffix());
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_CAMERA);
        } else {
            JToast.makeTextCenter(mActivity, getStringById(R.string.image_msg_no_camera));
        }
    }

    private void selectImageFromGrid(ImageItem image, boolean isMulti) {
        if (image != null) {
            if (isMulti) {
                if (resultList.contains(image.getPath())) {
                    resultList.remove(image.getPath());
                    if (onImageSelectListener != null) {
                        onImageSelectListener.onImageUnselected(image.getPath());
                    }
                } else {
                    if (imageConfig.getMaxSize() == resultList.size()) {
                        JToast.makeTextCenter(mActivity, getStringById(R.string.image_msg_amount_limit));
                        return;
                    }

                    resultList.add(image.getPath());
                    if (onImageSelectListener != null) {
                        onImageSelectListener.onImageSelected(image.getPath());
                    }
                }
                imageAdapter.selectOrRemoveImage(image);
            } else {
                if (onImageSelectListener != null) {
                    onImageSelectListener.onSingleImageSelected(image.getPath());
                }
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && folderPopupWindow != null
                && folderPopupWindow.isShowing()) {
            folderPopupWindow.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TAKE_CAMERA) {// 拍照回来
            if (resultCode == Activity.RESULT_OK) {
                if (onImageSelectListener != null) {
                    onImageSelectListener.onCameraShot(tempFile);
                }
            } else {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_TYPE_ALL) {
                CursorLoader cursorLoader =
                        new CursorLoader(getActivity(),
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                                null, null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;

            } else if (id == LOADER_TYPE_CATEGORY) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            JLog.printCursor(data);
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    List<ImageItem> tempImageList = new ArrayList<>();
                    imageList.clear();
                    folderList.clear();
                    data.moveToFirst();
                    do
                    {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));

                        //加载所有图片
                        ImageItem image = new ImageItem(path, name, dateTime);
                        tempImageList.add(image);

                        //加载所有文件夹
                        File imageFile = new File(path);
                        File folderFile = imageFile.getParentFile();
                        Folder folder = new Folder();
                        folder.setName(folderFile.getName());
                        folder.setPath(folderFile.getAbsolutePath());
                        folder.setCover(image);

                        if (!folderList.contains(folder)) {
                            List<ImageItem> imageList = new ArrayList<>();
                            imageList.add(image);
                            folder.setImages(imageList);
                            folderList.add(folder);
                        } else {
                            Folder f = folderList.get(folderList.indexOf(folder));
                            f.getImages().add(image);
                        }
                    }
                    while (data.moveToNext());

                    imageList.addAll(tempImageList);
                    imageAdapter.notifyDataSetChanged();

                    if (resultList != null && resultList.size() > 0) {
                        imageAdapter.setDefaultSelected(resultList);
                    }

                    folderAdapter.setData(folderList);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private String getStringById(int resId) {
        return mActivity.getResources().getString(resId);
    }

    public interface OnImageSelectListener {

        void onSingleImageSelected(String path);

        void onImageSelected(String path);

        void onImageUnselected(String path);

        void onCameraShot(File imageFile);
    }
}