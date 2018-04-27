// Copyright (c) 1998-2017 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.6.3.0
// ============================================================================
// CHANGE LOG
// CNT.6.8.0 : 2017-08-16 arvin.zheng CNT-30202
// CNT.6.3.0 : 2017-03-16 stephen.lai CNT-29009
// CNT.6.3.0 : 2017-03-14 terry.chen CNT-28991
// CNT.6.3.0 : 2017-03-10, terry.chen, creation CNT-28603
// ============================================================================
package com.core.cbx.ui.zk.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.core.cbx.action.ActionDispatcher;
import com.core.cbx.action.actionContext.DataInheritance;
import com.core.cbx.action.actionContext.InheritResponsibleParties;
import com.core.cbx.action.actionContext.LoadDoc;
import com.core.cbx.action.constants.BusinessActionConstants;
import com.core.cbx.action.constants.BusinessConstants;
import com.core.cbx.action.constants.CommonFieldConstants;
import com.core.cbx.action.constants.ResultMapConstants;
import com.core.cbx.action.exception.ActionException;
import com.core.cbx.action.inheritance.InheritanceTaskChain;
import com.core.cbx.data.constants.Qq;
import com.core.cbx.data.entity.DynamicEntity;
import com.core.cbx.data.entity.EntityConstants;
import com.core.cbx.qq.helper.QqHelper;
import com.core.cbx.ui.exception.UIExceptionHandler;
import com.core.cbx.ui.zk.component.MainWin;
import com.core.cbx.ui.zk.component.SearchViewWin;
import com.core.cbx.ui.zk.cul.grid.SearchViewGrid;
import com.core.cbx.ui.zk.util.CbxUiUtils;

/**
 * @author terry.chen
 */
public class QqBatchSearchNewRfqDocAction extends AbstractNeedCheckTagsNewDocAction {

    private static final String KEY_SELECT_RECORDS = "selectRecords";

    /*
     * (non-Javadoc)
     * @see com.core.cbx.ui.zk.action.AbstractBatchSearchNewDocAction#getInheritanceId()
     */
    @Override
    public String getInheritanceId() {
        return StringUtils.EMPTY;
    }

    /*
     * (non-Javadoc)
     * @see com.core.cbx.ui.zk.action.AbstractBatchSearchNewDocAction#getActionId()
     */
    @Override
    public String getActionId() {
        return BusinessActionConstants.QQ_NEW_RFQ_DOC;
    }

    @Override
    protected boolean stopExecute(final Map<String, Object> actionContext) {
        return isNotPassedValidation(actionContext);
    }

    /**
     * @param actionContext
     * @return
     */
    private boolean isNotPassedValidation(final Map<String, Object> actionContext) {
        final SearchViewWin searchWin = (SearchViewWin) actionContext.get(UIAction.ACT_CTX_TARGET);
        final SearchViewGrid searchGrid = searchWin.getSearchGrid();
        @SuppressWarnings("unchecked")
        final Set<DynamicEntity> selectedRecords = searchGrid.getSelectedRecords();
        final String message = checkConfirmedEditingStatus(selectedRecords);
        if (StringUtils.isNotEmpty(message)) {
            popupMsg(CbxUiUtils.getLabel(BusinessConstants.QQ_BATCH_NEW_RFQ_NO_CONFIRMED_DOC)
                    + "\r\n" + message, actionContext);
            return Boolean.TRUE;
        }
        if (isNotSelectedRecordsOrDifferentVendors(selectedRecords)) {
            popupMsg(BusinessConstants.QQ_BATCH_NEW_RFQ_NO_SELECT_DOC, actionContext);
            return Boolean.TRUE;
        }
        if (isHclSecurityMode() && !hclUniqueCheck(selectedRecords)) {
            popHclNotUniqueMessage(actionContext);
            return Boolean.TRUE;
        }
        final DynamicEntity record = selectedRecords.iterator().next();
        List<DynamicEntity> qqItems = new ArrayList<DynamicEntity>();

        actionContext.put(KEY_SELECT_RECORDS, selectedRecords);
        if (StringUtils.equals(Qq.ENTITY_NAME_QQ_ITEM, record.getEntityName())
                && isVendorDeclinedOrNoItem(selectedRecords)) {
            popupMsg(BusinessConstants.QQ_BATCH_NEW_RFQ_VENDOR_DECLINED_OR_NO_ITEM, actionContext);
            return Boolean.TRUE;
        }
        if (StringUtils.equals(Qq.ENTITY_NAME_QQ, record.getEntityName())) {
            try {
                filterAndCollectRealQqItem(selectedRecords, qqItems, actionContext);
                if (qqItems.size() == 0) {
                    popupMsg(BusinessConstants.QQ_BATCH_NEW_RFQ_ALL_VENDOR_DECLINED_OR_NO_ITEM, actionContext);
                    return Boolean.TRUE;
                }
            } catch (final ActionException e) {
                throw UIExceptionHandler.getInstance().handle(e);
            }
        }
        qqItems = qqItems.size() == 0 ? new ArrayList<DynamicEntity>(selectedRecords) : qqItems;
        if (qqItemWithSameItemNoButDifferentQq(qqItems)){
            popupMsg(BusinessConstants.RFQ_SELECT_QQ_SAME_ITEM_DIFFERENT_QQ, actionContext);
            return Boolean.TRUE;
        }
        return super.stopExecute(actionContext);
    }

