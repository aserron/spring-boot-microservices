package aserron.dlocal.demo.pm.rest.exception;

public enum ErrorMessages {

    SALE_SERV_INVAL_MERCHANT_ID("The merchant id fail the check request."),
    SALE_SERV_SALE_IDS_NOT_UNIQUE("The (merchant_id,transaction_id) pair already exist"),
    SALE_SERV_FIXERIO_MIN_WAIT_ERROR("Fixer.io: Time between request must be at less 30s"),
    // old
    /*
    MISSING_REQUIRED_FIELD("Missing required field. Please check documentation for required fields"),
    COULD_NOT_CREATE_USER_PROFILE("Could not create user profile"),
    COULD_NOT_UPDATE_USER_PROFILE("Could not update user profile"),
    COULD_NOT_DELETE_USER_PROFILE("Could not delete user profile"),
    */
    
    APP_NO_RECORD_FOUND("No record found for provided id"),
    APP_RECORD_ALREADY_EXISTS("Record already exists"),
    APP_INTERNAL_SERVER_ERROR("Something went wrong. Please repeat this operation later.");

    private String errorMessage;

    ErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
