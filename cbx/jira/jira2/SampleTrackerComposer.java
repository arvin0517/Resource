// Copyright (c) 1998-2017 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.6.7
// ============================================================================
// CHANGE LOG
// CNT.6.7 : 2017-07-12, gary.mo, CNT-31698
// CNT.6.5 : 2017-05-11, gary.mo, CNT-30455
// CNT.6.4 : 2017-03-30, connor.zeng, CNT-29553
// CNT.6.3.0 : 2017-03-11, Nick.Fu, CNT-28196
// CNT.6.3 : 2017-02-22, charles.tong, CNT-28568
// CNT.6.2 : 2017-02-16, winnie.yang, CNT-28451
// CNT.6.1 : 2017-01-20, charles.tong, CNT-28096
// CNT.6.0c : 2016-12-08, evan.chen, CNT-27173
// CNT.6.0c : 2016-12-02, kwinly.zhang, CNT-26937
// CNT.6.0c : 2016-11-29, joan.chen, CNT-27025
// CNT.6.0b : 2016-10-14, dato.zeng, CNT-26038
// CNT.5.17.0 : 2016-08-08, kevin.zhang, creation for CNT-24855
// CNT.5.17.0 : 2016-08-04, ives.li, CNT-24334
// CNT.5.16.0 : 2016-07-08, jared.zhang, CNT-24298
// CNT.5.16 : 2016-06-30, kulilin.xu, CNT-23847
// CNT.5.14 : 2016-05-09, alina.dong, CNT-22207
// CNT.5.13 : 2016-04-12, jerry.zhao, CNT-22341
// CNT.5.13 : 2016-2-29, ace.li, CNT-21588
// CNT.5.11.0 : 2015-12-24, lance.lu, CNT-20394
// CNT.5.11.0: 2015-12-01, rock.li, CNT-20322
// CNT.5.10 GA: 2015-8-14, kulilin.xu, CNT-18600
// CNT.5.10 GA : 2015-08-14, terry.chen, CNT-18925
// CNT.5.9 GA : 2015-05-15, bosto.li, CNT-17318
// CNT.5.9.0 : 2015-04-27, calvin.yang, CNT-17142
// CNT.5.0.074 : 2014-12-22, denny.deng, CNT-15501
// CNT.5.0.074 : 2014-12-12, hunting.he, CNT-15326
// CNT.5.0.074 : 2014-12-05, ivan.lo, CNT-14000
// CNT.5.0.1 : 2014-10-20, GenForm, system generated
// ============================================================================
package com.core.cbx.sampletracker.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Span;

import com.core.cbx.action.ActionDispatcher;
import com.core.cbx.action.actionContext.LoadDoc;
import com.core.cbx.action.constants.BusinessValueConstants;
import com.core.cbx.action.constants.ResultMapConstants;
import com.core.cbx.action.exception.ActionException;
import com.core.cbx.base.form.ClassificationEnabledSelectionPopupHelp;
import com.core.cbx.common.type.DateTime;
import com.core.cbx.conf.service.SystemConfigManager;
import com.core.cbx.data.DynamicEntityModel;
import com.core.cbx.data.codelist.service.CodelistManager;
import com.core.cbx.data.constants.Codelist;
import com.core.cbx.data.constants.Fact;
import com.core.cbx.data.constants.Item;
import com.core.cbx.data.constants.SampleEvaluation;
import com.core.cbx.data.constants.SampleTracker;
import com.core.cbx.data.constants.Selection;
import com.core.cbx.data.constants.SourcingRecord;
import com.core.cbx.data.constants.User;
import com.core.cbx.data.constants.Vendor;
import com.core.cbx.data.def.EntityDefManager;
import com.core.cbx.data.entity.DynamicEntity;
import com.core.cbx.data.entity.DynamicEntityImp;
import com.core.cbx.data.exception.DataException;
import com.core.cbx.locking.service.DocLockManager;
import com.core.cbx.logging.CNTLogger;
import com.core.cbx.logging.LogFactory;
import com.core.cbx.security.AuthenticationUtil;
import com.core.cbx.ui.LabelConstants;
import com.core.cbx.ui.UiConstants;
import com.core.cbx.ui.exception.UIException;
import com.core.cbx.ui.zk.action.UIAction;
import com.core.cbx.ui.zk.composer.BaseFormComposer;
import com.core.cbx.ui.zk.composer.GenericComposer;
import com.core.cbx.ui.zk.cul.CbxButton;
import com.core.cbx.ui.zk.cul.CbxCombobox;
import com.core.cbx.ui.zk.cul.CbxLabel;
import com.core.cbx.ui.zk.cul.Textarea;
import com.core.cbx.ui.zk.cul.selection.Selectionbox;
import com.core.cbx.ui.zk.util.CbxUiUtils;
import com.core.cbx.ui.zk.util.MessageUtil;

