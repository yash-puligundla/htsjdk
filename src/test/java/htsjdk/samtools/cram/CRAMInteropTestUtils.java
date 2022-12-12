package htsjdk.samtools.cram;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import htsjdk.utils.SamtoolsTestUtils;

/**
 * Interop test data is kept in a separate repository, currently at https://github.com/samtools/htscodecs
 * so it can be shared across htslib/samtools/htsjdk.
 */
public class CRAMInteropTestUtils {
    public static final String INTEROP_TEST_FILES_PATH = SamtoolsTestUtils.getCRAMInteropData();

    /**
     * @return true if interop test data is available, otherwise false
     */
    public static boolean isInteropTestDataAvailable() {
        final Path testDataPath = getInteropTestDataLocation();
        return Files.exists(testDataPath);
    }

    /**
     * @return the name and location of the local interop test data as specified by the
     * variable INTEROP_TEST_FILES_PATH
     */
    public static Path getInteropTestDataLocation() {
        return Paths.get(INTEROP_TEST_FILES_PATH);
    }

    // the input files have embedded newlines that the test removes before round-tripping
    public static final byte[] filterEmbeddedNewlines(final byte[] rawBytes) throws IOException {
        // 1. filters new lines if any.
        // 2. "q40+dir" file has an extra column delimited by tab. This column provides READ1 vs READ2 flag.
        //     This file is also new-line separated. The extra column, '\t' and '\n' are filtered.
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int skip = 0;
            for (final byte b : rawBytes) {
                if (b == '\t'){
                    skip = 1;
                }
                if (b == '\n') {
                    skip = 0;
                }
                if (skip == 0 && b !='\n') {
                    baos.write(b);
                }
            }
            return baos.toByteArray();
        }
    }

    // Given a test file name and the codec, map it to the corresponding compressed file path
    public static final Path getCompressedCodecPath(final String codecType, final Path uncompressedInteropPath, int formatFlags) {

        // Example uncompressedInteropPath: q4, codecType: r4x16, formatFlags: 193  => compressedFileName: r4x16/q4.193
        // the substring after "." in the compressedFileName is the formatFlags or the first byte of the compressed stream
        final String compressedFileName = String.format("%s/%s.%s", codecType, uncompressedInteropPath.getFileName(), formatFlags);
        return uncompressedInteropPath.getParent().resolve(compressedFileName);
    }

}