package io.coti.sdk.base;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface IPropagatable extends IEntity {
}
