package com.ttsea.jlibrary.photo.select;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
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
import com.ttsea.jlibrary.interfaces.OnItemViewClickListener;
import com.ttsea.jlibrary.utils.CacheDirUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageSelectorFragment extends android.support.v4.app.Fragment implements View.OnClickListener,
        OnItemViewClickListener, AdapterView.OnItemClickListener {
    private final String TAG = "Select.ImageSelectorFragment";

    private final int LOADER_TYPE_ALL = 0;
    private final int LOADER_TYPE_CATEGORY = 1;
    private final int REQUEST_CODE_TAKE_CAMERA = 0x111;
    private final int REQUEST_CODE_PREVIEW = 0x112;
    private final int REQUEST_CODE_CHECK_STORAGE_PERMISSION = 0x10;

    private Activity mActivity;

    private PopupWindow folderPopupWindow;
    private TextView tvDate;
    private TextView btnCategory;
    private GridView gvImages;
    private TextView tvNoPicture;
    private View popupAnchorView;
    private TextView tvPreview;
    private View llyPreview;

    private ArrayList<ImageItem> selectedList;
    private List<ImageItem> imageList;
    private List<Folder> folderList;

    private OnImageSelectListener onImageSelectListener;
    private ImageAdapter imageAdapter;
    private FolderAdapter folderAdapter;
    private SelectConfig selectConfig;
    private File tempFile;

    private int currentLoaderType = LOADER_TYPE_ALL;
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

        if (mActivity != null && mActivity.getIntent() != null
                && mActivity.getIntent().getExtras() != null) {
            Bundle bundle = mActivity.getIntent().getExtras();
            selectConfig = (SelectConfig) bundle.getSerializable("selectConfig");
        }

        if (selectConfig == null) {
            throw new NullPointerException("selectConfig could not be null");
        }

        if (ImageUtils.isEmpty(selectConfig.getOutPutPath())) {
            selectConfig.getBuilder().setOutPutPath(CacheDirUtils.getSdRootDir(mActivity));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.jimageselector_main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gvImages = (GridView) view.findViewById(R.id.gvImages);
        tvNoPicture = (TextView) view.findViewById(R.id.tvNoPicture);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        popupAnchorView = view.findViewById(R.id.rlyBottomView);
        btnCategory = (TextView) view.findViewById(R.id.btnCategory);
        tvPreview = (TextView) view.findViewById(R.id.tvPreview);
        llyPreview = view.findViewById(R.id.llyPreview);

        tvDate.setVisibility(View.GONE);
        btnCategory.setText(R.string.image_all_folder);
        btnCategory.setOnClickListener(this);
        tvPreview.setOnClickListener(this);

        init();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            String tempPath = savedInstanceState.getString("tempFile");
            if (!ImageUtils.isEmpty(tempPath)) {
                tempFile = new File(tempPath);
            }
        }
        loadImages(LOADER_TYPE_ALL);
    }

    private void init() {
        selectedList = new ArrayList<>();
        folderList = new ArrayList<>();
        imageList = new ArrayList<>();

        folderAdapter = new FolderAdapter(mActivity);
        imageAdapter = new ImageAdapter(mActivity, imageList);
        imageAdapter.setShowCamera(selectConfig.isShowCamera());
        imageAdapter.setShowSelectIndicator(selectConfig.isMultiSelect());
        imageAdapter.setOnItemViewClickListener(this);

        gvImages.setAdapter(imageAdapter);
        gvImages.setOnItemClickListener(this);

        selectedList = selectConfig.getPathList();
        if (selectedList == null) {
            selectedList = new ArrayList<ImageItem>();
        }
        if (onImageSelectListener != null) {
            onImageSelectListener.onRefreshSelectedList(selectedList);
        }
        refreshPreviewTVStatus();

        gvImages.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    tvDate.setVisibility(View.GONE);
                    //JImageLoader.getInstance().resume(mActivity);
                } else if (scrollState == SCROLL_STATE_FLING
                        || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    tvDate.setVisibility(View.VISIBLE);
                    //JImageLoader.getInstance().pause(mActivity);
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
                gvImages.setNumColumns(numCount);
                imageAdapter.setItemSize(columnWidth);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    gvImages.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    gvImages.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    /**
     * 载入图片
     *
     * @param loaderType see {@link #LOADER_TYPE_ALL} and {@link #LOADER_TYPE_CATEGORY}
     */
    private void loadImages(int loaderType) {
        currentLoaderType = loaderType;
        //当android为6.0以上版本并且targetSdkVersion>=23时，需要用户再次确认权限，否则即使添加过权限也会报错
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_CHECK_STORAGE_PERMISSION);
        } else {
            getActivity().getSupportLoaderManager().initLoader(loaderType, null, mLoaderCallback);
        }
    }

    private void refreshPreviewTVStatus() {
        String txt = (getStringById(R.string.image_preview)) + "(" + selectedList.size() + ")";
        tvPreview.setEnabled(true);

        if (selectedList.size() == 0) {
            txt = getStringById(R.string.image_preview);
            tvPreview.setEnabled(false);
        }
        tvPreview.setText(txt);

        if (selectConfig.isMultiSelect()) {
            llyPreview.setVisibility(View.VISIBLE);
        } else {
            llyPreview.setVisibility(View.INVISIBLE);
        }
    }

    private void showFolderPopupWindow() {
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

    /*** 创建弹出的ListView */
    private void createPopupFolderList(int width, int height) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View contentView = inflater.inflate(R.layout.jimageselector_pop_item, null);
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
                            loadImages(LOADER_TYPE_ALL);
                            btnCategory.setText(R.string.image_all_folder);
                            if (selectConfig.isShowCamera()) {
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
                                //设置默认选择项
                                imageAdapter.setDefaultSelected(selectedList);
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

    /** 选择相机 */
    private void showCameraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            tempFile = ImageUtils.createTmpFile(selectConfig.getOutPutPath(), selectConfig.getImageSuffix());
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_CAMERA);
        } else {
            JToast.makeTextCenter(mActivity, getStringById(R.string.image_msg_no_camera));
        }
    }

    /** 勾选或者取消勾选图片 */
    private void selectImageFromGrid(int position) {
        ImageItem image = imageAdapter.getItem(position);
        if (image == null) {
            JLog.d(TAG, "image is null, return");
            return;
        }
        if (selectConfig.isMultiSelect()) {
            if (selectedList.contains(image)) {
                selectedList.remove(image);
                image.setSelected(false);
                if (onImageSelectListener != null) {
                    onImageSelectListener.onImageUnselected(selectedList, image);
                }
            } else {
                if (selectedList.size() >= selectConfig.getMaxSize()) {
                    JToast.makeTextCenter(mActivity, getStringById(R.string.image_msg_amount_limit));
                    return;
                }
                selectedList.add(image);
                image.setSelected(true);
                if (onImageSelectListener != null) {
                    onImageSelectListener.onImageSelected(selectedList, image);
                }
            }
            imageAdapter.selectOrUnselect(image);
        } else {
            image.setSelected(true);
            if (onImageSelectListener != null) {
                onImageSelectListener.onSingleImageSelected(image);
            }
        }
        refreshPreviewTVStatus();
    }

    /** 进行预览 */
    private void previewList(List<ImageItem> list, int position) {
        //使用静态常量传输数据，避免list太大，导致bundle无法传输
        ImagePreviewActivity.setImageList(list);

        Intent intent = new Intent(mActivity, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        //bundle.putSerializable(ImageSelector.KEY_SELECTED_LIST, (Serializable) list);
        bundle.putInt(ImageSelector.KEY_SELECTED_POSITION, position);
        bundle.putInt(ImageSelector.KEY_MAX_SIZE, selectConfig.getMaxSize());
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_PREVIEW);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && folderPopupWindow != null
                && folderPopupWindow.isShowing()) {
            folderPopupWindow.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        if (tempFile != null) {
            outState.putString("tempFile", tempFile.getAbsolutePath());
        }
        super.onSaveInstanceState(outState);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (selectConfig.isMultiSelect()) {//多选
            if (imageAdapter.isShowCamera()) {//如果显示了拍照按钮
                if (position == 0) {
                    if (selectedList.size() >= selectConfig.getMaxSize()) {
                        JToast.makeTextCenter(mActivity, getStringById(R.string.image_msg_amount_limit));
                        return;
                    }
                    showCameraAction();
                    return;
                } else {
                    position--;
                }
            }
            previewList(imageList, position);

        } else {//单选
            if (imageAdapter.isShowCamera()) {//如果显示了拍照按钮
                if (position == 0) {
                    showCameraAction();
                    return;
                } else {
                    position--;
                }
            }
            ImageItem item = imageList.get(position);
            item.setSelected(true);
            if (onImageSelectListener != null) {
                onImageSelectListener.onSingleImageSelected(item);
            }
        }
    }

    @Override
    public void onItemViewClick(View v, int position) {
        if (v.getId() == R.id.flyCheck) {//选择和取消选择图片
            selectImageFromGrid(position);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCategory) {//点击所有图片，弹出相册选择pop
            showFolderPopupWindow();
        } else if (v.getId() == R.id.tvPreview) {//预览
            previewList(selectedList, 0);
        }
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
            return;
        }

        // 预览照片回来
        if (requestCode == REQUEST_CODE_PREVIEW && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                List<ImageItem> list = (List<ImageItem>) bundle.getSerializable(ImageSelector.KEY_SELECTED_LIST);
                selectedList.clear();
                if (list != null) {
                    selectedList.addAll(list);
                }
            }

            if (resultCode == Activity.RESULT_OK) {
                mActivity.setResult(Activity.RESULT_OK, data);
                mActivity.finish();
            } else {
                imageAdapter.setDefaultSelected(selectedList);
            }
            if (onImageSelectListener != null) {
                onImageSelectListener.onRefreshSelectedList(selectedList);
            }
            refreshPreviewTVStatus();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CHECK_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImages(currentLoaderType);
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                    ArrayList<ImageItem> tempImageList = new ArrayList<>();
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
                    //设置默认选择项
                    imageAdapter.setDefaultSelected(selectedList);

                    if ((folderList == null || folderList.size() < 1)
                            && !selectConfig.isShowCamera()) {
                        gvImages.setVisibility(View.GONE);
                        tvNoPicture.setVisibility(View.VISIBLE);
                    } else {
                        gvImages.setVisibility(View.VISIBLE);
                        tvNoPicture.setVisibility(View.GONE);
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

        /**
         * 单选时，选择图片后触发
         *
         * @param image 被选择的图片
         */
        void onSingleImageSelected(ImageItem image);

        /**
         * 多选时，选择图片后触发
         *
         * @param selectedList 选择图片后的image list
         * @param image        被选择的图片
         */
        void onImageSelected(ArrayList<ImageItem> selectedList, ImageItem image);

        /**
         * 取消选择图片后触发
         *
         * @param selectedList 取消选择图片后的image list
         * @param image        被取消选择的图片
         */
        void onImageUnselected(ArrayList<ImageItem> selectedList, ImageItem image);

        /**
         * 由于一些原因需要刷新被选择的图片列表
         *
         * @param selectedList 被选择的图片列表
         */
        void onRefreshSelectedList(ArrayList<ImageItem> selectedList);

        /**
         * 拍照后触发
         *
         * @param imageFile 拍照生成的图片文件
         */
        void onCameraShot(File imageFile);
    }
}