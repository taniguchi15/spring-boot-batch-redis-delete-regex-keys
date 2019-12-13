package tech.taniguchi.batch.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

@Component
public class RedisUtils {

    @Autowired
    RedisTemplate redisTemplate;


    /**
     *  パターンに一致するKeyの全量削除
     *
     *
     *      redis scanコマンドを使用し、同一コネクションによるキーのストリーム取得、削除を実行します。
     *
     *      scanコマンドは、コマンドの実行開始時点から終了時点までの時間に存在する、すべてのキーを重複チェックなしで返すという仕様
     *      です。そのため、削除対象のキーが増え続ける状況下で使用した場合、実行後コマンドがずっと終了せずに保存されるべきキーが消
     *      去され続けるという動きに注意してください。
     *
     *      大量データのキーの削除時は特に注意が必要です。
     *
     *
     * @param deleteKeyPattern
     */
    public void deleteKeys(String deleteKeyPattern) {
        int streamSize = 10000;
        redisTemplate.execute(conn -> {
            int i = 0;
            byte[][] buf = new byte[streamSize][];
            Cursor<byte[]> c = conn.scan(ScanOptions.scanOptions().count(streamSize).match(deleteKeyPattern).build());
            while (c.hasNext()) {
                buf[i++] = c.next();
                if (i >= streamSize) {
                    conn.del(buf);
                    i = 0;
                }
            }
            if (i > 0) conn.del(Arrays.copyOfRange(buf,0, i));
            return null;
        }, true);
    }


    /**
     *
     * Redis は正規表現が貧弱であるため、一回全量スキャン、対象判定をJava側で行い、削除対象キーを送信する方が高速である
     *
     * @param searchKeyPattern
     * @param deleteKeyPattern
     */
    public void deleteKeys(String searchKeyPattern, String deleteKeyPattern) {
        int streamSize = 10000;
        redisTemplate.execute(conn -> {
            int i = 0;
            byte[][] buf = new byte[streamSize][];
            Pattern p = Pattern.compile(deleteKeyPattern);
            Cursor<byte[]> c = conn.scan(ScanOptions.scanOptions().count(streamSize).match(searchKeyPattern).build());
            while (c.hasNext()) {
                byte[] b = c.next();
                if (p.matcher(new String(b)).matches()) {
                    buf[i++] = b;
                    if (i >= streamSize) {
                        conn.del(buf);
                        i = 0;
                    }
                }
            }
            if (i > 0) conn.del(Arrays.copyOfRange(buf,0, i));
            return null;
        }, true);
    }



}
