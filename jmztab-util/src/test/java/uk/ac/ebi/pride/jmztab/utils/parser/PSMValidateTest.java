package uk.ac.ebi.pride.jmztab.utils.parser;

import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.model.MZTabColumnFactory;
import uk.ac.ebi.pride.jmztab.model.Metadata;
import uk.ac.ebi.pride.jmztab.model.MsRun;
import uk.ac.ebi.pride.jmztab.utils.errors.*;

import java.io.FileNotFoundException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
* @author qingwei
* @since 16/09/13
*/
public class PSMValidateTest {
    private Logger logger = LoggerFactory.getLogger(PSMValidateTest.class);

    private MZTabErrorList errorList;
    private PSMLineParser psmParser;
    private MZTabColumnFactory psmFactory;

    public PSMValidateTest() throws Exception {
        MTDLineParserTest test = new MTDLineParserTest();
        String fileName = "testset/mtdFile.txt";
        Metadata metadata;

        URL uri = PSMValidateTest.class.getClassLoader().getResource(fileName);
        if(uri != null) {
            metadata = test.parseMetadata(uri.getFile());
        } else {
            throw new FileNotFoundException(fileName);
        }

        errorList = new MZTabErrorList();

        PSHLineParser pshParser = new PSHLineParser(metadata);
        String headerLine = "PSH\t" +
            "sequence\t" +
            "PSM_ID\t" +
            "accession\t" +
            "unique\t" +
            "database\t" +
            "database_version\t" +
            "search_engine\t" +
            "search_engine_score[1]\t" +
            "search_engine_score[2]\t" +
            "reliability\t" +
            "modifications\t" +
            "retention_time\t" +
            "charge\t" +
            "exp_mass_to_charge\t" +
            "calc_mass_to_charge\t" +
            "uri\t" +
            "spectra_ref\t" +
            "pre\t" +
            "post\t" +
            "start\t" +
            "end";
        pshParser.parse(1, headerLine, errorList);
        psmFactory = pshParser.getFactory();
        psmParser = new PSMLineParser(psmFactory, new PositionMapping(psmFactory, headerLine), metadata, errorList);
    }

    @After
    public void tearDown() throws Exception {
        for (MZTabError mzTabError : errorList.getErrorList()) {
            logger.debug(mzTabError.toString());
        }
        errorList.clear();
    }

    private void assertError(MZTabErrorType errorType) {
        assertTrue(errorList.getError(errorList.size() - 1).getType() == errorType);
    }

    @Test
    public void testSpectraRef() throws Exception {
        // ms_run[20] not defined in the metadata.
        assertTrue(psmParser.checkSpectraRef(psmFactory.findColumnByHeader("spectra_ref"), "ms_run[20]:index=7|ms_run[2]:index=9").size() == 0);
        assertError(FormatErrorType.SpectraRef);

        // ms_run[3] defined in the metadata, but ms_run[3]-location not provide, so we assume null (unknown).
        //after we allow null, the location is assumed is null and the parser continue parsing, before this change it was an error, now is a warning
        errorList.setLevel(MZTabErrorType.Level.Warn);
        psmParser.metadata.addMsRun(new MsRun(3));
        assertTrue(psmParser.checkSpectraRef(psmFactory.findColumnByHeader("spectra_ref"), "ms_run[3]:index=7|ms_run[2]:index=9").size() == 2);
        assertError(LogicalErrorType.SpectraRef);
    }
}
