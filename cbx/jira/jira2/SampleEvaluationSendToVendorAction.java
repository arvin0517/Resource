// Copyright (c) 1998-2017 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.6.1
// ============================================================================
// CHANGE LOG
// CNT.6.1 : 2017-01-12, Nick.Fu, CNT-26894
// CNT.6.1 : 2017-01-10, eason.li, CNT-27409
// CNT.6.1 : 2017-01-06, ives.li, CNT-25615, creation
// ============================================================================
package com.core.cbx.sampleevaluation.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.core.cbx.action.CheckRefEntitiesValidation;
import com.core.cbx.action.actionContext.SaveAndConfirm;
import com.core.cbx.action.constants.BusinessExceptionConstants;
import com.core.cbx.action.constants.BusinessValueConstants;
import com.core.cbx.action.constants.ExcelOperationConstants;
import com.core.cbx.action.exception.ActionException;
import com.core.cbx.action.workerAction.LoadDocWorkerAction;
import com.core.cbx.common.logging.CNTLogger;
import com.core.cbx.common.logging.LogFactory;
import com.core.cbx.common.type.DateTime;
import com.core.cbx.conf.service.SystemConfigManager;
import com.core.cbx.data.constants.NotificationProfile;
import com.core.cbx.data.constants.SampleEvaluation;
import com.core.cbx.data.constants.SampleTracker;
import com.core.cbx.data.constants.User;
import com.core.cbx.data.constants.Vendor;
import com.core.cbx.data.entity.DynamicEntity;
import com.core.cbx.data.exception.DataException;
import com.core.cbx.domain.externalAccess.ExternalAccessModel;
import com.core.cbx.exp.FormEntityExporter;
import com.core.cbx.notification.NotificationModel;
import com.core.cbx.sampleevaluation.util.SampleEvaluationUtil;
import com.core.cbx.security.AuthenticationUtil;
import com.core.cbx.ui.zk.util.CbxUiUtils;
import com.core.cbx.util.FormatUtil;

/**
 * @author ives.li
 *
 */
public class SampleEvaluationSendToVendorAction extends SaveAndConfirmAction implements CheckRefEntitiesValidation {

    private static final String TEMPLATE_ATTACHMENTS_NAME = "exportAttachmentName";
    private static final String KEY_RECIPIENT = "recipient";
    private static final String TEMPLATE_SENDER = "sender";
    private static final String TEMPLATE_SENDER_NAME = "senderName";
    private static final String TEMPLATE_SENDER_EMAIL = "senderEmail";
    private static final String TEMPLATE_SENDER_PHONE = "senderPhone";
    private static final String SAMPLE_ID = "sampleId";
    private static final String SAMPLE_VERSION = "sampleVersion";
    private static final String KEY_SENTON = "sentOn";
    private static final String NOTIFICATION_PROFILE_WITHOUT_VENDOR_ACCESS
        = "Sample Evaluation sending result - successful (without Vendor Access)";
    private static final CNTLogger LOGGER = LogFactory.getLogger(SampleEvaluationSendToVendorAction.class);
    private String attachName = StringUtils.EMPTY;

    @Override
    protected List<String> getByPassFieldPaths() {
        return SampleEvaluationUtil.getByPassFieldPaths();
    }

    @Override
    protected void postprocess(final SaveAndConfirm actionContext) throws ActionException {
        super.postprocess(actionContext);
        final DynamicEntity sampleEvaluation = actionContext.getDoc();
        final DynamicEntity vendor = sampleEvaluation.getEntity(SampleTracker.VENDOR);
        if (vendor != null) {
            final boolean isVendorAccess = ExternalAccessModel.hasExternalAccessRight(vendor.getId());
            if (BooleanUtils.isFalse(isVendorAccess)) {
                try {
                    sendNotification(sampleEvaluation);
                } catch (final DataException e) {
                    throw new ActionException(e.getId(), "Failed to send email to vendor, name="
                            + NOTIFICATION_PROFILE_WITHOUT_VENDOR_ACCESS, e);
                }
            } else {
                SampleEvaluationUtil.sendDoc(sampleEvaluation);
            }
        }
    }

