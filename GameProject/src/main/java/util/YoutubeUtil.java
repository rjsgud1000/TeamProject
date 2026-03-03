package util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * YouTube URL -> embed URL converter.
 *
 * Supports:
 *  - https://www.youtube.com/watch?v=VIDEO_ID
 *  - https://youtu.be/VIDEO_ID
 *  - https://www.youtube.com/shorts/VIDEO_ID
 *  - https://www.youtube.com/embed/VIDEO_ID
 */
public class YoutubeUtil {

    public static String toEmbedUrl(String url) {
        if (url == null) return null;
        String u = url.trim();
        if (u.isEmpty()) return null;

        try {
            URI uri = new URI(u);
            String host = uri.getHost();
            if (host == null) return null;
            host = host.toLowerCase();

            // youtu.be/{id}
            if (host.endsWith("youtu.be")) {
                String path = uri.getPath();
                String id = firstPathSegment(path);
                return id == null ? null : "https://www.youtube.com/embed/" + id;
            }

            if (host.contains("youtube.com")) {
                String path = uri.getPath() == null ? "" : uri.getPath();

                // /watch?v={id}
                if (path.equals("/watch")) {
                    String v = queryParam(uri.getQuery(), "v");
                    return v == null ? null : "https://www.youtube.com/embed/" + v;
                }

                // /shorts/{id}
                if (path.startsWith("/shorts/")) {
                    String id = firstPathSegment(path.substring("/shorts".length()));
                    return id == null ? null : "https://www.youtube.com/embed/" + id;
                }

                // /embed/{id}
                if (path.startsWith("/embed/")) {
                    String id = firstPathSegment(path.substring("/embed".length()));
                    return id == null ? null : "https://www.youtube.com/embed/" + id;
                }
            }

            return null;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private static String queryParam(String query, String key) {
        if (query == null || query.isEmpty()) return null;
        String[] parts = query.split("&");
        for (String p : parts) {
            int idx = p.indexOf('=');
            if (idx <= 0) continue;
            String k = p.substring(0, idx);
            String v = p.substring(idx + 1);
            if (key.equals(k) && !v.isEmpty()) {
                // remove possible extra params like &t=..
                int hash = v.indexOf('#');
                if (hash > 0) v = v.substring(0, hash);
                return v;
            }
        }
        return null;
    }

    private static String firstPathSegment(String path) {
        if (path == null) return null;
        String p = path;
        while (p.startsWith("/")) p = p.substring(1);
        if (p.isEmpty()) return null;
        int slash = p.indexOf('/');
        String seg = slash >= 0 ? p.substring(0, slash) : p;
        if (seg.isEmpty()) return null;
        // strip query-like leftovers
        int q = seg.indexOf('?');
        if (q > 0) seg = seg.substring(0, q);
        int hash = seg.indexOf('#');
        if (hash > 0) seg = seg.substring(0, hash);
        return seg.isEmpty() ? null : seg;
    }

    private YoutubeUtil() {}
}
