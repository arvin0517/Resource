// Copyright (c) 1998-2017 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.6.7
// ============================================================================
// CHANGE LOG
// CNT.6.7 : 2017-07-13, stephen.lai, CNT-30530
// CNT.6.4 : 2017-04-06, charles.tong, CNT-29429
// CNT.6.3 : 2017-03-08, kulilin.xu, CNT-28859
// CNT.6.3 : 2017-02-27, anthony.chen, CNT-28677
// CNT.6.2 : 2017-02-17, connor.zeng, CNT-28472
// CNT.6.1 : 2017-01-19, Fiona.lin, CNT-27989
// CNT.6.1 : 2017-01-13, terry.chen, CNT-27897
// CNT.6.1 : 2017-01-10, eason.li, CNT-27260
// CNT.6.1 : 2017-01-05, terry.chen, CNT-27631
// CNT.6.0b : 2016-10-14, dato.zeng, CNT-26038
// CNT.5.15.0  : 2016-06-08, anthony.chen, CNT-23382
// CNT.5.14.0 : 2016-05-17, damon.huang, CNT-22946
// CNT.5.13.0 : 2016-04-07, gary.mo, CNT-22333
// CNT.5.12.0 : 2016-03-14, lance.lu, CNT-21914
// CNT.5.12.0 : 2016-02-22, Nick.Fu, CNT-20494
// CNT.5.12.0: 2016-02-22, kulilin.xu, CNT-21446
// CNT.5.11.0: 2016-01-14, frank.long, CNT-20998
// CNT.5.11.0 : 2015-12-24, lance.lu, CNT-20394
// CNT.5.10GA : 2015-09-18, Ace.Li, CNT-19580
// CNT.5.10 GA : 2015-09-10, lennox.peng, CNT-19414
// CNT.5.10 GA : 2015-07-28, bosto.li, CNT-18501
// CNT.5.9 GA : 2015-06-03, colin.guo, CNT-17979
// CNT.5.9 GA : 2015-06-03, colin.guo, CNT-17986
// CNT.5.9 GA : 2015-05-15, colin.guo, CNT-17517
// CNT.5.9 GA : 2015-05-15, bosto.li, CNT-17318
// CNT.5.9 GA : 2015-05-08, bosto.li, CNT-17349
// CNT.5.8.0 : 2015-01-26, winnie.yang, CNT-14720
// CNT.5.0.074 : 2014-12-12, denny.deng, CNT-15501
// CNT.5.0.074 : 2014-12-05, ivan.lo, CNT-14000
// CNT.5.0.073 : 2014-11-06, denny.deng, CNT-15064
// CNT.5.0.072 : 2014-10-24, calvin.yang, creation for CNT-14851
// ============================================================================

package com.core.cbx.samplerequest.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.core.cbx.action.CheckRefEntitiesValidation;
import com.core.cbx.action.SaveAndConfirmAction;
import com.core.cbx.action.actionContext.DataInheritance;
import com.core.cbx.action.actionContext.InheritResponsibleParties;
import com.core.cbx.action.actionContext.SaveAndConfirm;
import com.core.cbx.action.exception.ActionException;
import com.core.cbx.action.inheritance.InheritanceTask;
import com.core.cbx.action.workerAction.DataInheritanceWorkAction;
import com.core.cbx.action.workerAction.InheritResponsiblePartiesWorkAction;
import com.core.cbx.action.workerAction.SendDocumentWorkerAction;
import com.core.cbx.common.type.DateTime;
import com.core.cbx.conf.service.SystemConfigManager;
import com.core.cbx.data.DynamicEntityModel;
import com.core.cbx.data.constants.Item;
import com.core.cbx.data.constants.NotificationProfile;
import com.core.cbx.data.constants.SampleRequest;
import com.core.cbx.data.constants.SampleTracker;
import com.core.cbx.data.constants.Selection;
import com.core.cbx.data.constants.User;
import com.core.cbx.data.constants.Vendor;
import com.core.cbx.data.entity.DynamicEntity;
import com.core.cbx.data.entity.EntityConstants;
import com.core.cbx.data.exception.DataException;
import com.core.cbx.data.search.Criterion;
import com.core.cbx.data.search.Restriction;
import com.core.cbx.domain.externalAccess.ExternalAccessModel;
import com.core.cbx.logging.CNTLogger;
import com.core.cbx.logging.LogFactory;
import com.core.cbx.notification.NotificationModel;
import com.core.cbx.notification.NotificationParameter.Builder;
import com.core.cbx.sampletracker.helper.DefaultSampleTrackerHelper;
import com.core.cbx.sampletracker.helper.SampleTrackerHelper;
import com.core.cbx.security.AuthenticationUtil;
import com.core.cbx.ui.constants.BusinessInheritanceConstants;
import com.core.cbx.ui.zk.action.UIAction;
import com.core.cbx.ui.zk.component.MainWin;
import com.core.cbx.ui.zk.composer.BaseFormComposer;
import com.core.cbx.ui.zk.util.CbxUiUtils;
import com.core.cbx.util.FormatUtil;

