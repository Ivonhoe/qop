package ivonhoe.java.bitmapanalyzer;

import com.squareup.haha.perflib.ClassInstance;
import com.squareup.haha.perflib.ClassObj;
import com.squareup.haha.perflib.Instance;
import com.squareup.haha.perflib.Snapshot;

import java.io.File;

import ivonhoe.java.util.Logger;

/**
 * @author Ivonhoe on 2017/3/24.
 */

public class StringAnalyzer implements SnapshotAnalyzer {

    private String mPathInput;
    private String mPathOutput;

    public StringAnalyzer(String[] args) {
        mPathInput = args[0];

        int index = mPathInput.lastIndexOf(File.separator);
        mPathOutput = mPathInput.substring(0, index + 1);


        if (args.length > 1) {
            mPathOutput = mPathOutput + args[1];
            if (!mPathOutput.endsWith(File.separator)) {
                mPathOutput = mPathOutput + File.separator;
            }
            File file = new File(mPathOutput);
            file.mkdirs();
        }
    }

    @Override
    public String filterKey() {
        return "java.lang.String";
    }

    @Override
    public void onProcess(Snapshot snapshot) {
        try {
            ClassObj someClass = snapshot.findClass(filterKey());
            Logger.d("length:"+someClass.getInstancesList().size());
            for (Instance instance : someClass.getInstancesList()) {
                if (instance instanceof ClassInstance) {
                    ClassInstance classInstance = (ClassInstance) instance;
                    Object[] byteArray = BitmapAnalyzer.Toolkit.getArrayField(classInstance, "value");
                    if (byteArray == null) {
                        Logger.d("mBuffer is null!");
                        continue;
                    }

                    if (byteArray.length < 100000) {
                        return;
                    }

                    char[] buffer = new char[byteArray.length];
                    for (int i = 0; i < buffer.length; i++) {
                        buffer[i] = ((Character) byteArray[i]).charValue();
                    }

                    Logger.d("buffer:" + new String(buffer));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
