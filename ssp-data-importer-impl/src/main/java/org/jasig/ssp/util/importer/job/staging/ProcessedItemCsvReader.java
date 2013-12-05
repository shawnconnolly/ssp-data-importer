/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.util.importer.job.staging;

import org.apache.commons.lang3.StringUtils;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.core.step.item.ChunkProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.validation.BindException;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProcessedItemCsvReader extends FlatFileItemReader<RawItem> implements LineCallbackHandler, FieldSetMapper<RawItem> {

    private DefaultLineMapper<RawItem> lineMapper;
    private String[] columnNames;

    public ProcessedItemCsvReader() {
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
        return lineTokenizer;
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
        item.setRecord(record);
        // TODO for now we're not worrying about setting the Resource b/c we happen to know the wrapping
        // MultiResourceItemReader will do it for us and there's no accessible getter on our super class. But
        // would be better to do it here.
        return item;
    }
}