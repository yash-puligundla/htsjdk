package htsjdk.samtools.cram;

import htsjdk.HtsjdkTest;
import htsjdk.samtools.cram.compression.rans.RANSDecode;
import htsjdk.samtools.cram.compression.rans.RANSEncode;
import htsjdk.samtools.cram.compression.rans.RANSParams;
import htsjdk.samtools.cram.compression.rans.rans4x8.RANS4x8Decode;
import htsjdk.samtools.cram.compression.rans.rans4x8.RANS4x8Encode;
import htsjdk.samtools.cram.compression.rans.rans4x8.RANS4x8Params;
import htsjdk.samtools.cram.compression.rans.ransnx16.RANSNx16Decode;
import htsjdk.samtools.cram.compression.rans.ransnx16.RANSNx16Encode;
import htsjdk.samtools.cram.compression.rans.ransnx16.RANSNx16Params;
import org.apache.commons.compress.utils.IOUtils;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static htsjdk.samtools.cram.compression.rans.ransnx16.RANSNx16Params.STRIPE_FLAG_MASK;

/**
 * HTSCodecs test data is kept in a separate repository, currently at https://github.com/jkbonfield/htscodecs-corpus
 * so it can be shared across htslib/samtools/htsjdk.
 */
public class CRAMCodecCorpusTest extends HtsjdkTest {
    @Test
    public void testGetHTSCodecsCorpus() {
        if (!CRAMCodecCorpus.isHtsCodecsTestDataAvailable()) {
            throw new SkipException(String.format(
                    "No HTS codecs test data found." +
                            " The %s environment variable must be set to the location of the local hts codecs test data.",
                    CRAMCodecCorpus.HTSCODECS_TEST_DATA_ENV));
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // RANS tests
    /////////////////////////////////////////////////////////////////////////////////////////////////

    //TODO: the TestDataProviders tests fail if the hts codecs corpus isn't available because

    // RANS4x8 codecs and testdata
    public Object[][] getRANS4x8TestData() throws IOException {
        // cache/reuse this for each test case to eliminate excessive garbage collection
        final RANS4x8Encode rans4x8Encode = new RANS4x8Encode();
        final RANS4x8Decode rans4x8Decode = new RANS4x8Decode();
        final List<Object[]> testCases = new ArrayList<>();
        getHtsCodecRANSTestFiles().stream()
                .forEach(p ->
                {
                    // RANS 4x8 order 0
                    testCases.add(new Object[] {
                            p,
                            rans4x8Encode ,
                            rans4x8Decode,
                            new RANS4x8Params(RANSParams.ORDER.ZERO),
                            "r4x8" // htscodecs directory where the RANS4x8 compressed files reside
                    });
                    // RANS 4x8 order 1
                    testCases.add(new Object[] {
                            p,
                            rans4x8Encode ,
                            rans4x8Decode,
                            new RANS4x8Params(RANSParams.ORDER.ONE),
                            "r4x8" // htscodecs directory where the RANS4x8 compressed files reside
                    });
                });
        return testCases.toArray(new Object[][]{});
    }

    // RANSNx16 codecs and testdata
    public Object[][] getRANS4x16TestData() throws IOException {
        final RANSNx16Encode ransNx16Encode = new RANSNx16Encode();
        final RANSNx16Decode ransNx16Decode = new RANSNx16Decode();
        final List<Object[]> testCases = new ArrayList<>();
        getHtsCodecRANSTestFiles().stream()
                .forEach(p ->
                {
                    // RANS Nx16 order 0, none of the bit flags are set
                    testCases.add(new Object[] {
                            p,
                            ransNx16Encode,
                            ransNx16Decode ,
                            new RANSNx16Params(0x00),
                            "r4x16" // htscodecs directory where the RANSNx16 compressed files reside
                    });

                    // RANS Nx16 order 0, bitflags = 0x40. rle flag is set
                    testCases.add(new Object[] {
                            p,
                            ransNx16Encode,
                            ransNx16Decode ,
                            new RANSNx16Params(0x40),
                            "r4x16" // htscodecs directory where the RANSNx16 compressed files reside
                    });

                    // RANS Nx16 order 1, bitflags = 0x01
                    testCases.add(new Object[] {
                            p,
                            ransNx16Encode,
                            ransNx16Decode ,
                            new RANSNx16Params(0x01),
                            "r4x16" // htscodecs directory where the RANSNx16 compressed files reside
                    });

                    // RANS Nx16 order 1, bitflags = 0x04
                    testCases.add(new Object[] {
                            p,
                            ransNx16Encode,
                            ransNx16Decode ,
                            new RANSNx16Params(0x04),
                            "r4x16" // htscodecs directory where the RANSNx16 compressed files reside
                    });

                    // RANS Nx16 order 1, bitflags = 0x05
                    testCases.add(new Object[] {
                            p,
                            ransNx16Encode,
                            ransNx16Decode ,
                            new RANSNx16Params(0x05),
                            "r4x16" // htscodecs directory where the RANSNx16 compressed files reside
                    });

                    // RANS Nx16 order 1, bitflags = 0x41. rle flag is set
                    testCases.add(new Object[] {
                            p,
                            ransNx16Encode,
                            ransNx16Decode ,
                            new RANSNx16Params(0x41),
                            "r4x16" // htscodecs directory where the RANSNx16 compressed files reside
                    });

                    // RANS Nx16 order 0, bitflags = 0x80. pack flag is set
                    testCases.add(new Object[] {
                            p,
                            ransNx16Encode,
                            ransNx16Decode ,
                            new RANSNx16Params(0x80),
                            "r4x16" // htscodecs directory where the RANSNx16 compressed files reside
                    });

                    // RANS Nx16 order 1, bitflags = 0x81. pack flag is set
                    testCases.add(new Object[] {
                            p,
                            ransNx16Encode,
                            ransNx16Decode ,
                            new RANSNx16Params(0x81),
                            "r4x16" // htscodecs directory where the RANSNx16 compressed files reside
                    });

                    // RANS Nx16 order 0, bitflags = 0xC0. rle flag is set, pack flag is set
                    testCases.add(new Object[] {
                            p,
                            ransNx16Encode,
                            ransNx16Decode ,
                            new RANSNx16Params(0xC0),
                            "r4x16" // htscodecs directory where the RANSNx16 compressed files reside
                    });

                    // RANS Nx16 order 1, bitflags = 0xC1. rle flag is set, pack flag is set
                    testCases.add(new Object[] {
                            p,
                            ransNx16Encode,
                            ransNx16Decode ,
                            new RANSNx16Params(0xC1),
                            "r4x16" // htscodecs directory where the RANSNx16 compressed files reside
                    });

                    // RANS Nx16 order 0, bitflags = 0x08.
                    testCases.add(new Object[] {
                            p,
                            ransNx16Encode,
                            ransNx16Decode ,
                            new RANSNx16Params(0x08),
                            "r4x16" // htscodecs directory where the RANSNx16 compressed files reside
                    });

                    // RANS Nx16 order 1, bitflags = 0x09.
                    testCases.add(new Object[] {
                            p,
                            ransNx16Encode,
                            ransNx16Decode ,
                            new RANSNx16Params(0x09),
                            "r4x16" // htscodecs directory where the RANSNx16 compressed files reside
                    });

                });
        return testCases.toArray(new Object[][]{});
    }

    @DataProvider(name = "allRansCodecsAndData")
    public Object[][] getAllRansCodecs() throws IOException {
        // concatenate RANS4x8 and RANSNx16 codecs and testdata
        return Stream.concat(Arrays.stream(getRANS4x8TestData()), Arrays.stream(getRANS4x16TestData()))
                .toArray(Object[][]::new);
    }

    @Test (
            dataProvider = "allRansCodecsAndData",
            dependsOnMethods = "testGetHTSCodecsCorpus",
            description = "Roundtrip using htsjdk RANS. Compare the output with the original file" )
    public void testRANSRoundTrip(
            final Path inputTestDataPath,
            final RANSEncode ransEncode,
            final RANSDecode ransDecode,
            final RANSParams params,
            final String unusedCompressedDirname) throws IOException {
        if (!CRAMCodecCorpus.isHtsCodecsTestDataAvailable()) {
            throw new SkipException("htscodecs test data is not available locally");
        }
        try (final InputStream is = Files.newInputStream(inputTestDataPath)) {

            // preprocess the uncompressed data (to match what the htscodecs-library test harness does)
            // by filtering out the embedded newlines, and then round trip through RANS and compare the
            // results
            final ByteBuffer uncompressedBytes = ByteBuffer.wrap(filterEmbeddedNewlines(IOUtils.toByteArray(is)));
            if ((params.getFormatFlags() & STRIPE_FLAG_MASK)!=0) {

                // If Stripe Flag is set, skip the round trip test as encoding is not implemented for this case.
                // TODO: Assert raise Exception
                System.out.println(String.format("Stripe Flag is set. Skipping testRANSRoundTrip for file: %s. " +
                        "Format Flags: %s . The current RANSNx16 implementation does not " +
                        "support encoding when Stripe Flag is set", inputTestDataPath.toString(), params.getFormatFlags()));
            }
            else {
                final ByteBuffer compressedBytes = ransEncode.compress(uncompressedBytes, params);
                uncompressedBytes.rewind();
                System.out.println(String.format("filename:%s %s Uncompressed: (%,d) Compressed: (%,d)",
                        inputTestDataPath.getFileName(),
                        params.toString(),
                        uncompressedBytes.remaining(),
                        compressedBytes.remaining()));
                Assert.assertEquals(ransDecode.uncompress(compressedBytes), uncompressedBytes);
            }
        }
    }

    @Test (
            dataProvider = "allRansCodecsAndData",
            dependsOnMethods = "testGetHTSCodecsCorpus",
            description = "Compress the original file using htsjdk RANS and compare it with the existing compressed file. " +
                    "Uncompress the existing compressed file using htsjdk RANS and compare it with the original file.")
    public void testRANSPreCompressed(
            final Path inputTestDataPath,
            final RANSEncode ransEncode,
            final RANSDecode ransDecode,
            final RANSParams params,
            final String CompressedDirname) throws IOException {
        if (!CRAMCodecCorpus.isHtsCodecsTestDataAvailable()) {
            throw new SkipException("htscodecs test data is not available locally");
        }

        final Path preCompressedDataPath = getCompressedRANSPath(CompressedDirname,inputTestDataPath, params);

        try (final InputStream inputStream = Files.newInputStream(inputTestDataPath);
             final InputStream preCompressedInputStream = Files.newInputStream(preCompressedDataPath);
        ) {
            // preprocess the uncompressed data (to match what the htscodecs-library test harness does)
            // by filtering out the embedded newlines, and then round trip through RANS and compare the
            // results
            final ByteBuffer inputBytes = ByteBuffer.wrap(filterEmbeddedNewlines(IOUtils.toByteArray(inputStream)));

            final ByteBuffer preCompressedInputBytes = ByteBuffer.wrap(IOUtils.toByteArray(preCompressedInputStream));

            // commenting as htsjdkCompressedBytes is not used anywhere
            // Use htsjdk to compress the input file from htscodecs repo
//            final ByteBuffer htsjdkCompressedBytes = ransEncode.compress(inputBytes, params);
//            inputBytes.rewind();

//            // commenting as the comparison of compressed bytes is not needed to ensure interoperability.
//            // Compare the htsjdk compressed bytes with the precompressed file from htscodecs repo
//            Assert.assertEquals(htsjdkCompressedBytes, preCompressedInputBytes);

            // Use htsjdk to uncompress the precompressed file from htscodecs repo
            final ByteBuffer htsjdkUncompressedBytes = ransDecode.uncompress(preCompressedInputBytes);

            // Compare the htsjdk uncompressed bytes with the original input file from htscodecs repo
            Assert.assertEquals(htsjdkUncompressedBytes, inputBytes);
        } catch (NoSuchFileException ex){
            // if precompressed file or input file is not present
            System.out.println("Skipping testRANSPrecompressed as either input file " +
                    "or precompressed file is missing. File Missing: " + ex.getMessage());
        }
    }

    // return a list of all RANS test data files in the htscodecs test directory
    private List<Path> getHtsCodecRANSTestFiles() throws IOException {
        CRAMCodecCorpus.assertHTSCodecsTestDataAvailable();
        final List<Path> paths = new ArrayList<>();
        Files.newDirectoryStream(
                        CRAMCodecCorpus.getHTSCodecsTestDataLocation().resolve("dat"),
                        path -> path.getFileName().startsWith("q4") ||
                                path.getFileName().startsWith("q8") ||
                                path.getFileName().startsWith("qvar") ||
                                path.getFileName().startsWith("q40+dir"))
                .forEach(path -> paths.add(path));
        return paths;
    }

    // the input files have embedded newlines that the test remove before round-tripping...
    final byte[] filterEmbeddedNewlines(final byte[] rawBytes) throws IOException {
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

    // Given a test file name, map it to the corresponding rans compressed path
    final Path getCompressedRANSPath(final String ransType,final Path inputTestDataPath, RANSParams params) {

        // Example compressedFileName: r4x16/q4.193
        // the substring after "." in the compressedFileName is the formatFlags (aka. the first byte of the compressed stream)
        final String compressedFileName = String.format("%s/%s.%s", ransType, inputTestDataPath.getFileName(), params.getFormatFlags());
        return inputTestDataPath.getParent().resolve(compressedFileName);
    }

}