    /**
     * @param sampleEvaluation
     * @throws ActionException
     * @throws DataException
     */
    private void sendNotification(final DynamicEntity sampleEvaluation) throws DataException, ActionException {
        final Map<String, Object> params = buildParams(sampleEvaluation);
        final Map<String, byte[]> attachments = new HashMap<String, byte[]>();
        final List<DynamicEntity> refDocs = new ArrayList<DynamicEntity>();
        final DynamicEntity vendor = sampleEvaluation.getEntity(SampleEvaluation.VENDOR);
        final LoadDocWorkerAction loadDoc = new LoadDocWorkerAction(vendor);
        loadDoc.execute();
        final DynamicEntity vendorFullEntity = loadDoc.getEntity();
        final String vendorBusinessName = vendor.getString(Vendor.BUSINESS_NAME);
        refDocs.add(sampleEvaluation);
        exportEntityForAttachment(vendorFullEntity, sampleEvaluation, attachments);
        params.put(KEY_RECIPIENT, vendorBusinessName);
        String toEmails = sampleEvaluation.getString(SampleEvaluation.VENDOR_EMAIL);
        if (StringUtils.isEmpty(toEmails)) {
            toEmails = vendorFullEntity.getString(Vendor.EMAIL);
        }
        params.put(NotificationProfile.TO_EMAIL, toEmails);
        params.put(TEMPLATE_ATTACHMENTS_NAME, attachName);
        NotificationModel.buildParameter(NOTIFICATION_PROFILE_WITHOUT_VENDOR_ACCESS, sampleEvaluation)
            .attachments(attachments, params).refDocs(refDocs).parameters(params).send();
    }

    /**
     * @param sampleEvaluation
     * @return
     */
    private Map<String, Object> buildParams(final DynamicEntity sampleEvaluation) {
        final Map<String, Object> params = new HashMap<String, Object>();
        final String sentOn = CbxUiUtils.formatDateTime(DateTime.now(), FormatUtil.getInternalDateTimeFormat());
        final DynamicEntity sender = AuthenticationUtil.getUser().getUserEntity();
        final Map<String, Object> detailIdAndVersion = getSampleIdAndVersion(sampleEvaluation);
        if (MapUtils.isNotEmpty(detailIdAndVersion)) {
            params.put(SAMPLE_VERSION, detailIdAndVersion.get(SAMPLE_VERSION));
            params.put(SAMPLE_ID, detailIdAndVersion.get(SAMPLE_ID));
        }
        params.put(KEY_SENTON, sentOn);
        params.put(TEMPLATE_SENDER, sender.getString(User.LOGIN_ID));
        params.put(TEMPLATE_SENDER_NAME, sender.getString(User.USER_NAME));
        params.put(TEMPLATE_SENDER_EMAIL, sender.getString(User.EMAIL));
        params.put(TEMPLATE_SENDER_PHONE, sender.getString(User.PHONE));
        return params;
    }

    private Map<String, Object> getSampleIdAndVersion(final DynamicEntity sampleEvaluation) {
        final Map<String, Object> detailIdAndVersion = new HashMap<String, Object>();
        final DynamicEntity sampleDetail = sampleEvaluation.getEntity(SampleEvaluation.SAMPLE_DETAIL);
        final DynamicEntity materialDetail = sampleEvaluation.getEntity(SampleEvaluation.MATERIAL_DETAIL);
        if (sampleEvaluation.getEntity(SampleEvaluation.SAMPLE_DETAIL) != null) {
            detailIdAndVersion.put(SAMPLE_ID, sampleDetail.getString(SAMPLE_ID));
            detailIdAndVersion.put(SAMPLE_VERSION, sampleDetail.getString(SAMPLE_VERSION));
        } else if (sampleEvaluation.getEntity(SampleEvaluation.MATERIAL_DETAIL) != null) {
            detailIdAndVersion.put(SAMPLE_ID, materialDetail.getString(SAMPLE_ID));
            detailIdAndVersion.put(SAMPLE_VERSION, materialDetail.getString(SAMPLE_VERSION));
        }
        return detailIdAndVersion;
     }

