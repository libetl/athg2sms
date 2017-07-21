package athg2sms;

import org.junit.Assert;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.HandlerHolder;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsFinder;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;
import org.toilelibre.libe.athg2sms.business.convert.Converter;
import org.toilelibre.libe.athg2sms.business.export.Exporter;
import org.toilelibre.libe.athg2sms.business.pattern.BuiltInFormat;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;

class Athg2SmsJUnitTester {

    static class JunitConvertListener implements ConvertListener<Object> {

        private List<Sms> messages;

        JunitConvertListener() {
            messages = new ArrayList<Sms>();
        }

        List<Sms> getMessages() {
            return messages;
        }

        @Override
        public ConvertListener bind() {
            return null;
        }

        public int delete (final URI uriDelete, final String where, final String [] strings) {
            return 0;
        }

        public void displayInserted(final ContextHolder<Object> contextHolder, final Converter.ConversionResult result) {
        }

        public void end () {
        }

        public void insert (final URI uri, final Sms sms) {
            messages.add (sms);
        }

        public void sayIPrepareTheList (final ContextHolder<Object> contextHolder, final int size) {
        }

        public void setMax (final int nb2) {
            messages = new ArrayList<Sms>(nb2);
        }

        public void updateProgress (final String text, final int i2, final int nb2) {
        }

    }

    private static class ExportDedicatedSmsFinder extends SmsFinder {
        private final List<Sms> messages;

        public ExportDedicatedSmsFinder(List<Sms> messages1) {
            this.messages = messages1;
        }

        @Override
        public <T> List<Sms> pickThemAll(ContextHolder<T> contextHolder, HandlerHolder<?> handler, ConvertListener<T> convertListener, Condition stopMonitor) {
            return messages;
        }
    }


    static JunitConvertListener importFile(final String classpathFile, final BuiltInFormat conversionSet) throws URISyntaxException {
        return importFile(classpathFile, conversionSet, false);
    }

    static JunitConvertListener importFile(final String classpathFile, final BuiltInFormat conversionSet, final boolean shouldBeEmpty) throws URISyntaxException {
        // Given
        final URL url = ImportTest.class.getClassLoader ().getResource (classpathFile);
        String content;
        try {
            final File file = url == null ? new File (classpathFile) : new File (url.toURI ());
            content = read(file);
        } catch (final IOException e) {
            throw new RuntimeException (e);
        }

        return importNow(conversionSet, shouldBeEmpty, content);
    }

    private static String read(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));

        for (String line = br.readLine(); line != null; line = br.readLine()) {
            stringBuilder.append(line).append('\n');
        }
        return stringBuilder.toString();
    }

    static JunitConvertListener importString(final String content, final BuiltInFormat conversionSet) throws URISyntaxException {
        return importString(content, conversionSet, false);
    }

    static JunitConvertListener importString(final String content, final BuiltInFormat conversionSet, final boolean shouldBeEmpty) throws URISyntaxException {
        return importNow(conversionSet, shouldBeEmpty, content);
    }

    private static JunitConvertListener importNow(BuiltInFormat conversionSet, boolean shouldBeEmpty, String content) {
        // Given
        final Converter converter = new Converter();
        final JunitConvertListener junitConvertListener = new JunitConvertListener();

        // When
        converter.convertNow(FormatSettings.getInstance().getFormats().get(conversionSet.getCompleteName()),
                content, junitConvertListener, null, new ContextHolder<Object>(null),
                null, null, null);

        // then
        if (!shouldBeEmpty) {
            Assert.assertTrue (junitConvertListener.getMessages().size() > 0);
        }

        return junitConvertListener;
    }

    static String exportNow(List<Sms> messages, BuiltInFormat conversionSet) {
        return exportNow(messages, conversionSet, false);
    }

    static String exportNow(List<Sms> messages, BuiltInFormat conversionSet, boolean shouldBeEmpty) {
        //Given
        final Exporter exporter = new Exporter();
        //When
        final String result =
                exporter.export(new ExportDedicatedSmsFinder(messages), new ContextHolder<Object>(null),
                        new HandlerHolder<Object>(null) {
                            @Override
                            public void postForHandler(Runnable runnable) {
                            }
                        }, conversionSet.getCompleteName(),
                        new JunitConvertListener(), null);

        //then
        if (!shouldBeEmpty) {
            Assert.assertTrue (result != null && result.length() > 0);
        }

        return result;
    }
}
