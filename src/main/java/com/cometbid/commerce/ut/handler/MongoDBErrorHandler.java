/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.handler;

import org.hibernate.ogm.compensation.BaseErrorHandler;
import org.hibernate.ogm.compensation.ErrorHandler.FailedGridDialectOperationContext;
import org.hibernate.ogm.compensation.ErrorHandler.RollbackContext;
import org.hibernate.ogm.compensation.ErrorHandlingStrategy;
import org.hibernate.ogm.compensation.operation.GridDialectOperation;
import org.hibernate.ogm.compensation.operation.InsertTuple;
import static org.hibernate.ogm.compensation.operation.OperationType.INSERT_TUPLE;
import static org.hibernate.ogm.compensation.operation.OperationType.REMOVE_TUPLE;
import org.hibernate.ogm.dialect.spi.TupleAlreadyExistsException;
import org.hibernate.ogm.model.key.spi.EntityKeyMetadata;
import org.hibernate.ogm.model.spi.Tuple;

/**
 *  
 * @author Gbenga
 */
public class MongoDBErrorHandler extends BaseErrorHandler {

    @Override
    public void onRollback(RollbackContext context) {
        // write all applied operations to a log file
        for (GridDialectOperation appliedOperation : context.getAppliedGridDialectOperations()) {
            switch (appliedOperation.getType()) {
                case INSERT_TUPLE:
                    EntityKeyMetadata entityKeyMetadata = appliedOperation.as(InsertTuple.class).getEntityKeyMetadata();
                    Tuple tuple = appliedOperation.as(InsertTuple.class).getTuple();

                    // write EKM and tuple to log file...
                    break;
                case REMOVE_TUPLE:
                    // ...
                    break;
                /*    case // ...
                // ...               
                
                    break;
                 */
                default:

            }
        }
    }

    @Override
    public ErrorHandlingStrategy onFailedGridDialectOperation(FailedGridDialectOperationContext context) {
        // Ignore this exception and continue
        if (context.getException() instanceof TupleAlreadyExistsException) {
            GridDialectOperation failedOperation = context.getFailedOperation();
            // write to log ...

            return ErrorHandlingStrategy.CONTINUE;
        } // But abort on all others
        else {
            return ErrorHandlingStrategy.ABORT;
        }
    }
}