public class SampleTrackerComposer extends BaseFormComposer {

    private static final CNTLogger LOGGER = LogFactory.getLogger(SampleTrackerComposer.class);
    private static final long serialVersionUID = 7697185229721218849L;
    private static final Map<MultiKey, List<DynamicEntity>> evaluateByMap = new HashMap<MultiKey, List<DynamicEntity>>();
    private static final String EVALUATE_USER = "evaluateUser";

    private static final String LATEEST_SAMPLE_DETAILS = "latestSampleDetials";
    private static final String LATEEST_MATERIAL_DETAILS = "latestMaterialDetails";
    private static final String LATEEST_DOCUMENT_DETAILS = "latestDocumentDetails";
    private static final String ALL_SAMPLE_DETAILS = "allSampleDetials";
    private static final String ALL_MATERIAL_DETAILS = "allMaterialDetails";
    private static final String ALL_DOCUMENT_DETAILS = "allDocumentDetails";
    private static final String IS_LATESET = "isLatest";
    private static final String SAMPLE_FILTER_BOOKNAME = "SAMPLE_VERSION_FILTER";
    private static final String CODE_ALL = "ALL";
    private static final String CODE_LATEST = "LATEST";
    private static final String LABEL_SHOW_ONLY = "lbl.sampleTracker.sampleDetailFilter.showOnly";
    private static final String CLASS_SAMPLE_FILTER_GRID = "cbx-grid-sampleVersionFilter";
    private static final String CLASS_FIELD_LABEL = "cbx-field-label";
    private static final String CLASS_FIELD_VALUE = "cbx-field-value";
    private static final String ON_OK = "onOK";
    private static final String IS_SINGLE = "SourcingRecord.setting";
    private static final String SINGLE = "SINGLE";
    private static final String SUBMIT_SAMPLE_DETAILS = "submitSampleDetails";
    private static final String SOURCING_RECORD = "sourcingRecord";
    private static final String SELECT_EMAIL = "lbl.sampleTracker.selectEmail";
    private static final String SELECT_VENDOR_BEFORE_SELECT_EMAIL = "lbl.sampleTracker.selectVendorBeforeSelectEmail";
    private static final String POPUP_SAMPLE_TRACKER_ADD_CONTACT = "popupSampleTrackerAddContact";

    @Override
    public void doAfterCompose(final Component component) throws Exception {
        super.doAfterCompose(component);
        doAfterSelectVendor();
        doAfterSelectFact();
        doAfterSelectItem();
        doAfterSelectSampleVersionFilter();
        buildSelectEmailButton(view);
        final Map<String, Object> actContext = new HashMap<String, Object>();
        addEvenListenerForSelectAttachmentOrImage(component, UiConstants.ADD_IMAGE, SampleTracker.IMAGE,
                SampleTracker.SAMPLE_TRACKER_IMAGES, actContext, GenericComposer.FILE_TYPE_IMAGE);
        addEvenListenerForSelectAttachmentOrImage(component, UiConstants.ADD_ATTACHMENT, SampleTracker.ATTACHMENT,
                SampleTracker.SAMPLE_TRACKER_ATTACHMENTS, actContext, GenericComposer.FILE_TYPE_ATTACHMENT);

        if (isHiddenSubmitButton()) {
            hiddenSubmitButton(component);
        } else {
            final DynamicEntity sampleTracker = (DynamicEntity) datasource;
            final String docId = sampleTracker.getId();
            final String userId = AuthenticationUtil.getUser().getId();
            if (docId != null) {
                if (isVisibleInBuyerReadMode(docId, userId)) {
                    hiddenSubmitButton(component);
                }
                if (isVisibleInVendorEditMode(docId, userId)) {
                    hiddenSubmitButton(component);
                }
            }
        }
    }

