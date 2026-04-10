package br.com.dled.dledbackend.core.exceptions;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import static org.apache.catalina.util.CharsetMapper.DEFAULT_RESOURCE;

@RequiredArgsConstructor
public class BaseMessageError {
    private final String DEFAULT_RESOURCE = "messages";

    private final String key;
    private String [] params;
    public static final BaseMessageError GENERIC_EXCEPTION = new BaseMessageError("generic");
    public static final BaseMessageError GENERIC_METHOD_NOT_ALLOW = new BaseMessageError("generic.methodNotAllow");
    public static final BaseMessageError INVALID_API_KEY = new BaseMessageError("security.apiKey.invalid");
    public static final BaseMessageError CATEGORY_NOT_FOUND = new BaseMessageError("category.notFound");
    public static final BaseMessageError PRODUCT_NOT_FOUND = new BaseMessageError("product.notFound");
    public static final BaseMessageError COMPANY_NOT_FOUND = new BaseMessageError("company.notFound");
    public static final BaseMessageError ORDER_NOT_FOUND = new BaseMessageError("order.notFound");
    public static final BaseMessageError USER_NOT_FOUND = new BaseMessageError("user.notFound");
    public static final BaseMessageError USERNAME_ALREADY_EXISTS = new BaseMessageError("user.usernameAlreadyExists");
    public static final BaseMessageError USER_EMAIL_ALREADY_EXISTS = new BaseMessageError("user.emailAlreadyExists");
    public  BaseMessageError params(final String ... params) {
        this.params = ArrayUtils.clone(params);
        return this;
    }
    public String getMassage() {
        var message = tryGetMessageFromBundle();
        if(ArrayUtils.isNotEmpty(params)) {
            final var fmt = new MessageFormat(message);
            message = fmt.format(params);
        }
        return message;
    }
    public String tryGetMessageFromBundle() {
        return gerResource().getString(key);
    }

    public ResourceBundle gerResource() {
        return ResourceBundle.getBundle(DEFAULT_RESOURCE);
    }

}
