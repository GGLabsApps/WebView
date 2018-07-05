package com.gglabs.mywebview;

/**
 * Created by GG on 13/12/2017.
 */

public class UrlUtils {

    private String url;

    private String checkUrl;
    static final String[] extensions = {".jpg", ".png", "jpeg", "bmp"};

    public UrlUtils(String url) {
        this.url = url;
    }

    /**
     * @param url check url provided
     * @return extension or null if extension not found
     */

    public static String getImageExtension(String url) {
        for (int i = 0; i < extensions.length; i++)
            if (url.contains(extensions[i])) return extensions[i];

        for (String ext : extensions) if (url.contains(ext)) return ext;

        return null;
    }

    /**
     * @param url        check url provided
     * @param extensions image extensions (jpg, png, jpeg, bmp)
     * @return extension or null if extension not found
     */

    public static String getImageExtension(String url, String[] extensions) {
        for (int i = 0; i < extensions.length; i++)
            if (url.contains(extensions[i])) return extensions[i];

        for (String ext : extensions) if (url.contains(ext)) return ext;

        return null;
    }

    public static boolean isUrlContainsImage(String url) {
        return getImageExtension(url, extensions) != null;
    }

}
