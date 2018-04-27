// Copyright (c) 1998-2017 CBX Software Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.6.3
// ============================================================================
// CHANGE LOG
// CNT.6.3 : 2017-03-03, oliver.lin, CNT-28709
// CNT.5.9.0  : 2015-06-02, henry.he, CNT-17868
// CNT.5.9.0  : 2015-05-13, colin.guo, CNT-17472
// CNT.5.9.GA : 2015-04-27, calvin.yang, CNT-17142
// CNT.5.9.0 : 2015-04-23, jensen.weng, creation for CNT-17178
// CNT.5.8 GA : 2014-11-21, hunting.he, CNT-15036
// CNT.5.0.074 : 2014-12-05, ivan.lo, CNT-14000
// CNT.5.0.072 : 2014-10-28, hunting.he, creation for CNT-14665
// ============================================================================
package com.core.cbx.ui.zk.action;

import java.util.Map;

import com.core.cbx.action.constants.BusinessConstants;
import com.core.cbx.action.constants.BusinessPopupConstants;
import com.core.cbx.action.exception.ActionException;
import com.core.cbx.data.constants.SampleRequest;
import com.core.cbx.data.entity.DynamicEntity;
import com.core.cbx.ui.UiConstants;
import com.core.cbx.ui.constants.BusinessInheritanceConstants;
import com.core.cbx.ui.zk.component.MainWin;
import com.core.cbx.ui.zk.component.PopupOpenDocWin;
import com.core.cbx.ui.zk.composer.BaseFormComposer;
import com.core.cbx.ui.zk.composer.GenericComposer;
import com.core.cbx.ui.zk.util.CbxUiUtils;
import com.core.cbx.ui.zk.util.MessageUtil;

/**
 * @author hunting.he
 */
public class SampleRequestAction extends AbstractDataInheritanceAction {

    private String itemId;

    /* (non-Javadoc)
     * @see com.core.cbx.ui.zk.action.AbstractDataInheritanceAction#inheritanceToDest(java.util.Map)
     */
    @Override
    protected DynamicEntity inheritanceToDest(final Map<String, Object> actContext) throws ActionException {
        final GenericComposer composer = (GenericComposer) actContext.get(UIAction.ACT_CTX_COMPOSER);
        final DynamicEntity srcEntity = (DynamicEntity) composer.getDatasource();
        itemId = srcEntity.getId();
        return super.inheritanceToDest(actContext);
    }

    @Override
    public String getInheritanceId() {
        return BusinessInheritanceConstants.INHERITANCE_ACTION_ID_ITEM_TO_SAMPLE_REQUEST;
    }

    /*
     * (non-Javadoc)
     * @see com.core.cbx.ui.zk.action.AbstractDataInheritanceAction#switchWin(java.util.Map,
     * com.core.cbx.data.entity.DynamicEntity)
     */
    @Override
    protected void switchWin(final Map<String, Object> actContext, final DynamicEntity switchDoc)
            throws ActionException {
        switchDoc.put("SampleRequestBasedItemId", itemId);
        actContext.put(UiConstants.ACT_CONTEXT_MODULE_ID, SampleRequest.MODULE_ID);
        actContext.put(com.core.cbx.ui.zk.composer.BaseFormComposer.DOC_ARG, switchDoc);
        new PopupOpenDocWin(actContext);
    }

    /* (non-Javadoc)
     * @see com.core.cbx.ui.zk.action.AbstractDataInheritanceAction#process(java.util.Map)
     */
    @Override
    public void process(final Map<String, Object> actContext) {
        final BaseFormComposer composerParent = (BaseFormComposer) MainWin.getCurrent().getMainPanel().getAttribute(
                BusinessPopupConstants.POPUP_$c);
        final DynamicEntity itemDoc = (DynamicEntity) composerParent.getDatasource();
        if (CbxUiUtils.isItemFullEntityModified(itemDoc)) {
            final String title = CbxUiUtils.getLabel(getPromptTitle(actContext));
            MessageUtil.info(CbxUiUtils.getLabel(BusinessConstants.PLEASE_SAVE_DOCUMENT_FIRST), title);
        } else {
            super.process(actContext);
        }

    }

    /* (non-Javadoc)
     * @see com.core.cbx.ui.zk.action.AbstractDataInheritanceAction#needCheckDocModify()
     */
    @Override
    protected boolean needCheckDocModify() {
        return false;
    }
}
