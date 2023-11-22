package com.dineshwayaman.okhttprxjava.Models;

public class LoginResult {
        private boolean isSuccess;
        private ResponseModel responseModel;

        public LoginResult(boolean isSuccess, ResponseModel responseModel) {
            this.isSuccess = isSuccess;
            this.responseModel = responseModel;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public ResponseModel getResponseModel() {
            return responseModel;
        }

}
