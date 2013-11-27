package org.jasig.ssp.util.importer.job.csv;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.jasig.ssp.util.importer.job.tasklet.BatchFinalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.core.io.Resource;
import org.springframework.validation.BindException;

public class RawItemCsvReader extends FlatFileItemReader<RawItem> implements StepExecutionListener, LineCallbackHandler, FieldSetMapper<RawItem> {

    final private String COLUMN_NAMES_KEY = "COLUMNS_NAMES_KEY";
    private StepExecution stepExecution;
    private DefaultLineMapper<RawItem> lineMapper;
    private String[] columnNames;
    private Resource itemResource;
    Logger logger = LoggerFactory.getLogger(RawItemCsvReader.class);

    public RawItemCsvReader() {
        setLinesToSkip(1);
        setSkippedLinesCallback(this);
    }

    @Override
    public void afterPropertiesSet() {
        // not in constructor to ensure we invoke the override
        final DefaultLineMapper<RawItem> lineMapper = new DefaultLineMapper<RawItem>();
        setLineMapper(lineMapper);
    }

    /**
     * Satisfies {@link LineCallbackHandler} contract and and Acts as the {@code skippedLinesCallback}.
     *
     * @param line
     */
    @Override
    public void handleLine(String line) {
        getLineMapper().setLineTokenizer(getTokenizer(line));
        getLineMapper().setFieldSetMapper(this);
    }

    private LineTokenizer getTokenizer(String line){
        this.columnNames = line.split(DelimitedLineTokenizer.DELIMITER_COMMA);
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setQuoteCharacter(DelimitedLineTokenizer.DEFAULT_QUOTE_CHARACTER);
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(columnNames);
        stepExecution.getExecutionContext().put(COLUMN_NAMES_KEY, columnNames);
        return lineTokenizer;
    }

    @Override
    public void setResource(Resource resource){
        //No longer using MultiResource Reader
        this.itemResource = resource;
        super.setResource(resource);
    }

    /**
     * Provides acces to an otherwise hidden field in parent class. We need this because we have to reconfigure
     * the {@link LineMapper} based on file contents.
     * @param lineMapper
     */
    @Override
    public void setLineMapper(LineMapper<RawItem> lineMapper) {
        if ( !(lineMapper instanceof DefaultLineMapper) ) {
            throw new IllegalArgumentException("Must specify a DefaultLineMapper");
        }
        this.lineMapper = (DefaultLineMapper)lineMapper;
        super.setLineMapper(lineMapper);
    }

    private DefaultLineMapper getLineMapper() {
        return this.lineMapper;
    }

    /**
     * Satisfies {@link FieldSetMapper} contract.
     * @param fs
     * @return
     * @throws BindException
     */
    @Override
    public RawItem mapFieldSet(FieldSet fs) throws BindException {
        if ( fs == null ) {
            return null;
        }
        Map<String,String> record = new LinkedHashMap<String, String>();
        for (String columnName : this.columnNames) {
            record.put(columnName, StringUtils.trimToNull(fs.readString(columnName)));
        }
        RawItem item = new RawItem();
        item.setResource(itemResource);
        item.setRecord(record);
        // TODO for now we're not worrying about setting the Resource b/c we happen to know the wrapping
        // MultiResourceItemReader will do it for us and there's no accessible getter on our super class. But
        // would be better to do it here.
        return item;
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // TODO Auto-generated method stub

        logger.info("Start Raw Read Step for " + itemResource.getFilename());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("End Raw Read Step for " + itemResource.getFilename() +
                "lines read: " +
                stepExecution.getReadCount() +
                "lines skipped: " + stepExecution.getReadSkipCount());
        return ExitStatus.COMPLETED;
    }
}
