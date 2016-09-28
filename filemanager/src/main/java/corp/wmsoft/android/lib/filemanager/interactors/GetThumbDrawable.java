package corp.wmsoft.android.lib.filemanager.interactors;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.util.MimeTypeHelper;
import corp.wmsoft.android.lib.mvpcrx.interactor.MVPCUseCase;
import corp.wmsoft.android.lib.mvpcrx.util.IMVPCSchedulerProvider;
import rx.Observable;


/**
 * <p>Created by WestMan2000 on 8/29/16 at 2:23 PM at 11:18 AM.<p>
 */
public class GetThumbDrawable extends MVPCUseCase<GetThumbDrawable.RequestValues, GetThumbDrawable.ResponseValues> {

    /**/
    private static final String TAG = "WM::GetThumbDrawable";

    /**/
    private final Context mContext;


    public GetThumbDrawable(IMVPCSchedulerProvider schedulerProvider, Context context) {
        super(schedulerProvider);
        mContext = context.getApplicationContext();
    }

    @Override
    public Observable<GetThumbDrawable.ResponseValues> buildUseCaseObservable(@NonNull final GetThumbDrawable.RequestValues requestValues) {
        return Observable.fromCallable(new Callable<GetThumbDrawable.ResponseValues>() {
            @Override
            public GetThumbDrawable.ResponseValues call() throws Exception {
                Drawable drawable = loadDrawable(requestValues.getFileSystemObject());
                return new ResponseValues(requestValues.getWeakImageView(), requestValues.getFileSystemObject().getFullPath(), drawable);
            }
        });
    }

    private Drawable loadDrawable(FileSystemObject fso) {

        final String filePath = fso.getFullPath();

        if (MimeTypeHelper.KnownMimeTypeResolver.isAndroidApp(mContext, fso)) {
            return getAppDrawable(fso);
        } else if (MimeTypeHelper.KnownMimeTypeResolver.isImage(mContext, fso)) {
            return getImageDrawable(filePath);
        } else if (MimeTypeHelper.KnownMimeTypeResolver.isVideo(mContext, fso)) {
            return getVideoDrawable(filePath);
        }

        return null;
    }

    /**
     * Method that returns the main icon of the app
     *
     * @param fso The FileSystemObject
     * @return Drawable The drawable or null if cannot be extracted
     */
    private Drawable getAppDrawable(FileSystemObject fso) {
        final String filepath = fso.getFullPath();
        PackageManager pm = mContext.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(filepath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            // Read http://code.google.com/p/android/issues/detail?id=9151, CM fixed this
            // issue. We retain it for compatibility with older versions and roms without
            // this fix. Required to access apk which are not installed.
            final ApplicationInfo appInfo = packageInfo.applicationInfo;
            appInfo.sourceDir = filepath;
            appInfo.publicSourceDir = filepath;
            return pm.getDrawable(appInfo.packageName, appInfo.icon, appInfo);
        }
        return null;
    }

    /**
     * Method that returns a thumbnail of the picture
     *
     * @param file The path to the file
     * @return Drawable The drawable or null if cannot be extracted
     */
    private Drawable getImageDrawable(String file) {

        Bitmap thumb = createImageThumbnail(file, MediaStore.Video.Thumbnails.MICRO_KIND);
        if (thumb == null) {
            return null;
        }

        return new BitmapDrawable(mContext.getResources(), thumb);
    }

    /**
     * Method that returns a thumbnail of the video
     *
     * @param file The path to the file
     * @return Drawable The drawable or null if cannot be extracted
     */
    private Drawable getVideoDrawable(String file) {

        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file, MediaStore.Video.Thumbnails.MICRO_KIND);
        if (thumb == null) {
            return null;
        }

        return new BitmapDrawable(mContext.getResources(), thumb);
    }

    /**
     * This method first examines if the thumbnail embedded in EXIF is bigger than our target
     * size. If not, then it'll create a thumbnail from original image. Due to efficiency
     * consideration, we want to let MediaThumbRequest avoid calling this method twice for
     * both kinds, so it only requests for MICRO_KIND and set saveImage to true.
     *
     * This method always returns a "square thumbnail" for MICRO_KIND thumbnail.
     *
     * @param filePath the path of image file
     * @param kind could be MINI_KIND or MICRO_KIND
     * @return Bitmap, or null on failures
     */
    private static Bitmap createImageThumbnail(String filePath, int kind) {
        Bitmap bitmap;
        try {
            Class<?> thumbnailUtilsClass = Class.forName("android.media.ThumbnailUtils");
            Class[] argTypes = new Class[] { String.class, int.class };
            Method methodCreateImageThumbnail = thumbnailUtilsClass.getDeclaredMethod("createImageThumbnail", argTypes);
            bitmap = (Bitmap) methodCreateImageThumbnail.invoke(null, filePath, kind);
        } catch (Exception e) {
            Log.e(TAG, "Exception="+e);
            e.printStackTrace();
            return null;
        }

        return bitmap;
    }


    public static class RequestValues extends MVPCUseCase.RequestValues {

        /**/
        private final FileSystemObject mFileSystemObject;
        /**/
        private final WeakReference<ImageView> mWeakImageView;


        public RequestValues(ImageView imageView, FileSystemObject mFileSystemObject) {
            this.mWeakImageView = new WeakReference<>(imageView);
            this.mFileSystemObject = mFileSystemObject;
        }

        FileSystemObject getFileSystemObject() {
            return mFileSystemObject;
        }

        public WeakReference<ImageView> getWeakImageView() {
            return mWeakImageView;
        }

    }

    public static class ResponseValues {

        /**/
        private final WeakReference<ImageView> mWeakImageView;
        /**/
        private final String mFileFullPath;
        /**/
        private final Drawable mDrawable;


        public ResponseValues(WeakReference<ImageView> weakImageView, String fileFullPath, Drawable drawable) {
            this.mWeakImageView = weakImageView;
            this.mFileFullPath = fileFullPath;
            this.mDrawable = drawable;
        }

        public WeakReference<ImageView> getWeakImageView() {
            return mWeakImageView;
        }

        public String getFileFullPath() {
            return mFileFullPath;
        }

        public Drawable getDrawable() {
            return mDrawable;
        }
    }
}

