package athg2sms;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.athg2sms.business.pattern.Format;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;

import java.util.Map;

/**
 * Created by lionel on 22/12/16.
 */

public class FormatTest {

    @Test
    public void no_format_should_avoid_having_a_folder_var () {
        for (Map.Entry<String, Format> entry :FormatSettings.getInstance().getFormats().entrySet()) {
            Assert.assertTrue(entry.getValue().getRegex().getIndexOfFolderCapturingGroup() >= 1);
        }
    }
}
