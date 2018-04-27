// Copyright (c) 1998-2014 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.5.0.072
// ============================================================================
// CHANGE LOG
// CNT.5.0.072 : 2014-10-24, calvin.yang, creation for CNT-14851
// ============================================================================

package com.core.cbx.samplerequest.action.actionContext;

import com.core.cbx.action.actionContext.SaveAndConfirm;
import com.core.cbx.data.entity.DynamicEntity;

/**
 * @author calvin.yang
 */
public class SendSampleRequest extends SaveAndConfirm {

    public SendSampleRequest(final DynamicEntity entity) {
        super(entity);
    }

    @Override
    public String getBaseActionId() {
        return this.getClass().getSimpleName();
    }
}
