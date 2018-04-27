// Copyright (c) 1998-2016 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.5.12.0
// ============================================================================
// CHANGE LOG
// CNT.6.0b : 2016-10-14, dato.zeng, CNT-26038
// CNT.5.12.0 : 2016-02-02, derrick.liang, creation for CNT-21404
// ============================================================================
package com.core.cbx.inheritance.customizedprocessor;

import com.core.cbx.data.entity.DynamicEntity;
import com.core.cbx.inheritance.entity.DataMappingContainer;
import com.core.cbx.inheritance.transform.DMRProcessor;
import com.core.cbx.security.user.CntUser;

/**
 * @author derrick.liang
 *
 */
public class SampleTrackerToSampleEvaluationProcessor implements DMRProcessor {

    @Override
    public DynamicEntity processTransform(final DynamicEntity seo, final DynamicEntity deo) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.core.cbx.inheritance.transform.DMRProcessor#processUpdate(com.core.cbx.data.entity.DynamicEntity)
     */
    @Override
    public void processUpdate(final DynamicEntity sampleEvaluation) throws Exception {
//        final DynamicEntity spec = sampleEvaluation.getEntity(SampleEvaluation.SPEC);
//        if (spec != null) {
//            final Collection<DynamicEntity> specSets = spec.getEntityCollection(Spec.SPEC_SET);
//            if (CollectionUtils.isNotEmpty(specSets)) {
//                for (final DynamicEntity specSet : specSets) {
//                    final String sampleSize = specSet.getString(Spec.SAMPLE_SIZE);
//                    DynamicEntity specSize = null;
//                    try {
//                         specSize = DynamicEntityModel.getFullEntity(Spec.ENTITY_NAME_SPEC_SIZE, sampleSize);
//                    } catch (final DataException e) {
//                        throw new ActionException(ExceptionConstants.ACTION_EXCEPTION_000001, e);
//                    }
//                    if (specSize != null) {
//                        final String altLabel = specSize.getString(Spec.ALT_LABEL);
//                        final String sizeName = specSize.getString(Spec.SIZE_NAME);
//                        final StringBuilder builder = new StringBuilder();
//                        final String formatItemSampleSize = builder.append(altLabel).append("(").append(sizeName)
//                                .append(")").toString();
//                        sampleEvaluation.putValue(SampleEvaluation.ITEM_SAMPLE_SIZE, formatItemSampleSize);
//                    }
//                }
//            }
//        }


    }

    /* (non-Javadoc)
     * @see com.core.cbx.inheritance.transform.DMRProcessor#setRuleContent(java.lang.Object)
     */
    @Override
    public void setRuleContent(final Object content) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.core.cbx.inheritance.transform.DMRProcessor#setUser(com.core.cbx.security.user.CntUser)
     */
    @Override
    public void setUser(final CntUser user) {
        // TODO Auto-generated method stub

    }


    @Override
    public void setContainer(final DataMappingContainer dmContainer) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.core.cbx.inheritance.transform.DMRProcessor#getContainer()
     */
    @Override
    public DataMappingContainer getContainer() {
        // TODO Auto-generated method stub
        return null;
    }

}
