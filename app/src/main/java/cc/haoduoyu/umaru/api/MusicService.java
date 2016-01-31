package cc.haoduoyu.umaru.api;


import cc.haoduoyu.umaru.model.ArtistInfo;
import cc.haoduoyu.umaru.model.TopArtists;
import cc.haoduoyu.umaru.model.TopTracks;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by XP on 2016/1/25.
 */
public interface MusicService {

    String BASE_URL = "http://geci.me/api/cover/";
    String LAST_FM_URL = "http://ws.audioscrobbler.com/2.0/";
    String API_KEY = "84b222a566e276c397ba03efec8e50a6";
    String BASE_ARTIST = "?method=artist.getinfo&lang=zh&api_key=84b222a566e276c397ba03efec8e50a6&format=json";
    String BASE_TOP_ARTISTS =
            "?method=Chart.getTopArtists&lang=zh&api_key=84b222a566e276c397ba03efec8e50a6&format=json";
    String BASE_TOP_TRACKS =
            "?method=Chart.getTopTracks&lang=zh&api_key=84b222a566e276c397ba03efec8e50a6&format=json";


    //http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&lang=zh&artist=周杰伦&format=json&api_key=84b222a566e276c397ba03efec8e50a6
    @GET(BASE_ARTIST)
    Call<ArtistInfo> getArtistInfo(@Query("artist") String artist);

    @GET(BASE_TOP_ARTISTS)
    Call<TopArtists> getTopArtists(@Query("page") int page, @Query("limit") int limit);

    @GET(BASE_TOP_TRACKS)
    Call<TopTracks> getTopTracks(@Query("page") int page, @Query("limit") int limit);
}