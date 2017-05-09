package ivonhoe.java.bitmapanalyzer;

import com.squareup.haha.perflib.Snapshot;

/**
 * @author Ivonhoe on 2017/3/24.
 */

public interface SnapshotAnalyzer {

    public String filterKey();

    public void onProcess(Snapshot snapshot);
}
