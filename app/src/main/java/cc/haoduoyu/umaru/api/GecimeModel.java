package cc.haoduoyu.umaru.api;

/**
 * Created by XP on 2016/1/28.
 */
public class GecimeModel {
    /**
     * count : 1
     * code : 0
     * result : {"cover":"http://s.geci.me/album-cover/157/1573814.jpg","thumb":"http://s.geci.me/album-cover/157/1573814-thumb.jpg"}
     */

    private int count;
    private int code;
    /**
     * cover : http://s.geci.me/album-cover/157/1573814.jpg
     * thumb : http://s.geci.me/album-cover/157/1573814-thumb.jpg
     */

    private ResultEntity result;

    public void setCount(int count) {
        this.count = count;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public int getCode() {
        return code;
    }

    public ResultEntity getResult() {
        return result;
    }

    public static class ResultEntity {
        private String cover;
        private String thumb;

        public void setCover(String cover) {
            this.cover = cover;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getCover() {
            return cover;
        }

        public String getThumb() {
            return thumb;
        }
    }
}