/**
 * @author calvin.yang
 */
public class SendSampleRequestAction extends SaveAndConfirmAction implements CheckRefEntitiesValidation {

    private static final CNTLogger LOGGER = LogFactory.getLogger(SendSampleRequestAction.class);
    private static final String REQUESTED_BY = "requestedBy";
    private static final String REQUESTED_ON = "requestedOn";
    private static final String RECIPIENT_NAME = "recipientName";
    private static final String CBX_SUPPORT_EMAIL = "cbx.support.email";
    private static final String SUPPORT_EAMIL = "supportEmail";
    private static final String NOTIFICATION_PROFILE =
            "Sample Request Notification Profile - successful (without Vendor access)";
    private static final String NOTIFICATION_PROFILE_ST =
            "Sample Request ST Notification Profile - success (without vendor access)";
    private static final String REF_DOCS_WITHIN_MAIN_ENTITY = "refDocsWithinMainEntity";
    private static final String SENDER_NAME = "senderName";
    private static final String SENDER_EMAIL = "senderEmail";
    private static final String SENDER_PHONE = "senderPhone";
    private static final String IS_FROM_REQUEST = "isFromRequest";
    // If not exists ST, no send the notification
    private static final String IS_NOT_SEND = "isNotSend";
    private boolean isFromSampleTracker = false;
    private boolean hasVendorAccess = false;
    private final List<DynamicEntity> sampleTrackers = new ArrayList<DynamicEntity>();
    private static final String SAMPLE_TRACKER_FIELD_PATH = "entity.sampleTracker";

    /*
     * (non-Javadoc)
     * @see com.core.cbx.action.BaseAction#preprocess(com.core.cbx.action.BaseActionContext)
     */
    @Override
    protected void preprocess(final SaveAndConfirm actionContext) throws ActionException {
        super.preprocess(actionContext);
        final DynamicEntity sampleRequest = actionContext.getDoc();
        final Object openInSampleTracker = sampleRequest.get(SampleRequest.IS_FROM_SAMPLE_TRACKER);
        if (openInSampleTracker != null && openInSampleTracker instanceof Boolean) {
            this.isFromSampleTracker = (Boolean) openInSampleTracker;
        }
        final Collection<DynamicEntity> sampleReqeustDetails = sampleRequest
                .getEntityCollection(SampleRequest.SAMPLE_REQUEST_DETAIL);
        final Collection<DynamicEntity> materalReqeustDetails = sampleRequest
                .getEntityCollection(SampleRequest.MATERIAL_REQUEST_DETAIL);
        final Collection<DynamicEntity> docReqeustDetails = sampleRequest
                .getEntityCollection(SampleRequest.DOCUMENT_REQUEST_DETAIL);
        replenishSampleVersion(sampleReqeustDetails);
        replenishSampleVersion(materalReqeustDetails);
        replenishSampleVersion(docReqeustDetails);
    }

