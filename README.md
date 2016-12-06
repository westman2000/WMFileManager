# WMFileManager


**Under development**


To use this library project, main project must use DataBinding
**build.gradle** 
```gradle
    android {
        dataBinding {
            enabled = true
        }
    }
```

on jcenter:

**build.gradle**
```
compile 'corp.wmsoft.android.lib:filemanager:+'
```

You can override colorAccent used in library, by override color:

**colors.xml**

```
<color name="wm_fm_colorAccent">#ff33bc14</color>
```