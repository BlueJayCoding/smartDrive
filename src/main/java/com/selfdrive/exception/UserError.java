package com.selfdrive.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserError {
    String message;
    String providerMessage;
}
