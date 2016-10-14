package corp.wmsoft.android.lib.filemanager.util;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.LruCache;
import android.util.Pair;
import android.widget.ImageView;

import java.util.Map;
import java.util.WeakHashMap;

import corp.wmsoft.android.lib.filemanager.WMFileManager;
import corp.wmsoft.android.lib.filemanager.di.Injection;
import corp.wmsoft.android.lib.filemanager.interactors.GetThumbDrawable;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;

import rx.Subscription;
import rx.functions.Action1;


/**
 * A class that holds icons for a more efficient access.
 */
public class IconsHelper {

    /**/
    private static final int INITIAL_CAPACITY = 500;

    /**/
    private static LruCache<Integer, Drawable> mIconsCache;
    /**/
    private static LruCache<String, Drawable> mThumbsCache;
    /* this used to store references to subscription and if there are created new subscription for this imageView - then first need to unsubscribe previous */
    private static WeakHashMap<ImageView, Pair<Subscription, Action1<GetThumbDrawable.ResponseValues>>> mSubscriptions;


    /**
     * Constructor of <code>IconsHelper</code>.
     */
    private IconsHelper() {
    }

    /**
     * Method that returns a drawable reference of a icon.
     *
     * @param drawableResId The resource identifier
     * @return Drawable The drawable icon reference
     */
    public static Drawable getDrawable(final @DrawableRes int drawableResId) {

        Drawable drawable = getDrawableFromIconsCache(drawableResId);

        if (drawable != null) {
            return drawable;
        }

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            drawable = ContextCompat.getDrawable(WMFileManager.getApplicationContext(), drawableResId);
        } else {
//            drawable = AppCompatDrawableManager.get().getDrawable(WMFileManager.getApplicationContext(), drawableResId);
            drawable = VectorDrawableCompat.create(WMFileManager.getApplicationContext().getResources(), drawableResId, WMFileManager.getApplicationContext().getTheme());
        }

        addDrawableToIconsCache(drawableResId, drawable);

        return drawable;
    }

    /**
     * Method that returns a drawable reference of a FileSystemObject.
     *
     * @param iconView View to load the drawable into
     * @param fso The FileSystemObject reference
     * @param defaultIcon Drawable to be used in case no specific one could be found
     */
    public static void loadDrawable(ImageView iconView, FileSystemObject fso, Drawable defaultIcon) {
        if (!PreferencesHelper.isShowThumbs()) {
            iconView.setImageDrawable(defaultIcon);
            return;
        }

        final String filePath = fso.getFullPath();

        final Drawable drawable = getDrawableFromThumbsCache(filePath);

        if (drawable != null) {
            iconView.setImageDrawable(drawable);
        } else {
            iconView.setImageDrawable(defaultIcon);

            if (mSubscriptions == null)
                mSubscriptions = new WeakHashMap<>();

            Pair<Subscription, Action1<GetThumbDrawable.ResponseValues>> pair = mSubscriptions.get(iconView);
            if (pair != null) {
                Subscription previousSubscriptionForView = pair.first;
                if (previousSubscriptionForView != null) {
                    previousSubscriptionForView.unsubscribe();
                }
            }

            GetThumbDrawable getThumbDrawable = Injection.provideGetThumbDrawable();
            GetThumbDrawable.RequestValues requestValue = new GetThumbDrawable.RequestValues(iconView, fso);

            Action1<GetThumbDrawable.ResponseValues> action = new Action1<GetThumbDrawable.ResponseValues>() {
                @Override
                public void call(GetThumbDrawable.ResponseValues responseValues) {
                    ImageView view = responseValues.getWeakImageView().get();
                    if (view == null) {
                        return;
                    }

                    if (mSubscriptions == null)
                        return;

                    Pair<Subscription, Action1<GetThumbDrawable.ResponseValues>> pair = mSubscriptions.get(view);
                    if (pair != null) {
                        Action1<GetThumbDrawable.ResponseValues> actionForImageView = mSubscriptions.get(view).second;
                        if (actionForImageView != this) {
                            return;
                        }
                    }

                    Drawable thumbDrawable = responseValues.getDrawable();
                    if (thumbDrawable != null) {
                        addDrawableToThumbsCache(responseValues.getFileFullPath(), thumbDrawable);
                        view.setImageDrawable(thumbDrawable);
                    }
                }
            };

            mSubscriptions.put(iconView, new Pair<>(getThumbDrawable.execute(requestValue, action), action));
        }
    }

    /**
     * Free any resources used by this instance
     */
    public static void cleanup() {
        if (mIconsCache != null)
            mIconsCache.evictAll();
        mIconsCache = null;

        if (mThumbsCache != null)
            mThumbsCache.evictAll();
        mThumbsCache = null;

        if (mSubscriptions != null) {
            for(Map.Entry<ImageView,  Pair<Subscription, Action1<GetThumbDrawable.ResponseValues>>> entry : mSubscriptions.entrySet()) {
                Pair<Subscription, Action1<GetThumbDrawable.ResponseValues>> pair = entry.getValue();
                if (pair != null)
                    pair.first.unsubscribe();
            }
            mSubscriptions.clear();
            mSubscriptions = null;
        }
    }

    private static void addDrawableToIconsCache(int key, Drawable drawable) {
        initIconsCacheIfNeeded();
        if (getDrawableFromIconsCache(key) == null) {
            mIconsCache.put(key, drawable);
        }
    }

    private static Drawable getDrawableFromIconsCache(int key) {
        initIconsCacheIfNeeded();
        return mIconsCache.get(key);
    }

    private static void initIconsCacheIfNeeded() {
        if (mIconsCache == null)
            mIconsCache = new LruCache<>(INITIAL_CAPACITY);
    }

    private static void addDrawableToThumbsCache(String key, Drawable drawable) {
        initThumbsCacheIfNeeded();
        if (getDrawableFromThumbsCache(key) == null) {
            mThumbsCache.put(key, drawable);
        }
    }

    private static Drawable getDrawableFromThumbsCache(String key) {
        initThumbsCacheIfNeeded();
        return mThumbsCache.get(key);
    }

    private static void initThumbsCacheIfNeeded() {
        if (mThumbsCache == null)
            mThumbsCache = new LruCache<>(INITIAL_CAPACITY);
    }

}
