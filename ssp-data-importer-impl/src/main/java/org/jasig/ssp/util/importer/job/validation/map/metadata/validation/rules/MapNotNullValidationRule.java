package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.rules;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.DatabaseConstraintMapValidationContext;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapViolation;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.NotNullViolation;

class MapNotNullValidationRule implements MapValueValidationRule {

    @Override
    public void validate(Object propertyValue, MapReference MapReference, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (propertyValue == null && valueIsExpected(MapReference, columnMetadata)) {
            context.addViolation(new NotNullViolation(MapReference, propertyValue));
        }
    }

    private boolean valueIsExpected(MapReference MapReference, ColumnMetadata columnMetadata) {
        return columnMetadata.isRequired() && !isGeneratable(MapReference, columnMetadata);
    }

    private boolean isGeneratable(MapReference MapReference, ColumnMetadata columnMetadata) {
        return columnMetadata.isGeneratable();
    }

}