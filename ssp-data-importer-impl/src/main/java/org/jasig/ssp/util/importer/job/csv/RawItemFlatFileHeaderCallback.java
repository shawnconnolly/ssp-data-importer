package org.jasig.ssp.util.importer.job.csv;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class RawItemFlatFileHeaderCallback implements FlatFileHeaderCallback {

    String[] columnNames;
    String  delimiter  = ",";

    public RawItemFlatFileHeaderCallback() {
       super();
    }

    @Override
    public void writeHeader(Writer writer) throws IOException {
        StringBuffer header = new StringBuffer();

        for(String columnName:columnNames){
            header.append(columnName).append(delimiter);
        }

        writer.write(StringUtils.chop(header.toString()));
    }


    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }


    public void setDelimiter(String delimiter){
        this.delimiter = delimiter;
    }

}