    /* (non-Javadoc)
     * @see com.core.cbx.action.BaseAction#getByPassFieldPaths()
     */
    @Override
    protected List<String> getByPassFieldPaths() {
        final List<String> passFieldPathList = super.getByPassFieldPaths();
        passFieldPathList.add(SAMPLE_TRACKER_FIELD_PATH);
        return passFieldPathList;
    }

    private void replenishSampleVersion(final Collection<DynamicEntity> collection) {
        if (!CollectionUtils.isEmpty(collection)) {
            for (final DynamicEntity entity : collection) {
                final String sampleVersion = entity.getString(SampleRequest.SAMPLE_VERSION);
                entity.put(SampleRequest.SAMPLE_VERSION, StringUtils.isBlank(sampleVersion) ? "1" : sampleVersion);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.core.cbx.action.BaseAction#process(com.core.cbx.action.BaseActionContext)
     */
    @Override
    protected void process(final SaveAndConfirm actionContext) throws ActionException {
        super.process(actionContext);
        final DynamicEntity sampleRequest = actionContext.getDoc();

        final Collection<DynamicEntity> sampleRequestItems = sampleRequest
                .getEntityCollection(SampleRequest.SAMPLE_REQUEST_ITEM);
        final Collection<DynamicEntity> sampleRequestVendors = sampleRequest
                .getEntityCollection(SampleRequest.SAMPLE_REQUEST_VENDOR);
        final Collection<DynamicEntity> responsibleParties = sampleRequest.getEntityCollection(SampleRequest.PARTIES);

        if (!isFromSampleTracker) {
            if (!CollectionUtils.isEmpty(sampleRequestVendors) && !CollectionUtils.isEmpty(sampleRequestItems)) {
                for (final DynamicEntity sampleRequestVendor : sampleRequestVendors) {
                    for (final DynamicEntity sampleRequestItem : sampleRequestItems) {
                        buildSampleTrackerWithSampleReq(sampleRequestItem, sampleRequestVendor, sampleRequest);
                    }
                }
            } else if (!CollectionUtils.isEmpty(sampleRequestItems)) {
                for (final DynamicEntity sampleRequestItem : sampleRequestItems) {
                    buildSampleTrackerWithSampleReq(sampleRequestItem, null, sampleRequest);
                }
            }
        } else {
            final BaseFormComposer sampleTrackerComposer = (BaseFormComposer) MainWin.getCurrent().getMainPanel()
                    .getAttribute(UIAction.ACT_CTX_COMPOSER);
            final DynamicEntity sampleTrackerDoc = (DynamicEntity) sampleTrackerComposer.getDatasource();
            saveSampleTrackerWithSampleRequest(sampleRequest, sampleTrackerDoc);
            sampleTrackerDoc.putValue(IS_FROM_REQUEST, true);
            sampleTrackers.add(sampleTrackerDoc);
        }
        sendNotification(responsibleParties, sampleRequestVendors, sampleRequest);
    }

    /**
     * @param sampleRequestItem
     * @param sampleRequest
     * @param sampleRequest2
     * @throws ActionException
     */
    private void buildSampleTrackerWithSampleReq(final DynamicEntity sampleRequestItem,
            final DynamicEntity sampleRequestVendor,
            final DynamicEntity sampleRequest) throws ActionException {
        final DynamicEntity existSampleTracker = DefaultSampleTrackerHelper.getSampleTrackerByRelatedSampelReq(
                sampleRequestVendor, sampleRequestItem);
        if (existSampleTracker != null) {
            if (existSampleTracker.getEntity(SampleTracker.FACTORY) == null && sampleRequestVendor != null) {
                existSampleTracker.put(SampleTracker.FACTORY, sampleRequestVendor.getEntity(SampleRequest.FACTORY));
            }
            saveSampleTrackerWithSampleRequest(sampleRequest, existSampleTracker);
            existSampleTracker.putValue(IS_FROM_REQUEST, true);
            sampleTrackers.add(existSampleTracker);
        } else {
            DynamicEntity sampleTracker = null;
            sampleTracker = sampleReqVendorToST(sampleRequestVendor, sampleTracker);
            buildSampleTracker(sampleRequestItem, sampleTracker, sampleRequest);
        }
    }

    private DynamicEntity saveSampleTrackerWithSampleRequest(
            final DynamicEntity sampleRequest, DynamicEntity sampleTrackerDoc) throws ActionException {
        sampleRequest.put(SampleTracker.REQUESTED_USER, AuthenticationUtil.getUser().getUserEntity());
        sampleRequest.put(SampleTracker.REQUESTED_DATE, DateTime.today());
        sampleTrackerDoc = sampleRequestSTInheriteToSampleTracker(sampleRequest, sampleTrackerDoc);
        sampleRequest.remove(SampleTracker.REQUESTED_USER);
        sampleRequest.remove(SampleTracker.REQUESTED_DATE);

        final SampleTrackerHelper helper = DefaultSampleTrackerHelper.getInstance();
        helper.lockSampleTrackerDoc(sampleTrackerDoc);
        try {
            helper.setSampleTracker(sampleTrackerDoc);
            if (StringUtils.equals(sampleTrackerDoc.getString(SampleTracker.EDITING_STATUS),
                    SampleTracker.WorkflowStatus.CONFIRMED)) {
                helper.amendEntity(sampleTrackerDoc);
            }
            helper.saveSampleEvaluationProcess();
            helper.confirmEntity(sampleTrackerDoc);
            helper.saveEntity(sampleTrackerDoc);
            helper.updateDetailofSampleEvaluation();
            helper.confirmSampleEvaluation(sampleTrackerDoc);
            helper.unlockSampleTrackerDoc(sampleTrackerDoc);
        } catch (final ActionException e) {
            throw e;
        } finally {
            helper.unlockSampleEvaluationProcess();
        }
        return sampleTrackerDoc;
    }

    private DynamicEntity sampleRequestSTInheriteToSampleTracker(final DynamicEntity sampleRequestST,
            final DynamicEntity sampleTracker) throws ActionException {
        final DataInheritance inheritanceCtx = new DataInheritance();
        final InheritanceTask task = new InheritanceTask(
                BusinessInheritanceConstants.INHERITANCE_ACTION_ID_SAMPLE_REQ_ST_TO_SAMPLE_TRACKER, sampleRequestST,
                sampleTracker, true);
        inheritanceCtx.addInheritanceTask(task);
        final DataInheritanceWorkAction dataInheritanceWorkAction = new DataInheritanceWorkAction(inheritanceCtx);
        dataInheritanceWorkAction.execute();
        return task.getResultEntity();
    }

    /**
     * @param sampleRequestVendor
     * @param sampleTracker
     * @param sampleRequest
     * @param sampleRequest2
     * @throws ActionException
     */
    private void buildSampleTracker(final DynamicEntity sampleRequestItem,
            final DynamicEntity sampleTracker, final DynamicEntity sampleRequest) throws ActionException {
        DynamicEntity sTToSave = null;
        if (sampleTracker != null) {
            sTToSave = sampleTracker.copy();
        }
        final DynamicEntity cloneSampleRequest = sampleRequest.clone();
        if (sampleRequestItem != null) {
            handleDetailByItem(cloneSampleRequest, sampleRequestItem, sampleRequest);
            sTToSave = sampleReqItemToST(sampleRequestItem, sTToSave);
        }
        sTToSave = sampleReqToSTWithoutVendorAndItem(cloneSampleRequest, sTToSave);
        resetLatestRefEntities(sTToSave);
        inheriteResponsibleParties(sampleRequest, sTToSave);
        if (StringUtils.isBlank(sTToSave.getString(EntityConstants.PTY_STATUS))) {
            sTToSave.putValue(EntityConstants.PTY_STATUS, SampleTracker.WorkflowStatus.DRAFT);
        }
        final SampleTrackerHelper helper = DefaultSampleTrackerHelper.getInstance();
        try {
            helper.setSampleTracker(sTToSave);
            helper.saveSampleEvaluationProcess();
            helper.confirmEntity(sTToSave);
            helper.saveEntity(sTToSave);
            helper.updateDetailofSampleEvaluation();
            helper.confirmSampleEvaluation(sTToSave);
        } catch (final ActionException e) {
            throw e;
        } finally {
            helper.unlockSampleEvaluationProcess();
        }
        sTToSave.putValue(IS_FROM_REQUEST, true);
        sampleTrackers.add(sTToSave);
    }


    private void inheriteResponsibleParties(final DynamicEntity srcEntity, final DynamicEntity destEntity)
            throws ActionException {
        final InheritResponsibleParties actionContext = new InheritResponsibleParties(srcEntity, destEntity);
        final InheritResponsiblePartiesWorkAction inheritResponsiblePartiesWorkAction =
                new InheritResponsiblePartiesWorkAction(actionContext);
        inheritResponsiblePartiesWorkAction.execute();
    }

    private void handleDetailByItem(final DynamicEntity cloneSampleRequest,
            final DynamicEntity sampleRequestItem, final DynamicEntity sampleRequest) {
        final Collection<DynamicEntity> sampleRequestDetails = sampleRequest
                .getEntityCollection(SampleRequest.SAMPLE_REQUEST_DETAIL);
        final Collection<DynamicEntity> materialRequestDetails = sampleRequest
                .getEntityCollection(SampleRequest.MATERIAL_REQUEST_DETAIL);
        final Collection<DynamicEntity> documentRequestDetails = sampleRequest
                .getEntityCollection(SampleRequest.DOCUMENT_REQUEST_DETAIL);

        final List<DynamicEntity> newSampleRequestDetails = new ArrayList<DynamicEntity>();
        final List<DynamicEntity> newMaterialRequestDetails = new ArrayList<DynamicEntity>();
        final List<DynamicEntity> newDocumentRequestDetails = new ArrayList<DynamicEntity>();

        filerDetailByItem(sampleRequestItem, sampleRequestDetails, newSampleRequestDetails);
        filerDetailByItem(sampleRequestItem, materialRequestDetails, newMaterialRequestDetails);
        filerDetailByItem(sampleRequestItem, documentRequestDetails, newDocumentRequestDetails);

        cloneSampleRequest.put(SampleRequest.SAMPLE_REQUEST_DETAIL, newSampleRequestDetails);
        cloneSampleRequest.put(SampleRequest.MATERIAL_REQUEST_DETAIL, newMaterialRequestDetails);
        cloneSampleRequest.put(SampleRequest.DOCUMENT_REQUEST_DETAIL, newDocumentRequestDetails);
    }

    private void resetLatestRefEntities(final DynamicEntity sampleTracker) throws ActionException {
        resetLatestItem(sampleTracker);
//        resetLatestSpec(sampleTracker);

    }

    private void resetLatestItem(final DynamicEntity sampleTracker) throws ActionException {
        final DynamicEntity item = sampleTracker.getEntity(SampleTracker.ITEM);
        if (item != null) {
            final Boolean isLatestItem = item.getBoolean(EntityConstants.PTY_IS_LATEST);
            if (BooleanUtils.isFalse(isLatestItem)) {
                try {
                    final Criterion criterion = new Criterion(item.getEntityName());
                    criterion.addRestriction(Restriction.eq(EntityConstants.PTY_REF_NO, item.getReference()));
                    criterion.addRestriction(Restriction.eq(EntityConstants.PTY_IS_LATEST, Boolean.TRUE));
                    final DynamicEntity latestItem = DynamicEntityModel.findUniqueBy(criterion, false);
                    sampleTracker.putValue(SampleTracker.ITEM, latestItem);
                } catch (final DataException e) {
                    throw new ActionException(e.getId(), "Fail to reset latest item into sampleTracker, item.refNo="
                            + item.getReference(), e);
                }

            }
        }
    }

//    private void resetLatestSpec(final DynamicEntity sampleTracker) throws ActionException {
//        final DynamicEntity spec = sampleTracker.getEntity(SampleTracker.SPEC);
//        if (spec != null) {
//            final Boolean isLatestSpec = spec.getBoolean(EntityConstants.PTY_IS_LATEST);
//            if (BooleanUtils.isFalse(isLatestSpec)) {
//                try {
//                    final Criterion criterion = new Criterion(spec.getEntityName());
//                    criterion.addRestriction(Restriction.eq(EntityConstants.PTY_REF_NO, spec.getReference()));
//                    criterion.addRestriction(Restriction.eq(EntityConstants.PTY_IS_LATEST, Boolean.TRUE));
//                    final DynamicEntity latestSepc = DynamicEntityModel.findUniqueBy(criterion, false);
//                    sampleTracker.putValue(SampleTracker.SPEC, latestSepc);
//                } catch (final DataException e) {
//                    throw new ActionException(e.getId(), "Fail to reset latest spec into sampleTracker, spec.refNo="
//                            + spec.getReference(), e);
//                }
//
//            }
//        }
//    }

    protected void sendNotification(final Collection<DynamicEntity> responsibleParties,
            final Collection<DynamicEntity> sampleRequestVendors, final DynamicEntity sampleRequest)
                    throws ActionException {

        if (!CollectionUtils.isEmpty(sampleRequestVendors)) {
            for (final DynamicEntity sampleRequestVendor : sampleRequestVendors) {
                final DynamicEntity vendor = sampleRequestVendor.getEntity(SampleRequest.VENDOR);
                final String companyName = vendor.getString(Vendor.BUSINESS_NAME);
                final boolean isVendorAccess = ExternalAccessModel.hasExternalAccessRight(vendor.getId());
                final Map<String, Object> params = new HashMap<String, Object>();
                if (isVendorAccess) {
                    hasVendorAccess = true;
                    LOGGER.debug("Vender is a exteranl user: " + companyName);
                    LOGGER.debug("Send the notification with vendor access.");
                    LOGGER.debug("Send the notification when Vendor receive the doc.");
                } else {
                    buildParams(sampleRequest, sampleRequestVendor, params);
                    params.put(RECIPIENT_NAME, companyName);
                    LOGGER.debug("Vender is not a exteranl user: " + companyName);
                    LOGGER.debug("Send the notification without vendor access.");
                    sendNotificationWithoutVendorAccess(sampleRequest, params, sampleRequestVendor);
                }
            }
        }
    }

    private void buildParams(final DynamicEntity sampleRequest, final DynamicEntity vendor,
            final Map<String, Object> params) {
        final boolean isCopyToMyself = sampleRequest.getBoolean(SampleRequest.IS_COPY_TO_MYSELF);
        final String date = CbxUiUtils.formatDateTime(DateTime.now(), FormatUtil.getInternalDateTimeFormat());
        final String cbxSupportEmail = SystemConfigManager.getInstance().getConfigValue(CBX_SUPPORT_EMAIL);

        final String email = vendor.getString(SampleRequest.EMAIL);
        params.put(NotificationProfile.TO_EMAIL, email);

        params.put(REQUESTED_ON, date);
        params.put(REQUESTED_BY, AuthenticationUtil.getUser().getWorkingDomainId());
        params.put(SUPPORT_EAMIL, cbxSupportEmail);
        params.put(SENDER_NAME, AuthenticationUtil.getUser().getUserEntity().getString(User.USER_NAME));
        params.put(SENDER_EMAIL, AuthenticationUtil.getUser().getEmail());
        params.put(SENDER_PHONE, AuthenticationUtil.getUser().getUserEntity().getString(User.PHONE, ""));
        if (isCopyToMyself) {
            params.put(NotificationProfile.CC_EMAIL, AuthenticationUtil.getUser().getEmail());
        }
    }

    private void sendNotificationWithoutVendorAccess(final DynamicEntity sampleRequest,
            final Map<String, Object> params, final DynamicEntity sampleRequestVendor) throws ActionException {

        if (!isFromSampleTracker) {
            final List<DynamicEntity> sampleTrackersWithSameVendor = new ArrayList<DynamicEntity>();
            final DynamicEntity vendor = sampleRequestVendor.getEntity(SampleRequest.VENDOR);
            for (final DynamicEntity sampleTracker : sampleTrackers) {
                final DynamicEntity sampleTrackerVendor = sampleTracker.getEntity(SampleTracker.VENDOR);
                if (sampleTrackerVendor.getId().equals(vendor.getId())) {
                    sampleTrackersWithSameVendor.add(sampleTracker);
                }
            }
            sampleRequest.put(REF_DOCS_WITHIN_MAIN_ENTITY, sampleTrackersWithSameVendor);
        } else {
            sampleRequest.put(REF_DOCS_WITHIN_MAIN_ENTITY, sampleTrackers);
        }

        try {
            String notificationProfileName = NOTIFICATION_PROFILE;
            if (isFromSampleTracker) {
                notificationProfileName = NOTIFICATION_PROFILE_ST;
            }
            final Builder builder = NotificationModel.buildParameter(notificationProfileName, sampleRequest);
            builder.parameters(params).send();
        } catch (final DataException e) {
            LOGGER.warn("Notify fail", "Fail to send notification without vendor access", e);
        }
    }

    protected DynamicEntity sampleReqVendorToST(final DynamicEntity sampleRequestVendor,
            final DynamicEntity draftSampleTracker) throws ActionException {
        if (sampleRequestVendor == null) {
            return draftSampleTracker;
        }
        final DataInheritance inheritanceContext = new DataInheritance();
        final InheritanceTask task = new InheritanceTask(
                BusinessInheritanceConstants.INHERITANCE_ACTION_ID_SAMPLE_REQ_VENDOR_TO_SAMPLE_TRACKER,
                sampleRequestVendor, draftSampleTracker, true);
        inheritanceContext.addInheritanceTask(task);
        final DataInheritanceWorkAction dataInheritanceWorkAction = new DataInheritanceWorkAction(inheritanceContext);
        dataInheritanceWorkAction.execute();
        final DynamicEntity sampleTracker = task.getResultEntity();
        return sampleTracker;
    }

    protected DynamicEntity sampleReqItemToST(final DynamicEntity sampleRequestItem,
            final DynamicEntity draftSampleTracker) throws ActionException {
        final DataInheritance inheritanceContext = new DataInheritance();
        final InheritanceTask task = new InheritanceTask(
                BusinessInheritanceConstants.INHERITANCE_ACTION_ID_SAMPLE_REQ_ITEM_TO_SAMPLE_TRACKER,
                sampleRequestItem, draftSampleTracker, true);
        inheritanceContext.addInheritanceTask(task);
        final DataInheritanceWorkAction dataInheritanceWorkAction = new DataInheritanceWorkAction(inheritanceContext);
        dataInheritanceWorkAction.execute();
        final DynamicEntity sampleTracker = task.getResultEntity();
        return sampleTracker;
    }

    protected DynamicEntity sampleReqToSTWithoutVendorAndItem(final DynamicEntity cloneSampleRequest,
            final DynamicEntity draftSampleTracker) throws ActionException {
        cloneSampleRequest.put(SampleTracker.REQUESTED_USER, AuthenticationUtil.getUser().getUserEntity());
        cloneSampleRequest.put(SampleTracker.REQUESTED_DATE, DateTime.today());
        final DataInheritance inheritanceContext = new DataInheritance();
        final InheritanceTask task = new InheritanceTask(
                BusinessInheritanceConstants.INHERITANCE_ACTION_ID_SAMPLE_REQ_CREATE_SAMPLE_TRACKER,
                cloneSampleRequest, draftSampleTracker, true);
        inheritanceContext.addInheritanceTask(task);
        final DataInheritanceWorkAction dataInheritanceWorkAction = new DataInheritanceWorkAction(inheritanceContext);
        dataInheritanceWorkAction.execute();
        final DynamicEntity sampleTracker = task.getResultEntity();
        return sampleTracker;
    }

    private void filerDetailByItem(final DynamicEntity sampleRequestItem, final Collection<DynamicEntity> details,
            final Collection<DynamicEntity> newdetails) {
        final DynamicEntity item1 = sampleRequestItem.getEntity(SampleRequest.ITEM);
        if (item1 != null) {
            final String itemRefNo1 = item1.getString(Item.REF_NO);
            for (final DynamicEntity detail : details) {
                final Collection<DynamicEntity> items = detail.getEntityCollection(SampleRequest.ITEMS);
                for (final DynamicEntity selectionItem : items) {
                    final DynamicEntity item2 = selectionItem.getEntity(Selection.REF);
                    final String itemRefNo2 = item2.getString(Item.REF_NO);
                    if (StringUtils.equalsIgnoreCase(itemRefNo1, itemRefNo2)) {
                        newdetails.add(detail);
                        break;
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.core.cbx.action.BaseAction#postprocess(com.core.cbx.action.BaseActionContext)
     */
    @Override
    protected void postprocess(final SaveAndConfirm actionContext) throws ActionException {
        super.postprocess(actionContext);
        for (final DynamicEntity sampleTracker : sampleTrackers) {
            final DynamicEntity vendor = sampleTracker.getEntity(SampleTracker.VENDOR);
            if (vendor != null) {
                final boolean isVendorAccess = ExternalAccessModel.hasExternalAccessRight(vendor.getId());
                if (isVendorAccess) {
                    final Boolean isFromRequest = sampleTracker.getBoolean(IS_FROM_REQUEST);
                    updateSampleTracker(sampleTracker);
                    DefaultSampleTrackerHelper.getInstance().saveEntity(sampleTracker);
                    final SendDocumentWorkerAction workerAction = new SendDocumentWorkerAction(sampleTracker);
                    workerAction.getProperties().setProperty(IS_NOT_SEND, "true");
                    if (BooleanUtils.isTrue(isFromRequest)) {
                        workerAction.getProperties().setProperty(IS_FROM_REQUEST, "true");
                    }
                    workerAction.execute();
                }
            }
        }
        if (hasVendorAccess) {
            final SendDocumentWorkerAction workerAction = new SendDocumentWorkerAction(actionContext.getDoc());
            workerAction.execute();
        }
    }

    private void updateSampleTracker(final DynamicEntity sampleTracker) {
        final String requestedUserName = sampleTracker
                .getString(SampleTracker.REQUESTED_USER_NAME);
        sampleTracker.putValue(SampleTracker.REQUESTED_USER_NAME_PRETEND,
                requestedUserName);
        final String reviewUserName = sampleTracker
                .getString(SampleTracker.REVIEW_USER_NAME);
        sampleTracker.putValue(SampleTracker.REVIEW_USER_NAME_PRETEND,
                reviewUserName);
        final Collection<DynamicEntity> sampleDetails = sampleTracker
                .getEntityCollection(SampleTracker.SAMPLE_DETAIL);
        final Collection<DynamicEntity> materialDetails = sampleTracker
                .getEntityCollection(SampleTracker.MATERIAL_DETAIL);
        final Collection<DynamicEntity> documentDetails = sampleTracker
                .getEntityCollection(SampleTracker.DOCUMENT_DETAIL);
        updateCollectionFields(sampleDetails);
        updateCollectionFields(materialDetails);
        updateCollectionFields(documentDetails);

    }


    private void updateCollectionFields(
            final Collection<DynamicEntity> records) {
        if (CollectionUtils.isNotEmpty(records)) {
               for (final DynamicEntity record : records) {
                      final String requestedUserName = record.getString(SampleTracker.REQUESTED_USER_NAME);
                      record.putValue(SampleTracker.REQUESTED_USER_NAME_PRETEND, requestedUserName);
               }
           }
    }

}