    /**
     * @param vendorFullEntity
     * @param sampleEvaluation
     * @param attachments
     * @throws ActionException
     */
    private void exportEntityForAttachment(final DynamicEntity vendorFullEntity, final DynamicEntity sampleEvaluation,
            final Map<String, byte[]> attachments) throws ActionException {
        final File sampleEvaluationExcel = generateSampleEvaluationExcel(sampleEvaluation, vendorFullEntity);
        writeInMapWithFile(sampleEvaluationExcel, attachments);
    }

    /**
     * @param sampleEvaluationExcel
     * @param attachments
     * @throws ActionException
     */
    private void writeInMapWithFile(final File sampleEvaluationExcel, final Map<String, byte[]> attachments)
            throws ActionException {
        FileInputStream is = null;
        if (sampleEvaluationExcel != null) {
            try {
                is = new FileInputStream(sampleEvaluationExcel);
                attachments.put(sampleEvaluationExcel.getName(), inputStreamToByte(is));
                attachName = sampleEvaluationExcel.getName();
            } catch (final FileNotFoundException e) {
                throw new ActionException(BusinessExceptionConstants.ACTION_EXCEPTION_100001,
                        "Can not find target file", e);
            } catch (final IOException e) {
                throw new ActionException(BusinessExceptionConstants.ACTION_EXCEPTION_100001,
                        "Can not write target file into map", e);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }

    /**
     * @param is
     * @return
     * @throws IOException
     */
    private byte[] inputStreamToByte(final FileInputStream is) throws IOException {
        final ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        try {
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            final byte[] data = bytestream.toByteArray();
            bytestream.close();
            return data;
        } finally {
            IOUtils.closeQuietly(bytestream);
        }
    }

    /**
     * @param sampleEvaluation
     * @param vendorFullEntity
     * @return
     * @throws ActionException
     */
    private File generateSampleEvaluationExcel(final DynamicEntity sampleEvaluation
            , final DynamicEntity vendorFullEntity) throws ActionException {
        final String vendorCode = vendorFullEntity.getString(Vendor.VENDOR_CODE);
        final File sampleEvaluationExcel =
                createExportTemplate(sampleEvaluation.getString(SampleEvaluation.REF_NO), vendorCode);
        exportTargetDoc(sampleEvaluation, sampleEvaluationExcel);
        return sampleEvaluationExcel;
    }

    /**
     * @param sampleEvaluation
     * @param rfiExcel
     * @throws ActionException
     */
    private void exportTargetDoc(final DynamicEntity sampleEvaluation, final File sampleEvaluationExcel)
            throws ActionException {
        try {
            final FormEntityExporter exporter = new FormEntityExporter();
            exporter.exportEntity(sampleEvaluation, sampleEvaluationExcel,
                    AuthenticationUtil.getUser().getCurrentLocale(),
                    AuthenticationUtil.getUserCurrentDomainId());
        } catch (final DataException e) {
            throw new ActionException(BusinessExceptionConstants.ACTION_EXCEPTION_000001,
                    "Can not write docs into file with FormEntityExporter.exportEntity() api", e);
        }
    }

    /**
     * @param string
     * @param vendorCode
     * @return
     */
    private File createExportTemplate(final String refNo, final String vendorCode) {
        final DateTime today = DateTime.now();
        final String templatePath = StringUtils
                .defaultIfBlank(
                        SystemConfigManager.getInstance().getConfigValue(
                                ExcelOperationConstants.ATTR_KEY_EXPORT_TEMPLATE_PATH), File.separator);
        final String path = templatePath + vendorCode + ExcelOperationConstants.FILENAME_SEPARATOR + refNo
                + ExcelOperationConstants.FILENAME_SEPARATOR + today.format(ExcelOperationConstants.DATE_FORMAT)
                + BusinessValueConstants.XLSX_SUFFIX;
        LOGGER.debug("Create an empty excel file, path = " + path);
        return new File(path);
    }
}
