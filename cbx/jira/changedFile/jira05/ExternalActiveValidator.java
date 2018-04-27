// Copyright (c) 1998-2017 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.6.5.0
// ============================================================================
// CHANGE LOG
// CNT.6.5.0 : 2017-05-15, lance.lu, CNT-30506
// CNT.6.3.0 : 2017-03-08, Nick.Fu, CNT-28631
// ============================================================================
package com.core.cbx.validation.validator;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.core.cbx.data.DynamicEntityModel;
import com.core.cbx.data.def.EntityDefManager;
import com.core.cbx.data.def.entity.FieldDefinition;
import com.core.cbx.data.entity.DynamicEntity;
import com.core.cbx.data.entity.EntityConstants;
import com.core.cbx.data.exception.DataException;
import com.core.cbx.validation.ValidationException;
import com.core.cbx.validation.entity.ValidationError;

/**
 * @author Nick.Fu
 */
public class ExternalActiveValidator extends AbstractValidator {
    private static final String ERROR_MESSAGE_PART_1 = "Recipient(s) of the document you are trying to send "
            + "is disconnected from your domain." + "\n";
    private static final String ERROR_MESSAGE_PART_2 = "Please set the related vendor as Active and try "
            + "again or remove the disconnected vendor from the document.";
    private static StringBuffer vendorCodeList;
    private static final String HEADER_MESSAGE = "The recipient of the document you are trying to send is "
            + "disconnected from your domain. Please set the related vendor as Active and try again";
    private static final String HEADER_ERROR_ID = "08010005";

    private static final String MODULE_ID = "vendor";
    private static final String VENDOR_ID = "vendorId";
    private static final String ENTITY_NAME_VENDOR = "Vendor";
    private static final String VENDOR_CODE = "vendorCode";
    private static final String ENTITY_NAME_FACT = "Fact";
    private static final String VENDOR_FACT = "vendorFact";
    private static final String ENTITY_NAME_FACT_AUDIT = "FactAudit";

    /*
     * (non-Javadoc)
     * @see com.core.cbx.validation.validator.Validator#validate()
     */
    @Override
    public ValidationError validate() throws ValidationException {
        boolean result = true;
        validationError = null;
        if (currentEntity != null) {
            FieldDefinition fieldDef;
            try {
                fieldDef = EntityDefManager.getFieldDefinition(entityName,
                        EntityDefManager.getLatestEntityVersion(entityName), fieldId);

                if (fieldDef != null && StringUtils.isNotEmpty(fieldId)) {
                    vendorCodeList = new StringBuffer();
                    final Object fieldObj = currentEntity.get(fieldId);
                    fieldData = fieldObj;
                    if (fieldObj != null) {
                        if (fieldObj instanceof Collection) {
                            errorId = "CustomizedErrorId";
                            @SuppressWarnings("unchecked")
                            final Collection<DynamicEntity> children = (Collection<DynamicEntity>) fieldObj;
                            for (final DynamicEntity child : children) {
                                if (!child.isDeletedEntity()) {
                                    DynamicEntity vendor;
                                    String vendorId = null;
                                    vendor = child.getEntity(MODULE_ID);
                                    if (vendor == null) {
                                        vendor = child.getEntity(VENDOR_ID);
                                    }
                                    if (vendor == null) {
                                        vendorId = child.getString(VENDOR_ID);
                                    } else {
                                        vendorId = vendor.getId();
                                    }
                                    final DynamicEntity vendorEntity = DynamicEntityModel
                                            .getEntityHeaderById(ENTITY_NAME_VENDOR, vendorId);
                                    if (StringUtils.equals(EntityConstants.DocStatus.INACTIVE,
                                            vendorEntity.getString(EntityConstants.PTY_DOC_STATUS))) {
                                        vendorCodeList.append("-" + vendorEntity.getString(VENDOR_CODE) + "\n");
                                        result = Boolean.FALSE;
                                    }
                                }
                            }

                        } else if (fieldObj instanceof DynamicEntity) {
                            errorId = HEADER_ERROR_ID;
                            final DynamicEntity entity = (DynamicEntity) fieldObj;
                            if (!entity.isDeletedEntity()) {
                                if(!StringUtils.equals(entityName, ENTITY_NAME_FACT_AUDIT)){
                                    final DynamicEntity vendorEntity = DynamicEntityModel
                                            .getEntityHeaderById(ENTITY_NAME_VENDOR, entity.getId());
                                    if (StringUtils.equals(EntityConstants.DocStatus.INACTIVE,
                                            vendorEntity.getString(EntityConstants.PTY_DOC_STATUS))) {
                                        result = Boolean.FALSE;
                                    }

                                }
                            }
                        } else if (fieldObj instanceof String) {
                            errorId = HEADER_ERROR_ID;
                            final String status = (String) fieldObj;
                            if (StringUtils.equals(EntityConstants.DocStatus.INACTIVE, status)) {
                                result = Boolean.FALSE;
                            }
                        }
                    }

                    if (!result) {
                        buildValidationError();
                    }
                }
            } catch (final DataException e) {
                throw new ValidationException("Cannot validate external active", e);
            }
        }
        return validationError;
    }

    @Override
    protected String getErrorMessage() {
        return ERROR_MESSAGE_PART_1 + vendorCodeList + ERROR_MESSAGE_PART_2;
    }

}