    /**
     * After filter selectedRecords would only have real QQ items.
     * @param selectedRecords
     * @param qqItems
     * @throws ActionException
     */

    private void filterAndCollectRealQqItem(final Set<DynamicEntity> selectedRecords, final List<DynamicEntity> qqItems,
            final Map<String, Object> actionContext)
            throws ActionException {
        final Set<DynamicEntity> fullQqRecords = new HashSet<DynamicEntity>();
        for (DynamicEntity qqEntity : selectedRecords) {
            final LoadDoc loadDoc = new LoadDoc(qqEntity.getId(), qqEntity.getEntityVersion(), Qq.MODULE_ID);
            ActionDispatcher.execute(loadDoc);
            if(loadDoc.getDoc() != null) {
                qqEntity = loadDoc.getDoc();
            }
            final List<DynamicEntity> realEntityItems = new ArrayList<DynamicEntity>();
            for (final DynamicEntity entity : qqEntity.getEntityCollection(Qq.QQ_ITEMS)){
                if (QqHelper.isRealItemAndNotDeclined(entity)) {
                    realEntityItems.add(entity);
                    qqItems.add(entity);
                }
            }
            qqEntity.putValue(Qq.QQ_ITEMS, realEntityItems);
            fullQqRecords.add(qqEntity);
        }
        actionContext.put(KEY_SELECT_RECORDS, fullQqRecords);
    }

    private boolean qqItemWithSameItemNoButDifferentQq(final List<DynamicEntity> selectedRecords) {
        final Map<String, String> itemNoToQq = new HashMap<String, String>();
        for (final DynamicEntity entity : selectedRecords) {
            final String itemNo = entity.getString(Qq.ITEM_NO);
            if(itemNo == null){
                continue;
            }
            final String qqId = entity.getString(Qq.QQ_ID);
            if (itemNoToQq.containsKey(itemNo)
                    && !StringUtils.equals(itemNoToQq.get(itemNo), entity.getString(Qq.QQ_ID))) {
                return Boolean.TRUE;
            } else {
                itemNoToQq.put(itemNo, qqId);
            }
        }
        return Boolean.FALSE;
    }

