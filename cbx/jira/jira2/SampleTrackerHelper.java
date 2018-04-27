// Copyright (c) 1998-2017 CBX Software Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.6.7
// ============================================================================
// CHANGE LOG
// CNT.6.7 : 2017-07-13, stephen.lai, CNT-30530
// ============================================================================
package com.core.cbx.sampletracker.helper;

import com.core.cbx.action.exception.ActionException;
import com.core.cbx.data.entity.DynamicEntity;

public interface SampleTrackerHelper {

    public void setSampleTracker(final DynamicEntity sampleTracker) ;

    public void saveSampleEvaluationProcess() throws ActionException ;

    public void updateDetailofSampleEvaluation() throws ActionException ;

    public void checkEvaluationNeedUpdatedProcess();

    public void lockSampleEvaluationProcess() throws ActionException ;

    public void unlockSampleEvaluationProcess() ;

    public void checkEvalNeedUpdatedForSampleDetailProcess() ;

    public void lockEvalForSampleDetailProcess() throws ActionException ;

    public void saveEvalForSampleDetailProcess() throws ActionException ;

    public void updateDetailofEvalForSampleDetail() throws ActionException ;

    public void unlockEvalForSampleDetailProcess() ;

    public void saveEntity(final DynamicEntity entity) throws ActionException ;

    public void confirmEntity(final DynamicEntity entity) throws ActionException ;

    public void amendEntity(final DynamicEntity entity) throws ActionException;

    public void lockSampleTrackerDoc(final DynamicEntity entity) ;

    public void unlockSampleTrackerDoc(final DynamicEntity entity) ;

    public void confirmSampleEvaluation(final DynamicEntity sampleTracker) throws ActionException;

}
