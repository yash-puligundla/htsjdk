package htsjdk.samtools;

import htsjdk.*;
import htsjdk.samtools.util.*;
import org.testng.*;
import org.testng.annotations.*;

import java.io.*;

public class SAMFileUtf8PositiveTest extends HtsjdkTest {
    private static final File TEST_DATA_DIR = new File("src/test/resources/htsjdk/samtools");

    // UTF-8 is allowed in the following fields in SAMv1
    // @SQ - DS
    // @RG - DS
    // @PG - CL, DS
    // @CO

    @DataProvider(name = "Utf8PositiveTestCases")
    public Object[][] WriteUtf8PositiveTestCases() {
        SAMSequenceRecord sequenceRecord_1 = new SAMSequenceRecord("chr1", 101);
        sequenceRecord_1.setAttribute("DS", "sq description");
        sequenceRecord_1.setSequenceIndex(0);

        SAMReadGroupRecord readGroupRecord_1 = new SAMReadGroupRecord("rg1");
        readGroupRecord_1.setAttribute("SM", "Hi,Mom!");
        readGroupRecord_1.setAttribute("DS", "rg description");

        SAMProgramRecord programRecord_1 = new SAMProgramRecord("33");
        programRecord_1.setAttribute("CL", "xy");
        programRecord_1.setAttribute("DS", "pg description");

        String comment_1 = "@CO\tcomment here";

        SAMFileHeader samFileHeader_1 = new SAMFileHeader();
        samFileHeader_1.addSequence(sequenceRecord_1);
        samFileHeader_1.addReadGroup(readGroupRecord_1);
        samFileHeader_1.addProgramRecord(programRecord_1);
        samFileHeader_1.addComment(comment_1);

        SAMSequenceRecord sequenceRecord_2 = new SAMSequenceRecord("chr1", 101);
        sequenceRecord_2.setAttribute("DS", "Emoji\uD83D\uDE0A");
        sequenceRecord_2.setSequenceIndex(0);

        SAMReadGroupRecord readGroupRecord_2 = new SAMReadGroupRecord("rg1");
        readGroupRecord_2.setAttribute("SM", "Hi,Mom!");
        readGroupRecord_2.setAttribute("DS", "Kanjiアメリカ");

        SAMProgramRecord programRecord_2 = new SAMProgramRecord("33");
        programRecord_2.setAttribute("CL", "äカ");
        programRecord_2.setAttribute("DS", "\uD83D\uDE00リ");

        String comment_2 = "@CO\tKanjiアメリカ\uD83D\uDE00リä";

        SAMFileHeader samFileHeader_2 = new SAMFileHeader();
        samFileHeader_2.addSequence(sequenceRecord_2);
        samFileHeader_2.addReadGroup(readGroupRecord_2);
        samFileHeader_2.addProgramRecord(programRecord_2);
        samFileHeader_2.addComment(comment_2);


        byte bases_1[] = {'C','A','A','C','A','G','A','A','G','C'};
        final SAMRecord samRecord_1 = new SAMRecord(samFileHeader_1);
        samRecord_1.setReadName("A");
        samRecord_1.setAlignmentStart(1);
        samRecord_1.setMappingQuality(255);
        samRecord_1.setReadBases(bases_1);
        samRecord_1.setCigarString("10M");
        samRecord_1.setReferenceName("chr2");
        samRecord_1.setReferenceIndex(0); //should be present in header sequence dictionary
        samRecord_1.setBaseQualities(SAMRecord.NULL_QUALS);

        byte bases_2[] = {'C','A','A','C','A','G','A','A','G','C'};
        final SAMRecord samRecord_2 = new SAMRecord(samFileHeader_2);
        samRecord_2.setReadName("A");
        samRecord_2.setAlignmentStart(1);
        samRecord_2.setMappingQuality(255);
        samRecord_2.setReadBases(bases_2);
        samRecord_2.setCigarString("10M");
        samRecord_2.setReferenceName("chr2");
        samRecord_2.setReferenceIndex(0); //should be present in header sequence dictionary
        samRecord_2.setBaseQualities(SAMRecord.NULL_QUALS);

        return new Object[][]{
                {"utf8_encoded_write_positive_1.sam",samRecord_1},
                {"utf8_encoded_write_positive_2.sam",samRecord_2}
        };
    }

    @Test(enabled = false,dataProvider = "Utf8PositiveTestCases", description = "Test reading of a SAM file with UTF-8 encoding present/absent in the permitted fields")
    public void ReadUtf8PositiveTests(final String inputFile, final SAMRecord samRecord) throws Exception {

        final File input = new File(TEST_DATA_DIR, inputFile);
        SAMFileHeader expected_samFileHeader = samRecord.getHeader();

        try (SamReader reader = SamReaderFactory.makeDefault().open(input)) {
            SAMFileHeader head = reader.getFileHeader();
            Assert.assertEquals(head.getSequence("chr1"), expected_samFileHeader.getSequence("chr1"));
            Assert.assertEquals(head.getReadGroup("rg1"), expected_samFileHeader.getReadGroup("rg1"));
            Assert.assertEquals(head.getProgramRecords().get(0), expected_samFileHeader.getProgramRecord("33"));
            Assert.assertEquals(head.getComments().get(0), expected_samFileHeader.getComments().get(0));
        }
    }

    @Test(dataProvider = "Utf8PositiveTestCases", description = "Test writing of a SAM file with UTF-8 encoding present/absent in the permitted fields")
    public void WriteUtf8PositiveTests(final String inputFile, SAMRecord expected_samRecord) throws Exception {

        final File input = new File(TEST_DATA_DIR, inputFile);
        final File outputFile = File.createTempFile("write-utf8-positive-out", ".sam");
//        final File outputFile = new File("write-utf8-positive-out.sam");
        outputFile.delete();
        outputFile.deleteOnExit();

        final SAMFileWriterFactory factory = new SAMFileWriterFactory();
        SAMFileWriter writer = factory.makeSAMWriter(expected_samRecord.getHeader(), false, new FileOutputStream(outputFile));
        writer.addAlignment(expected_samRecord);

        final String originalsam;
        try (InputStream is = new FileInputStream(input)) {
            originalsam = IOUtil.readFully(is);
        }
        final String writtenSam;
        try (InputStream is = new FileInputStream(outputFile)) {
            writtenSam = IOUtil.readFully(is);
        }
        System.out.println("..............");
        System.out.println(originalsam);
        System.out.println("..............");
        System.out.println(writtenSam);
        Assert.assertEquals(writtenSam, originalsam);
    }
}