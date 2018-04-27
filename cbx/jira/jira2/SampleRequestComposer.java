// Copyright (c) 1998-2016 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.6.0b
// ============================================================================
// CHANGE LOG
// CNT.6.0b : 2016-10-13, jayden.yang, CNT-25993
// CNT.5.17 : 2016-08-02, gary.mo, CNT-24776
// CNT.5.9.0 : 2015-05-21, colin.guo, CNT-17517
// CNT.5.0.074 : 2014-12-12, hunting.he, CNT-15326
// CNT.5.0.074 : 2014-12-05, ivan.lo, CNT-14000
// CNT.5.0.072 : 2014-10-28, hunting.he, creation for CNT-14665
// ============================================================================
package com.core.cbx.samplerequest.form;


import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;

import com.core.cbx.data.constants.Item;
import com.core.cbx.data.constants.SampleRequest;
import com.core.cbx.data.entity.DynamicEntity;
import com.core.cbx.data.entity.EntityConstants;
import com.core.cbx.samplerequest.util.SampleRequestUtil;
import com.core.cbx.ui.zk.composer.BaseFormComposer;
import com.core.cbx.ui.zk.cul.event.CbxEvents;
import com.core.cbx.ui.zk.cul.grid.BaseGrid;
import com.core.cbx.ui.zk.util.CbxUiUtils;
public class SampleRequestComposer extends BaseFormComposer {

    private static final long serialVersionUID = 4994365384962593179L;
    private static final String LBL_POPUP_SAMPLE_REQUEST_ST_TITLE = "lbl.Popup.SampleRequestST.title";
    private static final String COMMON_ITEM_COLOR_ID_IN_MAPPER = "commonItemColorId";
    /* (non-Javadoc)
     * @see com.core.cbx.ui.zk.composer.GenericComposer#doAfterCompose(org.zkoss.zk.ui.Component)
     */
    @Override
    public void doAfterCompose(final Component comp) throws Exception {
        super.doAfterCompose(comp);
        final Object isFromSampleTracker = datasource.get(SampleRequest.IS_FROM_SAMPLE_TRACKER);
        if (isFromSampleTracker != null && isFromSampleTracker instanceof Boolean && (Boolean) isFromSampleTracker) {
            final Hlayout header = (Hlayout) view.getFellowIfAny(SampleRequest.MODULE_ID + "#HEADER");
            final Component title = header.getFirstChild();
            if (title != null && title instanceof Label) {
                ((Label) title).setValue(StringUtils.stripToEmpty(CbxUiUtils
                        .getLabel(LBL_POPUP_SAMPLE_REQUEST_ST_TITLE)));
            }
        }
        addListenerToGetCommonColor(comp);
    }

    private void addListenerToGetCommonColor(final Component component) {
        final BaseGrid sampleRequestItemGrid = (BaseGrid) component.getFellowIfAny(SampleRequest.SAMPLE_REQUEST_ITEM);
        if (sampleRequestItemGrid == null || sampleRequestItemGrid.isReadonly()) {
            return;
        }

        sampleRequestItemGrid.addEventListener("onAfterRender", new EventListener<Event>() {
            @Override
            public void onEvent(final Event arg0) throws Exception {
                final String commonItemColorId = getCommonColorIds(Item.ITEM_COLOR);
                datasource.put(COMMON_ITEM_COLOR_ID_IN_MAPPER, commonItemColorId);
            }
        });

        sampleRequestItemGrid.addEventListener(CbxEvents.ON_AFTER_DEL_ROW, new EventListener<Event>() {
            @Override
            public void onEvent(final Event arg0) throws Exception {
                final String commonItemColorId = getCommonColorIds(Item.ITEM_COLOR);
                datasource.put(COMMON_ITEM_COLOR_ID_IN_MAPPER, commonItemColorId);
                binder.loadComponent(component.getFellowIfAny(SampleRequest.MATERIAL_REQUEST_DETAIL));
            }
        });
    }

    @SuppressWarnings("unchecked")
    private String getCommonColorIds(final String entityName) {
        final List<DynamicEntity> sampleRequestItems = (List<DynamicEntity>) datasource
                .get(SampleRequest.SAMPLE_REQUEST_ITEM);
        final Collection<DynamicEntity> commonColors = SampleRequestUtil.getCommonItemColor(sampleRequestItems);
        String commonColorsId = "";
        if (CollectionUtils.isNotEmpty(commonColors)) {
            commonColorsId = EntityConstants.SEPARATOR_LEFT_BRACKET;
            int i = 0;
            for (final DynamicEntity commonColor : commonColors) {
                commonColorsId += "\'" + commonColor.getId() + "\'";
                if (i < commonColors.size() - 1) {
                    commonColorsId += EntityConstants.SEPARATOR_COMMA;
                }
                i++;
            }
            commonColorsId += EntityConstants.SEPARATOR_RIGHT_BRACKET;
        } else {
            commonColorsId = EntityConstants.SEPARATOR_LEFT_BRACKET + "\'\'" + EntityConstants.SEPARATOR_RIGHT_BRACKET;
        }
        return commonColorsId;
    }
}