    private void buildSelectEmailButton(final Component view) {
        final Textarea vendorEmail = (Textarea) view.getFellowIfAny(SampleTracker.VENDOR_EMAIL);
        if (null == vendorEmail) {
            return;
        }
        final Div vendorEmailDiv =  (Div) vendorEmail.getParent();
        final Div buttonDiv = new Div();
        final CbxButton button = new CbxButton(CbxUiUtils.getLabel(SELECT_EMAIL));
        button.setId("selectEmailButton");
        button.setDisabled(vendorEmail.isDisabled());
        button.setStyle("font-family: Arial;font-size: 13 px;margin: 3px 0px 3px 0px;min-width: 64px;");
        final GenericComposer composer = this;
        button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(final Event evt) throws Exception {
                final DynamicEntity vendor = ((DynamicEntity) datasource).getEntity(SampleTracker.VENDOR);
                if (null == vendor) {
                    MessageUtil.warning(CbxUiUtils.getLabel(SELECT_VENDOR_BEFORE_SELECT_EMAIL));
                } else {
                    final Map<String, Object> actContext = new HashMap<String, Object>();
                    actContext.put(UIAction.ACT_CTX_COMPOSER, composer);
                    CbxUiUtils.buildAndOpenPopupWin(actContext, POPUP_SAMPLE_TRACKER_ADD_CONTACT, null);
                }
            }
        });
        buttonDiv.appendChild(button);
        vendorEmailDiv.appendChild(buttonDiv);
    }

    protected boolean isHiddenSubmitButton() {
        return false;
    }

    protected boolean isVisibleInBuyerReadMode(final String docId, final String userId) throws DataException {
        if (!AuthenticationUtil.isExternalUser() && !DocLockManager.isLockedByUser(docId, userId)) {
            return true;
        }
        return false;
    }

    protected boolean isVisibleInVendorEditMode(final String docId, final String userId) throws DataException {
        if (AuthenticationUtil.isExternalUser() && DocLockManager.isLockedByUser(docId, userId)) {
            return true;
        }
        return false;
    }

    protected void hiddenSubmitButton(final Component component) {
        final Component submitComponent = component.getFellowIfAny(SUBMIT_SAMPLE_DETAILS);
        if (submitComponent != null) {
            submitComponent.setVisible(false);
            final Component previousComponent = submitComponent.getPreviousSibling();
            if (previousComponent != null && previousComponent instanceof Span) {
                previousComponent.setVisible(false);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.core.cbx.ui.zk.composer.GenericComposer#dataBinding()
     */
    @Override
    public void dataBinding() {
        super.dataBinding();
        try {
            setupDataForForm();
        } catch (final DataException e) {
            LOGGER.error(e.getId(), e.getMessage());
        }
    }

    protected void doAfterSelectVendor() {
        // Select Vendor Information refresh the page.
        final Component vendorSelectBox = getView().getFellowIfAny(SampleTracker.VENDOR);
        final DynamicEntity vendor = (DynamicEntity) datasource.get(SampleTracker.VENDOR);
        final String trackerNo = (String) datasource.get(SampleTracker.TRACKER_NO);
        if (vendor != null && StringUtils.isNotBlank(trackerNo)) {
            if (vendorSelectBox != null) {
                ((Selectionbox) vendorSelectBox).setDisabled(true);
            }
        }

        if (vendorSelectBox instanceof Selectionbox) {
            vendorSelectBox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

                @Override
                public void onEvent(final Event event) throws Exception {
                    final DynamicEntity vendor = (DynamicEntity) event.getData();
                    binder.saveComponent(vendorSelectBox);
                    if (vendor != null) {
                        final DynamicEntity fullVendor = loadEntity(vendor.getId(), -1, Vendor.MODULE_ID);
                        datasource.put(SampleTracker.VENDOR, fullVendor);
                        String vendorEmail = "";
                        Boolean onlyEmail = true;
                        final DynamicEntity vendorDoc = DynamicEntityModel.getMainEntityByRefAndVersion(
                                vendor.getEntityName(), vendor.getString(Vendor.REF_NO), vendor.getVersion());
                        if (vendorDoc != null) {
                            final Collection<DynamicEntity> vendorContacts = vendorDoc
                                    .getEntityCollection(Vendor.CONTACTS);
                            String contactTypeCode = null;
                            if (!CollectionUtils.isEmpty(vendorContacts)) {
                                for (final DynamicEntity vendorContact : vendorContacts) {
                                    if (vendorContact != null) {
                                        if (BooleanUtils.isTrue(vendorContact.getBoolean(Vendor.IS_DISABLED))) {
                                            continue;
                                        }
                                        final Collection<DynamicEntity> contactTypes = vendorContact
                                                .getEntityCollection(Vendor.CONTACT_TYPE_ID);
                                        final String vendorContactEmail = vendorContact.getString(Vendor.EMAIL);
                                        if (!CollectionUtils.isEmpty(contactTypes)) {
                                            for (final DynamicEntity contactType : contactTypes) {
                                                contactTypeCode = contactType.getString("refRef");
                                                if (contactTypeCode != null
                                                        && contactTypeCode.equals("DEFAULT_FOR_SAMPLE")) {
                                                    if (onlyEmail) {
                                                        vendorEmail = vendorEmail + vendorContactEmail;
                                                    } else {
                                                        vendorEmail = vendorEmail + ";" + vendorContactEmail;
                                                    }
                                                    onlyEmail = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        datasource.put(SampleTracker.VENDOR_EMAIL, vendorEmail);
                        datasource.put(SampleTracker.VENDOR_CODE, vendor.getString(Vendor.VENDOR_CODE));
                        binder.loadComponent(getView().getFellowIfAny(SampleTracker.VENDOR_EMAIL));
                        binder.loadComponent(getView().getFellowIfAny(SampleTracker.VENDOR_CODE));

                    } else {
                        datasource.remove(SampleTracker.VENDOR_CODE);
                        binder.loadComponent(getView().getFellowIfAny(SampleTracker.VENDOR_CODE));
                    }
                    binder.loadComponent(getView().getFellowIfAny(SampleTracker.VENDOR_RATING_NAME));
                }

            });

            ((Selectionbox) vendorSelectBox).addEventListener(Selectionbox.EVT_POPUP_WIN, new EventListener<Event>() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    binder.saveAll();
                    final DynamicEntity doc = (DynamicEntity) datasource;

                    final String value = CbxUiUtils
                            .getConfigValue(BusinessValueConstants.DOMAIN_ATTRIBUTE_SYSYTEM_SECURITY_MODE);
                    if (StringUtils.equalsIgnoreCase(value,
                            BusinessValueConstants.CLASSIFICATION_SYSYTEM_SECURITY_MODE)) {
                        ClassificationEnabledSelectionPopupHelp
                        .setSelectionPopupViewParamsByClassification(vendorSelectBox, doc, true);
                    }
                }
            });
        }

    }

    protected void doAfterSelectFact() {
        final Component factSelectBox = getView().getFellowIfAny(SampleTracker.FACTORY);

        if (factSelectBox instanceof Selectionbox) {
            factSelectBox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

                @Override
                public void onEvent(final Event event) throws Exception {
                    binder.saveComponent(factSelectBox);
                    final DynamicEntity factory = (DynamicEntity) event.getData();

                    if (factory != null) {
                        final long factVer = factory.getEntityVersion();
                        final DynamicEntity fullFact = loadEntity(factory.getId(), (int) factVer, Fact.MODULE_ID);
                        final DynamicEntity country = fullFact.getEntity(Fact.COUNTRY_OF_ORIGIN);
                        datasource.put(SampleTracker.FACTORY, fullFact);
                        if (country != null) {
                            datasource.put(SampleTracker.COUNTRY, country);
                        }
                        final Component sampleCountry = getView().getFellowIfAny(SampleTracker.COUNTRY);
                        final Component assessmentLevelName = getView()
                                .getFellowIfAny(SampleTracker.ASSESSMENT_LEVEL_NAME);
                        final Component rankName = getView().getFellowIfAny(SampleTracker.RANK_NAME);
                        if (sampleCountry != null) {
                            binder.loadComponent(sampleCountry);
                        }
                        if (assessmentLevelName != null) {
                            binder.loadComponent(assessmentLevelName);
                        }
                        if (rankName != null) {
                            binder.loadComponent(rankName);
                        }

                    }
                }

            });
        }

    }

    protected void doAfterSelectItem() {
        // Select Vendor Information refresh the page.
        final Component itemSelectionBox = getView().getFellowIfAny(SampleTracker.ITEM);
        final DynamicEntity item = (DynamicEntity) datasource.get(SampleTracker.ITEM);
        final String trackerNo = (String) datasource.get(SampleTracker.TRACKER_NO);
        if (item != null && StringUtils.isNotBlank(trackerNo)) {
            ((Selectionbox) itemSelectionBox).setDisabled(true);
        }

        if (itemSelectionBox instanceof Selectionbox) {
            itemSelectionBox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

                @SuppressWarnings({"unchecked", "deprecation"})
                @Override
                public void onEvent(final Event event) throws Exception {

                    final String isSingle = SystemConfigManager.getInstance().getConfigValue(IS_SINGLE);
                    final DynamicEntity item = (DynamicEntity) event.getData();
                    final Collection<DynamicEntity> sampleDetails = (Collection<DynamicEntity>) datasource
                            .get(SampleTracker.SAMPLE_DETAIL);
                    final Collection<DynamicEntity> materialDetails = (Collection<DynamicEntity>) datasource
                            .get(SampleTracker.MATERIAL_DETAIL);
                    if (item != null) {
                        final String itemId = item.getId();
                        final int itemEntityVersion = item.getEntityVersion();
                        final DynamicEntity fullItem = loadEntity(itemId, itemEntityVersion, Item.MODULE_ID);
                        datasource.put(SampleTracker.ITEM, fullItem);
                        final DynamicEntity sourcingRecord = fullItem.getEntity(Item.DEFAULT_SOURCING_RECORD);
                        final DynamicEntity image = fullItem.getEntity(Item.FILE_ID);
                        if (sourcingRecord != null) {
                            datasource.put(SOURCING_RECORD, sourcingRecord);
                        }
                        if (image != null) {
                            datasource.put(SampleTracker.IMAGE, image);
                        }
                        if (null != fullItem && SINGLE.equalsIgnoreCase(isSingle)) {
                            datasource.put(SampleTracker.SEASON, fullItem.get(Item.SEASON));
                            datasource.put(SampleTracker.YEAR, fullItem.get(Item.YEAR));
                        } else if (sourcingRecord != null && !SINGLE.equalsIgnoreCase(isSingle)) {
                            datasource.put(SampleTracker.SEASON, sourcingRecord.get(SourcingRecord.SEASON));
                            datasource.put(SampleTracker.YEAR, sourcingRecord.get(SourcingRecord.YEAR));
                        }
                        setItemForDetails(sampleDetails, fullItem);
                        setItemForDetails(materialDetails, fullItem);
                        binder.loadAll();
                    } else if (item == null) {
                        boolean isNeedShowMessage = false;
                        for (final DynamicEntity materialDetail : materialDetails) {
                            final Collection<DynamicEntity> colorAndPattern = materialDetail
                                    .getEntityCollection(SampleTracker.COLOR_AND_PATTERN);
                            final String sizeCode = materialDetail.getString(SampleTracker.SIZE_CODE);
                            if (StringUtils.isNotEmpty(sizeCode)) {
                                isNeedShowMessage = true;
                                break;
                            }
                            if (CollectionUtils.isNotEmpty(colorAndPattern)) {
                                isNeedShowMessage = true;
                                break;
                            }
                        }
                        for (final DynamicEntity sampleDetail : sampleDetails) {
                            final Collection<DynamicEntity> colorAndPattern = sampleDetail
                                    .getEntityCollection(SampleTracker.COLOR_AND_PATTERN);
                            final String sizeCode = sampleDetail.getString(SampleTracker.SIZE_CODE);
                            if (StringUtils.isNotEmpty(sizeCode)) {
                                isNeedShowMessage = true;
                                break;
                            }
                            if (CollectionUtils.isNotEmpty(colorAndPattern)) {
                                isNeedShowMessage = true;
                                break;
                            }
                        }
                        if (isNeedShowMessage) {
                            MessageUtil.warning(CbxUiUtils.getLabel(LabelConstants.COLOR_OR_SIZE_SELECTED),
                                    CbxUiUtils.getLabel(LabelConstants.MSG_TITLE_WARNING),
                                    CbxUiUtils.getLabel(LabelConstants.MSG_WARNING_OK),
                                    CbxUiUtils.getLabel(LabelConstants.MSG_WARNING_CANCEL),
                                    new EventListener<Messagebox.ClickEvent>() {

                                @Override
                                public void onEvent(final ClickEvent e) throws Exception {
                                    if (e.getName().equals(ON_OK)) {
                                        datasource.put(SampleTracker.ITEM, null);
                                        datasource.put(SampleTracker.IMAGE, null);
                                        datasource.put(SOURCING_RECORD, null);

                                        if (CollectionUtils.isNotEmpty(materialDetails)) {
                                            for (final DynamicEntity materialDetail : materialDetails) {
                                                materialDetail.put(SampleTracker.ITEM, null);
                                                materialDetail.put(SampleTracker.COLOR_AND_PATTERN, null);
                                            }
                                        }
                                        if (CollectionUtils.isNotEmpty(sampleDetails)) {
                                            for (final DynamicEntity sampleDetail : sampleDetails) {
                                                sampleDetail.put(SampleTracker.ITEM, null);
                                                sampleDetail.put(SampleTracker.COLOR_AND_PATTERN, null);
                                            }
                                        }
                                        binder.loadAll();
                                    } else {
                                        binder.loadComponent(getView().getFellowIfAny(SampleTracker.ITEM));
                                    }
                                }
                            });
                        } else {
                            datasource.put(SampleTracker.ITEM, null);
                            datasource.put(SampleTracker.SEASON, null);
                            datasource.put(SampleTracker.YEAR, null);
                            datasource.put(SOURCING_RECORD, null);
                            datasource.put(SampleTracker.IMAGE, null);
                            setItemForDetails(sampleDetails, null);
                            setItemForDetails(materialDetails, null);
                            binder.loadAll();
                        }
                    }
                }
            });

            ((Selectionbox) itemSelectionBox).addEventListener(Selectionbox.EVT_POPUP_WIN, new EventListener<Event>() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    binder.saveAll();
                    final DynamicEntity doc = (DynamicEntity) datasource;

                    final String value = CbxUiUtils
                            .getConfigValue(BusinessValueConstants.DOMAIN_ATTRIBUTE_SYSYTEM_SECURITY_MODE);
                    if (StringUtils.equalsIgnoreCase(value,
                            BusinessValueConstants.CLASSIFICATION_SYSYTEM_SECURITY_MODE)) {
                        ClassificationEnabledSelectionPopupHelp
                        .setSelectionPopupViewParamsByClassification(itemSelectionBox, doc, true);
                    }
                }
            });
        }
    }

    private void doAfterSelectSampleVersionFilter() throws DataException {
        final DynamicEntity sampleTracker = (DynamicEntity) datasource;
        final CbxCombobox sampleVersionFilter = createSampleVersionFilterField(sampleTracker);
        if (!sampleTracker.isNewEntity()) {
            filterSampleByVersion(sampleTracker);
            datasource.put(SampleTracker.SAMPLE_DETAIL, datasource.get(LATEEST_SAMPLE_DETAILS));
            datasource.put(SampleTracker.MATERIAL_DETAIL, datasource.get(LATEEST_MATERIAL_DETAILS));
            datasource.put(SampleTracker.DOCUMENT_DETAIL, datasource.get(LATEEST_DOCUMENT_DETAILS));
            final Component sampleDetail = getView().getFellowIfAny(SampleTracker.SAMPLE_DETAIL);
            final Component materialDetail = getView().getFellowIfAny(SampleTracker.MATERIAL_DETAIL);
            final Component documentDetail = getView().getFellowIfAny(SampleTracker.DOCUMENT_DETAIL);
            if (sampleDetail != null) {
                binder.loadComponent(sampleDetail);
            }
            if (materialDetail != null) {
                binder.loadComponent(materialDetail);
            }
            if (documentDetail != null) {
                binder.loadComponent(documentDetail);
            }
        }

        sampleVersionFilter.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                final String selectedItem = (String) ((CbxCombobox) event.getTarget()).getData();
                filterSampleByVersion((DynamicEntity) datasource);
                if (selectedItem.equals(CODE_ALL) && sampleVersionFilter != null) {
                    datasource.put(SampleTracker.SAMPLE_DETAIL, datasource.get(ALL_SAMPLE_DETAILS));
                    datasource.put(SampleTracker.MATERIAL_DETAIL, datasource.get(ALL_MATERIAL_DETAILS));
                    datasource.put(SampleTracker.DOCUMENT_DETAIL, datasource.get(ALL_DOCUMENT_DETAILS));
                } else {
                    datasource.put(SampleTracker.SAMPLE_DETAIL, datasource.get(LATEEST_SAMPLE_DETAILS));
                    datasource.put(SampleTracker.MATERIAL_DETAIL, datasource.get(LATEEST_MATERIAL_DETAILS));
                    datasource.put(SampleTracker.DOCUMENT_DETAIL, datasource.get(LATEEST_DOCUMENT_DETAILS));
                }
                final Component sampleDetail = getView().getFellowIfAny(SampleTracker.SAMPLE_DETAIL);
                final Component materialDetail = getView().getFellowIfAny(SampleTracker.MATERIAL_DETAIL);
                final Component documentDetail = getView().getFellowIfAny(SampleTracker.DOCUMENT_DETAIL);
                if (sampleDetail != null) {
                    binder.loadComponent(sampleDetail);
                }
                if (materialDetail != null) {
                    binder.loadComponent(materialDetail);
                }
                if (documentDetail != null) {
                    binder.loadComponent(documentDetail);
                }
            }
        });
    }

    private DynamicEntity loadEntity(final String entityId, final Integer entityVersion, final String moduleId) {
        final LoadDoc loadDoc = new LoadDoc(entityId, entityVersion, moduleId);
        try {
            ActionDispatcher.execute(loadDoc);
        } catch (final ActionException e) {
            throw new UIException("000001", "Can not get vendor doc", e);
        }
        return (DynamicEntity) loadDoc.getResultValue(ResultMapConstants.KEY_DOC_ENTITY);
    }

    private void setupDataForForm() throws DataException {
        for (final String key : new String[] {SampleTracker.SAMPLE_DETAIL, SampleTracker.MATERIAL_DETAIL}) {
            final Collection<DynamicEntity> details = ((DynamicEntity) datasource).getEntityCollection(key);
            for (final DynamicEntity detail : details) {
                setupEvaluateUser(detail);
            }
        }
    }

    private void setupEvaluateUser(final DynamicEntity detail) throws DataException {
        final DynamicEntity sampleEvaluation = detail.getEntity(SampleTracker.SAMPLE_EVALUATION);
        if (sampleEvaluation == null) {
            return;
        }
        final String evaluateBy = sampleEvaluation.getString(SampleEvaluation.EVALUATE_BY_VALUE);
        if (StringUtils.isNotBlank(evaluateBy)) {
            final String[] evaluateUsers = StringUtils.split(evaluateBy, ",");
            List<DynamicEntity> selectionList;
            if ((selectionList = evaluateByMap.get(new MultiKey(evaluateUsers))) != null) {
                detail.put(EVALUATE_USER, selectionList);
            } else {
                selectionList = new ArrayList<DynamicEntity>();
                for (final String evaluateUser : evaluateUsers) {
                    final DynamicEntity userEntity = new DynamicEntityImp();
                    userEntity.put(User.USER_NAME, evaluateUser);
                    final DynamicEntity userSelection = DynamicEntityModel.createDynamicEntity(
                            EntityDefManager.getSelectionEntityNameByEntityName(userEntity.getEntityName()));
                    userSelection.put(Selection.REF, userEntity);
                    selectionList.add(userSelection);
                }
                detail.put(EVALUATE_USER, selectionList);
                evaluateByMap.put(new MultiKey(evaluateUsers), selectionList);
            }
        }
    }

    protected void filterSampleByVersion(final DynamicEntity doc) {
        final Collection<DynamicEntity> sampleDetials = doc.getEntityCollection(SampleTracker.SAMPLE_DETAIL);
        final Collection<DynamicEntity> materialDetails = doc.getEntityCollection(SampleTracker.MATERIAL_DETAIL);
        final Collection<DynamicEntity> documentDetails = doc.getEntityCollection(SampleTracker.DOCUMENT_DETAIL);

        Collection<DynamicEntity> allSampleDetials = doc.getEntityCollection(ALL_SAMPLE_DETAILS);
        Collection<DynamicEntity> allMaterialDetails = doc.getEntityCollection(ALL_MATERIAL_DETAILS);
        Collection<DynamicEntity> allDocumentDetails = doc.getEntityCollection(ALL_DOCUMENT_DETAILS);

        if (!doc.isNewEntity() && allSampleDetials.isEmpty() && allMaterialDetails.isEmpty()
                && allDocumentDetails.isEmpty()) {
            final DynamicEntity fullSampleTracker = loadEntity(doc.getId(), doc.getEntityVersion(),
                    SampleTracker.MODULE_ID);
            allSampleDetials = fullSampleTracker.getEntityCollection(SampleTracker.SAMPLE_DETAIL);
            allMaterialDetails = fullSampleTracker.getEntityCollection(SampleTracker.MATERIAL_DETAIL);
            allDocumentDetails = fullSampleTracker.getEntityCollection(SampleTracker.DOCUMENT_DETAIL);
        }

        for (final DynamicEntity sampleDetial : sampleDetials) {
            if (!allSampleDetials.contains(sampleDetial)) {
                allSampleDetials.add(sampleDetial);
            }
        }

        for (final DynamicEntity materialDetail : materialDetails) {
            if (!allMaterialDetails.contains(materialDetail)) {
                allMaterialDetails.add(materialDetail);
            }
        }

        for (final DynamicEntity documentDetail : documentDetails) {
            if (!allDocumentDetails.contains(documentDetail)) {
                allDocumentDetails.add(documentDetail);
            }
        }

        datasource.put(ALL_SAMPLE_DETAILS, allSampleDetials);
        datasource.put(ALL_MATERIAL_DETAILS, allMaterialDetails);
        datasource.put(ALL_DOCUMENT_DETAILS, allDocumentDetails);
        datasource.put(LATEEST_SAMPLE_DETAILS, getLatestVersionSample(allSampleDetials));
        datasource.put(LATEEST_MATERIAL_DETAILS, getLatestVersionSample(allMaterialDetails));
        datasource.put(LATEEST_DOCUMENT_DETAILS, getLatestVersionSample(allDocumentDetails));
    }

    private Collection<DynamicEntity> getLatestVersionSample(final Collection<DynamicEntity> collections) {
        final Collection<DynamicEntity> results = new ArrayList<DynamicEntity>();
        for (final DynamicEntity collection : collections) {
            DynamicEntity result = collection;
            boolean isNotRepeat = true;
            if (collection.get(SampleTracker.SAMPLE_ID) == null) {
                results.add(result);
                isNotRepeat = false;
            } else {
                for (final DynamicEntity dynamicEntity : results) {
                    if (dynamicEntity.get(SampleTracker.SAMPLE_ID) != null && collection.get(SampleTracker.SAMPLE_ID)
                            .equals(dynamicEntity.get(SampleTracker.SAMPLE_ID))) {
                        isNotRepeat = false;
                    }
                }
            }

            if (isNotRepeat) {
                int version = Integer.parseInt((String) collection.get(SampleTracker.SAMPLE_VERSION));
                for (final DynamicEntity compareCollection : collections) {
                    if (compareCollection.get(SampleTracker.SAMPLE_ID) != null && collection
                            .get(SampleTracker.SAMPLE_ID).equals(compareCollection.get(SampleTracker.SAMPLE_ID))) {
                        final int compareVersion = Integer
                                .parseInt((String) compareCollection.get(SampleTracker.SAMPLE_VERSION));
                        if (version < compareVersion) {
                            result = compareCollection;
                            version = compareVersion;
                        }
                    }
                }
                results.add(result);
            }
        }

        for (final DynamicEntity result : results) {
            result.put(IS_LATESET, true);
        }

        return results;
    }

    private CbxCombobox createSampleVersionFilterField(final DynamicEntity sampleTracker) throws DataException {
        final DateTime matchDate = DateTime.now();
        final String domainId = sampleTracker.getDomainId();
        List<DynamicEntity> codelists = new ArrayList<DynamicEntity>();

        codelists = AuthenticationUtil.runInSystemTempContext(domainId, domainId,
                new AuthenticationUtil.SystemTempContext<List<DynamicEntity>>() {
            @Override
            public List<DynamicEntity> run() throws DataException {
                return CodelistManager.loadCodelistItemByRestriction(SAMPLE_FILTER_BOOKNAME, domainId,
                        matchDate, null, false);
            }
        });

        final CbxCombobox sampleVersionFilter = new CbxCombobox(false);
        if (!codelists.isEmpty()) {
            for (final DynamicEntity codelist : codelists) {
                final Comboitem item = new Comboitem();
                item.setValue(codelist.get(Codelist.CODE));
                item.setLabel((String) codelist.get(Codelist.NAME));
                sampleVersionFilter.appendChild(item);
                if (codelist.get(Codelist.CODE).equals(CODE_LATEST)) {
                    sampleVersionFilter.setSelectedItem(item);
                }
            }
        } else {
            LOGGER.debug("Can't find SAMPLE_VERSION_FILTER codelist");
        }

        final Component refComponent = getView().getFellowIfAny(SampleTracker.SAMPLE_DETAIL).getParent().getParent();
        final Component parentComponent = refComponent.getParent();
        final Div div = new Div();
        div.setClass(CLASS_SAMPLE_FILTER_GRID);

        final Div labelDiv = new Div();
        labelDiv.setClass(CLASS_FIELD_LABEL);
        final CbxLabel label = new CbxLabel(CbxUiUtils.getLabel(LABEL_SHOW_ONLY));
        label.setParent(labelDiv);
        div.appendChild(labelDiv);

        final Div sampleDetialFilterDiv = new Div();
        sampleDetialFilterDiv.setClass(CLASS_FIELD_VALUE);
        sampleVersionFilter.setParent(sampleDetialFilterDiv);
        div.appendChild(sampleDetialFilterDiv);

        parentComponent.insertBefore(div, refComponent);
        return sampleVersionFilter;
    }

    private void setItemForDetails(final Collection<DynamicEntity> details, final DynamicEntity item) {
        if (CollectionUtils.isNotEmpty(details)) {
            for (final DynamicEntity detail : details) {
                detail.put(SampleTracker.ITEM, item);
            }
        }
    }

}
