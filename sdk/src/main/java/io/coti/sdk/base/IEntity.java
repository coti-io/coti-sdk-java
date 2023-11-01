package io.coti.sdk.base;


import java.io.Serializable;

public interface IEntity extends Serializable {
    Hash getHash();

    void setHash(Hash hash);
}