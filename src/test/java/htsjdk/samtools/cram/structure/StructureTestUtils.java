package htsjdk.samtools.cram.structure;

import htsjdk.HtsjdkTest;
import htsjdk.samtools.cram.structure.block.BlockCompressionMethod;
import org.testng.annotations.DataProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class StructureTestUtils extends HtsjdkTest {

    // Set of DataSeries that HTSJDK doesn't generate on write, but which may be present in
    // CRAM streams generated by other implementations. Used for synthesizing these encodings
    // for test purposes, and for filtering out the set of expected data series and encodings
    // in tests.
    public static final Set<DataSeries> DATASERIES_NOT_WRITTEN_BY_HTSJDK = Collections.unmodifiableSet(new LinkedHashSet<DataSeries>() {{
        add(DataSeries.TV_TestMark);
        add(DataSeries.TM_TestMark);
        add(DataSeries.BB_Bases);
        add(DataSeries.QQ_scores);
        add(DataSeries.TC_TagCount);
        add(DataSeries.TN_TagNameAndType);
    }});

    @DataProvider(name="externalCompressionMethods")
    public Object[] getExternalCompressionMethods() {
        return BlockCompressionMethod.values();
    }

}
