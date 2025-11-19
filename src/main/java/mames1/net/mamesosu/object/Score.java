package mames1.net.mamesosu.object;

import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.utils.log.AppLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Score {

    public double pp;
    public long mods;
    public String grade;
    public long score;

    public Beatmap beatmap;

    public String userName;
    public int userId;
    public int country;


    public Score getTopScoreFromUserId(int mode) {
        Score topScore = new Score();
        MySQL mysql = new MySQL();
        Connection connection = mysql.getConnection();
        PreparedStatement ps;
        ResultSet result;

        topScore.beatmap = new Beatmap();

        try {

            ps = connection.prepareStatement("SELECT s.status, s.id AS scoreid, s.userid, s.acc, s.pp, s.score, s.mods, s.grade, m.set_id, m.id, m.title, m.artist, m.version, u.country, u.name FROM scores s force index (idx_scores_mode_status_pp) JOIN users u ON u.id = s.userid JOIN maps m ON m.md5 = s.map_md5 WHERE s.mode = ? AND (u.priv & 1) = 1 AND m.status IN (2, 3) AND s.status = 2 ORDER BY s.pp DESC LIMIT 1");
            ps.setInt(1, mode);
            result = ps.executeQuery();

            if(result.next()) {
                topScore.pp = result.getDouble("pp");
                topScore.mods = result.getLong("mods");
                topScore.grade = result.getString("s.grade");
                topScore.score = result.getLong("s.score");

                topScore.beatmap.beatmapId = result.getLong("m.id");
                topScore.beatmap.beatmapSetId = result.getLong("m.set_id");
                topScore.beatmap.artist = result.getString("m.artist");
                topScore.beatmap.title = result.getString("m.title");
                topScore.beatmap.version = result.getString("m.version");

                topScore.userName = result.getString("u.name");
                topScore.country = result.getInt("u.country");

                return topScore;
            }
        } catch (Exception e) {
            AppLogger.log("トップスコアの取得に失敗しました: " + e.getMessage(), LogLevel.ERROR);
        }

        AppLogger.log("指定されたユーザーIDのトップスコアが見つかりません: " + this.userId, LogLevel.WARN);

        return null;
    }
}
