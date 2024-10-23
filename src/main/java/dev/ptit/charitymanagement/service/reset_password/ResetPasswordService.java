package dev.ptit.charitymanagement.service.reset_password;

import dev.ptit.charitymanagement.dtos.request.user.ForgotPasswordRequest;
import dev.ptit.charitymanagement.dtos.request.user.ResetPasswordRequest;

public interface ResetPasswordService {
    public boolean forgotPassword(ForgotPasswordRequest request);
    public boolean resetPassword(ResetPasswordRequest request);
}
