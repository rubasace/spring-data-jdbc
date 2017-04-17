package com.notech.oneapp.common.rest.common.util.persistence.repository.query.lookup;

public class ParameterProperties {

    private String name;
    private boolean ignoreCase;
    private boolean between;
    private SpecialLike specialLike;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public SpecialLike getSpecialLike() {
        return specialLike;
    }

    public void setSpecialLike(SpecialLike specialLike) {
        this.specialLike = specialLike;
    }

    public boolean isBetween() {
        return between;
    }

    public void setBetween(boolean between) {
        this.between = between;
    }

    public enum SpecialLike {
        STARTS_WITH,
        ENDS_WITH,
        CONTAINS
    }


}