    private boolean isVendorDeclinedOrNoItem(final Set<DynamicEntity> selectedRecords) {
        for (final DynamicEntity entity : selectedRecords) {
            if (StringUtils.isEmpty(entity.getString(Qq.ITEM_NO))
                    || BooleanUtils.isTrue(entity.getBoolean(Qq.VENDOR_DECLINED))) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private boolean isNotSelectedRecordsOrDifferentVendors(final Set<DynamicEntity> selectedRecords) {
        boolean result = Boolean.FALSE;
        if (CollectionUtils.isEmpty(selectedRecords)) {
            result = Boolean.TRUE;
        } else {
            String vendorRef = null;
            for (final DynamicEntity qq : selectedRecords) {
                if (vendorRef == null) {
                    vendorRef = qq.getString(Qq.VENDOR_REF);
                } else if (!StringUtils.equals(vendorRef, qq.getString(Qq.VENDOR_REF))){
                    result = Boolean.TRUE;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void process(final Map<String, Object> actContext) {
        final MainWin win = MainWin.getCurrent();
        try {
            final String moduleId = (String) actContext.get(BusinessConstants.MODULE_ID);
            final String entityName = (String) actContext.get(BusinessConstants.ENTITY_NAME);
            final SearchViewWin searchWin = (SearchViewWin) actContext.get(UIAction.ACT_CTX_TARGET);
            final SearchViewGrid searchGrid = searchWin.getSearchGrid();
            @SuppressWarnings("unchecked")
            final Set<DynamicEntity> selectedRecords = (Set<DynamicEntity>) actContext.get(KEY_SELECT_RECORDS);
            final Map<String, List<DynamicEntity>> map = this.aclChecking(selectedRecords);
            final List<DynamicEntity> successfulList = map.get("successfulList");
            final List<DynamicEntity> srcEntityList = map.get("srcEntityList");
            childLevelEntityList = successfulList;
            if (successfulList.size() > 1 && !isClassificationSecurityMode()) {
                this.buildPopupDataSource(moduleId, entityName, searchGrid, successfulList, srcEntityList, actContext);
                return;
            }
            if (successfulList.size() == 0) {
                popupMsg(BusinessConstants.BATCH_NEW_DOC_FAIL, actContext);
                actContext.put(NOT_NEED_POPUP_MESSAGE, BusinessConstants.BATCH_NEW_DOC_FAIL);
                return;
            }
            DynamicEntity newDoc = null;
            newDoc = inheritanceToDestination(successfulList, actContext);
            if (this.isInheritResponsibleParty() && srcEntityList.size() == 1) {
                final InheritResponsibleParties custActionContext =
                        this.buildActionContext(srcEntityList.get(0), newDoc);
                ActionDispatcher.execute(custActionContext);
                newDoc = (DynamicEntity) custActionContext.getResultValue(ResultMapConstants.KEY_RESULT);
            }
            if (isClassificationSecurityMode()) {
                removeRepetitiveProductCategory(newDoc);
            }
            CbxUiUtils.updateNavi(moduleId);
            final String formVersion = newDoc.getEntityVersion() == null ? BusinessConstants.DEFAULT_VERSION : String
                    .valueOf(newDoc.getEntityVersion());
            CbxUiUtils.renderForm(moduleId, formVersion, newDoc);
            win.toggleWestCollapse(true);
        } catch (final ActionException e) {
            throw UIExceptionHandler.getInstance().handle(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.core.cbx.ui.zk.action.AbstractBatchSearchNewDocAction#inheritanceToDestination(java.util.List,
     * java.util.Map)
     */
    @Override
    public DynamicEntity inheritanceToDestination(final List<DynamicEntity> successfulList, final Map<String, Object> actContext)
            throws ActionException {
        final DataInheritance inheritanceCtx = new DataInheritance();
        final InheritanceTaskChain taskChain = new InheritanceTaskChain();
        if (StringUtils.equals(successfulList.get(0).getEntityName(), Qq.ENTITY_NAME_QQ_ITEM)) {
            QqHelper.doInheritanceForQqItem(successfulList, taskChain);
        } else {
            QqHelper.doInheritanceForQq(successfulList, taskChain);
        }
        inheritanceCtx.addInheritanceTaskChain(taskChain);
        ActionDispatcher.execute(inheritanceCtx);
        return taskChain.getResultEntity();
    }

    /* (non-Javadoc)
     * @see com.core.cbx.ui.zk.action.AbstractBatchSearchNewDocAction#getCustomSelectDefaultOkAction()
     */
    @Override
    protected String getCustomSelectDefaultOkAction() {
        return QqPopupSelectDefaultOkAction.class.getSimpleName();
    }

    @Override
    protected String checkConfirmedEditingStatus(final Set<DynamicEntity> selectedRecords) {
        StringBuffer sb = new StringBuffer();
        if (CollectionUtils.isNotEmpty(selectedRecords)) {
            final List<String> refNos = new ArrayList<String>();
            for (final DynamicEntity record : selectedRecords) {
                if (!StringUtils.equals(EntityConstants.EditingStatus.CONFIRMED,
                        record.getString(CommonFieldConstants.EDITING_STATUS))) {
                    final String refNo = record.getBusinessReference();
                    if (!refNos.contains(refNo)) {
                        refNos.add(refNo);
                        sb = sb.append(record.getBusinessReference())
                                .append("\r\n");
                    }
                }
            }
            return sb.toString();
        }
        return StringUtils.EMPTY;
    }

}
